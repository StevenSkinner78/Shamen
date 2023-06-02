/**
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
package gov.doc.isu.shamen.models;

import java.sql.Timestamp;

/**
 * This class is a model used to store Messages from the DB.
 * 
 * @author <strong>Shane Duncan</strong> JCCC, Oct 6, 2015
 */
public class Message {
    private String id;
    private String type;
    private String message;
    private byte[] messageBytes;
    private Timestamp createTs;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 20, 2015
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 20, 2015
     * @param type
     *        the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 20, 2015
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 20, 2015
     * @param message
     *        the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 21, 2015
     * @return the createTs
     */
    public Timestamp getCreateTs() {
        return createTs;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 21, 2015
     * @param createTs
     *        the createTs to set
     */
    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Message [id=" + id + ", type=" + type + ", message=" + message + ", createTs=" + createTs + "]";
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 21, 2015
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 21, 2015
     * @param id
     *        the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 22, 2015
     * @return the messageBytes
     */
    public byte[] getMessageBytes() {
        return messageBytes;
    }

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Oct 22, 2015
     * @param messageBytes
     *        the messageBytes to set
     */
    public void setMessageBytes(byte[] messageBytes) {
        this.messageBytes = messageBytes;
    }
}
