package net.smackem.jobotwar.web.persist;

public class NoSuchBeanException extends Exception {
    public NoSuchBeanException(String message) {
        super(message);
    }

    public NoSuchBeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
