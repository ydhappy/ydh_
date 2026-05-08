window.YDH_ANIMATIONS = {
  version: 1,
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
    deathMs: 760
  },
  classes: {
    walking: 'is-walking',
    attacking: 'is-attacking',
    hit: 'is-hit',
    dead: 'is-dead'
  },
  futureSheetLayout: {
    idle: { row: 0, frames: 1 },
    walk: { row: 1, frames: 4 },
    attack: { row: 2, frames: 4 },
    hit: { row: 3, frames: 2 },
    death: { row: 4, frames: 6 }
  }
};
