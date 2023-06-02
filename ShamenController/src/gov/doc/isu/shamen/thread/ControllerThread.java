/**
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 */
package gov.doc.isu.shamen.thread;

import java.util.Calendar;

import gov.doc.isu.gtv.logging.ThreadLogger;

/**
 * This is the parent class for threads within the Shamen controller. This contains all logic common to the threads.
 *
 * @author <strong>Shane Duncan</strong> JCCC, Aug 21, 2015
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
public abstract class ControllerThread implements Runnable {
    // Logger created for this thread.
    protected ThreadLogger myLogger = null;
    private String threadName = null;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.thread.ControllerThread";

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     * @param threadName
     *        (required)
     */
    public ControllerThread(String threadName) {
        super();
        this.threadName = threadName;
        // Initialize an instance of ThreadLogger for this class.
        myLogger = new ThreadLogger(MY_CLASS_NAME, threadName);
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Aug 31, 2015
     */
    public ControllerThread() {
        super();
        this.threadName = "THREAD_DEFAULT_" + Calendar.getInstance().getTimeInMillis() + "_";
        myLogger = new ThreadLogger(MY_CLASS_NAME, threadName);
        myLogger.finer("ControllerThread", "Thread created.");
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    abstract public void run();

    /**
     * @return the myLogger
     */
    public synchronized ThreadLogger getLogger() {
        return myLogger;
    }// end getLogger

    /**
     * This method is properly shuts the thread down.
     *
     * @author <strong>Shane Duncan</strong> JCCC, Sep 3, 2015
     */
    public void killMyself() {
        String methodName = "killMyself";
        myLogger.entering(MY_CLASS_NAME, methodName);
        myLogger.finest(methodName, "This method properly shuts the thread down.");
        myLogger.info(methodName, "Thread ended.*****************************************************");
        myLogger.exiting(MY_CLASS_NAME, methodName);
        myLogger.close();
        myLogger = null;
    }
}
