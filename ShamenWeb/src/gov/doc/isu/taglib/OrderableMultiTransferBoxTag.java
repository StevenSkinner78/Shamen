package gov.doc.isu.taglib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.dwarf.util.StringUtil;

/**
 * <p>
 * This <b>OrderableMultiTransferBoxTag</b> is a custom tag that displays two option boxes and allows user to select multiple options from the first option box and transfer to second box.
 * </p>
 * <p>
 * After options are transferred to second option box then the order that they appear in the list can be moved either up or down.
 * </p>
 * <p>
 * To use this tag include the following in your jsp: <code>&lt;isu:multiTransferBox listName="codeList" firstSelectId="firstSelectId" secondSelectId="secondSelectId" selectedProperty="selectedValues" className="styleClassName"/&gt;</code>.
 * </p>
 * <p>
 * The user has to provide the following attributes to the Tag:
 * </p>
 * <ul>
 * <li><code>listName</code> - This list contains the object of type CodeModel and the data from this List appears on the first select box.</li>
 * <li><code>firstSelectId</code> - This is the Id for the first multi select box.</li>
 * <li><code>secondSelectId</code> - This is the Id for the second multi select box.</li>
 * <li><code>selectedProperty</code> - This is the user selected List which appears on the second select box.</li>
 * <li><code>className</code> - This is the style class name from an external stylesheet. (optional)</li>
 * <li><code>addButtonId</code> - This is the Id for the add Button (optional).</li>
 * <li><code>removeButtonId</code> - This is the Id for the remove Button (optional).</li>
 * </ul>
 *
 * @author <strong>nkr000is</strong> - 05/07/201 :: Initial Version<br>
 *         <strong>nkr000is</strong> - 06/27/2010 :: Refactored the code.<br>
 *         <strong>nkr000is</strong> - 06/29/2010 :: Added the searching feature to the multi-transfer box.<br>
 *         <strong>Steve Skinnner</strong> JCCC - 08/12/2016 :: Modified to be able to order selected values.
 */
public class OrderableMultiTransferBoxTag extends TagSupport {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Logger log = Logger.getLogger("gov.doc.isu.taglib.OrderableMultiTransferBoxTag");
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String LABEL_OPEN = "<label";
    private static final String LABEL_CLOSE = "</label>";
    private static final String DIV_OPEN = "<div";
    private static final String DIV_CLOSE = "</div>";
    private static final String CSS_CLASS = " class=\"";
    private static final String S_NAME = " name=\"";
    private static final String STYLE_ID = " id=\"";
    private static final String DOUBLE_QUOTATION = "\" ";
    private static final String SELECT_START = "<select multiple=\"multiple\"  ";
    private static final String CLOSE_TAG = ">";
    private static final String OPTION_START = "<option ";
    private static final String OPTION_END = "</option>";
    private static final String VALUE = "value=\"";
    private static final String TITLE = "title=\"";
    private static final String SELECT_END = "</select>";
    /** Add and Remove Button */
    private static final String BUTTONS = "<div class=\"col-lg-3 col-sm-3 text-center\"> <div class=\"btn-group btn-group-vertical mt-4\"> <button type=\"button\" id=\"moveUp\" value=\"Move Up\" class=\"btn btn-sm btn-primary\" title=\"Move Up\">Move Up</button>  <button type=\"button\" id=\"addAll\" value=\"Add All\" class=\"btn btn-sm btn-primary\" title=\"Add All\">Add All</button>  <button type=\"button\" id=\"addSelected\" value=\"Add\" class=\"btn btn-sm btn-primary\"  title=\"Add Selected\">Add</button> <button type=\"button\" id=\"removeSelected\" value=\"Remove\" class=\"btn btn-sm btn-primary\"  title=\"Remove Selected\">Remove</button>  <button type=\"button\" id=\"removeAll\" value=\"Remove All\" class=\"btn btn-sm btn-primary\" title=\"Remove All\">Remove All</button> <button type=\"button\" id=\"moveDown\" value=\"Move Down\" class=\"btn btn-sm btn-primary\" title=\"Move Down\">Move Down</button> </div> </div>";
    /** Header for the MultiTransfer Select Box */
    /**
     * Transfer method to tie up the buttons and the select box. The implementation of this method is in the jquery.transfer.js
     */
    private String className = "multi";
    private String firstSelectId;
    private String secondSelectId;
    private String addButtonId;
    private String removeButtonId;
    private String addAllButtonId;
    private String removeAllButtonId;
    private String moveUpButtonId;
    private String moveDownButtonId;
    private String listName;
    private List<CodeModel> optionBeanList;
    private String[] selectedList;
    private String createButtonId;
    private String createButtonLabel = "Create";
    private Map<String, List<CodeModel>> optionBeanMap;
    private String multiLevelId;
    private List<String> createdList;
    private String selectedProperty;

