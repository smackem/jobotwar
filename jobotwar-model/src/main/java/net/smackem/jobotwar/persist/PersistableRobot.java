package net.smackem.jobotwar.persist;

public interface PersistableRobot {
    String getSourceCode();
    void setSourceCode(String value);

    String getBaseName();
    void setBaseName(String value);
}
