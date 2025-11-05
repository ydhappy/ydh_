package lineage.network.packet.server;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


import lineage.bean.database.Item;
import lineage.bean.database.ItemSetoption;
import lineage.database.CharacterMarbleDatabase;
import lineage.database.ItemDatabase;
import lineage.database.ItemSetoptionDatabase;
import lineage.network.packet.ServerBasePacket;
import lineage.share.Lineage;
import lineage.util.Util;
import lineage.world.object.instance.ItemArmorInstance;
import lineage.world.object.instance.ItemInstance;
import lineage.world.object.instance.ItemWeaponInstance;
import lineage.world.object.instance.PcInstance;
import lineage.world.object.item.Candle;
import lineage.world.object.item.DogCollar;
import lineage.world.object.item.Letter;
import lineage.world.object.item.armor.ArmorOfchangcheon;
import lineage.world.object.item.armor.Turban;
import lineage.world.object.item.helm.Poly_Turban;
import lineage.world.object.item.weapon.Arrow;
import lineage.world.object.item.weapon.WeaponOfchangcheon;

public class S_Inventory extends ServerBasePacket {

	protected void toArmor(ItemInstance item) {
		if (item.getItem().getNameIdNumber() == 28609 || item.getItem().getNameIdNumber() == 431)
			toEtc(item.getItem(), item.getWeight());
		else
			toArmor(item.getItem(), (ItemArmorInstance) item, item.getDurability(), item.getEnLevel(), item.getWeight(), item.getDynamicMr(), item.getBless(), item.getDynamicStunDefence());

	}

	protected void toWeapon(ItemInstance item) {
		if (item.getItem().getType2().equalsIgnoreCase("fishing_rod"))
			toEtc(item.getItem(), item.getWeight());
		else
			toWeapon(item.getItem(), item, item.getDurability(), item.getEnLevel(), item.getWeight(), item.getBless());
	}

	protected void toEtc(ItemInstance item) {
		if (item.getItem().getNameIdNumber() == 1173) {
			DogCollar dc = (DogCollar) item;
			writeC(0x0f); // 15
			writeC(0x19);
			writeH(dc.getPetClassId());
			writeC(0x1a);
			writeH(dc.getPetLevel());
			writeC(0x1f);
			writeH(dc.getPetHp());
			writeC(0x17);
			writeC(item.getItem().getMaterial());
			writeD(item.getWeight());
		} else {
			if (!item.getItem().getType2().equalsIgnoreCase("sword_lack") && !item.getItem().getType2().equalsIgnoreCase("자동 칼질") && !item.getItem().getName().contains("기운을 잃은"))
				toEtc(item.getItem(), item.getWeight());
		}
	}

	// 19 AC +
	// 3 손상도
	// 7 [사용가능] 클래스
	// 6 추가데미지
	// 5 공격 성공
	// 8 STR [힘]
	// 9 DEX [텍스]
	// 10 CON [콘]
	// 11 WIS [위즈]
	// 12 INT [인트]
	// 13 CHA [카리]
	// 14 최대 HP
	// 24 최대 MP
	// 20 데미지 감소 +
	// 15 마법방어
	// 17 주술력
	// 18 헤이스트효과
	// 27 MP 회복량

	// 27 / 28 / 29 / 30 속성

