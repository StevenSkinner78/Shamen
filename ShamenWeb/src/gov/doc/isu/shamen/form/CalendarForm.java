package gov.doc.isu.shamen.form;

import java.util.List;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.shamen.models.BatchAppModel;
import net.sf.json.JSONArray;

/**
 * Form bean for the Overview page
 * 
 * @author Shane Duncan
 */
public class CalendarForm extends AbstractForm {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<BatchAppModel> bathcList;
    private JSONArray schedule;

    /**
     * @return the bathcList
     */
    public List<BatchAppModel> getBathcList() {
        return bathcList;
    }

    /**
     * @param bathcList
     *        the bathcList to set
     */
    public void setBathcList(List<BatchAppModel> bathcList) {
        this.bathcList = bathcList;
    }

    /**
     * @return the schedule
     */
    public JSONArray getSchedule() {
        return schedule;
    }

    /**
     * @param schedule
     *        the schedule to set
     */
    public void setSchedule(JSONArray schedule) {
        this.schedule = schedule;
    }

}
