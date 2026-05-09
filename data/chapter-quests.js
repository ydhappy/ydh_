window.YDH_CHAPTER_QUESTS = [
  {
    id: 'quest-ch1-black-tide',
    chapterId: 'ch1',
    title: '해안의 검은 파도',
    mapId: 'talking-island',
    description: '말하는 섬 해안에서 검은 조수의 흔적을 추적한다.',
    objectives: [
      { id: 'visit-talking-island', type: 'visitMap', target: 'talking-island', label: '말하는 섬 해안 방문', required: 1 },
      { id: 'talk-guide', type: 'talkNpc', target: '마을 안내인', label: '마을 안내인과 대화', required: 1 },
      { id: 'defeat-wolf', type: 'defeatMonster', target: '그림자 늑대', label: '그림자 늑대 처치', required: 1 }
    ],
    rewards: { gold: 80, exp: 40, item: '잃어버린 기억의 조각' }
  },
  {
    id: 'quest-ch2-raven-archive',
    chapterId: 'ch2',
    title: '은빛 숲의 기록자',
    mapId: 'silver-forest',
    description: '은빛 숲에서 까마귀 기록자의 단서를 찾는다.',
    objectives: [
      { id: 'visit-silver-forest', type: 'visitMap', target: 'silver-forest', label: '은빛 숲 방문', required: 1 },
      { id: 'talk-raven', type: 'talkNpc', target: '까마귀 기록자', label: '까마귀 기록자와 대화', required: 1 },
      { id: 'defeat-goblin', type: 'defeatMonster', target: '고블린 약탈자', label: '고블린 약탈자 처치', required: 2 }
    ],
    rewards: { gold: 130, exp: 70, item: '까마귀 깃펜' }
  },
  {
    id: 'quest-ch3-starlight-archive',
    chapterId: 'ch3',
    title: '별빛 기록관의 잉크',
    mapId: 'starlight-archive',
    description: '별빛 기록관에서 지워진 이름의 잉크를 회수한다.',
    objectives: [
      { id: 'visit-starlight', type: 'visitMap', target: 'starlight-archive', label: '별빛 기록관 방문', required: 1 },
      { id: 'defeat-ink', type: 'defeatMonster', target: '잉크 망령', label: '잉크 망령 처치', required: 2 },
      { id: 'obtain-raven-ring', type: 'obtainItem', target: '까마귀 인장 반지', label: '까마귀 인장 반지 획득', required: 1 }
    ],
    rewards: { gold: 220, exp: 120, item: '별빛 기록 조각' }
  },
  {
    id: 'quest-ch4-abyss-throne',
    chapterId: 'ch4',
    title: '심연의 왕좌',
    mapId: 'abyss-throne',
    description: '심연의 왕좌에서 검은 달의 군주를 봉인한다.',
    objectives: [
      { id: 'visit-abyss', type: 'visitMap', target: 'abyss-throne', label: '심연의 왕좌 방문', required: 1 },
      { id: 'talk-porter', type: 'talkNpc', target: '침묵의 짐꾼', label: '침묵의 짐꾼과 대화', required: 1 },
      { id: 'defeat-lord', type: 'defeatMonster', target: '검은 달의 군주', label: '검은 달의 군주 처치', required: 1 }
    ],
    rewards: { gold: 500, exp: 260, item: '검은 달 봉인석' }
  },
  {
    id: 'quest-ch5-moon-gate-marker',
    chapterId: 'ch5',
    title: '달문 석비의 경고',
    mapId: 'moon-gate-yard',
    description: '달문 광장에서 오래된 석비의 경고문을 확인한다.',
    objectives: [
      { id: 'visit-moon-gate', type: 'visitMap', target: 'moon-gate-yard', label: '달문 광장 방문', required: 1 },
      { id: 'inspect-moon-stone', type: 'inspectMarker', target: 'moon-gate-stone', label: '달문 석비 확인', required: 1 }
    ],
    rewards: { gold: 160, exp: 90, item: '달문 해석문' }
  }
];
