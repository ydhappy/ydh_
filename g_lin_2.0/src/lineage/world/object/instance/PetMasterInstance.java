package lineage.world.object.instance;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import lineage.bean.database.Npc;
import lineage.bean.lineage.Summon;
import lineage.database.DatabaseConnection;
import lineage.network.packet.BasePacketPooling;
import lineage.network.packet.ClientBasePacket;
import lineage.network.packet.server.S_Message;
import lineage.network.packet.server.S_WareHousePet;
import lineage.share.Lineage;
import lineage.world.controller.ChattingController;
import lineage.world.controller.SummonController;
import lineage.world.object.object;
import lineage.world.object.item.DogCollar;

public class PetMasterInstance extends object {
	
	private Npc npc;
	
	public PetMasterInstance(Npc npc){
		this.npc = npc;
	}
	
	@Override
	public void toDwarfAndShop(PcInstance pc, ClientBasePacket cbp){
		if(cbp.isRead(1) == false)
			return;
		
		switch(cbp.readC()){
			case 12:
				// 펫 찾기.
				toGetFinal(pc, cbp);
				break;
		}
	}
	
	/**
	 * 펫 맡기기 처리.
	 * @param pc
	 * @return
	 */
	public static boolean toPush(PcInstance pc) {
	    Summon s = SummonController.find(pc);
	    if (s != null) {
	        Connection con = null;
	        try {
	            con = DatabaseConnection.getLineage();

	            // allow_dead_pet_storage 설정 확인
	            boolean allowDeadPetStorage = Lineage.allow_dead_pet_storage; // 설정 값 가져오기
	            boolean deadPetExists = false; // 사망한 펫 존재 여부
	            boolean hasSavedPet = false;  // 정상적으로 저장된 펫이 있는지 여부

	            // 살아 있는 펫만 따로 리스트에 저장 (사망한 펫은 제거되지 않도록 처리)
	            List<Long> alivePetIds = new ArrayList<>();

	            // 올바른 리스트 메서드 사용
	            for (object obj : s.getList()) { // getList() 사용
	                if (obj instanceof PetInstance) { // PetInstance 타입인지 확인
	                    PetInstance pet = (PetInstance) obj;

	                    if (pet.isDead() && !allowDeadPetStorage) {
	                        deadPetExists = true; // 사망한 펫이 존재함을 기록
	                        continue; // 저장하지 않고 건너뜀
	                    }

	                    // 정상적으로 저장될 펫이 존재하는 경우 true
	                    SummonController.toSave(con, pet);
	                    hasSavedPet = true;
	                    alivePetIds.add(pet.getObjectId()); // 살아 있는 펫 ID만 저장
	                }
	            }

	            // 사망한 펫이 존재할 경우, 채팅 메시지를 한 번만 출력
	            if (deadPetExists) {
	                ChattingController.toChatting(pc, "\\fY사망한 펫은 보관할 수 없습니다.", Lineage.CHATTING_MODE_MESSAGE);
	            }

	            // 살아 있는 펫만 제거 (사망한 펫은 필드에 그대로 남도록 처리)
	            for (long petId : alivePetIds) {
	                s.removePet(petId); 
	            }

	            // 정상적으로 저장된 펫이 하나라도 있으면 true 반환
	            return hasSavedPet;

	        } catch (Exception e) {
	            lineage.share.System.println(PetMasterInstance.class.toString() + " : toPush(PcInstance pc)");
	            lineage.share.System.println(e);
	        } finally {
	            DatabaseConnection.close(con);
	        }
	    }
	    return false;
	}




	
	/**
	 * 펫 찾기 처리.
	 * @param pc
	 * @return
	 */
	protected boolean toGet(PcInstance pc){
		// 펫목걸이 찾기.
		List<ItemInstance> temp_list = new ArrayList<ItemInstance>();
		pc.getInventory().findDbNameId(1173, temp_list);
		if(temp_list.size() > 0){
			// 펫 목걸이 중 스폰안된것만 필터링.
			List<DogCollar> temp2_list = new ArrayList<DogCollar>();
			for(ItemInstance ii : temp_list){
				if(ii instanceof DogCollar){
					DogCollar dc = (DogCollar)ii;
					if(!dc.isPetSpawn() && !SummonController.isDeletePet(dc.getPetObjectId()))
						temp2_list.add(dc);
				}
			}
			// 1개 이상일경우 패킷처리.
			if(temp2_list.size() > 0){
				pc.toSender( S_WareHousePet.clone(BasePacketPooling.getPool(S_WareHousePet.class), this, temp2_list) );
				return true;
			}
		}
		return false;
	}
	
	/**
	 * toGet 거쳐서 펫을 선택하고 찾기 누르면 이곳으로 옴.
	 * @param pc
	 * @param cbp
	 */
	private void toGetFinal(PcInstance pc, ClientBasePacket cbp) {
		int count = cbp.readH();
		boolean isGM = pc.getGm() > 0;

		while (count-- > 0) {
			int inv_id = cbp.readD();
			cbp.readD(); // 필요 없는 데이터 무시

			DogCollar collar = (DogCollar) pc.getInventory().value(inv_id);
			if (collar == null) {
				continue;
			}

			boolean aden = isGM || pc.getInventory().isAden(Lineage.warehouse_pet_price, false);
			boolean pet = SummonController.toPet(pc, collar);

			if (aden && pet) {
				if (!isGM) {
					pc.getInventory().isAden(Lineage.warehouse_pet_price, true); // 아덴 차감
				}
			} else {
				if (!aden) {
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 189)); // 아덴 부족
				} else if (!pet) {
					pc.toSender(S_Message.clone(BasePacketPooling.getPool(S_Message.class), 489)); // 펫 생성 실패
				}
				break;
			}
		}
	}
}
