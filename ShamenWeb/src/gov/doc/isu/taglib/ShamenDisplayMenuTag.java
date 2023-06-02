package gov.doc.isu.taglib;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.struts.taglib.TagUtils;

import net.sf.navigator.displayer.MenuDisplayer;
import net.sf.navigator.menu.MenuComponent;
import net.sf.navigator.menu.MenuRepository;
import net.sf.navigator.taglib.DisplayMenuTag;

/**
 * This tag extends DisplayMenuTag to build menus without context path. This is the main tag of Struts Menu and can be used in a JSP as follows:
 *
 * <pre>
 * &lt;wdu:useMenuDisplayer name="ListMenu"&gt;
 *      &lt;wdu:displayMenu name="MyMenu"/&gt;
 *   &lt;/wdu:useMenuDisplayer&gt;
 *
 * </pre>
 *
 * @author ssayles, mraible
 * @author Steven Skinner
 * @version $Revision: 1.1.2.1.10.3.8.1 $ $Date: 2018/03/26 14:29:20 $
 */

public class ShamenDisplayMenuTag extends DisplayMenuTag {
    /**
     *
     */
    private static final long serialVersionUID = 3542137479122051229L;
    private static Logger log = Logger.getLogger(ShamenDisplayMenuTag.class);
    private String name;
    private String target;

    // ~ Methods ================================================================

    /**
     * @param name
     *        the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param target
     *        the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * (non-Javadoc)
     *
     * @see net.sf.navigator.taglib.DisplayMenuTag#doStartTag()
     * @return int
     * @throws JspException
     *         if an exception occurred
     */
    public int doStartTag() throws JspException {
        log.debug("Entering ShamenDisplayMenuTag.doStartTag");
        MenuDisplayer displayer = (MenuDisplayer) pageContext.getAttribute(ShamenUseMenuDisplayerTag.DISPLAYER_KEY);

        if(displayer == null){
            throw new JspException("Could not retrieve the menu displayer.");
        }// end if

        if(log.isDebugEnabled()){
            log.debug("Looking for dispalyer named '" + ShamenUseMenuDisplayerTag.DISPLAYER_KEY + "'");
        }// end if

        // This is set by the parent tag - UseMenuDisplayerTag
        MenuRepository repository = (MenuRepository) pageContext.getAttribute(ShamenUseMenuDisplayerTag.PRIVATE_REPOSITORY);

        if(repository == null){
            throw new JspException("Could not obtain the menu repository");
        }// end if

        if(log.isDebugEnabled()){
            log.debug("Looking for MenuComponent named '" + this.name + "'");
        }// end if

        MenuComponent menu = repository.getMenu(this.name);

        if(menu != null){
            try{
                // use the overridden target
                if(target != null){
                    displayer.setTarget(this.target);
                    if(log.isDebugEnabled()){
                        log.debug("Looking for target named '" + this.target + "'");
                    }// end if
                }// end if

                // set the location value to use
                // the context relative page attribute
                // if specified in the menu
                try{
                    if(log.isDebugEnabled()){
                        log.debug("Setting Page location.");
                    }// end if
                    setPageLocation(menu);
                }catch(MalformedURLException m){
                    log.error("Incorrect action or forward: " + m.getMessage());
                    log.warn("Menu '" + menu.getName() + "' location set to #");
                    menu.setLocation("#");
                }// end try

                displayer.display(menu);
                displayer.setTarget(null);
            }catch(Exception e){
                log.error("Exception occurred in ShamenDisplayMenuTag. e=" + e.getMessage());
                throw new JspException(e);
            }// end try
        }else{
            String error = ShamenUseMenuDisplayerTag.getMessages().getString("menu.not.found") + " " + this.name;
            log.warn(error);
            try{
                pageContext.getOut().write(error);
            }catch(IOException io){
                throw new JspException(error);
            }// end try
        }// end else
        log.debug("Exiting ShamenDisplayMenuTag.doStartTag");
        return SKIP_BODY;
    }// end method

    /**
     * Sets the value for the menu location to the appropriate value if location is null. If location is null, and the page attribute exists, it's value will be set to the the value for page prepended with the context path of the application. If the page is null, and the forward attribute exists, it's value will be looked up in struts-config.xml. FIXME - ssayles - 121102 Ideally, this should happen at menu initialization but I was unable to find a reliable way to get the context path outside of a request. The performance impact is probably negligable, but it would be better to check for this only once.
     *
     * @param menu
     *        The menu component to set the location for.
     * @throws MalformedURLException
     *         if an exception occurred
     * @throws JspException
     *         if an exception occurred
     */
    protected void setPageLocation(MenuComponent menu) throws MalformedURLException, JspException {
        log.debug("Entering ShamenDisplayMenuTag.setPageLocation");
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        if(log.isDebugEnabled()){
            log.debug("Setting location for menu component named - " + menu.getName());
        }// end if
        setLocation(menu);
        String url = menu.getLocation();

        // Check if there are parameters on the value
        if((url != null) && (url.indexOf("${") > -1)){
            String queryString = null;

            if(url.indexOf("?") > -1){
                queryString = url.substring(url.indexOf("?") + 1);
                url = url.substring(0, url.indexOf(queryString));
            }// end if

            // variable is in the URL
            if(queryString != null){
                menu.setUrl(url + parseString(queryString, request));
            }else{
                // parse the URL, rather than the queryString
                menu.setUrl(parseString(url, request).toString());
            }// end else
        }else{
            menu.setUrl(url);
        }// end else

        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        if(menu.getUrl() != null){
            menu.setUrl(response.encodeURL(menu.getUrl()));
        }// end if

        if(log.isDebugEnabled()){
            log.debug("Location = " + menu.getUrl());
        }// end if

        // do all contained menus
        MenuComponent[] subMenus = menu.getMenuComponents();

        if(subMenus.length > 0){
            for(int i = 0;i < subMenus.length;i++){
                this.setPageLocation(subMenus[i]);
            }// end for
        }// end if
        log.debug("Exiting ShamenDisplayMenuTag.setPageLocation");
    }// end method

