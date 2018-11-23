package org.open.dojo.rightcourt.serve.exception;

import org.open.dojo.rightcourt.persistence.enums.Side;

public enum PointErrorCodes {
    LEFT_POINT("Point for side: LEFT", 10010),
    RIGHT_POINT("Point for side: RIGHT", 10011);
    private final String message;
    private final int errorCode;

    PointErrorCodes(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public static PointErrorCodes of(Side side) {
        if (side == Side.RIGHT) {
            return RIGHT_POINT;
        } else {
            return LEFT_POINT;
        }
    }

    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