    /***
     * This tag creates the Multi-Transfer Box (Accumulative Box).
     *
     * @return int
     * @throws JspException
     *         if an exception occurred
     */
    @SuppressWarnings("unchecked")
    public int doStartTag() throws JspException {
        log.debug("Entering doStartTag");
        log.debug("This tag creates the Multi-Transfer Box (Accumulative Box).");
        String methodName = "doStartTag";
        try{
            HttpSession session = pageContext.getSession();
            log.debug("Check the form in the request if not then try to find in the session.");
            optionBeanList = (List<CodeModel>) pageContext.findAttribute(listName);
            if(optionBeanList == null){
                log.debug("the optionBeanList name passed in was null. Setting optionBeanList to session form.");
                optionBeanList = (List<CodeModel>) session.getAttribute(listName);
            } // end if
            selectedList = (String[]) pageContext.findAttribute(selectedProperty);
            if(selectedList == null){
                log.debug("the selectedList name passed in was null. Setting selectedList to session form.");
                selectedList = (String[]) session.getAttribute(selectedProperty);
            } // end if
            StringBuffer multiTransferBox = null;
            List<String> interalSelectedList = null;
            if(selectedList != null && selectedList.length > 0){
                interalSelectedList = new ArrayList<String>();
                interalSelectedList.addAll(Arrays.asList(selectedList));
            } // end if
            if(createdList != null && createdList.size() > 0){
                if(interalSelectedList == null){
                    interalSelectedList = new ArrayList<String>();
                } // end if
                interalSelectedList.addAll(createdList);
            } // end if
            log.debug("If the add and remove button id's are not give then generate one.");
            if(StringUtil.isNullOrEmpty(addButtonId)){
                addButtonId = "add" + firstSelectId;
            } // end if
            if(StringUtil.isNullOrEmpty(removeButtonId)){
                removeButtonId = "remove" + firstSelectId;
            } // end if
            if(!StringUtil.isNullOrEmpty(firstSelectId) && !StringUtil.isNullOrEmpty(secondSelectId)){
                multiTransferBox = new StringBuffer();
                /** Construct the two select boxes and the buttons */
                multiTransferBox = constructSelectsAndButtons(multiTransferBox, interalSelectedList);
            }else{
                String errMsg = "Missing either firstSelectId ".concat(firstSelectId).concat(" or secondSelectId").concat(secondSelectId);
                log.error("Error occurred in doStartTag(). error= " + errMsg);
                throw new JspException(errMsg);
            } // end if/else
            if(multiTransferBox != null){
                /** Load the two multi-select boxes */
                pageContext.getOut().print(multiTransferBox.toString());
                pageContext.getOut().print(constructJavascript());
            } // end if
        }catch(Exception e){
            log.error(methodName + " Error While constructing the MultiTransferBox ", e);
            throw new JspException(e.getMessage());
        } // end try/catch
        log.debug("Return value is: EVAL_PAGE=" + String.valueOf(EVAL_PAGE));
        log.debug("Exiting doStartTag");
        return EVAL_PAGE;
    }// end doStartTag

