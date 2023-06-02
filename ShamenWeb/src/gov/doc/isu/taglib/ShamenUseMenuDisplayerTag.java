package gov.doc.isu.taglib;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

import net.sf.navigator.displayer.MenuDisplayer;
import net.sf.navigator.displayer.MenuDisplayerMapping;
import net.sf.navigator.displayer.MessageResourcesMenuDisplayer;
import net.sf.navigator.menu.MenuRepository;
import net.sf.navigator.menu.PermissionsAdapter;
import net.sf.navigator.menu.RolesPermissionsAdapter;
import net.sf.navigator.taglib.UseMenuDisplayerTag;

/**
 * This is the main tag of Struts Menu and can be used in a JSP as follows:
 * 
 * <pre>
 *  &lt;menu:useMenuDisplayer name="ListMenu"&gt;
 *     &lt;menu:displayMenu name="MyMenu"/&gt;
 *  &lt;/menu:useMenuDisplayer&gt;
 * </pre>
 * 
 * @author ssayles, mraible
 * @version $Revision: 1.1.2.1.8.1.2.2.8.1 $ $Date: 2018/03/26 14:29:20 $
 */

public class ShamenUseMenuDisplayerTag extends UseMenuDisplayerTag {
    /**
     * 
     */
    private static final long serialVersionUID = -9203420458968428185L;
    private static Logger log = Logger.getLogger(ShamenUseMenuDisplayerTag.class);
    public static final String PRIVATE_REPOSITORY = "net.sf.navigator.repositoryKey";
    public static final String DISPLAYER_KEY = "net.sf.navigator.taglib.DISPLAYER";
    public static final String ROLES_ADAPTER = "rolesAdapter";
    public static final String MENU_ID = "net.sf.navigator.MENU_ID";

    private static ResourceBundle messages = ResourceBundle.getBundle("net.sf.navigator.taglib.LocalStrings");

    // ~ Instance fields ========================================================

    private MenuDisplayer menuDisplayer;
    private String localeKey;
    private String name;
    private String bundleKey;
    private String id;
    private String config;
    private String permissions;
    private String repository;
    private ResourceBundle rb; // used to allow setting of ResourceBundle
                               // from JSTL in EL tag

    // ~ Methods ================================================================

    /**
     * @return the bundleKey
     */
    public String getBundleKey() {
        return bundleKey;
    }

    /**
     * @param bundleKey
     *        the bundleKey to set
     */
    public void setBundleKey(String bundleKey) {
        this.bundleKey = bundleKey;
    }

    /**
     * @return the config
     */
    public String getConfig() {
        return config;
    }

    /**
     * @param config
     *        the config to set
     */
    public void setConfig(String config) {
        if(log.isDebugEnabled()){
            log.debug("setting config to: " + config);
        }

        this.config = config;
    }

    /**
     * @return the localeKey
     */
    public String getLocale() {
        return localeKey;
    }

