package gov.doc.isu.shamen.action;

import static gov.doc.isu.dwarf.resources.Constants.APPLICATION_USER;
import static gov.doc.isu.dwarf.resources.Constants.KEY;
import static gov.doc.isu.dwarf.resources.Constants.LIST;
import static gov.doc.isu.dwarf.resources.Constants.VAL;
import static gov.doc.isu.shamen.resources.AppConstants.DEFAULT_PAGE_SIZE;
import static gov.doc.isu.shamen.resources.AppConstants.PARAM_PAGESIZE;
import static gov.doc.isu.shamen.resources.AppConstants.SHAMEN_DEFAULT_LABEL;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import gov.doc.isu.dwarf.core.AbstractAction;
import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.dwarf.model.CodeModel;
import gov.doc.isu.dwarf.model.ColumnModel;
import gov.doc.isu.dwarf.model.CommonModel;
import gov.doc.isu.dwarf.util.AppUtil;
import gov.doc.isu.dwarf.util.StringUtil;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.util.MenuPermissionAdapter;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.navigator.menu.MenuComponent;
import net.sf.navigator.menu.MenuRepository;
import net.sf.navigator.menu.PermissionsAdapter;

/**
 * An abstract Dispatch Action for Web Data Utility projects that all actions should extend.
 *
 * @author <strong>Steven Skinner</strong>
 */
public class ShamenDispatchAction extends AbstractAction {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.action.ShamenDispatchAction");
    @SuppressWarnings("unused")
    private String className = "ShamenDispatchAction";

    /**
     * Automatically figures out the class name for extending classes
     */
    public ShamenDispatchAction() {
        this.className = this.getClass().getName();
    }// end constructor

    /**
     * Automatically figures out the class name for extending classes
     *
     * @param className
     *        className
     */
    public ShamenDispatchAction(String className) {
        this.className = className;
    }// end constructor

    /**
     * Retrieve the Application User tied to the request.
     *
     * @param request
     *        request
     * @return AuthorizedUserModel appUser
     */
    protected AuthorizedUserModel getAppUser(HttpServletRequest request) {
        log.debug("Entering gov.doc.isu.shamen.action.ShamenDispatchAction - method getAppUser()");
        HttpSession session = request.getSession(true);
        AuthorizedUserModel model = null;
        if(session.getAttribute(APPLICATION_USER) != null){
            model = (AuthorizedUserModel) session.getAttribute(APPLICATION_USER);
            log.debug("AppUser found in session.");
            log.debug("AppUser=" + String.valueOf(model));
        }else{
            log.debug("No appUser found in session.");
        }// end if/else
        log.debug("Exiting gov.doc.isu.shamen.action.ShamenDispatchAction - method getAppUser()");
        return model;
    }// end getAppUser

    /**
     * Verify there is a user in session
     *
     * @param request
     *        request
     * @return bolean
     */
    public boolean isLoggedIn(HttpServletRequest request) {
        log.debug("Entering gov.doc.isu.shamen.action.ShamenDispatchAction - method isLoggedIn()");
        AuthorizedUserModel appUser = this.getAppUser(request);
        log.debug(" appUser=" + appUser.toString());
        log.debug("Exiting gov.doc.isu.shamen.action.ShamenDispatchAction - method isLoggedIn()");
        return ((appUser != null) && (appUser.getUserID() != null));
    }// end isLoggedIn

    /**
     * sets the Create/Update UserRefId in the destination
     *
     * @param destination
     *        CommonModel where the timeStamp info should be copied to
     * @param request
     *        request
     */
    public void setUserRefIds(CommonModel destination, HttpServletRequest request) {
        log.debug("Entering gov.doc.isu.shamen.action.ShamenDispatchAction - method setUserRefIds()");
        destination.setCreateUserRefId(getAppUser(request).getUserRefId());
        destination.setUpdateUserRefId(getAppUser(request).getUserRefId());
        log.debug("Exiting gov.doc.isu.shamen.action.ShamenDispatchAction - method setUserRefIds()");

    }// end setUserRefIds

