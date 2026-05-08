window.YDH_LORE_CONTENT = {
  title: '검은 달 연대기',
  subtitle: '무너진 왕국 아르덴의 마지막 기록',
  premise: '하늘에 두 번째 달이 떠오른 밤, 아르덴 왕국의 마나맥은 검게 뒤틀렸다. 플레이어는 검은 달의 균열을 봉인하기 위해 잃어버린 지도, 침묵한 NPC, 기억을 먹는 몬스터를 추적한다.',

  chapters: [
    { id: 'ch1', title: '1장: 해안의 검은 파도', mapId: 'talking-island', summary: '말하는 섬 해안에 검은 조수가 밀려오고, 오래된 달문양 제단이 드러난다.' },
    { id: 'ch2', title: '2장: 은빛 숲의 기록자', mapId: 'silver-forest', summary: '은빛 숲의 나무들은 밤마다 사람의 목소리로 울고, 까마귀 기록자는 잃어버린 이름을 거래한다.' },
    { id: 'ch3', title: '3장: 별빛 기록관', mapId: 'starlight-archive', summary: '살아있는 책들이 기억을 훔쳐 잉크 망령으로 바꾸는 기록관.' },
    { id: 'ch4', title: '4장: 심연의 왕좌', mapId: 'abyss-throne', summary: '검은 달의 군주가 모든 포탈을 하나의 밤으로 접으려 한다.' }
  ],

  items: [
    { id: 'moonblade', name: '검은 달검', type: 'weapon', grade: 'rare', atk: 4, def: 0, story: '달빛이 닿지 않는 밤에만 칼날이 드러나는 검.' },
    { id: 'ravenRing', name: '까마귀 인장 반지', type: 'accessory', grade: 'magic', atk: 2, def: 1, story: '기록자의 까마귀가 물고 온 은빛 반지.' },
    { id: 'starlightCloak', name: '별빛 망토', type: 'armor', grade: 'rare', atk: 0, def: 3, story: '별빛 기록관의 먼지를 털어내면 은하수가 흐른다.' },
    { id: 'abyssCore', name: '심연핵', type: 'material', grade: 'epic', atk: 3, def: 3, story: '심연의 왕좌 아래에서 뛰는 검은 심장 조각.' }
  ],

  maps: [
    { id: 'black-moon-ruins', name: '검은 달 폐허', role: 'ruins', story: '달문양 제단이 무너진 자리. 밤마다 그림자가 먼저 움직인다.' },
    { id: 'starlight-archive', name: '별빛 기록관', role: 'archive', story: '기억과 이름을 보관하는 살아있는 서고.' },
    { id: 'mirror-marsh', name: '거울 늪', role: 'marsh', story: '수면에 비친 것은 현재가 아니라 선택하지 않은 미래다.' },
    { id: 'abyss-throne', name: '심연의 왕좌', role: 'boss', story: '검은 달의 군주가 잠든 최종 전장.' }
  ],

  npcs: [
    { id: 'ravenArchivist', name: '까마귀 기록자', role: 'lore', dialogue: '이름을 잃은 자는 기록에 남지 못한다. 네 이름을 지켜라.' },
    { id: 'moonPriestess', name: '달무녀 세리아', role: 'quest', dialogue: '검은 달은 저주가 아니라 문이다. 열쇠를 찾으면 닫을 수도 있다.' },
    { id: 'brokenSmith', name: '부서진 대장장이 로한', role: 'craft', dialogue: '심연핵을 가져오면 달검의 금을 다시 이을 수 있다.' },
    { id: 'silentPorter', name: '침묵의 짐꾼', role: 'travel', dialogue: '말은 하지 않지만 가장 위험한 포탈을 알고 있다.' }
  ],

  monsters: [
    { id: 'moonStalker', name: '달그림자 추적자', grade: 'normal', hp: 95, atk: 14, story: '빛보다 그림자를 먼저 물어뜯는 추적자.' },
    { id: 'inkWraith', name: '잉크 망령', grade: 'normal', hp: 120, atk: 17, story: '기록관에서 지워진 이름들이 뭉쳐 태어난 망령.' },
    { id: 'mirrorWitch', name: '반사 마녀', grade: 'elite', hp: 180, atk: 24, story: '상대의 공격을 미래의 상처로 되돌려 보내는 마녀.' },
    { id: 'abyssKnight', name: '심연 기사', grade: 'elite', hp: 260, atk: 32, story: '검은 왕좌를 지키기 위해 이름을 버린 기사.' },
    { id: 'blackMoonLord', name: '검은 달의 군주', grade: 'boss', hp: 720, atk: 68, story: '모든 포탈을 하나의 밤으로 접으려는 최종 적.' }
  ],

  skills: [
    { id: 'moonSlash', name: '월영참', icon: '🌙', mp: 14, cd: 4200, scale: 2.25, type: 'damage', story: '검은 달의 궤적을 따라 적을 베어낸다.' },
    { id: 'ravenMark', name: '까마귀 표식', icon: '🐦‍⬛', mp: 16, cd: 6500, scale: 1.7, heal: 18, type: 'drain', story: '까마귀가 적의 약점을 기록하고 일부 생명력을 회수한다.' },
    { id: 'starBarrier', name: '성휘결계', icon: '🌟', mp: 18, cd: 8500, buff: 5, type: 'guard', story: '별빛 기록관의 문장을 펼쳐 방어를 강화한다.' },
    { id: 'abyssBurst', name: '심연폭렬', icon: '🕳️', mp: 28, cd: 12500, scale: 4.2, type: 'ultimate', story: '심연핵의 마나를 폭발시켜 거대한 피해를 준다.' }
  ]
};