	protected void toArmor(Item item, ItemArmorInstance armor, int durability, int enlevel, int weight, int dynamic_mr, int bless, double dynamic_stun_defence) {
		ItemSetoption setoption = Lineage.server_version >= 200 ? ItemSetoptionDatabase.find(item.getSetId()) : null;

		ItemInstance temp = null;
		temp = ItemDatabase.newInstance(item);
		if (temp != null) {
			temp.setEnLevel(enlevel);
			temp.setBless(bless);
			temp.checkOption();
		}

		writeC(getOptionSize(item, armor, durability, enlevel, setoption, bless, dynamic_mr, dynamic_stun_defence));
		writeC(19); // 19 AC +
		writeC(item.getAc());
		writeC(item.getMaterial());
		if (Lineage.server_version > 300)
			writeC(-1); // Grade
		writeD(weight);

		// AC 0+enlevel
		if (enlevel != 0) {
			int ac = enlevel;

			writeC(0x02);
			writeC(ac);
		}

		if (durability != 0) {
			writeC(3); // 3 손상도
			writeC(durability);
		}

		int type = item.getRoyal() != 1 ? 0 : 1;
		type += item.getKnight() != 1 ? 0 : 2;
		type += item.getElf() != 1 ? 0 : 4;
		type += item.getWizard() != 1 ? 0 : 8;
		type += item.getDarkElf() != 1 ? 0 : 16;
		type += item.getDragonKnight() != 1 ? 0 : 32;
		type += item.getBlackWizard() != 1 ? 0 : 64;
		writeC(7); // 7 [사용가능] 클래스
		writeC(type);

		// 추가 데미지
		if (item.getAddDmg() != 0 || temp.getTollTipDmg() != 0) {
			int addDmg = item.getAddDmg() + temp.getTollTipDmg();
			if (addDmg > 0) {
				writeC(6); // 6 추가데미지
				writeC(addDmg);
			}
		}

		// 공격 성공
		if (item.getAddHit() != 0 || temp.getTollTipHit() != 0) {
			int addHit = item.getAddHit() + temp.getTollTipHit();

			if (addHit > 0) {
				writeC(5); // 5 공격 성공
				writeC(addHit);
			}
		}

		// 활 명중치
		if (item.getAddHitBow() != 0 || temp.getTollTipHitBow() != 0) {
			int addHitBow = item.getAddHitBow() + temp.getTollTipHitBow();
			if (addHitBow > 0) {
				writeC(20);
				writeC(addHitBow);
			}
		}
		
		// STR
		if (item.getAddStr() != 0) {
			writeC(8); // 8 STR [힘]
			writeC(item.getAddStr());
		}

		// DEX
		if (item.getAddDex() != 0) {
			writeC(9); // 9 DEX [텍스]
			writeC(item.getAddDex());
		}

		// CON
		if (item.getAddCon() != 0) {
			writeC(10); // 10 CON [콘]
			writeC(item.getAddCon());
		}

		// INT
		if (item.getAddInt() != 0) {
			writeC(12); // 12 INT [인트]
			writeC(item.getAddInt());
		}

		// WIS
		if (item.getAddWis() != 0) {
			writeC(11); // 11 WIS [위즈]
			writeC(item.getAddWis());
		}

		// CHA
		if (item.getAddCha() != 0) {
			writeC(13); // 13 CHA [카리]
			writeC(item.getAddCha());
		}

		// 속성
		if (item.getFireress() != 0) {
			writeC(27); // 불
			writeC(item.getFireress());
		}

		if (item.getWaterress() != 0) {
			writeC(28); // 물
			writeC(item.getWaterress());
		}

		if (item.getWindress() != 0) {
			writeC(29); // 바람
			writeC(item.getWindress());
		}

		if (item.getEarthress() != 0) {
			writeC(30); // 땅
			writeC(item.getEarthress());
		}

		// HP
		if (item.getAddHp() != 0 || temp.getTollTipHp() != 0) {
			int addHp = item.getAddHp() + temp.getTollTipHp();
			if (addHp > 0) {
				writeC(14); // 14 최대 HP
				writeH(addHp);
			}
		}

		// 최대 MP
		if (item.getAddMp() != 0 || temp.getTollTipMp() != 0) {
			writeC(24); // 24 최대 MP
			writeC(item.getAddMp() + temp.getTollTipMp());
		}

		// MR
		if (item.getAddMr() != 0 || dynamic_mr != 0 || temp.getTollTipMr() != 0) {
			writeC(15); // 15 마법방어
			writeH(item.getAddMr() + dynamic_mr + temp.getTollTipMr());
		}

		// SP
		if (item.getAddSp() != 0 || temp.getTollTipSp() != 0) {
			int addSp = item.getAddSp() + temp.getTollTipSp();
			if (addSp > 0) {
				writeC(17); // 17 주술력
				writeC(addSp);
			}
		}

		// 헤이스트 효과
		if (setoption != null && (setoption.isBrave() || setoption.isHaste()))
			writeC(18); // 18 헤이스트효과

		// HP회복
/*		if (item.getTicHp() != 0 || temp.getTollTipHp() != 0) {
			int addmp = item.getTicHp() + temp.getTollTipHp();
			writeC(32); // 20 HP 회복량
			writeC(addmp);
		}*/
		/*
		 * // MP회복 if (item.getTicMp() != 0 || temp.getTollTipTicMp() != 0) {
		 * int addmp = item.getTicMp() + temp.getTollTipTicMp(); writeC(32); //
		 * 32 MP 회복량 writeC(addmp); }
		 */

		// 초기화
		if (temp != null)
			ItemDatabase.setPool(temp);

	}

