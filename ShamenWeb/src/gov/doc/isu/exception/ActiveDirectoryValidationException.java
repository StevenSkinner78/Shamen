package gov.doc.isu.exception;

/**
 * Exception class used when validating User credentials against the Active Directory.
 * 
 * @author Steven L. Skinner
 */
public class ActiveDirectoryValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *        message
     * @param ex
     *        ex
     */
    public ActiveDirectoryValidationException(String message, Throwable ex) {
        super(message, ex);
    }

    /**
     * @param message
     *        message
     */
    public ActiveDirectoryValidationException(String message) {
        super(message);
    }
}
