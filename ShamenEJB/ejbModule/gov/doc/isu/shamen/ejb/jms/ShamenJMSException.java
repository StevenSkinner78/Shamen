package gov.doc.isu.shamen.ejb.jms;

/**
 * The base Exception for JMS processing
 * 
 * @author Steven L. Skinner
 */
public class ShamenJMSException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *        message
     * @param ex
     *        ex
     */
    public ShamenJMSException(final String message, final Throwable ex) {
        super(message, ex);
    }

    /**
     * @param message
     *        message
     */
    public ShamenJMSException(final String message) {
        super(message);
    }
}
