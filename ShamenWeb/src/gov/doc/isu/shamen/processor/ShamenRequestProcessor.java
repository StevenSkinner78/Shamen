package gov.doc.isu.shamen.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.TilesRequestProcessor;

import gov.doc.isu.dwarf.core.AbstractForm;
import gov.doc.isu.dwarf.resources.Constants;
import gov.doc.isu.shamen.models.AuthorizedUserModel;
import gov.doc.isu.shamen.util.MenuPermissionAdapter;
import net.sf.navigator.menu.MenuComponent;
import net.sf.navigator.menu.MenuRepository;
import net.sf.navigator.menu.PermissionsAdapter;

/**
 * <p>
 * Request Processor for the Shamen Application.
 * <p>
 * Modifications:
 * <ul>
 * <li>Added overridden processPreprocess method to handle setting up the menu repository for struts menu.</li>
 * <li>Added overridden doForward method to handle redirecting to the logon page.</li>
 * <li>Added return statement if session user is null in processRoles to allow processPreprocess and doForward methods to be called.</li>
 * </ul>
 * 
 * @author Steven L. Skinner
 * @author Joseph Burris JCCC - modification author
 */
public class ShamenRequestProcessor extends TilesRequestProcessor {
    private static Logger log = Logger.getLogger("gov.doc.isu.shamen.processor.ShamenRequestProcessor");

