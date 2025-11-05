package lineage.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * 월드맵 추출기 
 * @author Administrator
 *
 */
public class WorldMapAnalysis {
	private static StringBuffer _sb; // 좌표 저장
	private static StringBuffer _sb1; // 맵타일 저장
	private static HashMap<String, Map> _list;

	public static void main(String[] args) throws Exception {
		_sb = new StringBuffer();
		_sb1 = new StringBuffer();
		_list = new HashMap<String, Map>();

		WorldMapAnalysis s = new WorldMapAnalysis();
		// 파일 읽기
		System.out.println("파일 읽기 시작..");
		s.readFile();

		// 분석
		System.out.println("파일 분석 시작..");
		s.analyze();

		// 파일 출력
		System.out.println("파일 출력 시작..");
		s.writeFile();

		System.out.println("끝..");
	}

	public void readFile() throws Exception {
		BufferedReader lnr = new BufferedReader(new FileReader("./maps/worldmap/worldmap.cpp"));
		String line = lnr.readLine(); // 첫줄 무시
		while ((line = lnr.readLine()) != null) {
			if (line.startsWith("#define ")) {
				_sb.append(line.substring(8));
				_sb.append(",");
			} else if (line.startsWith("unsigned char _map_")) {
				_sb1.append(line.substring(19));
			} else {
				break;
			}
		}
	}

	public void analyze() throws Exception {
		// 1단계 분석
		StringTokenizer st = new StringTokenizer(_sb.toString(), ",");
		int count = st.countTokens(); // 갯수
		String temp = null; // 임시 저장용
		int tempCount; // 임시 저장용
		for (int i = 0; i < count; i += 4) {
			Map m = new Map();
			m.x1 = st.nextToken();
			m.x2 = st.nextToken();
			m.y1 = st.nextToken();
			m.y2 = st.nextToken();

			StringTokenizer st1 = new StringTokenizer(m.x1, "	");
			m.map = st1.nextToken().substring(7);
			m.x1 = st1.nextToken();
			st1 = new StringTokenizer(m.x2, "	");
			temp = st1.nextToken();
			m.x2 = st1.nextToken();
			st1 = new StringTokenizer(m.y1, "	");
			temp = st1.nextToken();
			m.y1 = st1.nextToken();
			st1 = new StringTokenizer(m.y2, "	");
			temp = st1.nextToken();
			m.y2 = st1.nextToken();
			_list.put(m.map, m);
		}

		// 2단계 분석
		st = new StringTokenizer(_sb1.toString(), ";");
		count = st.countTokens(); // 갯수
		for (int i = 0; i < count; ++i) {
			String line = st.nextToken();
			StringTokenizer st1 = new StringTokenizer(line, "_");
			Map m = _list.get(st1.nextToken());

			st1 = new StringTokenizer(st1.nextToken(), "][");
			tempCount = Integer.valueOf(st1.nextToken());
			m.size = st1.nextToken();

			st1 = new StringTokenizer(st1.nextToken(), "}");
			StringBuffer _s = new StringBuffer();
			_s.append(st1.nextToken().substring(1).substring(2));

			for (int j = 1; j < tempCount; ++j) {
				_s.append("\r\n");
				_s.append(st1.nextToken().substring(2));
			}
			m.tile = _s.toString();
		}
	}

	public void writeFile() throws Exception {
		File f = new File("maps");
		f.mkdir();
		f = new File("maps/Text");
		f.mkdir();

		BufferedOutputStream bw = null;
		StringBuffer _s = new StringBuffer();
		_s.append("# 번호,X 시작지점,X 끝지점,Y 시작지점,Y 끝지점,가로크기\r\n");
		// text파일 출력.
		for (Map m : _list.values()) {
			_s.append(m.map);
			_s.append(",");
			_s.append(m.x1);
			_s.append(",");
			_s.append(m.x2);
			_s.append(",");
			_s.append(m.y1);
			_s.append(",");
			_s.append(m.y2);
			_s.append(",");
			_s.append(m.size);
			_s.append("\r\n");

			bw = new BufferedOutputStream(new FileOutputStream("maps/Text/" + m.map + ".txt"));
			bw.write(m.tile.getBytes());
			bw.close();
		}
		// csv파일 출력
		bw = new BufferedOutputStream(new FileOutputStream("maps/maps.csv"));
		bw.write(_s.toString().getBytes());
		bw.close();
	}

	public class Map {
		public String map;
		public String x1;
		public String x2;
		public String y1;
		public String y2;
		public String size;
		public String tile;
	}

}