    /**
     * This method constructs the first select box
     *
     * @param multiTransferBox
     *        the multi transfer box in StringBuffer
     * @param interalSelectedList
     *        the internal selected list
     * @param optionMap
     *        the option map
     * @param multiLevelSelectedList
     *        the multi level selected list
     */
    private void constructFirstSelectBox(StringBuffer multiTransferBox, List<String> interalSelectedList, HashMap<String, String> optionMap, List<CodeModel> multiLevelSelectedList) {
        log.debug("Entering constructFirstSelectBox");
        log.debug("This method constructs the first select box");
        log.debug("Entry parameters are: multiTransferBox=" + String.valueOf(multiTransferBox) + ", interalSelectedList=" + String.valueOf(interalSelectedList) + ", optionMap=" + String.valueOf(optionMap) + ", multiLevelSelectedList=" + String.valueOf(multiLevelSelectedList));
        final String delimiter = "$!";
        if(optionBeanList != null && optionBeanMap == null){
            multiLevelId = "false";
            for(CodeModel optionBean : optionBeanList){
                optionMap.put(optionBean.getCode(), optionBean.getDescription());
                String optionClass = "";
                if(interalSelectedList != null && interalSelectedList.contains(optionBean.getCode())){
                    optionClass = "class=\"added\"";
                } // end if
                multiTransferBox.append(NEW_LINE);
                multiTransferBox.append(OPTION_START).append(Constants.EMPTY_SPACE).append(optionClass);
                multiTransferBox.append(TITLE).append(optionBean.getDescription()).append("\"");
                multiTransferBox.append(VALUE).append(optionBean.getCode()).append("\"").append(CLOSE_TAG);
                multiTransferBox.append(optionBean.getDescription());
                multiTransferBox.append(OPTION_END);
                multiTransferBox.append(NEW_LINE);
            } // end for
        }else if(optionBeanMap != null){
            // Send a map containing keys and List of CodeModels as values
            multiLevelId = "true";
            Set<String> keysSet = optionBeanMap.keySet();
            for(String key : keysSet){
                List<CodeModel> values = optionBeanMap.get(key);
                optionMap.put(key, key);
                multiTransferBox.append(NEW_LINE);
                multiTransferBox.append(OPTION_START).append(Constants.EMPTY_SPACE);
                // Check if the child is selected. if so disable the parent in the First Select box.
                if(interalSelectedList != null && interalSelectedList.size() > 0){
                    boolean breakFlag = false;
                    for(String selectedKey : interalSelectedList){
                        for(CodeModel option : values){
                            if(option.getCode().equals(selectedKey)){
                                multiTransferBox.append("class=\"added\"");
                                breakFlag = true;
                                break;
                            } // end if
                        } // end for
                        if(breakFlag){
                            break;
                        } // end if
                    } // end for
                } // end if
                multiTransferBox.append(VALUE).append(key).append("\"").append(CLOSE_TAG);
                multiTransferBox.append(key);
                multiTransferBox.append(OPTION_END);
                multiTransferBox.append(NEW_LINE);
                for(CodeModel optionBean : values){
                    optionMap.put(optionBean.getCode(), optionBean.getDescription());
                    String optionClass = "";
                    if(interalSelectedList != null && interalSelectedList.contains(optionBean.getCode())){
                        optionClass = "class=\"added\"";
                    } // end if
                    multiTransferBox.append(NEW_LINE);
                    multiTransferBox.append(OPTION_START).append(Constants.EMPTY_SPACE).append(optionClass);
                    multiTransferBox.append(VALUE).append(optionBean.getCode()).append(delimiter).append(key).append("\"").append(CLOSE_TAG);
                    multiTransferBox.append("&nbsp;" + "&nbsp;" + (Constants.EMPTY_SPACE) + optionBean.getDescription());
                    multiTransferBox.append(OPTION_END);
                    multiTransferBox.append(NEW_LINE);
                } // end for
            } // end for
              // Update selected List to include Parent and update each key value to include parent name as suffix. Update value to have space at the begining.
            if(selectedList != null){
                multiLevelSelectedList = new ArrayList<CodeModel>();
                // Loop index has to be moved back on condition. Hence using older version of for loop.
                for(int index = 0;index < interalSelectedList.size();index++){
                    String selectedKeyValue = interalSelectedList.get(index);
                    boolean breakFlag = false;
                    Set<String> keysSet1 = optionBeanMap.keySet();
                    for(String key : keysSet1){
                        List<CodeModel> values = optionBeanMap.get(key);
                        for(CodeModel mapOptions : values){
                            if(mapOptions.getCode().equals(selectedKeyValue)){
                                if(!interalSelectedList.contains(key)){
                                    interalSelectedList.add(key);
                                    multiLevelSelectedList.add(new CodeModel(key, key));
                                } // end if
                                for(CodeModel optionBean : values){
                                    if(optionBean.getCode().equals(selectedKeyValue)){
                                        // Remove the original key value from the selectedList and put it back with the delimiter and parent name
                                        interalSelectedList.remove(index);
                                        interalSelectedList.add(optionBean.getCode() + delimiter + key);
                                        multiLevelSelectedList.add(new CodeModel("&nbsp;" + "&nbsp;" + (Constants.EMPTY_SPACE) + optionBean.getDescription(), optionBean.getCode() + delimiter + key));
                                        index--;
                                        breakFlag = true;
                                        break;
                                    } // end if
                                } // end for
                            } // end if
                        } // end for
                        if(breakFlag){
                            break;
                        } // end if
                    } // end for
                } // end for
            } // end if
        }else{
            log.error("constructFirstSelectBox - optionBeanList is null");
        } // end if/else
        log.debug("Exiting constructFirstSelectBox");
    }// end constructFirstSelectBox

