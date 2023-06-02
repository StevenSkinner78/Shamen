package gov.doc.isu.shamen.form;

import static gov.doc.isu.dwarf.resources.Constants.NEW_LINE;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.shamen.models.BatchAppModel;
import net.sf.json.JSONArray;

/**
 * Form bean for the Shamen Stats Information for the Shamen Web Interface application.
 * 
 * @author <strong>Shane Duncan</strong>
 */
public class StatForm extends AbstractForm {

    private static final long serialVersionUID = 3772594624482339396L;
    private BatchAppModel batchApp;
    private String caller;
    private List<CodeModel> pocList;
    private String chartCsv;
    private Map chartMap;// used to store previous chart.
    private String chartTitle;
    private String[] showChart;
    private String showChartString;
    private String[] userLabels;
    private String[] selectedLabels;
    private String selectedLabelsString;
    private String colors;
    private JSONArray userData;
    private JSONArray systemData;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        showChart = null;
        userLabels = null;
        selectedLabels = null;

    }// end reset

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer builder = new StringBuffer();
        builder.append("StatForm [batchApp=");
        builder.append(batchApp).append(NEW_LINE);
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }// end toString

    /**
     * @return the caller
     */
    public String getCaller() {
        return caller;
    }

    /**
     * @param caller
     *        the caller to set
     */
    public void setCaller(String caller) {
        this.caller = caller;
    }

    /**
     * @return the pocList
     */
    public List<CodeModel> getPocList() {
        return pocList;
    }

    /**
     * @param pocList
     *        the pocList to set
     */
    public void setPocList(List<CodeModel> pocList) {
        this.pocList = pocList;
    }

    /**
     * @return the chartCsv
     */
    public String getChartCsv() {
        return chartCsv;
    }

    /**
     * @param chartCsv
     *        the chartCsv to set
     */
    public void setChartCsv(String chartCsv) {
        this.chartCsv = chartCsv;
    }

    /**
     * @return the batchApp
     */
    public BatchAppModel getBatchApp() {
        return batchApp;
    }

    /**
     * @param batchApp
     *        the batchApp to set
     */
    public void setBatchApp(BatchAppModel batchApp) {
        this.batchApp = batchApp;
    }

    /**
     * @return the chartMap
     */
    public Map getChartMap() {
        return chartMap;
    }

    /**
     * @param chartMap
     *        the chartMap to set
     */
    public void setChartMap(Map chartMap) {
        this.chartMap = chartMap;
    }

    /**
     * @return the chartTitle
     */
    public String getChartTitle() {
        return chartTitle;
    }

    /**
     * @param chartTitle
     *        the chartTitle to set
     */
    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    /**
     * @return the showChart
     */
    public String[] getShowChart() {
        return showChart;
    }

    /**
     * @param showChart
     *        the showChart to set
     */
    public void setShowChart(String[] showChart) {
        this.showChart = showChart;
    }

    /**
     * @return the showChartString
     */
    public String getShowChartString() {
        return showChartString;
    }

    /**
     * @param showChartString
     *        the showChartString to set
     */
    public void setShowChartString(String showChartString) {
        this.showChartString = showChartString;
    }

    /**
     * @return the userData
     */
    public JSONArray getUserData() {
        return userData;
    }

    /**
     * @param userData
     *        the userData to set
     */
    public void setUserData(JSONArray userData) {
        this.userData = userData;
    }

    /**
     * @return the userLabels
     */
    public String[] getUserLabels() {
        return userLabels;
    }

    /**
     * @param userLabels
     *        the userLabels to set
     */
    public void setUserLabels(String[] userLabels) {
        this.userLabels = userLabels;
    }

    /**
     * @return the selectedLabels
     */
    public String[] getSelectedLabels() {
        return selectedLabels;
    }

    /**
     * @param selectedLabels
     *        the selectedLabels to set
     */
    public void setSelectedLabels(String[] selectedLabels) {
        this.selectedLabels = selectedLabels;
    }

    /**
     * @return the selectedLabelsString
     */
    public String getSelectedLabelsString() {
        return selectedLabelsString;
    }

    /**
     * @param selectedLabelsString
     *        the selectedLabelsString to set
     */
    public void setSelectedLabelsString(String selectedLabelsString) {
        this.selectedLabelsString = selectedLabelsString;
    }

    /**
     * @return the systemData
     */
    public JSONArray getSystemData() {
        return systemData;
    }

    /**
     * @param systemData the systemData to set
     */
    public void setSystemData(JSONArray systemData) {
        this.systemData = systemData;
    }


    /**
     * @return the colors
     */
    public String getColors() {
        return colors;
    }

    /**
     * @param colors
     *        the colors to set
     */
    public void setColors(String colors) {
        this.colors = colors;
    }
}// end SystemForm
