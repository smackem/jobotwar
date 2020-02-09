package net.smackem.jobotwar.gui;

/**
 * Alternate entry point for fat jars.
 * See https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing
 */
public class JarMain {
    public static void main(String[] args) {
        App.main(args);
    }
}