    /**
     * This method constructs the second(selected) select box
     *
     * @param multiTransferBox
     *        the multi transfer box in StringBuffer
     * @param optionMap
     *        the option map
     * @param multiLevelSelectedList
     *        the multi level selected list
     */
    private void constructSecondSelectBox(StringBuffer multiTransferBox, HashMap<String, String> optionMap, List<CodeModel> multiLevelSelectedList) {
        log.debug("Entering constructSecondSelectBox");
        log.debug("This method constructs the second(selected) select box");
        log.debug("Entry parameters are: multiTransferBox=" + String.valueOf(multiTransferBox) + ", optionMap=" + String.valueOf(optionMap) + ", multiLevelSelectedList=" + String.valueOf(multiLevelSelectedList));
        final String delimiter = "$!";
        if(selectedList != null && optionBeanMap == null){
            for(Object selectValue : selectedList){
                multiTransferBox.append(NEW_LINE);
                multiTransferBox.append(OPTION_START);
                multiTransferBox.append(TITLE).append(optionMap.get(selectValue)).append("\"");
                multiTransferBox.append(VALUE).append(selectValue).append("\"").append(CLOSE_TAG);
                multiTransferBox.append(optionMap.get(selectValue));
                multiTransferBox.append(OPTION_END);
                multiTransferBox.append(NEW_LINE);
            } // end for
        }else if(optionBeanMap != null){
            /** Group the multiLevelSelectedList by Parent. */
            if(multiLevelSelectedList != null){
                Map<String, List<CodeModel>> sortedMap = new HashMap<String, List<CodeModel>>();
                for(CodeModel optionBean : multiLevelSelectedList){
                    String[] tokens = optionBean.getCode().split("\\" + delimiter);
                    if(tokens.length > 1){
                        if(sortedMap.containsKey(tokens[1])){
                            List<CodeModel> sortedList = sortedMap.get(tokens[1]);
                            if(sortedList != null){
                                sortedList.add(optionBean);
                            } // end if
                        }else{
                            List<CodeModel> optionList = new ArrayList<CodeModel>();
                            optionList.add(optionBean);
                            sortedMap.put(tokens[1], optionList);
                        } // end if/else
                    } // end if
                } // end for
                for(String key : sortedMap.keySet()){
                    multiTransferBox.append(NEW_LINE);
                    multiTransferBox.append(OPTION_START);
                    multiTransferBox.append(VALUE).append(key).append("\"").append(CLOSE_TAG);
                    multiTransferBox.append(key);
                    multiTransferBox.append(OPTION_END);
                    multiTransferBox.append(NEW_LINE);
                    List<CodeModel> sortedMapList = sortedMap.get(key);
                    if(sortedMapList != null){
                        for(CodeModel optionBean : sortedMapList){
                            multiTransferBox.append(NEW_LINE);
                            multiTransferBox.append(OPTION_START);
                            multiTransferBox.append(VALUE).append(optionBean.getCode()).append("\"").append(CLOSE_TAG);
                            multiTransferBox.append(optionBean.getDescription());
                            multiTransferBox.append(OPTION_END);
                            multiTransferBox.append(NEW_LINE);
                        } // end for
                    } // end if
                } // end for
            } // end if
        } // end if/else
        log.debug("Exiting constructSecondSelectBox");
    }// end constructSecondSelectBox