	protected void toWeapon(Item item, ItemInstance weapon, int durability, int enlevel, int weight, int bless) {
		ItemSetoption setoption = Lineage.server_version >= 200 ? ItemSetoptionDatabase.find(item.getSetId()) : null;

		ItemInstance temp = null;
		temp = ItemDatabase.newInstance(item);
		if (temp != null) {
			temp.setEnLevel(enlevel);
			temp.setBless(bless);
			temp.checkOption();
		}

		writeC(getOptionSize(item, weapon instanceof Arrow ? weapon : (ItemWeaponInstance) weapon, durability, enlevel, setoption, bless, 0, 0));
		writeC(0x01);
		writeC(item.getSmallDmg());
		writeC(item.getBigDmg());
		writeC(item.getMaterial());
		writeD(weight);
		if (enlevel != 0) {
			writeC(0x02);
			writeC(enlevel);
		}
		if (durability != 0) {
			writeC(3);
			writeC(durability);
		}

		if (item.isTohand())
			writeC(4);

		if (item.getAddHit() != 0 || temp.getTollTipHit() != 0) {
			writeC(5);
			writeC(item.getAddHit() + temp.getTollTipHit());
		}

		if (item.getAddDmg() != 0 || temp.getTollTipDmg() != 0) {
			;
			writeC(6);
			writeC(item.getAddDmg() + temp.getTollTipDmg());
		}

		int type = item.getRoyal() != 1 ? 0 : 1;
		type += item.getKnight() != 1 ? 0 : 2;
		type += item.getElf() != 1 ? 0 : 4;
		type += item.getWizard() != 1 ? 0 : 8;
		type += item.getDarkElf() != 1 ? 0 : 16;
		type += item.getDragonKnight() != 1 ? 0 : 32;
		type += item.getBlackWizard() != 1 ? 0 : 64;
		writeC(7);
		writeC(type);

		if (item.getAddStr() != 0) {
			writeC(8);
			writeC(item.getAddStr());
		}

		if (item.getAddDex() != 0) {
			writeC(9);
			writeC(item.getAddDex());
		}

		if (item.getAddCon() != 0) {
			writeC(10);
			writeC(item.getAddCon());
		}

		if (item.getAddWis() != 0) {
			writeC(11);
			writeC(item.getAddWis());
		}

		if (item.getAddInt() != 0) {
			writeC(12);
			writeC(item.getAddInt());
		}

		if (item.getAddCha() != 0) {
			writeC(13);
			writeC(item.getAddCha());
		}

		if (item.getAddHp() != 0 || temp.getTollTipHp() != 0) {
			writeC(14);
			writeH(item.getAddHp() + temp.getTollTipHp());
		}

		if (item.getAddMr() != 0 || temp.getTollTipMr() != 0) {
			writeC(15);
			writeH(item.getAddMr() + temp.getTollTipMr());
		}

		if (item.getStealMp() != 0) {
			writeC(16);
		}

		if (item.getAddSp() != 0 || temp.getTollTipSp() != 0) {
			writeC(17);
			writeC(item.getAddSp() + temp.getTollTipSp());
		}

		if (setoption != null && (setoption.isBrave() || setoption.isHaste()))
			writeC(18);

		if (item.getAddMp() != 0 || temp.getTollTipMp() != 0) {
			writeC(24);
			writeC(item.getAddMp() + temp.getTollTipMp());
		}

		// MP회복
		// if (temp.getTollTipTicMp() != 0 || item.getTicMp() != 0) {
		// writeC(20);
		// writeC(temp.getTollTipTicMp() + item.getTicMp());
		// }

		if (temp != null)
			ItemDatabase.setPool(temp);

	}

