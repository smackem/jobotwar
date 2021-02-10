package net.smackem.jobotwar.web.beans;

import java.util.UUID;

public class IdGenerator {
    public static String next() {
        return UUID.randomUUID().toString();
    }
}