    /**
     * Construct the boxes and buttons
     *
     * @param multiTransferBox
     *        multiTransferBox
     * @param interalSelectedList
     *        interalSelectedList
     * @return StringBuffer
     */
    private StringBuffer constructSelectsAndButtons(StringBuffer multiTransferBox, List<String> interalSelectedList) {
        log.debug("Entering constructSelectsAndButtons");
        log.debug("Construct the boxes and buttons");
        log.debug("Entry parameters are: multiTransferBox=" + String.valueOf(multiTransferBox) + ", interalSelectedList=" + String.valueOf(interalSelectedList));
        multiTransferBox.append(DIV_OPEN).append(CSS_CLASS).append("col-lg-3 col-sm-10").append(DOUBLE_QUOTATION).append(CLOSE_TAG).append(NEW_LINE);
        multiTransferBox.append(DIV_OPEN).append(CSS_CLASS).append("form-group").append(DOUBLE_QUOTATION).append(CLOSE_TAG).append(NEW_LINE);
        multiTransferBox.append(LABEL_OPEN).append(CLOSE_TAG).append("Available").append(LABEL_CLOSE).append(NEW_LINE);
        multiTransferBox.append(SELECT_START).append(CSS_CLASS).append(className).append(DOUBLE_QUOTATION).append(S_NAME).append(firstSelectId).append(DOUBLE_QUOTATION).append(STYLE_ID).append(firstSelectId).append(DOUBLE_QUOTATION).append(CLOSE_TAG);
        HashMap<String, String> optionMap = new HashMap<String, String>();
        List<CodeModel> multiLevelSelectedList = null;
        /** Creating the first select Box */
        constructFirstSelectBox(multiTransferBox, interalSelectedList, optionMap, multiLevelSelectedList);
        multiTransferBox.append(SELECT_END);
        multiTransferBox.append(NEW_LINE);
        multiTransferBox.append(DIV_CLOSE).append(NEW_LINE).append(DIV_CLOSE).append(NEW_LINE);
        /** Replace the button id's with the user provided id's **/
        multiTransferBox.append(BUTTONS);
        // .replaceFirst(":addButtonId:", addButtonId).replaceFirst(":removeButtonId:", removeButtonId));
        multiTransferBox.append(DIV_OPEN).append(CSS_CLASS).append("col-lg-3 col-sm-10").append(DOUBLE_QUOTATION).append(CLOSE_TAG).append(NEW_LINE);
        multiTransferBox.append(DIV_OPEN).append(CSS_CLASS).append("form-group").append(DOUBLE_QUOTATION).append(CLOSE_TAG).append(NEW_LINE);
        multiTransferBox.append(LABEL_OPEN).append(CLOSE_TAG).append("Selected").append(LABEL_CLOSE).append(NEW_LINE);
        multiTransferBox.append(SELECT_START).append(CSS_CLASS).append(className).append(DOUBLE_QUOTATION).append(S_NAME).append(!StringUtil.isNullOrEmpty(selectedProperty) ? selectedProperty : secondSelectId).append(DOUBLE_QUOTATION).append(STYLE_ID).append(secondSelectId).append(DOUBLE_QUOTATION).append(CLOSE_TAG);
        /** Creating the second select Box */
        constructSecondSelectBox(multiTransferBox, optionMap, multiLevelSelectedList);
        multiTransferBox.append(NEW_LINE);
        multiTransferBox.append(SELECT_END);
        multiTransferBox.append(NEW_LINE);
        multiTransferBox.append(DIV_CLOSE).append(NEW_LINE).append(DIV_CLOSE).append(NEW_LINE);
        log.debug("Return value is: multiTransferBox=" + String.valueOf(multiTransferBox));
        log.debug("Exiting constructSelectsAndButtons");
        return multiTransferBox;
    }// end constructSelectsAndButtons

