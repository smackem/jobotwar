package net.smackem.jobotwar.runtime;

public final record RadarBeam(Robot sourceRobot,
                              Vector hitPosition,
                              RadarBeamHitKind hitKind) {
}
