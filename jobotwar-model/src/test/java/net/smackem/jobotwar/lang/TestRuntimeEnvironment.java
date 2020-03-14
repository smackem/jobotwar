package net.smackem.jobotwar.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

class TestRuntimeEnvironment implements RuntimeEnvironment {
    private double aim;
    private double radar;
    private double speedX;
    private double speedY;
    private double x;
    private double y;
    private double shot;
    private double damage;
    private final Collection<Double> loggedValues = new ArrayList<>();
    private final Collection<String> loggedCategories = new ArrayList<>();

    @Override
    public double readAim() {
        return this.aim;
    }

    @Override
    public void writeAim(double value) {
        this.aim = value;
    }

    @Override
    public double readRadar() {
        return this.radar;
    }

    @Override
    public void writeRadar(double value) {
        this.radar = value;
    }

    @Override
    public double readSpeedX() {
        return this.speedX;
    }

    @Override
    public void writeSpeedX(double value) {
        this.speedX = value;
    }

    @Override
    public double readSpeedY() {
        return this.speedY;
    }

    @Override
    public void writeSpeedY(double value) {
        this.speedY = value;
    }

    @Override
    public double readX() {
        return this.x;
    }

    @Override
    public double readY() {
        return this.y;
    }

    @Override
    public double readDamage() {
        return this.damage;
    }

    @Override
    public double readShot() {
        return this.shot;
    }

    @Override
    public void writeShot(double value) {
        this.shot = value;
    }

    @Override
    public double getRandom() {
        return ThreadLocalRandom.current().nextDouble();
    }

    @Override
    public void log(String category, double value) {
        this.loggedCategories.add(category);
        this.loggedValues.add(value);
    }

    void setX(double value) {
        this.x = value;
    }

    void setY(double value) {
        this.y = value;
    }

    void setDamage(double value) {
        this.damage = value;
    }

    Collection<Double> loggedValues() {
        return this.loggedValues;
    }

    Collection<String> loggedCategories() {
        return this.loggedCategories;
    }
}
