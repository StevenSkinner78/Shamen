/**
 * Licensed under the Artistic License; you may not use this file except in compliance with the License. You may obtain a copy of the License at http://displaytag.sourceforge.net/license.html THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package gov.doc.isu.shamen.taglib.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.doc.isu.shamen.util.ShamenClientUtil;

/**
 * Interface representing an URI (the href parameter of an &lt;a> tag). Provides methods to insert new parameters. It doesn't support multiple parameter values
 * 
 * @author Fabrizio Giustina
 * @author Steven Skinner JCCC
 */
public class Href implements Cloneable, Serializable {

    /**
    *
    */
    private static final long serialVersionUID = 1L;
    private static Log log = LogFactory.getLog("gov.doc.isu.imp.models.Href");
    private String url;
    private Map<String, String> parameters;
    private String anchor;

    /**
     * This overloaded constructor is used to instantiate this class.
     *
     * @param baseUrl
     *        The URL to be used as the location for the hyper-link.
     */
    public Href(String baseUrl) {
        super();
        parameters = new HashMap<String, String>();
        setFullUrl(baseUrl);
    }// end constructor

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(30);
        buffer.append(url);
        if(parameters.size() > 0){
            buffer.append('?');
            String key, value;
            Iterator<String> iter = parameters.keySet().iterator();
            while(iter.hasNext()){
                key = iter.next();
                value = parameters.get(key);
                buffer.append(key).append('=').append(value);
                if(iter.hasNext()){
                    buffer.append("&amp;");
                }// end if
            }// end while
        }// end if
        if(anchor != null){
            buffer.append('#');
            buffer.append(anchor);
        }// end if
        return buffer.toString();
    }// end toString

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((anchor == null) ? 0 : anchor.hashCode());
        result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }// end hashCode

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }// end if
        if(obj == null){
            return false;
        }// end if
        if(!(obj instanceof Href)){
            return false;
        }// end if
        Href other = (Href) obj;
        if(anchor == null){
            if(other.anchor != null){
                return false;
            }// end if
        }else if(!anchor.equals(other.anchor)){
            return false;
        }// end if/else
        if(parameters == null){
            if(other.parameters != null){
                return false;
            }// end if
        }else if(!parameters.equals(other.parameters)){
            return false;
        }// end if/else
        if(url == null){
            if(other.url != null){
                return false;
            }// end if
        }else if(!url.equals(other.url)){
            return false;
        }// end if/else
        return true;
    }// end equals

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        Href href;
        try{
            href = (Href) super.clone();
        }catch(CloneNotSupportedException e){
            throw new RuntimeException("gov.doc.isu.shamen.taglib.util.Href", e.getCause());
        }// end try/catch
        href.parameters = new HashMap<String, String>(parameters);
        return href;
    }// end clone

    /**
     * This method is used to set the location for the hyper-link as well as attach parameters to be carried by the URL.
     *
     * @param baseUrl
     *        The URL to be used as the location for the hyper-link.
     */
    public void setFullUrl(String baseUrl) {
        baseUrl = baseUrl.replace("%26", "&");
        url = null;
        anchor = null;
        int anchorposition = baseUrl.indexOf('#');
        String noAnchorUrl;
        if(anchorposition != -1){
            noAnchorUrl = baseUrl.substring(0, anchorposition);
            anchor = baseUrl.substring(anchorposition + 1);
        }else{
            noAnchorUrl = baseUrl;
        }// end if/else
        if(noAnchorUrl.indexOf('?') == -1){
            url = noAnchorUrl;
            return;
        }// end if
        StringTokenizer tokenizer = new StringTokenizer(noAnchorUrl, "?");
        url = (baseUrl.startsWith("?")) ? "" : tokenizer.nextToken();
        if(!tokenizer.hasMoreTokens()){
            return;
        }// end if
        String[] keyValue;
        String key, value;
        for(StringTokenizer paramTokenizer = new StringTokenizer(tokenizer.nextToken(), "&");paramTokenizer.hasMoreTokens();){
            keyValue = splitWorker(paramTokenizer.nextToken(), '=');
            key = escapeHtml(keyValue[0]);
            value = keyValue.length <= 1 ? "" : escapeHtml(keyValue[1]);
            if(!parameters.containsKey(key)){
                parameters.put(key, value);
            }// end if/else
        }// end for
    }// end setFullUrl

    /**
     * This method is used to split the parameter {@code String} using the parameter separator character.
     *
     * @param str
     *        The {@code String} to split.
     * @param separatorChar
     *        The {@code char} to split on.
     * @return A {@code String[]} of values.
     */
    private String[] splitWorker(String str, char separatorChar) {
        if(str == null){
            return null;
        }// end if
        int len = str.length();
        if(len == 0){
            return ShamenClientUtil.EMPTY_STRING_ARRAY;
        }// end if
        List<String> list = new ArrayList<String>();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while(i < len){
            if(str.charAt(i) == separatorChar){
                if(match){
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }// end if
                start = ++i;
            }else{
                lastMatch = false;
                match = true;
                ++i;
            }// end if/else
        }// end while
        if((match) || (lastMatch)){
            list.add(str.substring(start, i));
        }// end if
        return (list.toArray(new String[list.size()]));
    }// end splitWorker

    /**
     * This method is used to add a parameter to the map for the hyper-link.
     *
     * @param name
     *        The name of the parameter.
     * @param value
     *        The value of the parameter.
     * @return The current instance of this class.
     */
    public Href addParameter(String name, Object value) {
        parameters.put(name, ((value == null) ? null : value.toString()));
        return this;
    }// end addParameter

    /**
     * This method is used to add a parameter to the map for the hyper-link.
     *
     * @param name
     *        The name of the parameter.
     * @param value
     *        The value of the parameter.
     * @return The current instance of this class.
     */
    public Href addParameter(String name, int value) {
        parameters.put(name, String.valueOf(value));
        return this;
    }// end addParameter

    /**
     * This method is used to remove a parameter from the map.
     *
     * @param name
     *        The name of the parameter to remove.
     */
    public void removeParameter(String name) {
        parameters.remove(escapeHtml(name));
    }// end removeParameter

    /**
     * @return A copied instance of the parameters set for this instance of the class.
     */
    public Map<String, Object> getParameterMap() {
        Map<String, Object> copyMap = new HashMap<String, Object>(parameters.size());
        copyMap.putAll(parameters);
        return copyMap;
    }// end getParameterMap

    /**
     * This method is used to add the parameters from one map to those for the instance of this class.
     *
     * @param parameterMap
     *        The map of parameters to add.
     */
    public void setParameterMap(Map<String, String> parameterMap) {
        parameters = new HashMap<String, String>(parameterMap.size());
        String key, value;
        Iterator<String> iter = parameterMap.keySet().iterator();
        while(iter.hasNext()){
            key = iter.next();
            key = escapeHtml(key);
            if(!parameters.containsKey(key)){
                value = parameterMap.get(key);
                if(value != null){
                    value = escapeHtml(value.toString());
                }// end if
                parameters.put(key, value);
            }// end if
        }// end while
    }// end setParameterMap

    /**
     * @return The URL for this hyper-link.
     */
    public String getBaseUrl() {
        return url;
    }// end getBaseUrl

    /**
     * This method is used to escape any HTML tags found in the parameter {@code String}.
     *
     * @param str
     *        The value to escape.
     * @return The escaped {@code String}.
     */
    private String escapeHtml(String str) {
        if(str == null){
            return null;
        }// end if
        try{
            StringWriter writer = new StringWriter((int) (str.length() * 1.5D));
            Entities.HTML40.escape(writer, str);
            return writer.toString();
        }catch(IOException e){
            log.warn("An exception occurred while escaping HTML characters in value: " + String.valueOf(str), e);
        }// end try/catch
        return null;
    }// end escapeHtml

}
