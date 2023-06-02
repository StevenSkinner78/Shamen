/**
 * 
 */
package gov.doc.isu.shamen.main;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is provided to customize handling of unexpected exceptions while the thread is executing.
 * 
 * @author Shane Duncan JCCC
 */
public class ShamenUncaughtExceptionHandler implements UncaughtExceptionHandler {
    private static Logger logger; 

    /**
     * This overridden constructor must be used to instantiate this class.
     * <p>
     * This class is intended to catch and log the uncaught exceptions inherent to a thread.
     * 
     * @param logger
     *        The logger associated with a running thread that has thrown an uncaught exception.
     */
    public ShamenUncaughtExceptionHandler(String name) {
        Logger.getLogger(name + "Exception");
    }// end constructor

    /*
     * (non-Javadoc)
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.log(Level.SEVERE, "An exception has been caught by the uncaught exception method for thread. Thread values are: " + (t != null ? t.toString() : "null") + ". Message is: " + e.getMessage(), e);
    }// end uncaughtException
}// end class
