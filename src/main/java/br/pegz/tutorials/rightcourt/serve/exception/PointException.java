package br.pegz.tutorials.rightcourt.serve.exception;

public class PointException extends Exception {

    private final String message;
    private final int code;

    public PointException(PointErrorCodes pointErrorCodes) {
        super(pointErrorCodes.getMessage());
        this.message = pointErrorCodes.getMessage();
        this.code = pointErrorCodes.getErrorCode();
    }

    @Override
    public String toString() {
        return String.format("{\"error\":\"%s\",\"errorCode\":%d}", message, code);
    }
}
