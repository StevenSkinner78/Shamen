/**
 * Licensed under the Artistic License; you may not use this file except in compliance with the License. You may obtain a copy of the License at http://displaytag.sourceforge.net/license.html THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package gov.doc.isu.shamen.taglib.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.taglib.exception.TablePropertiesLoadException;

/**
 * The properties used by the Table tags. The properties are loaded in the following order, in increasing order of priority. The locale of getInstance() is used to determine the locale of the property file to use; if the key required does not exist in the specified file, the key will be loaded from a more general property file.
 * <ol>
 * <li>First, from the TableTag.properties included with the DisplayTag distribution.</li>
 * <li>Then, from the file displaytag.properties, if it is present; these properties are intended to be set by the user for sitewide application. Messages are gathered according to the Locale of the property file.</li>
 * <li>Finally, if this class has a userProperties defined, all of the properties from that Properties object are copied in as well.</li>
 * </ol>
 * 
 * @author Fabrizio Giustina
 * @author rapruitt
 * @author Steven Skinner JCCC
 */
public final class TableProperties implements Cloneable {

    /**
     * logger.
     */
    private static Log log = LogFactory.getLog(TableProperties.class);

    /**
     * name of the default properties file name ("displaytag.properties").
     */
    public static final String DEFAULT_FILENAME = "shamenclient.properties"; //$NON-NLS-1$

    /**
     * The name of the local properties file that is searched for on the classpath. Settings in this file will override the defaults loaded from TableTag.properties.
     */
    public static final String LOCAL_PROPERTIES = "shamenclient"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.placement</code>.
     */
    public static final String PROPERTY_STRING_BANNER_PLACEMENT = "paging.banner.placement"; //$NON-NLS-1$

    /**
     * property <code>error.msg.invalid_page</code>.
     */
    public static final String PROPERTY_STRING_PAGING_INVALIDPAGE = "error.msg.invalid_page"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.item_name</code>.
     */
    public static final String PROPERTY_STRING_PAGING_ITEM_NAME = "paging.banner.item_name"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.items_name</code>.
     */
    public static final String PROPERTY_STRING_PAGING_ITEMS_NAME = "paging.banner.items_name"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.no_items_found</code>.
     */
    public static final String PROPERTY_STRING_PAGING_NOITEMS = "paging.banner.no_items_found"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.one_item_found</code>.
     */
    public static final String PROPERTY_STRING_PAGING_FOUND_ONEITEM = "paging.banner.one_item_found"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.all_items_found</code>.
     */
    public static final String PROPERTY_STRING_PAGING_FOUND_ALLITEMS = "paging.banner.all_items_found"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.some_items_found</code>.
     */
    public static final String PROPERTY_STRING_PAGING_FOUND_SOMEITEMS = "paging.banner.some_items_found"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.group_size</code>.
     */
    public static final String PROPERTY_INT_PAGING_GROUPSIZE = "paging.banner.group_size"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.onepage</code>.
     */
    public static final String PROPERTY_STRING_PAGING_BANNER_ONEPAGE = "paging.banner.onepage"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.first</code>.
     */
    public static final String PROPERTY_STRING_PAGING_BANNER_FIRST = "paging.banner.first"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.last</code>.
     */
    public static final String PROPERTY_STRING_PAGING_BANNER_LAST = "paging.banner.last"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.full</code>.
     */
    public static final String PROPERTY_STRING_PAGING_BANNER_FULL = "paging.banner.full"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.page.link</code>.
     */
    public static final String PROPERTY_STRING_PAGING_PAGE_LINK = "paging.banner.page.link"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.page.selected</code>.
     */
    public static final String PROPERTY_STRING_PAGING_PAGE_SELECTED = "paging.banner.page.selected"; //$NON-NLS-1$

    /**
     * property <code>paging.banner.page.separator</code>.
     */
    public static final String PROPERTY_STRING_PAGING_PAGE_SPARATOR = "paging.banner.page.separator"; //$NON-NLS-1$

    /**
     * Property <code>pagination.pagenumber.param</code>. If external pagination and sorting is used, it holds the name of the parameter used to hold the page number in generated links
     */
    public static final String PROPERTY_STRING_PAGINATION_PAGE_NUMBER_PARAM = "pagination.pagenumber.param"; //$NON-NLS-1$

    /**
     * Property <code>pagination.searchid.param</code>. If external pagination and sorting is used, it holds the name of the parameter used to hold the search ID in generated links
     */
    public static final String PROPERTY_STRING_PAGINATION_SEARCH_ID_PARAM = "pagination.searchid.param"; //$NON-NLS-1$

