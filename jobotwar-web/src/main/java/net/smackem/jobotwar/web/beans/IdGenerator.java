package net.smackem.jobotwar.web.beans;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger nextId = new AtomicInteger(1);

    public static String next() {
        int id = nextId.getAndIncrement();
        return String.valueOf(id);
    }
}
