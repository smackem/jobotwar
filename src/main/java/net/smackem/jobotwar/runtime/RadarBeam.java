package net.smackem.jobotwar.runtime;

public final class RadarBeam {
    private final Robot sourceRobot;
    private final Vector hitPosition;
    private final RadarBeamHitKind hitKind;

    public RadarBeam(Robot sourceRobot, Vector hitPosition, RadarBeamHitKind hitKind) {
        this.sourceRobot = sourceRobot;
        this.hitPosition = hitPosition;
        this.hitKind = hitKind;
    }

    public Robot sourceRobot() {
        return this.sourceRobot;
    }

    public Vector hitPosition() {
        return this.hitPosition;
    }

    public RadarBeamHitKind hitKind() {
        return this.hitKind;
    }
}
