package gov.doc.isu.shamen.taglib.exception;

/**
 * This class is used to model a custom exception thrown if the table properties file cannot be loaded.
 *
 * @author Joseph Burris JCCC, jsb000is
 */
public class TablePropertiesLoadException extends BaseNestableRuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * This overloaded constructor is used to instantiate this class.
     *
     * @param source
     *        The name of the source class throwing the exception.
     * @param propertiesFileName
     *        The name of the properties file to be loaded.
     * @param cause
     *        The exception cause.
     */
    public TablePropertiesLoadException(String source, String propertiesFileName, Throwable cause) {
        super(source, "Unable to load file " + propertiesFileName, cause);
    }// end constructor

}// end class
