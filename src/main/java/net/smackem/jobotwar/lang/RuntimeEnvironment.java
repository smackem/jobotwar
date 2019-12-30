package net.smackem.jobotwar.lang;

public interface RuntimeEnvironment {
    double readAim();
    void writeAim(double value);

    double readRadar();
    void writeRadar(double value);

    double readSpeedX();
    void writeSpeedX(double value);

    double readSpeedY();
    void writeSpeedY(double value);

    double readX();
    double readY();

    double readDamage();

    double readShot();
    void writeShot(double value);

    double getRandom();
}
