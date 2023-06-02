/**
 * This class is abstract and is only intended to consolidate the consistencies between batch apps and batch app collections.
 * 
 * @see gov.doc.isu.shamen.jms.models.JmsBatchApp
 * @see gov.doc.isu.shamen.jms.models.JmsBatchAppCollection
 * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
package gov.doc.isu.shamen.interfaces;

import java.util.List;

import gov.doc.isu.shamen.jms.models.JmsBaseObject;
import gov.doc.isu.shamen.jms.models.JmsSchedule;

/**
 * This class is abstract and is only intended to consolidate the consistencies between batch apps and batch app collections.
 * 
 * @see gov.doc.isu.shamen.jms.models.JmsBatchApp
 * @see gov.doc.isu.shamen.jms.models.JmsBatchAppCollection
 * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
 * @author <strong>Steven Skinner</strong> JCCC, Aug 17, 2020
 * @author <strong>Gary Campbell</strong> JCCC, Jan 12, 2021
 */
public abstract class Scheduleable extends JmsBaseObject {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<JmsSchedule> schedule;
    private String name;
    private String description;

    private String controllerName;
    private String controllerStatus;
    private Long controllerRefId;
    private String applicationName;
    private String applicationEnvironment;
    private String systemName;
    private String lastRunStatusCd;
    private String fileLocation;
    private String fileNm;
    private String type;
    private String typeDescription;

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @return the jmsSchedule
     */
    public List<JmsSchedule> getSchedule() {
        return schedule;
    }// end getSchedule

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @param schedule
     *        the jmsSchedule to set
     */
    public void setSchedule(List<JmsSchedule> schedule) {
        this.schedule = schedule;
    }// end setSchedule

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @return the name
     */
    public String getName() {
        return name;
    }// end getName

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @param name
     *        the name to set
     */
    public void setName(String name) {
        this.name = name;
    }// end setName

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @return the description
     */
    public String getDescription() {
        return description;
    }// end getDescription

    /**
     * @author <strong>Shane Duncan</strong> JCCC, Jul 20, 2016
     * @param description
     *        the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }// end setDescription

    /**
     * @return the controllerName
     */
    public String getControllerName() {
        return controllerName;
    }// end getControllerName

    /**
     * @param controllerName
     *        the controllerName to set
     */
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }// end setControllerName

    /**
     * @return the controllerStatus
     */
    public String getControllerStatus() {
        return controllerStatus;
    }// end getControllerStatus

    /**
     * @param controllerStatus
     *        the controllerStatus to set
     */
    public void setControllerStatus(String controllerStatus) {
        this.controllerStatus = controllerStatus;
    }// end setControllerStatus

    /**
     * @return the controllerRefId
     */
    public Long getControllerRefId() {
        return controllerRefId;
    }// end getControllerRefId

    /**
     * @param controllerRefId
     *        the controllerRefId to set
     */
    public void setControllerRefId(Long controllerRefId) {
        this.controllerRefId = controllerRefId;
    }// end setControllerRefId

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }// end getApplicationName

    /**
     * @param applicationName
     *        the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }// end setApplicationName

    /**
     * @return the applicationEnvironment
     */
    public String getApplicationEnvironment() {
        return applicationEnvironment;
    }// end getApplicationEnvironment

    /**
     * @param applicationEnvironment
     *        the applicationEnvironment to set
     */
    public void setApplicationEnvironment(String applicationEnvironment) {
        this.applicationEnvironment = applicationEnvironment;
    }// end setApplicationEnvironment

    /**
     * @return the systemName
     */
    public String getSystemName() {
        return systemName;
    }// end getSystemName

    /**
     * @param systemName
     *        the systemName to set
     */
    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }// end setSystemName

    /**
     * @return the lastRunStatusCd
     */
    public String getLastRunStatusCd() {
        return lastRunStatusCd;
    }// end getLastRunStatusCd

    /**
     * @param lastRunStatusCd
     *        the lastRunStatusCd to set
     */
    public void setLastRunStatusCd(String lastRunStatusCd) {
        this.lastRunStatusCd = lastRunStatusCd;
    }// end setLastRunStatusCd

    /**
     * @return the fileLocation
     */
    public String getFileLocation() {
        return fileLocation;
    }// end getFileLocation

    /**
     * @param fileLocation
     *        the fileLocation to set
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }// end setFileLocation

    /**
     * @return the fileNm
     */
    public String getFileNm() {
        return fileNm;
    }// end getFileNm

    /**
     * @param fileNm
     *        the fileNm to set
     */
    public void setFileNm(String fileNm) {
        this.fileNm = fileNm;
    }// end setFileNm

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }// end getType

    /**
     * @param type
     *        the type to set
     */
    public void setType(String type) {
        this.type = type;
    }// end setType

    /**
     * @return the typeDescription
     */
    public String getTypeDescription() {
        return typeDescription;
    }// end getTypeDescription

    /**
     * @param typeDescription
     *        the typeDescription to set
     */
    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }// end setTypeDescription

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("Scheduleable [schedule=");
        builder.append(schedule);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", controllerName=");
        builder.append(controllerName);
        builder.append(", controllerStatus=");
        builder.append(controllerStatus);
        builder.append(", controllerRefId=");
        builder.append(controllerRefId);
        builder.append(", applicationName=");
        builder.append(applicationName);
        builder.append(", applicationEnvironment=");
        builder.append(applicationEnvironment);
        builder.append(", systemName=");
        builder.append(systemName);
        builder.append(", lastRunStatusCd=");
        builder.append(lastRunStatusCd);
        builder.append(", fileLocation=");
        builder.append(fileLocation);
        builder.append(", fileNm=");
        builder.append(fileNm);
        builder.append(", type=");
        builder.append(type);
        builder.append(", typeDescription=");
        builder.append(typeDescription);
        builder.append("]");
        return builder.toString();
    }// end toString
}// end class
