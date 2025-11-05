package lineage.world.object.item.yadolan;

import lineage.bean.database.Item;
import lineage.bean.database.Skill;
import lineage.database.SkillDatabase;
import lineage.database.SpriteFrameDatabase;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_ObjectAction;
import lineage.network.packet.server.S_ObjectAttack;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.controller.ChattingController;
import lineage.world.object.Character;
import lineage.world.object.object;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.magic.Lightning;

public class PenguinHuntingStick extends ItemInstance {

    private Skill skill;

    static synchronized public ItemInstance clone(ItemInstance item) {
        if (item == null) {
            item = new PenguinHuntingStick();
        }
        item.setSkill(SkillDatabase.find(955));
        return item;
    }

    @Override
    public Skill getSkill() {
        return skill;
    }

    @Override
    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    @Override
    public ItemInstance clone(Item item) {
        return super.clone(item);
    }

    public void toClick(Character cha, ClientBasePacket cbp) {
        int obj_id = cbp.readD();
        int x = cbp.readH();
        int y = cbp.readH();
        
        // 방향 전환
        cha.setHeading(Util.calcheading(cha, x, y));

        // 객체 찾기
        object o = (obj_id == cha.getObjectId()) ? cha : cha.findInsideList(obj_id);
        if (o == null) {
            return;
        }

        if (!SpriteFrameDatabase.findGfxMode(cha.getGfx(), Lineage.GFX_MODE_WAND)) {
            cha.toSender(S_ObjectAction.clone(BasePacketPooling.getPool(S_ObjectAction.class), cha, Lineage.GFX_MODE_WAND), true);
        }

        try {
            if (cha.getMap() >= 2101 && cha.getMap() <= 2151) {
                if (obj_id == cha.getObjectId()) {
                    cha.toSender(S_ObjectAttack.clone(BasePacketPooling.getPool(S_ObjectAttack.class), cha, Lineage.GFX_MODE_WAND, getItem().getEffect(), x, y), cha instanceof PcInstance);
                } else {
                    Lightning.toBuuff(cha, skill, o, Lineage.GFX_MODE_WAND, skill.getRange(), skill.getCastGfx(), 300);
                    cha.getInventory().count(this, getCount() - 1, true);
                }
            } else {

                ChattingController.toChatting(cha, "해당 맵에서는 사용 불가합니다.", Lineage.CHATTING_MODE_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}