window.YDH_COMBAT_MOTIONS = {
  version: 1,
  timings: {
    attackMs: 420,
    hitMs: 260,
    deathMs: 760,
    castMs: 520,
    healMs: 520,
    guardMs: 520
  },
  classes: {
    attacking: 'is-attacking',
    hit: 'is-hit',
    dead: 'is-dead',
    casting: 'is-casting',
    healing: 'is-healing',
    guarding: 'is-guarding'
  },
  events: {
    play: 'ydh-battle-motion',
    resolved: 'ydh-battle-motion-resolved'
  },
  futureSheetLayout: {
    idle: { row: 0, frames: 1 },
    walk: { row: 1, frames: 4 },
    attack: { row: 2, frames: 4 },
    cast: { row: 3, frames: 4 },
    hit: { row: 4, frames: 2 },
    death: { row: 5, frames: 6 }
  }
};