    /**
     * Property <code>pagination.sort.skippagenumber</code>. If external pagination and sorting is used, it determines if the current page number must be added in sort links or not. If this property is true, it means that each click on a generated sort link will re-sort the list, and go back to the default page number. If it is false, each click on a generated sort link will re-sort the list, and ask the current page number.
     */
    public static final String PROPERTY_BOOLEAN_PAGINATION_SKIP_PAGE_NUMBER_IN_SORT = "pagination.sort.skippagenumber"; //$NON-NLS-1$

    /**
     * property <code>expand.collapse.banner</code>.
     */
    public static final String PROPERTY_EXPAND_COLLAPSE_BANNER = "expand.collapse.banner"; //$NON-NLS-1$
    // </JBN>

    public static final String PROPERTY_STRING_URL_ADD_PARAMS = "url.add.params";

    /**
     * The userProperties are local, non-default properties; these settings override the defaults from displaytag.properties and TableTag.properties.
     */
    private static Properties userProperties = new Properties();

    /**
     * TableProperties for each locale are loaded as needed, and cloned for public usage.
     */
    private static Map prototypes = new HashMap();

    /**
     * Loaded properties (defaults from defaultProperties + custom from bundle).
     */
    private Properties properties;

    /**
     * The locale for these properties.
     */
    private Locale locale;

    /**
     * Loads default properties (TableTag.properties).
     * 
     * @return loaded properties
     * @throws TablePropertiesLoadException
     *         if default properties file can't be found
     */
    private static Properties loadBuiltInProperties() throws TablePropertiesLoadException {
        Properties defaultProperties = new Properties();

        try{
            InputStream is = TableProperties.class.getResourceAsStream(DEFAULT_FILENAME);
            if(is == null){
                throw new TablePropertiesLoadException("gov.doc.isu.shamen.taglib.properties.TableProperties", DEFAULT_FILENAME, null);
            }
            defaultProperties.load(is);
        }catch(IOException e){
            throw new TablePropertiesLoadException("gov.doc.isu.shamen.taglib.properties.TableProperties", DEFAULT_FILENAME, e);
        }

        return defaultProperties;
    }

    /**
     * Loads user properties (displaytag.properties) according to the given locale. User properties are not guarantee to exist, so the method can return <code>null</code> (no exception will be thrown).
     * 
     * @param locale
     *        requested Locale
     * @return loaded properties
     */
    private static ResourceBundle loadUserProperties(Locale locale) {
        ResourceBundle bundle = null;
        try{
            bundle = ResourceBundle.getBundle(LOCAL_PROPERTIES, locale);
        }catch(MissingResourceException e){
            // if no resource bundle is found, try using the context classloader
            try{
                bundle = ResourceBundle.getBundle(LOCAL_PROPERTIES, locale, Thread.currentThread().getContextClassLoader());
            }catch(MissingResourceException mre){
                if(log.isDebugEnabled()){
                    log.debug("Was not able to load a custom imptag.properties; " + mre.getMessage());
                }
            }
        }

        return bundle;
    }

    /**
     * Initialize a new TableProperties loading the default properties file and the user defined one. There is no caching used here, caching is assumed to occur in the getInstance factory method.
     * 
     * @param myLocale
     *        the locale we are in
     * @throws TablePropertiesLoadException
     *         for errors during loading of properties files
     */
    private TableProperties(Locale myLocale) throws TablePropertiesLoadException {
        this.locale = myLocale;
        // default properties will not change unless this class is reloaded
        Properties defaultProperties = loadBuiltInProperties();

        properties = new Properties(defaultProperties);
        addProperties(myLocale);

        // Now copy in the user properties (properties file set by calling setUserProperties()).
        // note setUserProperties() MUST BE CALLED before the first TableProperties instantation
        Enumeration keys = userProperties.keys();
        while(keys.hasMoreElements()){
            String key = (String) keys.nextElement();
            if(key != null){
                properties.setProperty(key, (String) userProperties.get(key));
            }
        }
    }

    /**
     * Try to load the properties from the local properties file, displaytag.properties, and merge them into the existing properties.
     * 
     * @param userLocale
     *        the locale from which the properties are to be loaded
     */
    private void addProperties(Locale userLocale) {
        ResourceBundle bundle = loadUserProperties(userLocale);

        if(bundle != null){
            Enumeration keys = bundle.getKeys();
            while(keys.hasMoreElements()){
                String key = (String) keys.nextElement();
                properties.setProperty(key, bundle.getString(key));
            }
        }
    }

    /**
     * Clones the properties as well.
     * 
     * @return a new clone of oneself
     */
    protected Object clone() {
        TableProperties twin;
        try{
            twin = (TableProperties) super.clone();
        }catch(CloneNotSupportedException e){
            // should never happen
            throw new UnhandledException(e);
        }
        twin.properties = (Properties) this.properties.clone();
        return twin;
    }

