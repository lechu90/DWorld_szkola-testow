public class IncorrectVatException extends Throwable {

    private final String message;

    public IncorrectVatException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
