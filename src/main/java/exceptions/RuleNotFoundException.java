package exceptions;

public class RuleNotFoundException extends RuntimeException{
    public RuleNotFoundException(String message) {
        super(message);
    }
}
