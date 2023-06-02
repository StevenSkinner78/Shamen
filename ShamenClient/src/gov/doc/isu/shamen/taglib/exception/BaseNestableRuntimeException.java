package gov.doc.isu.shamen.taglib.exception;

/**
 * This class is the super class for all custom exceptions needing to extend {@link RuntimeException}.
 *
 * @author Joseph Burris JCCC, jsb000is
 */
public abstract class BaseNestableRuntimeException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String source;
    private Throwable cause;

    /**
     * This overloaded constructor is used to instantiate this class.
     *
     * @param source
     *        The source class name.
     * @param message
     *        The exception message.
     * @param cause
     *        The exception cause.
     */
    public BaseNestableRuntimeException(String source, String message, Throwable cause) {
        super(message);
        this.source = source;
        this.cause = cause;
    }// end constructor

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BaseNestableRuntimeException [" + (source != null ? "source=" + source + ", " : "") + (cause != null ? "cause=" + cause : "") + "]";
    }// end toString

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }// end getSource

    /*
     * (non-Javadoc)
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public Throwable getCause() {
        return cause;
    }// end getCause
}// end class
