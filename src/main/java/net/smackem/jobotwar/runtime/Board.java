package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.util.Arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class Board {
    private final int width;
    private final int height;
    private final Collection<Robot> robots;
    private final Collection<Projectile> projectiles = new ArrayList<>();

    public Board(int width, int height, Collection<Robot> robots) {
        this.width = Arguments.requireRange(width, 0, 10 * 1000);
        this.height = Arguments.requireRange(height, 0, 10 * 1000);
        this.robots = new ArrayList<>(Objects.requireNonNull(robots));
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Collection<Robot> getRobots() {
        return Collections.unmodifiableCollection(this.robots);
    }

    public Collection<Projectile> projectiles() {
        return this.projectiles;
    }
}
