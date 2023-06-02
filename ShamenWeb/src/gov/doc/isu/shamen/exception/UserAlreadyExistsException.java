package gov.doc.isu.shamen.exception;

import gov.doc.isu.dwarf.exception.BaseException;

/**
 * Exception class used when an attempt is made to add a user who's user name already exists.
 * 
 * @author AAF00#IS, Steven L. Skinner
 */
public class UserAlreadyExistsException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *        message
     * @param ex
     *        throwable exception
     */
    public UserAlreadyExistsException(final String message, final Throwable ex) {
        super(message, ex);
    }

    /**
     * @param message
     *        message
     */
    public UserAlreadyExistsException(final String message) {
        super(message);
    }

}