    /**
     * Construct the javascript functions
     *
     * @return StringBuffer
     */
    private StringBuffer constructJavascript() {
        log.debug("Entering MultiTransferTag.constructJavascript");
        StringBuffer sb = new StringBuffer();
        sb.append(NEW_LINE).append("<script type=\"text/javascript\">").append(NEW_LINE);
        sb.append("var moveUpBtn = document.getElementById(\"moveUp\");").append(NEW_LINE);
        sb.append("moveUpBtn.onclick = function(){").append(NEW_LINE);
        sb.append("var firstSelect = document.getElementById('").append(firstSelectId).append("');").append(NEW_LINE);
        sb.append("var secondSelect = document.getElementById('").append(secondSelectId).append("');").append(NEW_LINE);
        sb.append("var selected;").append(NEW_LINE);
        sb.append("for(var i = 0; i < secondSelect.options.length; i++){").append(NEW_LINE);
        sb.append("if(secondSelect.options[i].selected == true){").append(NEW_LINE);
        sb.append("selected = secondSelect.options[i];").append(NEW_LINE);
        sb.append("break;").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("if(selected.index != 0){").append(NEW_LINE);
        sb.append("var temp = secondSelect.options[selected.index - 1];").append(NEW_LINE);
        sb.append("secondSelect.options[selected.index] = temp.cloneNode(true);").append(NEW_LINE);
        sb.append("secondSelect.options[temp.index] = selected;").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("firstSelect.focus();").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("var addAllBtn = document.getElementById(\"addAll\");").append(NEW_LINE);
        sb.append("addAllBtn.onclick = function(){").append(NEW_LINE);
        sb.append("var firstSelect = document.getElementById('").append(firstSelectId).append("');").append(NEW_LINE);
        sb.append("var secondSelect = document.getElementById('").append(secondSelectId).append("');").append(NEW_LINE);
        sb.append("secondSelect.innerHTML = \"\";").append(NEW_LINE);
        sb.append("for(var i = 0; i < firstSelect.options.length; i++){").append(NEW_LINE);
        sb.append("var opt = new Option(firstSelect.options[i].label,firstSelect.options[i].value);").append(NEW_LINE);
        sb.append("opt.value = firstSelect.options[i].value;").append(NEW_LINE);
        sb.append("opt.label = firstSelect.options[i].label;").append(NEW_LINE);
        sb.append("secondSelect.options[secondSelect.options.length] = opt;").append(NEW_LINE);
        sb.append("firstSelect.options[i].className = 'added';").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("firstSelect.focus();").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("var addSelectedBtn = document.getElementById(\"addSelected\");").append(NEW_LINE);
        sb.append("addSelectedBtn.onclick = function(){").append(NEW_LINE);
        sb.append("var firstSelect = document.getElementById('").append(firstSelectId).append("');").append(NEW_LINE);
        sb.append("var secondSelect = document.getElementById('").append(secondSelectId).append("');").append(NEW_LINE);
        sb.append("for(var i = 0; i < firstSelect.options.length; i++){").append(NEW_LINE);
        sb.append("if(firstSelect.options[i].selected == true && firstSelect.options[i].className !='added' ){").append(NEW_LINE);
        sb.append("var opt = new Option(firstSelect.options[i].label,firstSelect.options[i].value);").append(NEW_LINE);
        sb.append("opt.value = firstSelect.options[i].value;").append(NEW_LINE);
        sb.append("opt.label = firstSelect.options[i].label;").append(NEW_LINE);
        sb.append("secondSelect.options[secondSelect.options.length] = opt;").append(NEW_LINE);
        sb.append("firstSelect.options[i].className = 'added'").append(NEW_LINE);
        sb.append("firstSelect.options[i].selected = false;").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("firstSelect.focus();").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("var removeSelectedBtn = document.getElementById(\"removeSelected\");").append(NEW_LINE);
        sb.append("removeSelectedBtn.onclick = function(){").append(NEW_LINE);
        sb.append("var firstSelect = document.getElementById('").append(firstSelectId).append("');").append(NEW_LINE);
        sb.append("var secondSelect = document.getElementById('").append(secondSelectId).append("');").append(NEW_LINE);
        sb.append("var newOpts = new Array();").append(NEW_LINE);
        sb.append("for(var i = 0; i < secondSelect.options.length; i++){").append(NEW_LINE);
        sb.append("if(secondSelect.options[i].selected == true){").append(NEW_LINE);
        sb.append("selVal = secondSelect.options[i].value;").append(NEW_LINE);
        sb.append("selLabel = secondSelect.options[i].label;").append(NEW_LINE);
        sb.append("for(var j = 0; j < firstSelect.options.length; j++){").append(NEW_LINE);
        sb.append("if(firstSelect.options[j].value == selVal && firstSelect.options[j].label == selLabel){").append(NEW_LINE);
        sb.append("firstSelect.options[j].className = '';").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("}else{").append(NEW_LINE);
        sb.append("newOpts[newOpts.length] = secondSelect.options[i];").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("secondSelect.innerHTML = \"\";").append(NEW_LINE);
        sb.append("for(var k = 0; k < newOpts.length; k++){").append(NEW_LINE);
        sb.append("secondSelect.options[k] = newOpts[k];").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("firstSelect.focus();").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("var removeAllBtn = document.getElementById(\"removeAll\");").append(NEW_LINE);
        sb.append("removeAllBtn.onclick = function(){").append(NEW_LINE);
        sb.append("var firstSelect = document.getElementById('").append(firstSelectId).append("');").append(NEW_LINE);
        sb.append("var secondSelect = document.getElementById('secondSelectId')").append(NEW_LINE);
        sb.append("secondSelect.innerHTML = \"\";").append(NEW_LINE);
        sb.append("secondSelect.value = \"\";").append(NEW_LINE);
        sb.append("for(var i = 0; i < firstSelect.options.length; i++){").append(NEW_LINE);
        sb.append("firstSelect.options[i].className = ''").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("firstSelect.focus();").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("var moveDownBtn = document.getElementById(\"moveDown\");").append(NEW_LINE);
        sb.append("moveDownBtn.onclick = function(){").append(NEW_LINE);
        sb.append("var firstSelect = document.getElementById('").append(firstSelectId).append("');").append(NEW_LINE);
        sb.append("var secondSelect = document.getElementById('secondSelectId')").append(NEW_LINE);
        sb.append("var selected;").append(NEW_LINE);
        sb.append("for(var i = 0; i < secondSelect.options.length; i++){").append(NEW_LINE);
        sb.append("if(secondSelect.options[i].selected == true){").append(NEW_LINE);
        sb.append("selected = secondSelect.options[i];").append(NEW_LINE);
        sb.append("break;").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("if(selected.index != (secondSelect.options.length - 1)){").append(NEW_LINE);
        sb.append("var temp = secondSelect.options[selected.index + 1];").append(NEW_LINE);
        sb.append("secondSelect.options[selected.index] = temp.cloneNode(true);").append(NEW_LINE);
        sb.append("secondSelect.options[temp.index] = selected;").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("firstSelect.focus();").append(NEW_LINE);
        sb.append("}").append(NEW_LINE);
        sb.append("</script>").append(NEW_LINE);
        log.debug("Exiting MultiTransferTag.constructJavascript");
        return sb;
    }// end constructJavascript

