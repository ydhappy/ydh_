window.YDH_ANIMATIONS = {
  version: 2,
  framePolicy: {
    current: 'procedural-css',
    target: 'sheet-16dir-x-4walk',
    directionCount: 16,
    walkFrames: 4,
    idleFrames: 1,
    attackFrames: 4,
    hitFrames: 2,
    deathFrames: 6
  },
  timings: {
    walkMs: 320,
    stepMs: 80,
    attackMs: 420,
    hitMs: 260,
    deathMs: 760,
    uiEnterMs: 260,
    uiHoverMs: 180,
    mapPulseMs: 1200,
    portalMs: 2500,
    lootMs: 680,
    runeMs: 18000
  },
  classes: {
    walking: 'is-walking',
    attacking: 'is-attacking',
    hit: 'is-hit',
    dead: 'is-dead',
    uiEnter: 'ui-enter',
    uiReady: 'ui-ready',
    lootPop: 'loot-pop',
    runeGlow: 'rune-glow',
    qualityLow: 'quality-low',
    qualityBalanced: 'quality-balanced',
    qualityHigh: 'quality-high'
  },
  easing: {
    standard: 'cubic-bezier(.2,.8,.2,1)',
    snap: 'cubic-bezier(.18,1.25,.32,1)',
    heavy: 'cubic-bezier(.08,.72,.18,1)'
  },
  uiPresets: {
    cardEnter: { opacity: [0, 1], translateY: [12, 0], duration: 260 },
    toastPop: { opacity: [0, 1, 0], translateY: [10, 0, -8], duration: 1600 },
    buttonTap: { scale: [1, .97, 1], duration: 160 },
    panelGlow: { opacity: [.45, .78, .45], duration: 2600 }
  },
  effectPresets: {
    slash: { icon: '⚔️', duration: 420, shake: true },
    fire: { icon: '🔥', duration: 520, glow: '#ff835f' },
    ice: { icon: '❄️', duration: 520, glow: '#73a7ff' },
    heal: { icon: '✚', duration: 560, glow: '#69dda0' },
    loot: { icon: '✦', duration: 680, glow: '#f7c85f' },
    portal: { icon: '◎', duration: 760, glow: '#ad82ff' }
  },
  futureSheetLayout: {
    idle: { row: 0, frames: 1 },
    walk: { row: 1, frames: 4 },
    attack: { row: 2, frames: 4 },
    hit: { row: 3, frames: 2 },
    death: { row: 4, frames: 6 },
    cast: { row: 5, frames: 4 },
    emote: { row: 6, frames: 4 }
  },
  qualityMultipliers: {
    low: .65,
    balanced: 1,
    high: 1.2
  }
};