    /**
     * @param locale
     *        the localeKey to set
     */
    public void setLocale(String locale) {
        this.localeKey = locale;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param id
     *        the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the repository
     */
    public String getRepository() {
        return repository;
    }

    /**
     * This method allows users to override the key used to lookup the repository. If not specified - the default repository is used, which is "net.sf.navigator.MENU_REPOSITORY" or UseMenuDisplayerTag.MENU_REPOSITORY_KEY.
     * 
     * @param repository
     *        the repository to set
     */
    public void setRepository(String repository) {
        this.repository = repository;
    }

    /**
     * @return the messages
     */
    public static ResourceBundle getMessages() {
        return messages;
    }

    /**
     * @param messages
     *        the messages to set
     */
    public static void setMessages(ResourceBundle messages) {
        ShamenUseMenuDisplayerTag.messages = messages;
    }

    /**
     * Getter for property permissions.
     * 
     * @return Value of property permissions.
     */
    public String getPermissions() {
        return this.permissions;
    }

    /**
     * Setter for property permissions.
     * 
     * @param permissions
     *        New value of property permissions.
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    /**
     * @see net.sf.navigator.taglib.UseMenuDisplayerTag#doStartTag()
     * @return int
     * @throws JspException
     *         if an exception occurred
     */
    public int doStartTag() throws JspException {
        log.debug("Entering ShamenUseMenuDisplayerTag.doStartTag");

        if(repository == null){
            repository = MenuRepository.MENU_REPOSITORY_KEY;
        }// end if

        if(log.isDebugEnabled()){
            log.debug("Looking for repository named '" + repository + "'");
        }// end if

        // get the menu repository
        MenuRepository rep = (MenuRepository) pageContext.findAttribute(this.repository);

        if(rep == null){
            throw new JspException(messages.getString("menurepository.not.found"));
        }else{
            // set repository as a pageContext variable so that DisplayMenuTag
            // can grab it.
            if(log.isDebugEnabled()){
                log.debug("stuffing repository into pageContext...");
            }// end if
            pageContext.setAttribute(PRIVATE_REPOSITORY, rep);
        }// end else

        // get the displayer mapping
        MenuDisplayerMapping displayerMapping = rep.getMenuDisplayerMapping(this.name);

        if(displayerMapping == null){
            throw new JspException(messages.getString("displayer.mapping.not.found"));
        }// end if

        PermissionsAdapter permissionsAdapter = getPermissionsAdapter();

        // get an instance of the menu displayer
        MenuDisplayer displayerInstance;

        try{
            displayerInstance = (MenuDisplayer) Class.forName(displayerMapping.getType()).newInstance();
            menuDisplayer = displayerInstance;
            // default to use the config on the mapping
            if(displayerMapping.getConfig() != null){
                // this value (config) is set on the displayer below
                setConfig(displayerMapping.getConfig());
            }// end if
        }catch(Exception e){
            throw new JspException(e.getMessage());
        }// end try

        if(bundleKey == null){
            this.bundleKey = "org.apache.struts.action.MESSAGE";
        }// end if

        // setup the displayerInstance
        // if the displayer is a MessageResourceMenuDisplayer
        // and a bundle is specified, then pass it the bundle (message resources) and
        // the locale
        if((bundleKey != null && !"".equals(bundleKey)) && (displayerInstance instanceof MessageResourcesMenuDisplayer)){
            MessageResourcesMenuDisplayer mrDisplayerInstance = (MessageResourcesMenuDisplayer) displayerInstance;
            Locale locale;

            if(localeKey == null){
                // default to Struts locale
                locale = (Locale) pageContext.findAttribute("org.apache.struts.action.LOCALE");
                if(locale == null){
                    locale = pageContext.getRequest().getLocale();
                }// end if
            }else{
                locale = (Locale) pageContext.findAttribute(localeKey);
            }// end if
            mrDisplayerInstance.setLocale(locale);

            if(rb != null){
                mrDisplayerInstance.setMessageResources(rb);
            }else{
                Object resources = pageContext.findAttribute(bundleKey);

                if(resources == null){
                    // try a simple ResourceBundle
                    try{
                        rb = ResourceBundle.getBundle(bundleKey, locale);
                        mrDisplayerInstance.setMessageResources(rb);
                    }catch(MissingResourceException mre){
                        log.error(mre.getMessage());
                    }// end try
                }else{
                    mrDisplayerInstance.setMessageResources(resources);
                }// end else
            }// end else
        }// end if

        displayerInstance.setConfig(config);
        if(id != null){
            pageContext.setAttribute("menuId", id);
        }// end if

        displayerInstance.init(pageContext, displayerMapping);
        displayerInstance.setPermissionsAdapter(permissionsAdapter);

        pageContext.setAttribute(DISPLAYER_KEY, displayerInstance);
        log.debug("Exiting ShamenUseMenuDisplayerTag.doStartTag");
        return (EVAL_BODY_INCLUDE);
    }// end method

    /**
     * @see net.sf.navigator.taglib.UseMenuDisplayerTag#getPermissionsAdapter()
     * @return PermissionsAdapter
     * @throws JspException
     *         if an exception occurred
     */
    protected PermissionsAdapter getPermissionsAdapter() throws JspException {
        PermissionsAdapter adapter = null;

        if(permissions != null){
            // If set to "rolesAdapter", then create automatically
            if(permissions.equalsIgnoreCase(ROLES_ADAPTER)){
                adapter = new RolesPermissionsAdapter((HttpServletRequest) pageContext.getRequest());
            }else{
                adapter = (PermissionsAdapter) pageContext.findAttribute(permissions);

                if(adapter == null){
                    throw new JspException(messages.getString("permissions.not.found"));
                }// end if
            }// end else
        }// end if

        return adapter;
    }// end method

    /**
     * (non-Javadoc)
     * 
     * @see net.sf.navigator.taglib.UseMenuDisplayerTag#doEndTag()
     * @return int
     * @throws JspException
     *         if an exception occurred
     */
    public int doEndTag() throws JspException {
        menuDisplayer.end(pageContext);
        pageContext.removeAttribute(DISPLAYER_KEY);
        pageContext.removeAttribute(PRIVATE_REPOSITORY);
        return (EVAL_PAGE);
    }// end method

    /**
     * (non-Javadoc)
     * 
     * @see net.sf.navigator.taglib.UseMenuDisplayerTag#release()
     */
    public void release() {
        if(log.isDebugEnabled()){
            log.debug("release() called");
        }// end if

        this.menuDisplayer = null;
        this.bundleKey = null;
        this.config = MenuDisplayer.DEFAULT_CONFIG;
        this.localeKey = null;
        this.name = null;
        this.menuDisplayer = null;
        this.repository = null;
        this.permissions = null;
        this.rb = null;
    }// end method

}// end class