	protected void toEtc(Item item, int weight) {
		writeC(0x06);
		writeC(0x17);
		writeC(item.getMaterial());
		writeD(weight);
	}

	protected String getName(ItemInstance item) {
	    PcInstance pc = null;
	    String name = CharacterMarbleDatabase.getItemName(item);
	    if (name != null) {
	        return name;
	    }

	    StringBuffer sb = new StringBuffer();

	    if (item.getItem().getNameIdNumber() == 1075 && item.getItem().getInvGfx() != 464) {
	        Letter letter = (Letter) item;
	        sb.append(letter.getFrom());
	        sb.append(" : ");
	        sb.append(letter.getSubject());
	    } else {
	        // 봉인 표현
	        if (item.isDefinite() && item.getBless() < 0) {
	            sb.append("[봉인] ");
	        }

	        if (item.isDefinite() && (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance)
	                && !item.getItem().getType2().equalsIgnoreCase("fishing_rod")
	                && item.getItem().getNameIdNumber() != 28609
	                && item.getItem().getNameIdNumber() != 431) {

	            String element_name = null;
	            int element_en = 0;

	            if (item.getEnWind() > 0) {
	                element_name = "풍령";
	                element_en = item.getEnWind();
	            } else if (item.getEnEarth() > 0) {
	                element_name = "지령";
	                element_en = item.getEnEarth();
	            } else if (item.getEnWater() > 0) {
	                element_name = "수령";
	                element_en = item.getEnWater();
	            } else if (item.getEnFire() > 0) {
	                element_name = "화령";
	                element_en = item.getEnFire();
	            }

	            if (element_name != null) {
	                sb.append(element_name).append(":").append(element_en).append("단 ");
	            }

	            if (item.getEnLevel() >= 0) {
	                sb.append("+");
	            }
	            sb.append(item.getEnLevel()).append(" ");
	        }

	        // 이름 표현
	        if (item.isDefinite()) {
	            sb.append(item.getName());
	        } else {
	            sb.append(item.getItem().getItemId());
	        }

	        if (item.isDefinite() && item.getQuantity() > 0) {
	            sb.append(" (").append(item.getQuantity()).append(")");
	        }

	        if (item.getCount() > 1) {
	            sb.append(" (").append(Util.changePrice(item.getCount()));
	            if (item.getItem().getNameIdNumber() == 28258) {
	                sb.append("냥)");
	            } else {
	                sb.append(")");
	            }
	        }

	        if (item.getItem().getNameIdNumber() == 1173) {
	            DogCollar dc = (DogCollar) item;
	            sb.append(" [Lv.").append(dc.getPetLevel()).append(" ").append(dc.getPetName()).append("]");
	        }

	        if (item.getInnRoomKey() > 0) {
	            sb.append(" #").append(item.getInnRoomKey());
	        }

	        if (item instanceof ArmorOfchangcheon || item instanceof WeaponOfchangcheon) {
	            sb.append(" [").append(item.getNowTime()).append("]");
	        }

	        if (item.getLimitTime() > 0) {
	            sb.append(" ").append(Util.getLocaleItemNameString(item.getLimitTime()));
	        }

	        // itemTimek 처리 (yyyyMMddHHmmss 또는 epoch millis 대응)
	        if (item.getItemTimek() != null && item.getItemTimek().length() > 0) {
	            try {
	                ZonedDateTime itemZonedDateTime;
	                String itemTimek = item.getItemTimek().trim();

	                if (itemTimek.length() == 14 && itemTimek.matches("\\d+")) {
	                    // yyyyMMddHHmmss 형식
	                    LocalDateTime itemDateTime = LocalDateTime.parse(itemTimek, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	                    itemZonedDateTime = itemDateTime.atZone(ZoneId.of("Asia/Seoul"));
	                } else if (itemTimek.matches("\\d{13}")) {
	                    // epoch milliseconds 형식
	                    long millis = Long.parseLong(itemTimek);
	                    itemZonedDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Seoul"));
	                } else {
	                    itemZonedDateTime = null; // 처리 불가한 형식은 무시
	                }

	                if (itemZonedDateTime != null) {
	                    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
	                    Duration duration = Duration.between(now, itemZonedDateTime);
	                    long remainingTime = duration.getSeconds();

	                    if (remainingTime > 0) {
	                        int nameIdNumber = item.getItem().getNameIdNumber();
	                        if (nameIdNumber < 29011 || nameIdNumber > 29018) {
	                            long hour = remainingTime / 3600;
	                            remainingTime %= 3600;
	                            long min = remainingTime / 60;
	                            long sec = remainingTime % 60;

	                            sb.append(" (")
	                              .append(hour > 0 ? hour + "시간 " : "")
	                              .append(min > 0 ? min + "분 " : "")
	                              .append(sec).append("초) ");
	                        }
	                    }
	                }
	            } catch (Exception e) {
	                lineage.share.System.println("itemTimek 파싱 오류: " + item.getItemTimek());
	            }
	        }

	        // 착용중인 아이템 표현
	        if (item.isEquipped()) {
	            if (item instanceof ItemWeaponInstance) {
	                sb.append(" ($9)");
	            } else if (item instanceof ItemArmorInstance) {
	                sb.append(" ($117)");
	                if (item instanceof Turban || item instanceof Poly_Turban) {
	                    sb.insert(sb.length() - 7, " (" + item.getNowTime() + ")");
	                }
	            } else if (item instanceof Candle) {
	                sb.append(" ($10)");
	            }
	        }
	    }

	    // 삭제 예정 시간 표시 (getItemTimek 기준 - epoch millis 전용)
	    if (item.isTimeCheck()) {
	        try {
	            String itemTimek = item.getItemTimek();
	            ZonedDateTime expireTime = null;

	            if (itemTimek != null && itemTimek.matches("\\d{13}")) {
	                long millis = Long.parseLong(itemTimek);
	                expireTime = Instant.ofEpochMilli(millis).atZone(ZoneId.of("Asia/Seoul"));
	            }

	            if (expireTime != null) {
	                int month = expireTime.getMonthValue();
	                int day = expireTime.getDayOfMonth();
	                int hour = expireTime.getHour();
	                int minute = expireTime.getMinute();

	                String msg = String.format("[%d-%d %02d:%02d]", month, day, hour, minute);
	                sb.append(msg);
	            }
	        } catch (Exception e) {
	            lineage.share.System.println("삭제시간 파싱 오류: " + item.getItemTimek());
	        }
	    }

	    return sb.toString().trim();
	}

	
/*	protected String getName(ItemInstance item) {
		PcInstance pc = null;
		String name = CharacterMarbleDatabase.getItemName(item);
		if (name != null) {
			return name;
		}

		StringBuffer sb = new StringBuffer();
		if (item.getItem().getNameIdNumber() == 1075 && item.getItem().getInvGfx() != 464) {
			Letter letter = (Letter) item;
			sb.append(letter.getFrom());
			sb.append(" : ");
			sb.append(letter.getSubject());
		} else {
			// 봉인 표현
			if (item.isDefinite() && item.getBless() < 0) {
				sb.append("[봉인]");
				sb.append(" ");
			}

			if (item.isDefinite() && (item instanceof ItemWeaponInstance || item instanceof ItemArmorInstance) && !item.getItem().getType2().equalsIgnoreCase("fishing_rod") && item.getItem().getNameIdNumber() != 28609
					&& item.getItem().getNameIdNumber() != 431) {
				// 속성 인첸 표현.
				String element_name = null;
				Integer element_en = 0;

				if (item.getEnWind() > 0) {
					element_name = "풍령";
					element_en = item.getEnWind();
				}
				if (item.getEnEarth() > 0) {
					element_name = "지령";
					element_en = item.getEnEarth();
				}
				if (item.getEnWater() > 0) {
					element_name = "수령";
					element_en = item.getEnWater();
				}
				if (item.getEnFire() > 0) {
					element_name = "화령";
					element_en = item.getEnFire();
				}
				if (element_name != null) {
					sb.append(element_name).append(":").append(element_en).append("단");
					sb.append(" ");
				}

				// 인첸 표현.
				if (item.getEnLevel() >= 0) {
					sb.append("+");
				}
				sb.append(item.getEnLevel());
				sb.append(" ");

			}
			
			// 이름 표현
			if (item.isDefinite()) {
				sb.append(item.getName());
			} else {
				sb.append(item.getItem().getItemId());
			}

			if (item.isDefinite() && item.getQuantity() > 0) {
				sb.append(" (");
				sb.append(item.getQuantity());
				sb.append(")");
			}

			if (item.getCount() > 1) {
				sb.append(" (");
				sb.append(Util.changePrice(item.getCount()));
				if (item.getItem().getNameIdNumber() == 28258) {
					sb.append("냥)");
				} else {
					sb.append(")");
				}
			}

			if (item.getItem().getNameIdNumber() == 1173) {
				DogCollar dc = (DogCollar) item;
				sb.append(" [Lv.");
				sb.append(dc.getPetLevel());
				sb.append(" ");
				sb.append(dc.getPetName());
				sb.append("]");
			}

			if (item.getInnRoomKey() > 0) {
				sb.append(" #");
				sb.append(item.getInnRoomKey());
			}

			if (item instanceof ArmorOfchangcheon || item instanceof WeaponOfchangcheon || item instanceof Turban || item instanceof Poly_Turban) {
				sb.append(" [");
				sb.append(item.getNowTime());
				sb.append("]");
			}

			if (item.getLimitTime() > 0) {
				sb.append(" ").append(Util.getLocaleItemNameString(item.getLimitTime()));
			}

			if (item.getItemTimek() != null && item.getItemTimek().length() > 0) {
				long currentTimeSeconds = System.currentTimeMillis() / 1000;

				// 아이템 시간 (KST - Korea Standard Time, UTC+9)
				LocalDateTime itemDateTime = LocalDateTime.parse(item.getItemTimek(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				ZonedDateTime itemZonedDateTime = ZonedDateTime.of(itemDateTime, ZoneId.of("Asia/Seoul"));

				// 현재 시간 (KST - Korea Standard Time, UTC+9)
				ZonedDateTime currentZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(currentTimeSeconds), ZoneId.of("Asia/Seoul"));

				// 남은 시간 계산
				Duration duration = Duration.between(currentZonedDateTime, itemZonedDateTime);
				long remainingTime = duration.getSeconds();

				if (remainingTime > 0) {
					// 특정 아이템 ID에 대해 sb.append를 실행하지 않도록 조건 추가
					int nameIdNumber = item.getItem().getNameIdNumber();

					if (nameIdNumber != 29011 && nameIdNumber != 29012 && nameIdNumber != 29013 && nameIdNumber != 29014 && nameIdNumber != 29015 && nameIdNumber != 29016 && nameIdNumber != 29017
							&& nameIdNumber != 29018) {
						// 시간, 분, 초 계산
						long hour = remainingTime / 3600;
						remainingTime %= 3600;
						long min = remainingTime / 60;
						remainingTime %= 60;
						long sec = remainingTime;

						// 알림 추가
						sb.append(" [" + (hour > 0 ? hour + "시간 " : "") + (min > 0 ? min + "분 " : "") + sec + "초] ");
					}
				}
			}

			// 착용중인 아이템 표현
			if (item.isEquipped()) {
				if (item instanceof ItemWeaponInstance) {
					sb.append(" ($9)");
				} else if (item instanceof ItemArmorInstance) {
					sb.append(" ($117)");
				} else if (item instanceof Candle) {
					// 양초, 등잔
					sb.append(" ($10)");
				}
			}
		}

		if (item.isTimeCheck()) {
			try {
				String[] lastDate = item.getTimestamp().toString().split(" ")[0].split("-");
				int lastMonth = Integer.valueOf(lastDate[1]);
				int lastDay = Integer.valueOf(lastDate[2]);
				String[] lastDate2 = item.getTimestamp().toString().split(" ")[1].split(":");
				int lastHour = Integer.valueOf(lastDate2[0]);
				int lastMin = Integer.valueOf(lastDate2[1]);
				String msg = "[" + (lastMonth) + "-" + (lastDay) + " " + (lastHour) + ":" + (lastMin) + "]";
				sb.append(msg);
			} catch (Exception e) {
				lineage.share.System.println("삭제시간 표시에러");
			}
		}
		return sb.toString().trim();
	}*/

	protected int getOptionSize(ItemInstance item) {
		return getOptionSize(item.getItem(), item, item.getDurability(), item.getEnLevel(), null, item.getBless(), item.getDynamicMr(), item.getDynamicStunDefence());
	}

	protected int getOptionSize(Item item, ItemInstance ii, int durability, int enlevel, ItemSetoption setoption, int bless, int dynamic_mr, double dynamic_stun_defence) {
		int size = 0;

		// System.out.println(item.getName() + " (기존 방식) : " + size);
		size = checkSize(item, ii, enlevel, bless);
		// System.out.println(item.getName() + " (개선 방식) : " + size);
		return size;
	}

	private int checkSize(Item i, ItemInstance ii, int en, int bress) {
		int size = 0;

		if (ii == null) {
			Item item = ItemDatabase.find(i.getName());
			if (item != null) {
				ii = ItemDatabase.newInstance(item);
				if (ii != null) {
					ii.setEnLevel(en);
					ii.setBless(bress);
					ii.checkOption();
					size = ii.getStatusBytes().length;
					ItemDatabase.setPool(ii);
				}
			}
		} else {
			ii.checkOption();
			size = ii.getStatusBytes().length;
		}

		// System.out.println(i.getName() + " (개선 방식) : " + size);

		return size;
	}

	protected int getOptionSize(Item item, int durability, int enlevel, ItemSetoption setoption, int bless, int dynamic_mr, double dynamic_stun_defence, int dynamic_sp, int dynamic_reduction) {
		int size = 0;

		if (item.getType1().equalsIgnoreCase("armor")) {
			if (Lineage.server_version > 200)
				size += 10;
			else
				size += 9;

			if (enlevel != 0)
				size += 2;
			if (item.getAddStr() != 0)
				size += 2;
			if (item.getAddDex() != 0)
				size += 2;
			if (item.getAddCon() != 0)
				size += 2;
			if (item.getAddInt() != 0)
				size += 2;
			if (item.getAddCha() != 0)
				size += 2;
			if (item.getAddWis() != 0)
				size += 2;

			if (item.getFireress() != 0)
				size += 2;
			if (item.getWaterress() != 0)
				size += 2;
			if (item.getWindress() != 0)
				size += 2;
			if (item.getEarthress() != 0)
				size += 2;

			if (item.getName().equalsIgnoreCase("완력의 부츠") || item.getName().equalsIgnoreCase("민첩의 부츠") || item.getName().equalsIgnoreCase("지식의 부츠")) {
				if ((bless == 0 || bless == -128) || enlevel > 6)
					size += 3;
				if (enlevel == 9)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("necklace")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0 || enlevel > 0)
					size += 3;
				if (item.getAddMp() != 0)
					size += 2;
				if (item.getAddMr() != 0 || dynamic_mr != 0)
					size += 3;
				if (item.getAddSp() != 0 || dynamic_sp != 0)
					size += 2;
				if (item.getStunDefense() != 0 || dynamic_stun_defence != 0 || enlevel > 6)
					size += 2;
				if (item.getAddReduction() > 0 || dynamic_reduction != 0)
					size += 2;

				// 물약 회복량
				if (enlevel > 4)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("ring")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128) || enlevel > 4)
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0 || enlevel > 0)
					size += 3;
				if (item.getAddMp() != 0)
					size += 2;
				if ((item.getAddMr() != 0 || dynamic_mr != 0) || enlevel > 5)
					size += 3;
				if (item.getAddSp() != 0 || dynamic_sp != 0 || enlevel > 6)
					size += 2;
				if (item.getStunDefense() != 0 || dynamic_stun_defence != 0)
					size += 2;
				if (item.getAddReduction() > 0 || dynamic_reduction != 0)
					size += 2;