    /**
     * Returns a new TableProperties instance for the given locale.
     * 
     * @param request
     *        HttpServletRequest needed to extract the locale to use. If null the default locale will be used.
     * @return TableProperties instance
     */
    public static TableProperties getInstance(HttpServletRequest request) {
        Locale locale = Locale.getDefault();

        TableProperties props = (TableProperties) prototypes.get(locale);
        if(props == null){
            TableProperties lprops = new TableProperties(locale);
            prototypes.put(locale, lprops);
            props = lprops;
        }
        return (TableProperties) props.clone();
    }

    /**
     * Unload all cached properties. This will not clear properties set by by setUserProperties; you must clear those manually.
     */
    public static void clearProperties() {
        prototypes.clear();
    }

    /**
     * Local, non-default properties; these settings override the defaults from displaytag.properties and TableTag.properties. Please note that the values are copied in, so that multiple calls with non-overlapping properties will be merged, not overwritten. Note: setUserProperties() MUST BE CALLED before the first TableProperties instantation.
     * 
     * @param overrideProperties
     *        - The local, non-default properties
     */
    public static void setUserProperties(Properties overrideProperties) {
        // copy keys here, so that this can be invoked more than once from different sources.
        // if default properties are not yet loaded they will be copied in constructor
        Enumeration keys = overrideProperties.keys();
        while(keys.hasMoreElements()){
            String key = (String) keys.nextElement();
            if(key != null){
                userProperties.setProperty(key, (String) overrideProperties.get(key));
            }
        }
    }

