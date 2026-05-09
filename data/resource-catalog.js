window.YDH_RESOURCES = {
  version: 1,
  groups: {
    styles: [
      'styles.css',
      'map.css',
      'visual-polish.css',
      'entity-sprites.css',
      'battle-sprites.css',
      'battle-motions.css',
      'walk-animations.css',
      'account-character.css',
      'item-equipment.css',
      'chapter-quests.css',
      'lore-codex.css',
      'codex-unlocks.css',
      'gm-console.css',
      'server-sync.css',
      'realtime-sync.css',
      'realtime-map-peers.css'
    ],
    data: [
      'data/lore-content.js',
      'data/item-catalog.js',
      'data/chapter-quests.js',
      'data/codex-unlocks.js',
      'data/save-schema.js',
      'data/maps.js',
      'data/atlas.js',
      'data/tiled-map-registry.js',
      'data/tiled-map-loader.js',
      'data/entities.js',
      'data/animations.js',
      'data/combat-motions.js'
    ],
    scripts: [
      'server-auth.js',
      'atlas-loader.js',
      'game-map-bridge.js',
      'tiled-map-bootstrap.js',
      'server-custom-map-sync.js',
      'map-engine.js',
      'walk-animations.js',
      'game.js',
      'server-sync.js',
      'account-character.js',
      'server-auth-panel.js',
      'item-equipment.js',
      'chapter-quests.js',
      'battle-sprites.js',
      'battle-motions.js',
      'lore-codex.js',
      'gm-console.js',
      'server-sync-panel.js',
      'realtime-sync.js',
      'realtime-map-peers.js',
      'ui-enhancements.js'
    ],
    images: [
      'assets/atlas/tiles-atlas.svg',
      'assets/atlas/tiles-atlas.webp',
      'assets/atlas/tiles-atlas.png',
      'assets/ui/ydh-crest.svg',
      'assets/ui/ornament-frame.svg',
      'assets/effects/spark-rune.svg'
    ]
  },
  budgets: {
    mobileInitialKb: 650,
    imageKb: 180,
    scriptKb: 900,
    cssKb: 420
  },
  qualityPresets: {
    low: {
      label: '저사양',
      particles: false,
      blur: false,
      shadows: 'soft',
      animationScale: 0.65
    },
    balanced: {
      label: '균형',
      particles: true,
      blur: true,
      shadows: 'normal',
      animationScale: 1
    },
    high: {
      label: '고품질',
      particles: true,
      blur: true,
      shadows: 'cinematic',
      animationScale: 1.2
    }
  },
  preload: {
    criticalImages: [
      'assets/atlas/tiles-atlas.svg',
      'assets/ui/ydh-crest.svg'
    ],
    opportunisticImages: [
      'assets/atlas/tiles-atlas.webp',
      'assets/atlas/tiles-atlas.png',
      'assets/ui/ornament-frame.svg',
      'assets/effects/spark-rune.svg'
    ]
  }
};
