package openerp.openerpresourceserver.exception;

public class InternalServerException extends RuntimeException {
    public InternalServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(final String message) {
        super(message);
    }
}