    /**
     * The locale for which these properties are intended.
     * 
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_INVALIDPAGE</code> property.
     * 
     * @return String
     */
    public String getPagingInvalidPage() {
        return getProperty(PROPERTY_STRING_PAGING_INVALIDPAGE);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_ITEM_NAME</code> property.
     * 
     * @return String
     */
    public String getPagingItemName() {
        return getProperty(PROPERTY_STRING_PAGING_ITEM_NAME);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_ITEMS_NAME</code> property.
     * 
     * @return String
     */
    public String getPagingItemsName() {
        return getProperty(PROPERTY_STRING_PAGING_ITEMS_NAME);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_NOITEMS</code> property.
     * 
     * @return String
     */
    public String getPagingFoundNoItems() {
        return getProperty(PROPERTY_STRING_PAGING_NOITEMS);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_FOUND_ONEITEM</code> property.
     * 
     * @return String
     */
    public String getPagingFoundOneItem() {
        return getProperty(PROPERTY_STRING_PAGING_FOUND_ONEITEM);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_FOUND_ALLITEMS</code> property.
     * 
     * @return String
     */
    public String getPagingFoundAllItems() {
        return getProperty(PROPERTY_STRING_PAGING_FOUND_ALLITEMS);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_FOUND_SOMEITEMS</code> property.
     * 
     * @return String
     */
    public String getPagingFoundSomeItems() {
        return getProperty(PROPERTY_STRING_PAGING_FOUND_SOMEITEMS);
    }

    /**
     * Getter for the <code>PROPERTY_INT_PAGING_GROUPSIZE</code> property.
     * 
     * @return int
     */
    public int getPagingGroupSize() {
        // default size is 8
        return getIntProperty(PROPERTY_INT_PAGING_GROUPSIZE, 8);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_BANNER_ONEPAGE</code> property.
     * 
     * @return String
     */
    public String getPagingBannerOnePage() {
        return getProperty(PROPERTY_STRING_PAGING_BANNER_ONEPAGE);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_BANNER_FIRST</code> property.
     * 
     * @return String
     */
    public String getPagingBannerFirst() {
        return getProperty(PROPERTY_STRING_PAGING_BANNER_FIRST);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_BANNER_LAST</code> property.
     * 
     * @return String
     */
    public String getPagingBannerLast() {
        return getProperty(PROPERTY_STRING_PAGING_BANNER_LAST);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_BANNER_FULL</code> property.
     * 
     * @return String
     */
    public String getPagingBannerFull() {
        return getProperty(PROPERTY_STRING_PAGING_BANNER_FULL);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_PAGE_LINK</code> property.
     * 
     * @return String
     */
    public String getPagingPageLink() {
        return getProperty(PROPERTY_STRING_PAGING_PAGE_LINK);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_PAGE_SELECTED</code> property.
     * 
     * @return String
     */
    public String getPagingPageSelected() {
        return getProperty(PROPERTY_STRING_PAGING_PAGE_SELECTED);
    }

    /**
     * Getter for the <code>PROPERTY_STRING_PAGING_PAGE_SPARATOR</code> property.
     * 
     * @return String
     */
    public String getPagingPageSeparator() {
        return getProperty(PROPERTY_STRING_PAGING_PAGE_SPARATOR);
    }

    /**
     * Return the banner for expand and collapse all when sub row is used in table.
     * 
     * @return String banner
     */
    public String getExpandCollapseBanner() {
        return getProperty(PROPERTY_EXPAND_COLLAPSE_BANNER);
    }

    /**
     * Should paging banner be added before the table?
     * 
     * @return boolean
     */
    public boolean getAddPagingBannerTop() {
        String placement = getProperty(PROPERTY_STRING_BANNER_PLACEMENT);
        return "top".equals(placement) || "both".equals(placement); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Should paging banner be added after the table?
     * 
     * @return boolean
     */
    public boolean getAddPagingBannerBottom() {
        String placement = getProperty(PROPERTY_STRING_BANNER_PLACEMENT);
        return "bottom".equals(placement) || "both".equals(placement); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String getPaginationPageNumberParam() {
        String result = getProperty(PROPERTY_STRING_PAGINATION_PAGE_NUMBER_PARAM);
        if(result == null){
            result = "page";
        }
        return result;
    }

    public String getPaginationSearchIdParam() {
        String result = getProperty(PROPERTY_STRING_PAGINATION_SEARCH_ID_PARAM);
        if(result == null){
            result = "searchId";
        }
        return result;
    }

    public boolean getPaginationSkipPageNumberInSort() {
        String s = getProperty(PROPERTY_BOOLEAN_PAGINATION_SKIP_PAGE_NUMBER_IN_SORT);
        if(s == null){
            return true;
        }else{
            return getBooleanProperty(PROPERTY_BOOLEAN_PAGINATION_SKIP_PAGE_NUMBER_IN_SORT);
        }
    }

    /**
     * @return The {@code String} value of the default.button.style property.
     */
    public String getDefaultButtonStyle() {
        return getProperty("default.button.style");
    }// end getDefaultButtonStyle

    /**
     * @return The {@code String} value of the default.label.style property.
     */
    public String getDefaultLabelStyle() {
        return getProperty("default.label.style");
    }// end getDefaultLabelStyle

    /**
     * @return The {@code String} value of the default.input.style property.
     */
    public String getDefaultInputStyle() {
        return getProperty("default.input.style");
    }// end getDefaultInputStyle

    /**
     * @return The {@code String} value of the legend.list.style property.
     */
    public String getLegendListStyle() {
        return getProperty("legend.list.style");
    }// end getLegendListStyle

    /**
     * @return The {@code String} value of the legend.list.item.style property.
     */
    public String getLegendListItemStyle() {
        return getProperty("legend.list.item.style");
    }// end getLegendListItemStyle

    /**
     * @return The {@code String} value of the default.legend.icon property.
     */
    public String getDefaultLegendIcon() {
        return getProperty("default.legend.icon");
    }// end getDefaultLegendIcon

    /**
     * @return The {@code String} value of the default.caret.icon property.
     */
    public String getDefaultCaretIcon() {
        return getProperty("default.caret.icon");
    }// end getDefaultCaretIcon

    public Map<String, Object> getAddParams() {
        Map<String, Object> result = null;
        String list = getProperty(PROPERTY_STRING_URL_ADD_PARAMS);
        if(list != null){
            result = new HashMap<String, Object>();
            String[] hold = list.split(",");
            for(int i = 0;i < hold.length;i++){
                String[] val = hold[i].split(":");
                if(val.length > 1){
                    result.put(val[0], val[1]);
                }

            }
        }
        return result;
    }

    /**
     * Reads a String property.
     * 
     * @param key
     *        property name
     * @return property value or <code>null</code> if property is not found
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Sets a property.
     * 
     * @param key
     *        property name
     * @param value
     *        property value
     */
    public void setProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    /**
     * Reads a boolean property.
     * 
     * @param key
     *        property name
     * @return boolean <code>true</code> if the property value is "true", <code>false</code> for any other value.
     */
    private boolean getBooleanProperty(String key) {
        return Boolean.TRUE.toString().equals(getProperty(key));
    }

    /**
     * Reads an int property.
     * 
     * @param key
     *        property name
     * @param defaultValue
     *        default value returned if property is not found or not a valid int value
     * @return property value
     */
    private int getIntProperty(String key, int defaultValue) {
        try{
            return Integer.parseInt(getProperty(key));
        }catch(NumberFormatException e){
            // Don't care, use default
            log.warn("Invalid value for " + key + " property: value=" + getProperty(key) + "; using default " + new Integer(defaultValue));
        }

        return defaultValue;
    }
}
