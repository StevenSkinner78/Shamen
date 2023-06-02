/**
 * @(#)HtmlAttributeMap.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                           CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                           acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is used to model a map of attributes to apply to an HTML tag.
 *
 * @author Joseph Burris JCCC, jsb000is
 */
public class HtmlAttributeMap extends HashMap<Object, Object> {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * This default constructor is used to instantiate this class.
     */
    public HtmlAttributeMap() {
        super();
    }// end constructor

    /*
     * (non-Javadoc)
     * @see java.util.AbstractMap#toString()
     */
    @Override
    public String toString() {
        if(size() == 0){
            return "";
        }// end if
        StringBuffer buffer = new StringBuffer(size() * 30);
        Map.Entry<Object, Object> entry;
        Iterator<Map.Entry<Object, Object>> iter = entrySet().iterator();
        while(iter.hasNext()){
            entry = iter.next();
            buffer.append(' ').append(entry.getKey()).append('=').append('"').append(entry.getValue()).append('"');
        }// end while
        return buffer.toString();
    }// end toString
}// end class
