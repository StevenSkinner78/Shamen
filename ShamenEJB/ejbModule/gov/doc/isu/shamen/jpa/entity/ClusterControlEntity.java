package gov.doc.isu.shamen.jpa.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the Controller Status database table.
 */
@Entity
@Table(name = "Cluster_Control", schema = "Trans")
public class ClusterControlEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Control_Nbr", unique = true)
    private Long controlNumber;

    @Column(name = "Control_Type")
    private String controlType;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "Create_Ts", insertable = true, updatable = false)
    private Timestamp createTime;

    /**
     * Default Constructor
     */
    public ClusterControlEntity() {}

    /**
     * @param controlNumber
     *        controlNumber
     */
    public ClusterControlEntity(Long controlNumber) {
        super();
        this.controlNumber = controlNumber;
    }

    /**
     * @return the controlNumber
     */
    public Long getControlNumber() {
        return controlNumber;
    }

    /**
     * @param controlNumber
     *        the controlNumber to set
     */
    public void setControlNumber(Long controlNumber) {
        this.controlNumber = controlNumber;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment
     *        the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the createTime
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     *        the createTime to set
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

}
