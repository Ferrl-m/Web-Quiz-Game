package engine.exceptions;

public class UserException extends Exception{
    private String msg;

    public UserException(String msg) {
        super(msg);
    }
}