    /*
     * (non-Javadoc)
     * @see org.apache.struts.action.RequestProcessor#processPreprocess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected boolean processPreprocess(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Entering processPreprocess.");
        if(request != null){
            if(request.getSession() != null){
                HttpSession session = request.getSession();
                log.debug("Setting local variable urlString from formatHttpServletRequestURL(request). (String)");
                if(request.getRequestURI().endsWith("/Logon.do") || request.getRequestURI().endsWith("/logonAction.do") || request.getRequestURI().endsWith("/logoff.do")){
                    log.debug("Request URL is a Log On or Log Off request. Not setting up the menu repository.");
                }else{
                    log.debug("Request URL is not a Log On or Log Off request. Setting up the menu repository.");
                    log.debug("Loading the MenuRepository Object named repository from request.getSession().getServletContext().getAttribute(MenuRepository.MENU_REPOSITORY_KEY)");
                    try{
                        MenuRepository repository = (MenuRepository) session.getServletContext().getAttribute(MenuRepository.MENU_REPOSITORY_KEY);
                        if(repository != null){
                            log.debug("A MenuRepository was found. " + repository.toString());
                        }else{
                            log.debug("The MenuRepository is null!");
                        }// end if
                        AuthorizedUserModel user = null;
                        log.debug("Checking session for appUser attribute.");
                        if(session.getAttribute(Constants.APPLICATION_USER) != null){
                            log.debug("appUser attribute found in session setting user Object.");
                            user = (AuthorizedUserModel) session.getAttribute(Constants.APPLICATION_USER);
                            
                            log.debug("Assigning local variable menuNames from repository.getTopMenusNames(). (java.utilList<?>)");
                            List<?> menuNames = repository.getTopMenusNames();
                            log.debug("Initializing local variable menusToEnable. (List<String>)");
                            List<String> menusToEnable = new ArrayList<String>();
                            log.debug("Iterating through menuNames.");
                            for(Iterator<?> it1 = menuNames.iterator();it1.hasNext();){
                                log.debug("Assigning local variable menu within menuNames iteration.");
                                MenuComponent menu = repository.getMenu(it1.next().toString());
                                log.debug("Assigning local variable matchFound from menu.getRoles().contains(user.getAuthority()). (boolean)");
                                boolean matchFound = menu.getRoles().contains(user.getAuthority());
                                if(matchFound){
                                    log.debug("matchFound calling - menu.setDescription(true)");
                                    menu.setDescription("true");
                                }// end if
                                log.debug("Assigning local variable showTab from menu.getDescription(). (boolean)");
                                boolean showTab = Boolean.valueOf(menu.getDescription());
                                if(showTab){
                                    log.debug("showTab are true calling - menusToEnable.add(menu.getName())");
                                    menusToEnable.add(menu.getName());
                                }// end if
                            }// end for
                            log.debug("assigning local variable theMenus.(String[])");
                            String[] theMenus = new String[menusToEnable.size()];
                            log.debug("Setting theMenus from menusToEnable.");
                            for(int i = 0, j = menusToEnable.size();i < j;i++){
                                theMenus[i] = menusToEnable.get(i).toString();
                            }// end for
                            log.debug("Assigning local variable permissions.(PermissionsAdapter)");
                            PermissionsAdapter permissions = new MenuPermissionAdapter(theMenus);
                            log.debug("Setting A request attribute, (name=permissionsAdapter value=permissions)");
                            request.setAttribute("permissionsAdapter", permissions);
                        }else{
                            log.error("System user in session is null. Unable to determine authority for menu tabs.");
                            doForward("/Logon.do", request, response);
                            return false;
                        }// end if/else
                    }catch(Exception e){
                        log.error("An Exception occured in processPreprocess( ). Exception is " + e);
                    }// end try/catch
                }// end if/else
            }else{
                log.error("Session is null. Unable to process menu tabs.");
            }// end if/else
        }else{
            log.error("Request is null. Unable to process menu tabs.");
        }// end if/else
        log.debug("Exiting processPreprocess.");
        return super.processPreprocess(request, response);
    }// end processPreprocess

    /*
     * (non-Javadoc)
     * @see org.apache.struts.tiles.TilesRequestProcessor#doForward(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doForward(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.debug("Entering doForward.");
        if(request != null){
            if(request.getSession() != null){
                AuthorizedUserModel user = null;
                HttpSession session = request.getSession();
                log.debug("Checking session for appUser attribute.");
                if(session.getAttribute(Constants.APPLICATION_USER) != null){
                    log.debug("appUser attribute found in session setting user Object.");
                    user = (AuthorizedUserModel) session.getAttribute(Constants.APPLICATION_USER);
                }// end if
                if(user == null){
                    // Check for valid attribute in session. If not, forward to welcome page.
                    log.error("user not logged on correctly....");
                    if(request.getRequestURI().endsWith("/Logon.do") || request.getRequestURI().endsWith("/logonAction.do") || request.getRequestURI().endsWith("/logoff.do")){
                        super.doForward(uri, request, response);
                    }else{
                        response.sendRedirect(request.getContextPath() + "/Logon.do");
                    }// end if/else
                }else{
                    super.doForward(uri, request, response);
                }// end if
            }else{
                log.error("Session is null. Unable to retrieve a system user from session.");
                super.doForward(uri, request, response);
            }// end if/else
        }else{
            log.error("Request is null. Unable to retrieve a system user from session.");
            super.doForward(uri, request, response);
        }// end if/else
        log.debug("Exiting doForward.");
    }// end doForward

    /**
     * This method processes the users security clearance, if the page requires clearance, and sets the restriction flags inherent to the form of the page being accessed.
     * 
     * @param request
     *        the request
     * @param response
     *        the response
     * @param mapping
     *        the mapping
     * @return boolean (always true)
     * @throws IOException
     *         the IOException
     * @throws ServletException
     *         the ServletException
     */
    protected boolean processRoles(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws IOException, ServletException {
        log.debug("Entering processRoles.");
        if(request != null){
            HttpSession session = request.getSession(false);
            if(session == null){
                log.error("Session is null. Unable to processRoles.");
                log.debug("Exiting processRoles.");
                return false;
            }// end if

            AuthorizedUserModel appUser = (AuthorizedUserModel) session.getAttribute(Constants.APPLICATION_USER);
            if(appUser == null){
                log.error("System user in session is null. Unable to process roles.");
                // Return true here so the doForward will process and redirect the system user to the logon page.
                return true;
            }// end if

            // Requested action (e.g. "/logon", "/logoff", etc..)
            String path = processPath(request, response);
            log.debug("ShamenRequestProcessor: path= " + path);
            // Exit if logging on or off or launching the application
            if(path.equalsIgnoreCase("/Logon") || path.equalsIgnoreCase("/logonAction") || path.equalsIgnoreCase("/logoff") || path.equalsIgnoreCase("/reportsLink")){
                log.debug("Exiting processRoles.");
                return true;
            }// end if
            AbstractForm theForm = (AbstractForm) processActionForm(request, response, mapping);
            String[] roles = mapping.getRoleNames();
            if((roles == null) || (roles.length < 1)){
                log.debug("Exiting processRoles.");
                return (true);
            }// end if
             // Authority not used in this application
            if(appUser.getAuthority().equalsIgnoreCase("USER")){
                theForm.setRestrict(true);
            }else{
                theForm.setRestrict(false);
            }// end lese
            for(int i = 0, j = roles.length;i < j;i++){
                if(roles[i].equalsIgnoreCase(appUser.getAuthority())){
                    if(appUser.getAuthority().equalsIgnoreCase("ADMN")){
                        theForm.setAdministrator(true);
                        log.debug("Exiting processRoles.");
                    }// end if
                    return true;
                }// end if
            }// end for
        }else{
            log.error("Request is null. Unable to processRoles.");
        }// end if/else
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, getInternal().getMessage("notAuthorized", mapping.getPath()));
        log.debug("Exiting processRoles.");
        return (false);
    }// end method
}// end class
