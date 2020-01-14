package net.smackem.jobotwar.lang;

/**
 * Provides access to the registers that control a robot.
 */
public interface RuntimeEnvironment {
    /**
     * @return The contents of the AIM register: angle of the gun.
     */
    double readAim();

    /**
     * Writes to the AIM register: angle of the gun.
     */
    void writeAim(double value);

    /**
     * @return The contents of the RADAR register: distance of last detected object (positive if wall, negative if other robot)
     */
    double readRadar();

    /**
     * Writes to the RADAR register: set angle of the radar beam, activate radar.
     */
    void writeRadar(double value);

    /**
     * @return The contents of the SPEEDX register: current x-speed.
     */
    double readSpeedX();

    /**
     * Writes to the SPEEDX register: current x-speed.
     */
    void writeSpeedX(double value);

    /**
     * @return The contents of the SPEEDY register: current y-speed.
     */
    double readSpeedY();

    /**
     * Writes to the SPEEDY register: current y-speed.
     */
    void writeSpeedY(double value);

    /**
     * @return The contents of the X register: current x-position.
     */
    double readX();

    /**
     * @return The contents of the Y register: current y-position.
     */
    double readY();

    /**
     * @return The contents of the DAMAGE register: 100 for full health, 0 for dead.
     */
    double readDamage();

    /**
     * @return The contents of the SHOT register: heat value of the gun. 0 if gun is ready.
     */
    double readShot();

    /**
     * Writes to the SHOT register: shoot a projectile that explodes in distance {@code value}.
     */
    void writeShot(double value);

    /**
     * @return The contents of the RANDOM register: random value between 0 and 1.
     */
    double getRandom();
}
