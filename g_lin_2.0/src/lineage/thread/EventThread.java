package lineage.thread;

import java.util.ArrayList;
import java.util.List;

import lineage.Main;
import lineage.bean.event.Event;
import lineage.share.Common;
import lineage.share.Lineage;
import lineage.share.TimeLine;
import lineage.util.Util;

public final class EventThread implements Runnable {

    static private EventThread thread; // 쓰레드 인스턴스
    static private boolean running; // 쓰레드 활성화 상태 플래그
    static private List<Event> pool; // 메모리 재사용을 위한 풀
    static private List<Event> list; // 처리 대기 중인 이벤트 리스트
    static private List<Event> run; // 현재 처리 중인 이벤트 리스트

    /**
     * 초기화 처리 함수.
     * - 풀, 리스트 초기화
     * - 쓰레드 시작
     */
    static public void init() {
        TimeLine.start("EventThread..");

        pool = new ArrayList<>(); // 풀 리스트 초기화
        run = new ArrayList<>(); // 처리 중 이벤트 리스트 초기화
        list = new ArrayList<>(); // 대기 중 이벤트 리스트 초기화
        thread = new EventThread(); // 쓰레드 인스턴스 생성
        start(); // 쓰레드 시작

        TimeLine.end();
    }

    /**
     * 쓰레드 활성화 함수.
     * - 쓰레드 실행을 위한 플래그 설정 및 시작
     */
    static private void start() {
        running = true; // 활성화 플래그 설정
        Thread t = new Thread(thread); // 새로운 쓰레드 생성
        t.setName(EventThread.class.toString()); // 쓰레드 이름 설정
        t.start(); // 쓰레드 실행
    }

    /**
     * 쓰레드 종료처리 함수.
     * - 플래그를 변경하여 종료를 요청
     */
    static public void close() {
        running = false; // 활성화 플래그 해제
        thread = null; // 쓰레드 참조 해제
        // 개선점: 자원 해제를 위해 리스트 초기화 추가
        list.clear();
        run.clear();
        pool.clear();
    }

    /**
     * 새로운 이벤트 추가.
     * - 처리 대기 리스트에 이벤트를 추가.
     * 
     * @param e 추가할 이벤트 객체
     */
    static public void append(Event e) {
        if (!running) // 쓰레드가 종료 상태면 무시
            return;

        synchronized (list) { // 동기화 처리로 멀티쓰레드 안전성 보장
            list.add(e);
        }
    }

    /**
     * 풀 메모리를 정리.
     * - 풀 크기가 과도할 경우 초기화 및 GC 호출.
     */
    static private void clearPool() {
        TimeLine.start("EventThread 에서 Pool 초과로 메모리 정리 중..");

        pool.clear(); // 풀 초기화
        System.gc(); // 가비지 컬렉션 호출
        // 개선점: System.gc() 호출 대신 풀 크기 제한을 설정하는 것이 더 적합

        TimeLine.end();
    }

    /**
     * 이벤트 객체를 풀에 추가.
     * 
     * @param e 추가할 이벤트 객체
     */
    static public void setPool(Event e) {
        if (Lineage.pool_eventthread) { // 풀링 활성화 여부 확인
            synchronized (pool) {
                if (Main.running && Util.isPoolAppend(pool)) {
                    pool.add(e); // 풀에 이벤트 추가
                } else {
                    e = null; // 사용하지 않는 객체를 null로 설정
                    clearPool(); // 필요 시 풀 정리
                }
            }
        } else {
            e = null; // 풀링 비활성화 시 객체 제거
        }
    }

    /**
     * 풀에서 이벤트 객체 가져오기.
     * - 재사용 가능한 객체 반환.
     * 
     * @param c 가져올 객체의 클래스 타입
     * @return 해당 클래스의 이벤트 객체
     */
    static public Event getPool(Class<?> c) {
        if (Lineage.pool_eventthread) {
            synchronized (pool) {
                Event e = findPool(c); // 해당 클래스 타입의 객체 검색
                if (e != null)
                    pool.remove(e); // 풀에서 제거 후 반환
                return e;
            }
        }
        return null; // 풀링 비활성화 시 null 반환
    }

    /**
     * 풀에서 특정 클래스 타입의 객체 검색.
     * 
     * @param c 검색할 클래스 타입
     * @return 해당 객체, 없으면 null
     */
    static private Event findPool(Class<?> c) {
        for (Event e : pool) {
            if (e.getClass().equals(c)) // 클래스 타입 일치 확인
                return e;
        }
        return null; // 해당 타입 객체가 없을 경우
    }

    /**
     * 쓰레드 메인 로직.
     * - 이벤트를 처리 대기 리스트에서 실행 리스트로 옮긴 후 실행.
     * - 풀링 활성화 시 처리 완료 후 풀에 추가.
     */
    @Override
    public void run() {
        while (running) { // 쓰레드 활성화 동안 반복
            try {
                if (list.size() == 0) { // 대기 중인 이벤트가 없으면 대기
                    Thread.sleep(Common.THREAD_SLEEP);
                    continue;
                }

                synchronized (list) { // 동기화 후 실행 리스트로 이동
                    run.addAll(list);
                    list.clear();
                }

                // 실행 리스트의 이벤트 처리
                for (Event e : run) {
                    try {
                        e.init(); // 이벤트 초기화
                    } catch (Exception ex) {
                        lineage.share.System.printf(
                            "lineage.thread.EventThread.run()\r\n : %s\r\n : %s\r\n",
                            e.toString(), ex.toString()
                        ); // 오류 로그 출력
                    } finally {
                        e.close(); // 이벤트 종료
                    }
                }

                if (Lineage.pool_eventthread) { // 풀링 활성화 시 처리 완료 후 풀에 추가
                    synchronized (pool) {
                        if (Main.running)
                            pool.addAll(run); // 실행된 이벤트를 풀로 이동
                        if (!Util.isPoolAppend(pool)) // 풀 크기 확인 후 정리
                            clearPool();
                    }
                }

                run.clear(); // 실행 리스트 초기화
            } catch (Exception e) {
                lineage.share.System.printf(
                    "lineage.thread.EventThread.run()\r\n : %s\r\n",
                    e.toString()
                ); // 쓰레드 처리 중 예외 발생 시 로그 출력
            }
        }
    }

    /**
     * 대기 중인 이벤트 리스트 크기 반환.
     * 
     * @return 대기 중인 이벤트 개수
     */
    static public int getListSize() {
        return list.size();
    }

    /**
     * 실행 중인 이벤트 리스트 크기 반환.
     * 
     * @return 실행 중인 이벤트 개수
     */
    static public int getRunSize() {
        return run.size();
    }

    /**
     * 풀 크기 반환.
     * 
     * @return 풀에 저장된 이벤트 개수
     */
    static public int getPoolSize() {
        return pool.size();
    }
}