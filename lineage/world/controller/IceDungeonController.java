package lineage.world.controller;

import java.util.ArrayList;
import java.util.List;

import lineage.bean.lineage.IceDungeon;
import lineage.database.BackgroundDatabase;
import lineage.database.MonsterDatabase;
import lineage.database.MonsterSpawnlistDatabase;
import lineage.share.TimeLine;
import lineage.world.object.monster.IceDungeonBoss;
import lineage.world.object.monster.IceDungeonDoorMan;
import lineage.world.object.npc.background.door.IceDungeonDoor;


public class IceDungeonController {

	static private List<IceDungeon> list; // 관리중인 얼음수정 동굴 목록.

	static public void init() {
		TimeLine.start("IceDungeonController..");
		
		try {
			list = new ArrayList<IceDungeon>();
			// 적대적인
			IceDungeon icedungeon = new IceDungeon(2101);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32784, 32818, icedungeon.getMap(), 6, 6640);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32761, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32758, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32758, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32761, 32810, 6);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32760, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32758, 32825, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32757, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32761, 32825, 6);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32772, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32769, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32769, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32772, 32810, 6);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32772, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32769, 32825, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32769, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32772, 32825, 6);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32782, 32816, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32782, 32821, 6);
			synchronized (list) {
				list.add(icedungeon);
			}
			////////////////2 진형
			// 적대적인
			icedungeon = new IceDungeon(2101);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32852, 32806, icedungeon.getMap(), 6, 6640);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32829, 32805, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32831, 32807, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32831, 32805, 6);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32835, 32802, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32837, 32802, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32837, 32799, 6);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32835, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32837, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32835, 32813, 6);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32839, 32807, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32842, 32805, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32842, 32807, 6);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32849, 32804, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32849, 32810, 6);
			synchronized (list) {
				list.add(icedungeon);
			}
			////////////////3 진형
			// 적대적인
			icedungeon = new IceDungeon(2101);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32822, 32855, icedungeon.getMap(), 6, 6640);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32855, 32847, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32853, 32847, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32855, 32845, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32853, 32845, 0);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32855, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32853, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32855, 32856, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32853, 32856, 0);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32844, 32845, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32844, 32848, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32842, 32848, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32842, 32845, 0);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32844, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32841, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32844, 32856, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32841, 32856, 0);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32826, 32854, 2);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32826, 32858, 2);
			synchronized (list) {
				list.add(icedungeon);
			}
			
			////////////////4 진형
			// 적대적인
			icedungeon = new IceDungeon(2101);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6642, null), 32762, 32916, icedungeon.getMap(), 6, 6642);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32770, 32892, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32769, 32894, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32771, 32896, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32766, 32891, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32764, 32893, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32769, 32898, 0);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32770, 32902, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32768, 32900, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32766, 32901, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32764, 32903, 0);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32762, 32894, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32760, 32892, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32761, 32896, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32759, 32898, 0);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32761, 32901, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32759, 32904, 0);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32765, 32910, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32761, 32910, 0);
			synchronized (list) {
				list.add(icedungeon);
			}
			////////////////여왕방
			// 적대적인
			icedungeon = new IceDungeon(2101);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32853, 32920, icedungeon.getMap(), 6, 6640);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32813, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32819, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32825, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32830, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32836, 32914, 4);
			
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32812, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32818, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32824, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32829, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32835, 32927, 0);
			//얼음여왕
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("얼음 여왕")), 32842, 32921, 6);
			synchronized (list) {
				list.add(icedungeon);
			}
			
			// 우호적인
			icedungeon = new IceDungeon(2151);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32784, 32818, icedungeon.getMap(), 6, 6640);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32761, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32758, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32758, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32761, 32810, 6);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32760, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32758, 32825, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32757, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32761, 32825, 6);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32772, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32769, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32769, 32813, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32772, 32810, 6);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32772, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32769, 32825, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32769, 32822, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32772, 32825, 6);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32782, 32816, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32782, 32821, 6);

			synchronized (list) {
				list.add(icedungeon);
			}
			////////////////2 진형
			// 우호적인
			icedungeon = new IceDungeon(2151);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32852, 32806, icedungeon.getMap(), 6, 6640);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32829, 32805, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32831, 32807, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32831, 32805, 6);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32835, 32802, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32837, 32802, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32837, 32799, 6);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32835, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32837, 32810, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32835, 32813, 6);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 스콜피온")), 32839, 32807, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 에티")), 32842, 32805, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32842, 32807, 6);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32849, 32804, 6);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32849, 32810, 6);

			synchronized (list) {
				list.add(icedungeon);
			}
			////////////////3 진형
			// 우호적인
			icedungeon = new IceDungeon(2151);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32822, 32855, icedungeon.getMap(), 6, 6640);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32855, 32847, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32853, 32847, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32855, 32845, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32853, 32845, 0);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32855, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32853, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32855, 32856, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32853, 32856, 0);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32844, 32845, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32844, 32848, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32842, 32848, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32842, 32845, 0);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스맨")), 32844, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32841, 32859, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32844, 32856, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32841, 32856, 0);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32826, 32854, 2);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32826, 32858, 2);

			synchronized (list) {
				list.add(icedungeon);
			}
			
			////////////////4 진형
			// 우호적인
			icedungeon = new IceDungeon(2151);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6642, null), 32762, 32916, icedungeon.getMap(), 6, 6642);
			//1구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32770, 32892, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32769, 32894, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32771, 32896, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32766, 32891, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32764, 32893, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32769, 32898, 0);
			//2구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32770, 32902, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32768, 32900, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32766, 32901, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32764, 32903, 0);
			//3구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32762, 32894, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32760, 32892, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32761, 32896, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32759, 32898, 0);
			//4구역
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32761, 32901, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 아이스 골렘")), 32759, 32904, 0);
			//입구
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32765, 32910, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 활")), 32761, 32910, 0);
			
			synchronized (list) {
				list.add(icedungeon);
			}
			////////////////데몬방
			// 우호적인
			icedungeon = new IceDungeon(2151);
			icedungeon.appendDoor((IceDungeonDoor) BackgroundDatabase.toObject(null, 6640, null), 32853, 32920, icedungeon.getMap(), 6, 6640);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32813, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32819, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32825, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32830, 32914, 4);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32836, 32914, 4);
			
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32812, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32818, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32824, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32829, 32927, 0);
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("혹한의 얼음 여왕 근위병 창")), 32835, 32927, 0);

			//아이스 데몬
			icedungeon.appendDoorMan((IceDungeonDoorMan) MonsterSpawnlistDatabase.newInstance(MonsterDatabase.find("아이스 데몬")), 32842, 32921, 6);
			synchronized (list) {
				list.add(icedungeon);
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 스폰
		synchronized (list) {
			for (IceDungeon l : list)
				l.init();
		}

		TimeLine.end();
	}

	static public void close() {
		synchronized (list) {
			for (IceDungeon l : list)
				l.close(true);
			list.clear();
		}
	}

	static public void toTimer(long time) {
		synchronized (list) {
			for (IceDungeon icedungeon : list)
				try {
					icedungeon.toTimer(time);
				} catch (Exception e) {
				}
		}
	}
}
