package com.leyo.exception;

/**
 * Created by haizhu on 16/5/21.
 */

public class BuildException extends Exception {
    public BuildException() {
        super("初始化异常");
    }

    public BuildException(String detailMessage) {
        super(detailMessage);
    }

    public BuildException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BuildException(Throwable throwable) {
        super(throwable);
    }
}