    /**
     * {@inheritDoc}
     */
    public int doEndTag() throws JspException {
        log.debug("Entering doEndTag");
        log.debug("{@inheritDoc}");
        release();
        log.debug("Return value is: EVAL_PAGE=" + String.valueOf(EVAL_PAGE));
        log.debug("Exiting doEndTag");
        return EVAL_PAGE;
    }// end doEndTag

    /**
     * {@inheritDoc}
     */
    public void release() {
        super.release();
        setAddButtonId(null);
        setRemoveButtonId(null);
        setAddAllButtonId(null);
        setRemoveAllButtonId(null);
        setMoveUpButtonId(null);
        setMoveDownButtonId(null);
    }// end release

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }// end getClassName

    /**
     * @param className
     *        the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }// end setClassName

    /**
     * @return the firstSelectId
     */
    public String getFirstSelectId() {
        return firstSelectId;
    }// end getFirstSelectId

    /**
     * @param firstSelectId
     *        the firstSelectId to set
     */
    public void setFirstSelectId(String firstSelectId) {
        this.firstSelectId = firstSelectId;
    }// end setFirstSelectId

    /**
     * @return the secondSelectId
     */
    public String getSecondSelectId() {
        return secondSelectId;
    }// end getSecondSelectId

    /**
     * @param secondSelectId
     *        the secondSelectId to set
     */
    public void setSecondSelectId(String secondSelectId) {
        this.secondSelectId = secondSelectId;
    }// end setSecondSelectId

    /**
     * @return the addButtonId
     */
    public String getAddButtonId() {
        return addButtonId;
    }// end getAddButtonId

    /**
     * @param addButtonId
     *        the addButtonId to set
     */
    public void setAddButtonId(String addButtonId) {
        this.addButtonId = addButtonId;
    }// end setAddButtonId

    /**
     * @return the removeButtonId
     */
    public String getRemoveButtonId() {
        return removeButtonId;
    }// end getRemoveButtonId

    /**
     * @param removeButtonId
     *        the removeButtonId to set
     */
    public void setRemoveButtonId(String removeButtonId) {
        this.removeButtonId = removeButtonId;
    }// end setRemoveButtonId

    /**
     * @return the addAllButtonId
     */
    public String getAddAllButtonId() {
        return addAllButtonId;
    }// end getAddAllButtonId

    /**
     * @param addAllButtonId
     *        the addAllButtonId to set
     */
    public void setAddAllButtonId(String addAllButtonId) {
        this.addAllButtonId = addAllButtonId;
    }// end setAddAllButtonId

    /**
     * @return the removeAllButtonId
     */
    public String getRemoveAllButtonId() {
        return removeAllButtonId;
    }// end getRemoveAllButtonId

    /**
     * @param removeAllButtonId
     *        the removeAllButtonId to set
     */
    public void setRemoveAllButtonId(String removeAllButtonId) {
        this.removeAllButtonId = removeAllButtonId;
    }// end setRemoveAllButtonId

    /**
     * @return the moveUpButtonId
     */
    public String getMoveUpButtonId() {
        return moveUpButtonId;
    }// end getMoveUpButtonId

    /**
     * @param moveUpButtonId
     *        the moveUpButtonId to set
     */
    public void setMoveUpButtonId(String moveUpButtonId) {
        this.moveUpButtonId = moveUpButtonId;
    }// end setMoveUpButtonId

    /**
     * @return the moveDownButtonId
     */
    public String getMoveDownButtonId() {
        return moveDownButtonId;
    }// end getMoveDownButtonId

    /**
     * @param moveDownButtonId
     *        the moveDownButtonId to set
     */
    public void setMoveDownButtonId(String moveDownButtonId) {
        this.moveDownButtonId = moveDownButtonId;
    }// end setMoveDownButtonId

    /**
     * @return the listName
     */
    public String getListName() {
        return listName;
    }// end getListName

    /**
     * @param listName
     *        the listName to set
     */
    public void setListName(String listName) {
        this.listName = listName;
    }// end setListName

    /**
     * @return the optionBeanList
     */
    public List<CodeModel> getOptionBeanList() {
        return optionBeanList;
    }// end getOptionBeanList

    /**
     * @param optionBeanList
     *        the optionBeanList to set
     */
    public void setOptionBeanList(List<CodeModel> optionBeanList) {
        this.optionBeanList = optionBeanList;
    }// end setOptionBeanList

    /**
     * @return the selectedList
     */
    public String[] getSelectedList() {
        return selectedList;
    }// end getSelectedList

    /**
     * @param selectedList
     *        the selectedList to set
     */
    public void setSelectedList(String[] selectedList) {
        this.selectedList = selectedList;
    }// end setSelectedList

    /**
     * @return the createButtonId
     */
    public String getCreateButtonId() {
        return createButtonId;
    }// end getCreateButtonId

    /**
     * @param createButtonId
     *        the createButtonId to set
     */
    public void setCreateButtonId(String createButtonId) {
        this.createButtonId = createButtonId;
    }// end setCreateButtonId

    /**
     * @return the createButtonLabel
     */
    public String getCreateButtonLabel() {
        return createButtonLabel;
    }// end getCreateButtonLabel

    /**
     * @param createButtonLabel
     *        the createButtonLabel to set
     */
    public void setCreateButtonLabel(String createButtonLabel) {
        this.createButtonLabel = createButtonLabel;
    }// end setCreateButtonLabel

    /**
     * @return the optionBeanMap
     */
    public Map<String, List<CodeModel>> getOptionBeanMap() {
        return optionBeanMap;
    }// end getOptionBeanMap

    /**
     * @param optionBeanMap
     *        the optionBeanMap to set
     */
    public void setOptionBeanMap(Map<String, List<CodeModel>> optionBeanMap) {
        this.optionBeanMap = optionBeanMap;
    }// end setOptionBeanMap

    /**
     * @return the multiLevelId
     */
    public String getMultiLevelId() {
        return multiLevelId;
    }// end getMultiLevelId

    /**
     * @param multiLevelId
     *        the multiLevelId to set
     */
    public void setMultiLevelId(String multiLevelId) {
        this.multiLevelId = multiLevelId;
    }// end setMultiLevelId

    /**
     * @return the createdList
     */
    public List<String> getCreatedList() {
        return createdList;
    }// end getCreatedList

    /**
     * @param createdList
     *        the createdList to set
     */
    public void setCreatedList(List<String> createdList) {
        this.createdList = createdList;
    }// end setCreatedList

    /**
     * @return the selectedProperty
     */
    public String getSelectedProperty() {
        return selectedProperty;
    }// end getSelectedProperty

    /**
     * @param selectedProperty
     *        the selectedProperty to set
     */
    public void setSelectedProperty(String selectedProperty) {
        this.selectedProperty = selectedProperty;
    }// end setSelectedProperty

}// end class
