package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.Lastavard;
import lineage.bean.lineage.LastavardRaid;
import lineage.database.BackgroundDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.share.TimeLine;
import lineage.world.object.monster.LastavardBoss;
import lineage.world.object.monster.LastavardDoorMan;
import lineage.world.object.npc.background.door.LastavardDoor;

public class LastavardController {

	static private List<Lastavard> list; // 관리중인 라스타바드 목록.

	static public void init() {
		TimeLine.start("LastavardController..");
		
		try {
			list = new ArrayList<Lastavard>();
			// 1층 집회장 9시 문지기 [완료]
			Lastavard lastavard = new Lastavard(451);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32785, 32809, lastavard.getMap(), 4, 4735);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 1")), 32784, 32813, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 집회장 11시 문지기 [완료]
			lastavard = new Lastavard(451);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32813, 32833, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 2")), 32809, 32834, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 돌격대 훈련장 [완료]
			lastavard = new Lastavard(452);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32811, 32836, lastavard.getMap(), 6, 4711);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 1층] 여단장 다크펜서")), 32784, 32836, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 마수군왕의 집무실 입구 [완료] 
			lastavard = new Lastavard(453);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32741, 32851, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 3")), 32738, 32851, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 마수군왕의 집무실 3시 문지기 [완료]
			lastavard = new Lastavard(453);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32793, 32852, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 4")), 32790, 32853, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 마수군왕의 집무실 9시 문지기 [완료]
			lastavard = new Lastavard(453);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32768, 32830, lastavard.getMap(), 4, 4713);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 5")), 32767, 32834, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 마수군왕의 집무실 12시 문지기 [완료]
			lastavard = new Lastavard(453);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32807, 32832, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 6")), 32804, 32832, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 마수군왕의 집무실 [완료]
			lastavard = new Lastavard(453);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32749, 32736, lastavard.getMap(), 6, 4734);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 1층] 마수군왕 바란카 (변신전)")), 32838, 32759, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 마수 소환실 [완료]
			lastavard = new Lastavard(456);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32792, 32795, lastavard.getMap(), 6, 4734);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 1층] 사단장 싱클레어")), 32761, 32820, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 야수 조련실  [완료]
			lastavard = new Lastavard(454);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32749, 32855, lastavard.getMap(), 4, 4735);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 7")), 32748, 32858, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 야수 조련실 3시 문지기 [완료]
			lastavard = new Lastavard(454);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32813, 32822, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 8")), 32810, 32823, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 1층 야수 훈련장 [완료]
			lastavard = new Lastavard(455);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32801, 32862, lastavard.getMap(), 6, 4734);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 1층] 마수단장 카이바르")), 32758, 32823, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			
			// 지하통로 [완료]
			lastavard = new Lastavard(491);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32711, 32860, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 9")), 32708, 32860, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			//지하통로->지하결투장 [완료]
			lastavard = new Lastavard(491);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32733, 32860, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 10")), 32730, 32861, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 지하 결투장 [완료]
			lastavard = new Lastavard(495);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32773, 32785, lastavard.getMap(), 4, 4713);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 지하] 암살단장 블레이즈")), 32762, 32845, lastavard.getMap(), 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 지하감옥 [완료]
			lastavard = new Lastavard(495);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32815, 32817, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 11")), 32812, 32818, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 지하 통제실 9시
			lastavard = new Lastavard(493);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32746, 32723, lastavard.getMap(), 4, 4713);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 12")), 32745, 32726, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 지하 통제실 1시 [완료]
			lastavard = new Lastavard(493);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32775, 32776, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 13")), 32772, 32777, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			//지하 통제실 1시 -> 지하 처형장 [완료]
			lastavard = new Lastavard(493);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32806, 32770, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 14")), 32803, 32771, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 지하 처형장 
			lastavard = new Lastavard(494);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32856, 32758, lastavard.getMap(), 6, 4734);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 지하] 친위대장 카이트")), 32832, 32756, lastavard.getMap(), 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 지하 훈련장
			lastavard = new Lastavard(490);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32731, 32810, lastavard.getMap(), 6, 4711);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 친위대")), 32683, 32830, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 암살군왕의 집무실
			lastavard = new Lastavard(492);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32843, 32847, lastavard.getMap(), 4, 4735);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 지하] 암살군왕 슬레이브")), 32833, 32819, lastavard.getMap(), 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 2층 정령 소환실
			lastavard = new Lastavard(464);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32806, 32829, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 15")), 32803, 32830, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 2층 정령 서식지
			lastavard = new Lastavard(465);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32811, 32808, lastavard.getMap(), 6, 4711);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 어둠정령 수호자")), 32790, 32810, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 2층 암흑정령 연구실
			lastavard = new Lastavard(466);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32792, 32835, lastavard.getMap(), 6, 4734);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 2층] 신관장 바운티")), 32771, 32828, lastavard.getMap(), 5);
			synchronized (list) {
				list.add(lastavard);
			}
			// 2층 흑마법 수련장
			lastavard = new Lastavard(460);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32814, 32819, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 16")), 32811, 32820, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 2층 흑마법 연구실
			lastavard = new Lastavard(461);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32769, 32803, lastavard.getMap(), 6, 4734); //중간보스 첫번째 문
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 17")), 32684, 32861, 4); // 1번째
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(461);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32782, 32812, lastavard.getMap(), 4, 4735); //중간보스 두번째문
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 18")), 32748, 32858, 4); // 2번째
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(461);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32794, 32825, lastavard.getMap(), 6, 4734); //중간보스 세번째 문
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 19")), 32744, 32798, 4);  // 3번째
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(461);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32688, 32837, lastavard.getMap(), 4, 4735);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 2층] 마법단장 카르미엘")), 32841, 32820, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(461); 
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32695, 32797, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 20")), 32691, 32798, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 2층 마령군왕의 집무실
			lastavard = new Lastavard(462);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32813, 32865, lastavard.getMap(), 6, 4711);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 2층] 라이아")), 32804, 32839, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 2층 마령군왕의 서재
			lastavard = new Lastavard(463);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32734, 32854, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 21")), 32731, 32855, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(463);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32783, 32815, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 22")), 32780, 32815, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 용병 훈련장
			lastavard = new Lastavard(472);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32748, 32806, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 23")), 32745, 32806, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(472);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32808, 32799, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 24")), 32805, 32800, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 데빌로드 용병실
			lastavard = new Lastavard(477);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32804, 32857, lastavard.getMap(), 4, 4735);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 25")), 32803, 32860, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(477);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4732, null), 32772, 32807, lastavard.getMap(), 6, 4732);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 26")), 32777, 32806, 2);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(477);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32746, 32788, lastavard.getMap(), 4, 4713);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 27")), 32745, 32791, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 데빌로드 제단
			lastavard = new Lastavard(471);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32767, 32851, lastavard.getMap(), 4, 4735);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 28")), 32766, 32854, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(471);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32783, 32786, lastavard.getMap(), 4, 4713);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 3층] 용병대장 메파이스토")), 32790, 32815, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 악령제단
			lastavard = new Lastavard(470);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32751, 32839, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 29")), 32748, 32839, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(470);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32817, 32806, lastavard.getMap(), 4, 4713);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 30")), 32867, 32843, 5);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 명법군의 훈련장
			lastavard = new Lastavard(473);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32794, 32832, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 31")), 32791, 32833, 6); 
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(473);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4732, null), 32779, 32805, lastavard.getMap(), 6, 4732); 
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 32")), 32785, 32804, 2);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(473);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32885, 32816, lastavard.getMap(), 6, 4734); 
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 33")), 32738, 32827, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(473);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32833, 32792, lastavard.getMap(), 4, 4713);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 3층] 명법단장 크리퍼스")), 32922, 32847, lastavard.getMap(), 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 통제구역
			lastavard = new Lastavard(478);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32750, 32812, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 34")), 32746, 32813, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(478);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32735, 32786, lastavard.getMap(), 4, 4713);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 35")), 32734, 32790, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 중앙 통제실
			lastavard = new Lastavard(476);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4735, null), 32780, 32850, lastavard.getMap(), 4, 4735);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 36")), 32779, 32853, 4);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(476);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32805, 32800, lastavard.getMap(), 6, 4711);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 37")), 32801, 32801, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 3층 명법군왕의 집무실
			lastavard = new Lastavard(475);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32705, 32849, lastavard.getMap(), 6, 4734);
			lastavard.appendDoorMan((LastavardDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 문지기] 라스타바드 문지기 38")), 32701, 32850, 6);
			synchronized (list) {
				list.add(lastavard);
			}
			lastavard = new Lastavard(475);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32780, 32807, lastavard.getMap(), 4, 4713);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 3층] 명법군왕 헬바인")), 32781, 32843, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}
			// 4층 케이나 집무실
			LastavardRaid lastavard_raid = new LastavardRaid(530);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 케이나")), 32863, 32839, lastavard_raid.getMap(), 6);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32877, 32817, lastavard_raid.getMap(), 6, 4711);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			
			lastavard_raid = new LastavardRaid(531);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 비아타스")), 32757, 32739, lastavard_raid.getMap(), 4);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32789, 32738, lastavard_raid.getMap(), 6, 4711);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			//
			lastavard_raid = new LastavardRaid(531);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 바로메스")), 32792, 32789, lastavard_raid.getMap(), 0);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null), 32774, 32815, lastavard_raid.getMap(), 4, 4735);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			//
			//
			lastavard_raid = new LastavardRaid(531);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 엔디아스")), 32845, 32856, lastavard_raid.getMap(), 6);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4711, null),  32851, 32872, lastavard_raid.getMap(), 6, 4711);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			// 4층 이데아 집무실
			lastavard_raid = new LastavardRaid(532);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 이데아")), 32790, 32811, lastavard_raid.getMap(), 6);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32777, 32791, lastavard_raid.getMap(), 4, 4713);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			lastavard_raid = new LastavardRaid(533);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 티아메스")), 32871, 32896, lastavard_raid.getMap(), 6);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32824, 32885, lastavard_raid.getMap(), 4,  4735);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			// 
			lastavard_raid = new LastavardRaid(533);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 라미아스")), 32786, 32891, lastavard_raid.getMap(), 6);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null), 32765, 32856, lastavard_raid.getMap(), 4,  4735);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			//
			lastavard_raid = new LastavardRaid(533);
			lastavard_raid.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층] 대법관 바로드")), 32754, 32798, lastavard_raid.getMap(), 4);
			lastavard_raid.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4713, null),  32727, 32797, lastavard_raid.getMap(), 4,  4713);
			synchronized (list) {
				list.add(lastavard_raid);
			}
			// 4층 장로 회의장
			lastavard = new Lastavard(534);
			lastavard.appendDoor((LastavardDoor) BackgroundDatabase.toObject(null, 4734, null), 32879, 32842, lastavard.getMap(), 6, 4734);
			lastavard.appendBoss((LastavardBoss) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("[라스타바드 4층 카산드라방] 부제사장 카산드라")), 32865, 32821, lastavard.getMap(), 6);
			synchronized (list) {
				list.add(lastavard);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 스폰
		synchronized (list) {
			for (Lastavard l : list)
				l.init();
		}

		TimeLine.end();
	}

	static public void close() {
		synchronized (list) {
			for (Lastavard l : list)
				l.close(true);
			list.clear();
		}
	}

	static public void toTimer(long time) {
		synchronized (list) {
			for (Lastavard lastavard : list)
				try {
					lastavard.toTimer(time);
				} catch (Exception e) {
					System.out.println("toTimer(long time) : " + e);
				}
		}
	}

}
