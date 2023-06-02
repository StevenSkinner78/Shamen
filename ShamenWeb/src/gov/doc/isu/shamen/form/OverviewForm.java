package gov.doc.isu.shamen.form;

import java.util.List;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.shamen.models.SystemModel;

/**
 * Form bean for the Overview page
 * 
 * @author Shane Duncan
 */
public class OverviewForm extends AbstractForm {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private SystemModel system;
    private List<SystemModel> systemList;
    private String radio;

    /**
     * @return the system
     */
    public SystemModel getSystem() {
        return system;
    }

    /**
     * @param system
     *        the system to set
     */
    public void setSystem(SystemModel system) {
        this.system = system;
    }

    /**
     * @return the systemList
     */
    public List<SystemModel> getSystemList() {
        return systemList;
    }

    /**
     * @param systemList
     *        the systemList to set
     */
    public void setSystemList(List<SystemModel> systemList) {
        this.systemList = systemList;
    }

    /**
     * @return the radio
     */
    public String getRadio() {
        return radio;
    }

    /**
     * @param radio
     *        the radio to set
     */
    public void setRadio(String radio) {
        this.radio = radio;
    }

}// end OverviewForm
