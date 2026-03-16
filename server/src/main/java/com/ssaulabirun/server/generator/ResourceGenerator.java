package com.ssaulabirun.server.generator;

import com.ssaulabirun.server.game.item.ItemManager;
import com.ssaulabirun.server.game.item.L1Item;
import com.ssaulabirun.server.game.npc.NpcManager;
import com.ssaulabirun.server.game.npc.NpcTemplate;
import com.ssaulabirun.server.game.world.GameMap;
import com.ssaulabirun.server.game.world.L1World;
import com.ssaulabirun.server.game.world.MapTile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class ResourceGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ResourceGenerator.class);

    public void generate() {
        generateMaps();
        generateNpcTemplates();
        generateItems();
        spawnNpcs();
        logger.info("리소스 생성 완료: 맵, NPC, 아이템이 생성되었습니다.");
    }

    private void generateMaps() {
        String[] mapNames = {"시작 마을", "숲", "던전", "성 마을", "전장"};
        int[] mapIds = {1, 2, 3, 4, 5};
        MapTile.TileType[][] patterns = {
                {MapTile.TileType.GRASS, MapTile.TileType.TOWN},
                {MapTile.TileType.FOREST, MapTile.TileType.GRASS},
                {MapTile.TileType.DUNGEON, MapTile.TileType.MOUNTAIN},
                {MapTile.TileType.TOWN, MapTile.TileType.GRASS},
                {MapTile.TileType.GRASS, MapTile.TileType.MOUNTAIN}
        };

        for (int m = 0; m < mapIds.length; m++) {
            GameMap map = new GameMap(mapIds[m], mapNames[m], 200, 200);
            Random rng = new Random(mapIds[m] * 31L);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    MapTile.TileType type = rng.nextDouble() < 0.85
                            ? patterns[m][0]
                            : patterns[m][1];
                    boolean passable = type != MapTile.TileType.WATER && type != MapTile.TileType.MOUNTAIN;
                    int elevation = rng.nextInt(5);
                    map.setTile(x, y, new MapTile(type, passable, elevation));
                }
            }
            L1World.getInstance().addMap(map);
        }
        logger.info("맵 5개 생성 완료");
    }

    private void generateNpcTemplates() {
        NpcManager nm = NpcManager.getInstance();

        // Map 1 - Starting village (level 1-5)
        nm.addTemplate(new NpcTemplate(1, "고블린", 1, 30, 0, 8, 8, 8, 4, 4, 1, "none", new int[]{1, 2}, 15, 10));
        nm.addTemplate(new NpcTemplate(2, "슬라임", 1, 20, 0, 6, 6, 8, 2, 2, 1, "none", new int[]{1}, 10, 5));
        nm.addTemplate(new NpcTemplate(3, "쥐", 1, 15, 0, 5, 10, 6, 2, 2, 1, "none", new int[]{}, 8, 3));
        nm.addTemplate(new NpcTemplate(4, "작은 고블린", 2, 40, 0, 10, 8, 10, 4, 4, 1, "none", new int[]{1, 3}, 20, 12));
        nm.addTemplate(new NpcTemplate(5, "고블린 병사", 3, 60, 0, 12, 10, 10, 4, 4, 1, "none", new int[]{1, 3, 4}, 30, 18));

        // Map 2 - Forest (level 5-15)
        nm.addTemplate(new NpcTemplate(6, "늑대", 5, 80, 0, 14, 14, 12, 4, 4, 1, "none", new int[]{5}, 50, 25));
        nm.addTemplate(new NpcTemplate(7, "큰 늑대", 7, 110, 0, 16, 14, 14, 4, 4, 1, "none", new int[]{5, 6}, 70, 35));
        nm.addTemplate(new NpcTemplate(8, "오크", 8, 130, 0, 18, 10, 16, 6, 4, 1, "none", new int[]{6, 7}, 85, 45));
        nm.addTemplate(new NpcTemplate(9, "오크 전사", 10, 160, 0, 20, 12, 16, 6, 4, 1, "none", new int[]{7, 8}, 105, 55));
        nm.addTemplate(new NpcTemplate(10, "독거미", 8, 100, 0, 12, 16, 10, 6, 4, 1, "poison", new int[]{9}, 80, 40));
        nm.addTemplate(new NpcTemplate(11, "큰 독거미", 12, 150, 0, 14, 18, 12, 6, 4, 1, "poison", new int[]{9, 10}, 120, 60));
        nm.addTemplate(new NpcTemplate(12, "트롤", 14, 220, 0, 22, 10, 20, 4, 4, 1, "none", new int[]{10, 11}, 145, 75));
        nm.addTemplate(new NpcTemplate(13, "도적단원", 10, 140, 0, 14, 16, 12, 8, 6, 1, "none", new int[]{8, 12}, 100, 80));
        nm.addTemplate(new NpcTemplate(14, "사냥꾼", 12, 160, 0, 14, 18, 12, 10, 8, 2, "none", new int[]{8, 13}, 115, 90));
        nm.addTemplate(new NpcTemplate(15, "멧돼지", 6, 90, 0, 14, 12, 14, 2, 2, 1, "none", new int[]{5}, 60, 30));

        // Map 3 - Dungeon (level 15-30)
        nm.addTemplate(new NpcTemplate(16, "해골 전사", 15, 200, 0, 20, 14, 16, 6, 4, 1, "undead", new int[]{14, 15}, 160, 100));
        nm.addTemplate(new NpcTemplate(17, "좀비", 16, 230, 0, 18, 10, 18, 4, 4, 1, "undead", new int[]{14, 16}, 175, 110));
        nm.addTemplate(new NpcTemplate(18, "유령", 18, 180, 0, 16, 18, 10, 14, 14, 2, "dark", new int[]{17, 18}, 200, 120));
        nm.addTemplate(new NpcTemplate(19, "해골 마법사", 20, 190, 100, 12, 12, 10, 18, 16, 3, "dark", new int[]{18, 19}, 230, 140));
        nm.addTemplate(new NpcTemplate(20, "뱀파이어 박쥐", 17, 150, 0, 14, 20, 12, 10, 8, 2, "dark", new int[]{14}, 180, 115));
        nm.addTemplate(new NpcTemplate(21, "미라", 22, 280, 0, 22, 10, 20, 8, 8, 1, "undead", new int[]{20, 21}, 265, 160));
        nm.addTemplate(new NpcTemplate(22, "어둠의 기사", 25, 320, 0, 26, 16, 22, 10, 8, 1, "dark", new int[]{21, 22}, 310, 200));
        nm.addTemplate(new NpcTemplate(23, "리치", 28, 300, 200, 14, 12, 12, 24, 20, 4, "dark", new int[]{22, 23}, 360, 230));
        nm.addTemplate(new NpcTemplate(24, "가고일", 20, 250, 0, 22, 14, 20, 10, 8, 2, "none", new int[]{20, 24}, 240, 150));
        nm.addTemplate(new NpcTemplate(25, "드래곤 새끼", 30, 450, 50, 30, 16, 28, 14, 10, 2, "fire", new int[]{24, 25}, 420, 280));

        // Map 4 - Castle Town (level 25-40)
        nm.addTemplate(new NpcTemplate(26, "어둠의 기사단원", 25, 350, 0, 28, 18, 24, 10, 8, 1, "dark", new int[]{22, 26}, 330, 220));
        nm.addTemplate(new NpcTemplate(27, "타락한 사제", 28, 280, 150, 16, 12, 14, 22, 20, 3, "dark", new int[]{23, 27}, 380, 250));
        nm.addTemplate(new NpcTemplate(28, "악마 졸개", 30, 400, 0, 32, 16, 28, 14, 10, 1, "dark", new int[]{25, 28}, 440, 300));
        nm.addTemplate(new NpcTemplate(29, "뿔달린 악마", 35, 500, 50, 36, 18, 30, 16, 12, 2, "fire", new int[]{28, 29}, 560, 380));
        nm.addTemplate(new NpcTemplate(30, "악마 마법사", 33, 380, 200, 18, 14, 16, 28, 24, 4, "dark", new int[]{27, 30}, 510, 350));
        nm.addTemplate(new NpcTemplate(31, "성채의 수호자", 38, 600, 100, 40, 20, 38, 14, 12, 1, "none", new int[]{29, 31}, 660, 450));
        nm.addTemplate(new NpcTemplate(32, "암흑 드래곤", 40, 800, 200, 44, 18, 40, 20, 14, 3, "dark", new int[]{30, 31, 32}, 880, 600));

        // Map 5 - Battlefield (level 40-50)
        nm.addTemplate(new NpcTemplate(33, "전장의 악마", 40, 700, 100, 42, 22, 38, 20, 16, 2, "dark", new int[]{31, 32}, 820, 560));
        nm.addTemplate(new NpcTemplate(34, "지옥의 기사", 43, 800, 0, 48, 24, 44, 16, 12, 1, "fire", new int[]{32, 33}, 950, 650));
        nm.addTemplate(new NpcTemplate(35, "대악마", 45, 900, 200, 50, 20, 46, 26, 20, 3, "dark", new int[]{33, 34, 35}, 1100, 760));
        nm.addTemplate(new NpcTemplate(36, "전쟁신의 사도", 48, 1100, 300, 54, 24, 50, 28, 22, 3, "holy", new int[]{34, 35, 36}, 1350, 900));
        nm.addTemplate(new NpcTemplate(37, "파멸의 드래곤", 50, 1500, 400, 60, 22, 56, 30, 24, 4, "fire", new int[]{35, 36, 37}, 1800, 1200));

        // Additional mobs for variety
        nm.addTemplate(new NpcTemplate(38, "고블린 샤먼", 5, 70, 60, 8, 8, 8, 14, 12, 3, "none", new int[]{2, 3}, 55, 30));
        nm.addTemplate(new NpcTemplate(39, "오크 주술사", 12, 140, 100, 12, 10, 12, 16, 14, 3, "none", new int[]{9, 10}, 130, 70));
        nm.addTemplate(new NpcTemplate(40, "숲의 요정", 10, 100, 80, 10, 16, 10, 16, 14, 3, "nature", new int[]{7, 8}, 100, 55));
        nm.addTemplate(new NpcTemplate(41, "돌 골렘", 18, 300, 0, 24, 6, 30, 4, 4, 1, "earth", new int[]{15, 16}, 210, 130));
        nm.addTemplate(new NpcTemplate(42, "얼음 마법사", 22, 200, 150, 10, 12, 10, 22, 18, 4, "ice", new int[]{19, 20}, 255, 165));
        nm.addTemplate(new NpcTemplate(43, "번개 원소", 26, 260, 120, 14, 20, 14, 22, 16, 3, "lightning", new int[]{22, 23}, 330, 210));
        nm.addTemplate(new NpcTemplate(44, "불의 악마", 32, 420, 80, 36, 16, 30, 20, 14, 2, "fire", new int[]{28, 29}, 480, 320));
        nm.addTemplate(new NpcTemplate(45, "심연의 괴물", 36, 550, 150, 40, 18, 36, 22, 18, 2, "dark", new int[]{30, 31}, 620, 420));
        nm.addTemplate(new NpcTemplate(46, "전설의 늑대왕", 15, 250, 0, 22, 22, 18, 6, 6, 1, "none", new int[]{11, 12}, 185, 120));
        nm.addTemplate(new NpcTemplate(47, "독사", 8, 90, 0, 10, 18, 8, 6, 4, 1, "poison", new int[]{6}, 75, 38));
        nm.addTemplate(new NpcTemplate(48, "석화 메두사", 24, 300, 100, 20, 16, 18, 18, 16, 3, "stone", new int[]{21, 22}, 285, 185));
        nm.addTemplate(new NpcTemplate(49, "망각의 정령", 29, 340, 180, 16, 14, 14, 26, 22, 4, "spirit", new int[]{24, 25}, 400, 260));
        nm.addTemplate(new NpcTemplate(50, "싸울아비의 분신", 50, 2000, 500, 60, 50, 55, 45, 40, 5, "all", new int[]{36, 37}, 5000, 3000));

        logger.info("NPC 템플릿 50개 생성 완료");
    }

    private void generateItems() {
        ItemManager im = ItemManager.getInstance();

        // Weapons
        im.loadItem(new L1Item(1, "낡은 단검", L1Item.ItemType.WEAPON, 3, 0, 5, 50, "낡고 녹슨 단검"));
        im.loadItem(new L1Item(2, "목검", L1Item.ItemType.WEAPON, 2, 0, 8, 30, "나무로 만든 연습용 검"));
        im.loadItem(new L1Item(3, "단검", L1Item.ItemType.WEAPON, 5, 0, 4, 150, "날카로운 단검"));
        im.loadItem(new L1Item(4, "짧은 검", L1Item.ItemType.WEAPON, 7, 0, 7, 300, "표준형 단검"));
        im.loadItem(new L1Item(5, "긴 검", L1Item.ItemType.WEAPON, 10, 0, 12, 600, "기본적인 한손검"));
        im.loadItem(new L1Item(6, "도끼", L1Item.ItemType.WEAPON, 12, 0, 15, 700, "무거운 도끼"));
        im.loadItem(new L1Item(7, "큰 도끼", L1Item.ItemType.WEAPON, 16, 0, 20, 1200, "강력한 양손 도끼"));
        im.loadItem(new L1Item(8, "단창", L1Item.ItemType.WEAPON, 9, 0, 10, 450, "짧은 창"));
        im.loadItem(new L1Item(9, "장창", L1Item.ItemType.WEAPON, 14, 0, 18, 900, "긴 창"));
        im.loadItem(new L1Item(10, "활", L1Item.ItemType.WEAPON, 8, 0, 9, 400, "나무 활"));
        im.loadItem(new L1Item(11, "장궁", L1Item.ItemType.WEAPON, 13, 0, 14, 850, "강력한 장궁"));
        im.loadItem(new L1Item(12, "마법 지팡이", L1Item.ItemType.WEAPON, 4, 0, 6, 500, "마력이 깃든 지팡이"));
        im.loadItem(new L1Item(13, "화염 지팡이", L1Item.ItemType.WEAPON, 8, 0, 8, 1500, "화염 속성 지팡이"));
        im.loadItem(new L1Item(14, "성스러운 검", L1Item.ItemType.WEAPON, 15, 2, 14, 3000, "성스러운 기운이 깃든 검"));
        im.loadItem(new L1Item(15, "어둠의 검", L1Item.ItemType.WEAPON, 18, 0, 14, 5000, "어둠의 기운이 깃든 검"));
        im.loadItem(new L1Item(16, "전설의 대검", L1Item.ItemType.WEAPON, 25, 0, 25, 20000, "전설에 등장하는 강대한 검"));
        im.loadItem(new L1Item(17, "싸울아비의 검", L1Item.ItemType.WEAPON, 35, 5, 20, 100000, "싸울아비만이 다룰 수 있는 전설의 무기"));
        im.loadItem(new L1Item(18, "곤봉", L1Item.ItemType.WEAPON, 6, 0, 10, 200, "나무 곤봉"));
        im.loadItem(new L1Item(19, "철퇴", L1Item.ItemType.WEAPON, 11, 0, 14, 800, "쇠로 만든 철퇴"));
        im.loadItem(new L1Item(20, "전투 해머", L1Item.ItemType.WEAPON, 14, 0, 18, 1100, "무거운 전투 해머"));

        // Armors
        im.loadItem(new L1Item(21, "천 옷", L1Item.ItemType.ARMOR, 0, 2, 5, 100, "얇은 천으로 만든 옷"));
        im.loadItem(new L1Item(22, "가죽 갑옷", L1Item.ItemType.ARMOR, 0, 5, 15, 400, "가죽으로 만든 갑옷"));
        im.loadItem(new L1Item(23, "사슬 갑옷", L1Item.ItemType.ARMOR, 0, 8, 25, 1200, "쇠사슬로 만든 갑옷"));
        im.loadItem(new L1Item(24, "판금 갑옷", L1Item.ItemType.ARMOR, 0, 12, 40, 3000, "두꺼운 쇠로 만든 갑옷"));
        im.loadItem(new L1Item(25, "성기사의 갑옷", L1Item.ItemType.ARMOR, 0, 15, 35, 8000, "성스러운 힘이 깃든 갑옷"));
        im.loadItem(new L1Item(26, "어둠의 갑옷", L1Item.ItemType.ARMOR, 2, 14, 35, 10000, "어둠의 힘이 깃든 갑옷"));
        im.loadItem(new L1Item(27, "마법사 로브", L1Item.ItemType.ARMOR, 0, 3, 4, 600, "마력을 증폭시키는 로브"));
        im.loadItem(new L1Item(28, "천 모자", L1Item.ItemType.ARMOR, 0, 1, 2, 80, "기본 천 모자"));
        im.loadItem(new L1Item(29, "가죽 투구", L1Item.ItemType.ARMOR, 0, 3, 8, 300, "가죽 투구"));
        im.loadItem(new L1Item(30, "철 투구", L1Item.ItemType.ARMOR, 0, 5, 15, 800, "철로 만든 투구"));
        im.loadItem(new L1Item(31, "방패", L1Item.ItemType.ARMOR, 0, 5, 20, 500, "나무 방패"));
        im.loadItem(new L1Item(32, "철 방패", L1Item.ItemType.ARMOR, 0, 8, 30, 1200, "철 방패"));
        im.loadItem(new L1Item(33, "탑 방패", L1Item.ItemType.ARMOR, 0, 12, 45, 3500, "거대한 탑 방패"));
        im.loadItem(new L1Item(34, "장갑", L1Item.ItemType.ARMOR, 0, 2, 5, 200, "가죽 장갑"));
        im.loadItem(new L1Item(35, "철 장갑", L1Item.ItemType.ARMOR, 0, 4, 10, 600, "철 장갑"));

        // Accessories
        im.loadItem(new L1Item(36, "반지", L1Item.ItemType.ACCESSORY, 0, 1, 1, 200, "간단한 반지"));
        im.loadItem(new L1Item(37, "마법사의 반지", L1Item.ItemType.ACCESSORY, 0, 2, 1, 1500, "마력이 깃든 반지"));
        im.loadItem(new L1Item(38, "전사의 반지", L1Item.ItemType.ACCESSORY, 1, 1, 1, 1200, "힘이 깃든 반지"));
        im.loadItem(new L1Item(39, "목걸이", L1Item.ItemType.ACCESSORY, 0, 2, 2, 400, "기본 목걸이"));
        im.loadItem(new L1Item(40, "생명의 목걸이", L1Item.ItemType.ACCESSORY, 0, 3, 2, 2000, "생명력을 높여주는 목걸이"));
        im.loadItem(new L1Item(41, "귀걸이", L1Item.ItemType.ACCESSORY, 0, 1, 1, 300, "기본 귀걸이"));
        im.loadItem(new L1Item(42, "현자의 귀걸이", L1Item.ItemType.ACCESSORY, 0, 2, 1, 2500, "지혜를 높여주는 귀걸이"));
        im.loadItem(new L1Item(43, "전설의 반지", L1Item.ItemType.ACCESSORY, 3, 3, 1, 50000, "전설적인 힘이 깃든 반지"));

        // Consumables
        im.loadItem(new L1Item(44, "소형 HP 포션", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 50, "소량의 HP를 회복한다 (+50 HP)"));
        im.loadItem(new L1Item(45, "중형 HP 포션", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 200, "중간 양의 HP를 회복한다 (+150 HP)"));
        im.loadItem(new L1Item(46, "대형 HP 포션", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 800, "많은 양의 HP를 회복한다 (+400 HP)"));
        im.loadItem(new L1Item(47, "소형 MP 포션", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 60, "소량의 MP를 회복한다 (+30 MP)"));
        im.loadItem(new L1Item(48, "중형 MP 포션", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 250, "중간 양의 MP를 회복한다 (+80 MP)"));
        im.loadItem(new L1Item(49, "대형 MP 포션", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 1000, "많은 양의 MP를 회복한다 (+200 MP)"));
        im.loadItem(new L1Item(50, "생명의 물", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 5000, "HP를 완전히 회복한다"));
        im.loadItem(new L1Item(51, "마력의 물", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 5000, "MP를 완전히 회복한다"));
        im.loadItem(new L1Item(52, "해독제", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 100, "독 상태를 해제한다"));
        im.loadItem(new L1Item(53, "회춘 물약", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 2000, "HP와 MP를 일정량 회복한다"));
        im.loadItem(new L1Item(54, "분노의 물약", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 1500, "잠시 동안 공격력이 증가한다"));
        im.loadItem(new L1Item(55, "보호의 물약", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 1500, "잠시 동안 방어력이 증가한다"));

        // Etc
        im.loadItem(new L1Item(56, "고블린 귀", L1Item.ItemType.ETC, 0, 0, 1, 20, "고블린의 귀. 퀘스트 아이템"));
        im.loadItem(new L1Item(57, "늑대 가죽", L1Item.ItemType.ETC, 0, 0, 3, 80, "늑대 가죽. 재료 아이템"));
        im.loadItem(new L1Item(58, "뼈 조각", L1Item.ItemType.ETC, 0, 0, 2, 50, "해골에서 나온 뼈 조각"));
        im.loadItem(new L1Item(59, "마석", L1Item.ItemType.ETC, 0, 0, 2, 500, "마력이 깃든 돌"));
        im.loadItem(new L1Item(60, "어둠의 결정", L1Item.ItemType.ETC, 0, 0, 2, 1000, "어둠의 에너지가 응축된 결정"));
        im.loadItem(new L1Item(61, "드래곤 비늘", L1Item.ItemType.ETC, 0, 0, 5, 5000, "드래곤에서 얻은 비늘. 최고급 재료"));
        im.loadItem(new L1Item(62, "전설의 파편", L1Item.ItemType.ETC, 0, 0, 1, 100000, "전설적인 무기를 만들 수 있는 파편"));
        im.loadItem(new L1Item(63, "오크 이빨", L1Item.ItemType.ETC, 0, 0, 1, 60, "오크의 이빨. 재료 아이템"));
        im.loadItem(new L1Item(64, "거미줄", L1Item.ItemType.ETC, 0, 0, 1, 30, "독거미의 거미줄. 재료 아이템"));
        im.loadItem(new L1Item(65, "트롤 혈액", L1Item.ItemType.ETC, 0, 0, 3, 200, "트롤에서 얻은 혈액. 연금 재료"));
        im.loadItem(new L1Item(66, "고블린 금화", L1Item.ItemType.ETC, 0, 0, 1, 100, "고블린이 모은 금화"));
        im.loadItem(new L1Item(67, "마법 두루마리", L1Item.ItemType.ETC, 0, 0, 1, 3000, "마법이 적힌 두루마리"));
        im.loadItem(new L1Item(68, "소환 주문서", L1Item.ItemType.ETC, 0, 0, 1, 5000, "몬스터를 소환하는 주문서"));
        im.loadItem(new L1Item(69, "귀환 주문서", L1Item.ItemType.ETC, 0, 0, 1, 500, "시작 마을로 귀환한다"));
        im.loadItem(new L1Item(70, "싸울아비의 증표", L1Item.ItemType.ETC, 0, 0, 1, 999999, "싸울아비임을 증명하는 증표"));

        // More weapons for different classes
        im.loadItem(new L1Item(71, "사제의 지팡이", L1Item.ItemType.WEAPON, 5, 0, 7, 600, "사제 전용 지팡이"));
        im.loadItem(new L1Item(72, "드루이드의 지팡이", L1Item.ItemType.WEAPON, 6, 0, 8, 700, "자연의 힘이 깃든 지팡이"));
        im.loadItem(new L1Item(73, "단도", L1Item.ItemType.WEAPON, 6, 0, 3, 250, "도적 전용 단도"));
        im.loadItem(new L1Item(74, "음악 류트", L1Item.ItemType.WEAPON, 3, 0, 5, 400, "음유시인 전용 악기"));
        im.loadItem(new L1Item(75, "수도사의 건틀릿", L1Item.ItemType.WEAPON, 8, 2, 6, 900, "수도사 전용 무기"));
        im.loadItem(new L1Item(76, "네크로의 지팡이", L1Item.ItemType.WEAPON, 7, 0, 7, 1200, "강령술사 전용 지팡이"));
        im.loadItem(new L1Item(77, "광전사의 도끼", L1Item.ItemType.WEAPON, 20, 0, 22, 4000, "광전사 전용 대도끼"));
        im.loadItem(new L1Item(78, "기사의 창", L1Item.ItemType.WEAPON, 16, 2, 20, 5000, "기사 전용 신성한 창"));
        im.loadItem(new L1Item(79, "대마법사의 지팡이", L1Item.ItemType.WEAPON, 12, 0, 9, 15000, "대마법사 전용 지팡이"));
        im.loadItem(new L1Item(80, "전쟁군주의 검", L1Item.ItemType.WEAPON, 22, 3, 22, 30000, "전쟁군주 전용 검"));

        // Additional armor sets
        im.loadItem(new L1Item(81, "마법사 로브 상의", L1Item.ItemType.ARMOR, 0, 4, 5, 800, "마법사 로브 세트 - 상의"));
        im.loadItem(new L1Item(82, "마법사 로브 하의", L1Item.ItemType.ARMOR, 0, 3, 4, 600, "마법사 로브 세트 - 하의"));
        im.loadItem(new L1Item(83, "마법사 로브 두건", L1Item.ItemType.ARMOR, 0, 2, 2, 400, "마법사 로브 세트 - 두건"));
        im.loadItem(new L1Item(84, "전사 중갑옷", L1Item.ItemType.ARMOR, 0, 14, 45, 4000, "전사용 중형 갑옷"));
        im.loadItem(new L1Item(85, "성기사 풀세트", L1Item.ItemType.ARMOR, 0, 20, 50, 25000, "성기사의 완전 갑옷 세트"));
        im.loadItem(new L1Item(86, "도적 경갑옷", L1Item.ItemType.ARMOR, 0, 6, 10, 1800, "도적 전용 경량 갑옷"));
        im.loadItem(new L1Item(87, "궁수 가죽갑옷", L1Item.ItemType.ARMOR, 0, 7, 12, 2200, "궁수 전용 가죽 갑옷"));
        im.loadItem(new L1Item(88, "수도사 도복", L1Item.ItemType.ARMOR, 0, 5, 8, 1600, "수도사 전용 도복"));
        im.loadItem(new L1Item(89, "사제 의복", L1Item.ItemType.ARMOR, 0, 6, 10, 2000, "사제 전용 의복"));
        im.loadItem(new L1Item(90, "싸울아비의 갑옷", L1Item.ItemType.ARMOR, 5, 25, 30, 500000, "싸울아비 전용 전설 갑옷"));

        // Rare accessories
        im.loadItem(new L1Item(91, "용의 비늘 목걸이", L1Item.ItemType.ACCESSORY, 2, 5, 2, 30000, "드래곤 비늘로 만든 목걸이"));
        im.loadItem(new L1Item(92, "영웅의 반지", L1Item.ItemType.ACCESSORY, 3, 3, 1, 80000, "영웅만이 낄 수 있는 반지"));
        im.loadItem(new L1Item(93, "마왕의 반지", L1Item.ItemType.ACCESSORY, 4, 2, 1, 120000, "마왕에게서 얻은 반지"));
        im.loadItem(new L1Item(94, "싸울아비의 증표 반지", L1Item.ItemType.ACCESSORY, 5, 5, 1, 999999, "싸울아비 고유 액세서리"));
        im.loadItem(new L1Item(95, "성스러운 십자가", L1Item.ItemType.ACCESSORY, 0, 8, 2, 15000, "성스러운 힘이 깃든 십자가"));

        // More consumables
        im.loadItem(new L1Item(96, "경험치 물약", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 10000, "경험치를 2배로 받는다 (30분)"));
        im.loadItem(new L1Item(97, "변신 주문서", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 3000, "잠시 다른 모습으로 변신한다"));
        im.loadItem(new L1Item(98, "투명 포션", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 2000, "잠시 투명 상태가 된다"));
        im.loadItem(new L1Item(99, "전설의 묘약", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 50000, "모든 능력치가 잠시 2배가 된다"));
        im.loadItem(new L1Item(100, "부활 구슬", L1Item.ItemType.CONSUMABLE, 0, 0, 1, 20000, "사망 시 부활한다"));

        logger.info("아이템 100개 생성 완료");
    }

    private void spawnNpcs() {
        NpcManager nm = NpcManager.getInstance();
        Random rng = new Random(42L);

        // Map 1 - Starting Village: low-level mobs (npc ids 1-5, 38)
        int[] map1Npcs = {1, 2, 3, 4, 5, 38};
        spawnGroup(nm, 1, map1Npcs, 30, rng);

        // Map 2 - Forest: mid-level mobs (6-15, 39, 40, 46, 47)
        int[] map2Npcs = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 39, 40, 46, 47};
        spawnGroup(nm, 2, map2Npcs, 60, rng);

        // Map 3 - Dungeon: mid-high mobs (16-25, 41, 42, 43, 48, 49)
        int[] map3Npcs = {16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 41, 42, 43, 48, 49};
        spawnGroup(nm, 3, map3Npcs, 50, rng);

        // Map 4 - Castle Town: high mobs (26-32, 44, 45)
        int[] map4Npcs = {26, 27, 28, 29, 30, 31, 32, 44, 45};
        spawnGroup(nm, 4, map4Npcs, 40, rng);

        // Map 5 - Battlefield: boss-level mobs (33-37, 50)
        int[] map5Npcs = {33, 34, 35, 36, 37, 50};
        spawnGroup(nm, 5, map5Npcs, 30, rng);

        logger.info("NPC 스폰 완료");
    }

    private void spawnGroup(NpcManager nm, int mapId, int[] npcIds, int count, Random rng) {
        GameMap map = L1World.getInstance().getMap(mapId);
        if (map == null) return;
        for (int i = 0; i < count; i++) {
            int templateId = npcIds[rng.nextInt(npcIds.length)];
            int x = 10 + rng.nextInt(180);
            int y = 10 + rng.nextInt(180);
            if (map.canMove(x, y)) {
                var npc = nm.spawnNpc(templateId, mapId, x, y);
                if (npc != null) {
                    map.addNpc(npc);
                }
            }
        }
    }
}