    /**
     * Sets the value for the menu location to the appropriate value if location is null. If location is null, and the page attribute exists, it's value will be set to the the value for page prepended with the context path of the application. If the page is null, and the forward attribute exists, it's value will be looked up in struts-config.xml. FIXME - ssayles - 121102 Ideally, this should happen at menu initialization but I was unable to find a reliable way to get the context path outside of a request. The performance impact is probably negligable, but it would be better to check for this only once.
     *
     * @param menu
     *        The menu component to set the location for.
     * @throws MalformedURLException
     *         if an exception occurred
     */
    protected void setLocation(MenuComponent menu) throws MalformedURLException {
        log.debug("Entering ShamenDisplayMenuTag.setLocation");
        // if the location attribute is null, then set it with a context relative page
        // attribute if it exists
        if(menu.getLocation() == null){
            try{
                if(menu.getPage() != null){

                    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
                    menu.setLocation(request.getContextPath() + getPage(menu.getPage()));
                    HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
                    menu.setLocation(response.encodeURL(menu.getLocation()));
                }else if(menu.getForward() != null){
                    menu.setLocation(TagUtils.getInstance().computeURL(pageContext, menu.getForward(), null, null, null, menu.getModule(), null, null, false));
                }else if(menu.getAction() != null){
                    if(menu.getAction().indexOf("#") > -1){
                        menu.setLocation("javascript:void(0)");
                    }else{
                        // generate Struts Action URL,
                        // this will append Context Path (if any),
                        // Servlet Mapping (path mapping or extension mapping)
                        // Module Prefix (if any) & Session ID (if any)
                        menu.setLocation(TagUtils.getInstance().computeURL(pageContext, null, null, null, menu.getAction(), menu.getModule(), null, null, false));
                    }// end else
                }// end else if
            }catch(NoClassDefFoundError e){
                if(menu.getForward() != null){
                    throw new MalformedURLException("forward '" + menu.getForward() + "' invalid - no struts.jar");
                }else if(menu.getAction() != null){
                    throw new MalformedURLException("action '" + menu.getAction() + "' invalid - no struts.jar");
                }// end else if
            }// end try
        }// end if
        if(log.isDebugEnabled()){
            log.debug("menu component location=" + menu.getLocation());
        }// end if
        log.debug("Exiting ShamenDisplayMenuTag.setLocation");
    }// end method

    /**
     * Returns the value with page prepended with a "/" if it is not already.
     *
     * @param page
     *        The value for the page.
     * @return String
     */
    protected String getPage(String page) {
        log.debug("Entering ShamenDisplayMenuTag.getPage");
        log.debug("Paramaters: page=" + String.valueOf(page));
        if(page.startsWith("/")){
            return page;
        }else{
            page = "/" + page;
        }// end else
        log.debug("Exiting ShamenDisplayMenuTag.getPage");
        return page;
    }// end method

    /**
     * @param str
     *        str
     * @param request
     *        request
     * @return StringBuffer
     */
    private StringBuffer parseString(String str, HttpServletRequest request) {
        log.debug("Entering ShamenDisplayMenuTag.parseString");
        log.debug("Paramaters: str=" + String.valueOf(str) + " ,HttpServletRequest=" + (request == null ? "null" : request.toString()));
        StringBuffer sb = new StringBuffer();

        while(str.indexOf("${") >= 0){
            sb.append(str.substring(0, str.indexOf("${")));

            String variable = str.substring(str.indexOf("${") + 2, str.indexOf("}"));
            String value = String.valueOf(pageContext.findAttribute(variable));

            if(value == null){
                // look for it as a request parameter
                value = request.getParameter(variable);
            }// end if

            // is value still null?!
            if(value == null){
                log.warn("Value for '" + variable + "' not found in pageContext or as a request parameter");
            }// end if

            sb.append(value);
            str = str.substring(str.indexOf("}") + 1, str.length());
        }// end while
        log.debug("Exiting ShamenDisplayMenuTag.parseString");
        return sb.append(str);
    }// end method

    /**
     * (non-Javadoc)
     *
     * @see net.sf.navigator.taglib.DisplayMenuTag#release()
     */
    public void release() {
        this.name = null;
        this.target = null;
    }// end method

}// end class