    /**
     * This method handles whether or not a navigation tab is displayed.
     *
     * @param request
     *        HttpServletRequest
     * @param link
     *        link name
     */
    public void setupTabs(HttpServletRequest request, String link, boolean showSubMenu) {
        log.debug("Entering gov.doc.isu.shamen.action.ShamenDispatchAction - method setupTabs()");
        MenuRepository repository = (MenuRepository) request.getSession().getServletContext().getAttribute(MenuRepository.MENU_REPOSITORY_KEY);
        if(getAppUser(request) != null){
            String authLevel = getAppUser(request).getAuthority();
            String[] menuNames = {"OverviewLink", "CalendarLink", "SystemLink", "SystemInfoLink", "AdminLink", "ApplicationLink", "ControllerLink", "ControllerInfoLink", "BatchLink", "BatchInfoLink", "BatchCollectionLink", "BatchCollectionInfoLink", "StatusLink", "StatLink"};
            List<String> menusToEnable = new ArrayList<String>();
            for(int i = 0, j = menuNames.length;i < j;i++){
                MenuComponent menu = repository.getMenu(menuNames[i]);
                boolean matchFound = menu.getRoles().contains(authLevel);
                if(matchFound){
                    if(menu.getName().equalsIgnoreCase("ControllerLink") || menu.getName().equalsIgnoreCase("SystemLink")){
                        repository.getMenu(menuNames[i]).setDescription("true");
                        menusToEnable.add(menu.getName());
                        for(int k = 0;k < menu.getComponents().size();k++){
                            MenuComponent menu2 = (MenuComponent) menu.getComponents().get(k);
                            if(menu2.getRoles().contains(authLevel)){
                                menusToEnable.add(menu2.getName());
                            }// end if
                        }// end for
                    }else if(menu.getName().equalsIgnoreCase("ApplicationLink") || menu.getName().equalsIgnoreCase("BatchLink") || menu.getName().equalsIgnoreCase("BatchCollectionLink") || menu.getName().equalsIgnoreCase("AdminLink") || menu.getName().equalsIgnoreCase("StatLink")){
                        repository.getMenu(menuNames[i]).setDescription("true");
                        menusToEnable.add(menu.getName());
                        for(int k = 0;k < menu.getComponents().size();k++){
                            MenuComponent menu2 = (MenuComponent) menu.getComponents().get(k);
                            if(link == null){
                                if(menu2.getName().contains("Info")){
                                    menu2.setDescription("false");
                                }else{
                                    menu2.setDescription("true");
                                }// end if/else
                            }else if(link != null && link.equals(menu2.getName())){
                                menu2.setDescription("true");
                            }else if(link != null && !link.equals(menu2.getName())){
                                menu2.setDescription("false");
                            }// end if/else
                            if(menu2.getDescription().equalsIgnoreCase("true")){
                                if(menu2.getRoles().contains(authLevel)){
                                    menusToEnable.add(menu2.getName());
                                }// end if
                            }// end if
                        }// end for
                    }else if(menu.getName().equalsIgnoreCase("ControllerInfoLink") || menu.getName().equalsIgnoreCase("SystemInfoLink") || menu.getName().equalsIgnoreCase("BatchInfoLink") || menu.getName().equalsIgnoreCase("BatchCollectionInfoLink") && link != null){
                        if(menu.getName().equalsIgnoreCase(link)){
                            repository.getMenu(menuNames[i]).setTitle(repository.getMenu(menuNames[i]).getTitle());
                            menusToEnable.add(menu.getName());
                            for(int k = 0;k < menu.getComponents().size();k++){
                                MenuComponent menu2 = (MenuComponent) menu.getComponents().get(k);
                                if(menu2.getRoles().contains(authLevel)){
                                    if(menu.getName().equalsIgnoreCase("BatchInfoLink") || menu.getName().equalsIgnoreCase("BatchCollectionInfoLink")){
                                        if(showSubMenu){
                                            menusToEnable.add(menu2.getName());
                                        }
                                    }else{
                                        menusToEnable.add(menu2.getName());
                                    }// end if
                                }
                            }// end for
                        }else{
                            repository.getMenu(menuNames[i]).setDescription("false");
                        }// end if/else
                    }else if(menu.getName().equalsIgnoreCase("ControllerInfoLink") || menu.getName().equalsIgnoreCase("SystemInfoLink") || menu.getName().equalsIgnoreCase("BatchInfoLink") || menu.getName().equalsIgnoreCase("BatchCollectionInfoLink") && link == null){
                        repository.getMenu(menuNames[i]).setDescription("false");
                    }else{
                        repository.getMenu(menuNames[i]).setDescription("true");
                        repository.getMenu(menuNames[i]).setTitle(repository.getMenu(menuNames[i]).getTitle());
                        menusToEnable.add(menu.getName());
                    }// end else
                }// end if
            }// end for
            String[] theMenus = new String[menusToEnable.size()];
            for(int i = 0, j = menusToEnable.size();i < j;i++){
                theMenus[i] = menusToEnable.get(i).toString();
            }// end for
            request.getSession().getServletContext().setAttribute(MenuRepository.MENU_REPOSITORY_KEY, repository);
            PermissionsAdapter permissions = new MenuPermissionAdapter(theMenus);
            request.setAttribute("permissionsAdapter", permissions);
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.action.ShamenDispatchAction - method setupTabs()");
    }// end setupTabs

    /**
     * This is used to build a JSON Array to be returned in an Ajax request.
     *
     * @param optionList
     *        - a list of options beans
     * @return JSONArray - a JSON ArrayList of all of the option beans
     */
    public JSONArray createJSONArray(List<CodeModel> optionList) {
        log.debug("Entering gov.doc.isu.shamen.action.ShamenDispatchAction - method createJSONArray()");
        JSONArray ja = new JSONArray();

        if(optionList != null && optionList.size() > 0){
            for(int i = 0, j = optionList.size();i < j;i++){
                JSONObject jo = new JSONObject();
                jo.put(KEY, optionList.get(i).getCode());
                jo.put(VAL, optionList.get(i).getDescription());
                ja.add(jo);
            }// end for
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.action.ShamenDispatchAction - method createJSONArray()");
        return ja;
    }// end createJSONArray

    /**
     * Returns true if every field in the list passed in is blank or empty, false if not.
     *
     * @param list
     *        the search object fields
     * @return true|false value of the check
     */
    public boolean checkFilterSearchObject(final List<ColumnModel> list) {
        log.debug("Entering gov.doc.isu.shamen.action.ShamenDispatchAction - method checkFilterSearchObject()");
        log.debug("Parameters are: list.size()=" + (list == null ? "null" : list.size()));
        boolean isAllBlank = false;
        Integer counter = 0;
        for(int i = 0, j = list.size();i < j;i++){
            if(list.get(i).getColumnValue() instanceof String[]){
                if(!AppUtil.checkForNotDefaultValue((String[]) list.get(i).getColumnValue())){
                    counter++;
                }// end if
            }else{
                if(StringUtil.isNullOrEmpty(String.valueOf(list.get(i).getColumnValue())) || SHAMEN_DEFAULT_LABEL.equalsIgnoreCase(String.valueOf(list.get(i).getColumnValue()))){
                    counter++;
                }// end if
            }// end if/else
        }// end for
        if(counter == list.size()){
            log.debug(" Search boxes were empty.");
            isAllBlank = true;
        }// end if
        log.debug("Exiting gov.doc.isu.shamen.action.ShamenDispatchAction - method checkFilterSearchObject()");
        return isAllBlank;
    }// end checkFilterSearchObject

    /**
     * This method loads page size selection into form. Is called from the onChange event handler in PageSizeTag.
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param form
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward changePageSize(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        log.debug("Entering gov.doc.isu.shamen.action.ShamenDispatchAction - method changePageSize()");
        AbstractForm theForm = (AbstractForm) form;
        String pagesize = DEFAULT_PAGE_SIZE;
        if(!StringUtil.isNullOrEmpty(request.getParameter(PARAM_PAGESIZE))){
            pagesize = request.getParameter(PARAM_PAGESIZE);
        }// end if
        log.debug(" setting pagesize to=" + pagesize);
        theForm.setPageSize(pagesize);
        setupTabs(request, null, true);
        log.debug("Exiting gov.doc.isu.shamen.action.ShamenDispatchAction - method changePageSize()");
        return mapping.findForward(LIST);
    }// end changePageSize

    /**
     * This method captures any data that is submitted from any of the list page This is required for enhanced touch list tag library
     *
     * @param mapping
     *        (REQUIRED)- Information that the controller, RequestProcessor, knows about mapping the request to the instance of this Action class.
     * @param actionForm
     *        (REQUIRED) - A JavaBean associated with mapping. It will have its properties initialized from the request parameters. Subsequently, properties of this bean have been populated
     * @param request
     *        (REQUIRED) - Provides data including parameter name and values, attributes, and an input stream
     * @param response
     *        (OPTIONAL) - Assist in sending a response to the client
     * @return ActionForward - Describes where and how control should be forwarded
     */
    public ActionForward updateListPageData(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) {
        log.debug("updateListPageData for pagination");
        setupTabs(request, null, true);
        return new ActionForward(mapping.getInput());
    }// end updateListPageData
}// end ShamenDispatchAction
