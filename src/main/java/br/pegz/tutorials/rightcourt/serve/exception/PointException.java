package br.pegz.tutorials.rightcourt.serve.exception;

import br.pegz.tutorials.rightcourt.persistence.enums.Side;

public class PointException extends Throwable {

    private String message;
    private int code = 100000;

    public PointException(Side side) {
        super("Point for side: " + side);
        this.message = "Point for side: " + side;
        if(Side.LEFT == side) {
            code += 10;
        } else if (Side.RIGHT == side){
            code += 11;
        }
    }

    @Override
    public String toString() {
        return String.format("{\"error\":\"%s\",\"errorCode\":%d}", message, code);
    }
}