				// PvP 데미지
				if (enlevel > 6)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("belt")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0 || enlevel > 5)
					size += 3;
				if (item.getAddMp() != 0 || enlevel > 0)
					size += 2;
				if (item.getAddMr() != 0 || dynamic_mr != 0)
					size += 3;
				if (item.getAddSp() != 0 || dynamic_sp != 0)
					size += 2;
				if (item.getStunDefense() != 0 || dynamic_stun_defence != 0)
					size += 2;
				if (item.getAddReduction() > 0 || dynamic_reduction != 0 || enlevel > 4)
					size += 2;

				// PvP 리덕션
				if (enlevel > 6)
					size += 2;

				return size;
			}

			if (item.getType2().equalsIgnoreCase("earring")) {
				if (item.getAddDmg() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHit() != 0 || (bless == 0 || bless == -128))
					size += 2;
				if (item.getAddHp() != 0)
					size += 3;
				if (item.getAddMp() != 0)
					size += 2;
				if (item.getAddMr() != 0)
					size += 3;
				if (item.getAddSp() != 0)
					size += 2;
				if (item.getStunDefense() != 0)
					size += 2;
				if (item.getAddReduction() > 0)
					size += 2;

				return size;
			}
		} else if (item.getType1().equalsIgnoreCase("weapon")) {
			size += 10;
			if (enlevel != 0)
				size += 2;
			if (item.getAddStr() != 0)
				size += 2;
			if (item.getAddDex() != 0)
				size += 2;
			if (item.getAddCon() != 0)
				size += 2;
			if (item.getAddInt() != 0)
				size += 2;
			if (item.getAddCha() != 0)
				size += 2;
			if (item.getAddWis() != 0)
				size += 2;
			if (durability != 0)
				size += 2;
			if (item.isTohand())
				size += 1;
			if (item.getStealMp() != 0)
				size += 1;
		} else {
			return 0;
		}

		if (item.getAddReduction() != 0 || dynamic_reduction != 0)
			size += 2;
		if (item.getStunDefense() != 0 || dynamic_stun_defence != 0)
			size += 2;
		if (item.getAddMp() != 0)
			size += 2;
		if (item.getAddMr() != 0 || dynamic_mr != 0)
			size += 3;
		if (item.getAddSp() != 0 || ((bless == 0 || bless == -128) && item.getType2().equalsIgnoreCase("wand")) || dynamic_sp != 0)
			size += 2;
		if (item.getAddHp() != 0 || (bless == 0 || bless == -128) && item.getType1().equalsIgnoreCase("armor"))
			size += 3;
		if (item.getAddDmg() != 0 || ((bless == 0 || bless == -128) && item.getType1().equalsIgnoreCase("weapon") && !item.getType2().equalsIgnoreCase("wand")))
			size += 2;
		if (item.getAddHit() != 0 || ((item.getName().equalsIgnoreCase("수호성의 파워 글로브") || item.getName().equalsIgnoreCase("수호성의 활 골무")) && enlevel > 4))
			size += 2;
		if (item.getAddSp() != 0 || dynamic_sp != 0 || (item.getName().equalsIgnoreCase("리치 로브") && enlevel > 3))
			size += 2;
		if (item.getTicMp() != 0 || (item.getName().equalsIgnoreCase("리치 로브")))
			size += 3;
		if (setoption != null && (setoption.isBrave() || setoption.isHaste()))
			size += 1;

		return size;
	}
}