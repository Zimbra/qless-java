package com.zimbra.qless;

@SuppressWarnings("serial")
public class QlessException extends RuntimeException {
    public QlessException() {
    }

    public QlessException(String message) {
	super(message);
    }

    public QlessException(Throwable cause) {
	super(cause);
    }

    public QlessException(String message, Throwable cause) {
	super(message, cause);
    }
}
