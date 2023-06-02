/**
 * @(#)Entities.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED CONDITIONS,
 *                   REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You acknowledge that this
 *                   software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.taglib.util;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to model XML and HTML entity values.
 *
 * @author Joseph Burris JCCC, jsb000is
 */
public class Entities {
    private static final String[][] BASIC_ARRAY = {{"quot", "34"}, {"amp", "38"}, {"lt", "60"}, {"gt", "62"}};
    private static final String[][] APOS_ARRAY = {{"apos", "39"}};
    private static final String[][] ISO8859_1_ARRAY = {{"nbsp", "160"}, {"iexcl", "161"}, {"cent", "162"}, {"pound", "163"}, {"curren", "164"}, {"yen", "165"}, {"brvbar", "166"}, {"sect", "167"}, {"uml", "168"}, {"copy", "169"}, {"ordf", "170"}, {"laquo", "171"}, {"not", "172"}, {"shy", "173"}, {"reg", "174"}, {"macr", "175"}, {"deg", "176"}, {"plusmn", "177"}, {"sup2", "178"}, {"sup3", "179"}, {"acute", "180"}, {"micro", "181"}, {"para", "182"}, {"middot", "183"}, {"cedil", "184"}, {"sup1", "185"}, {"ordm", "186"}, {"raquo", "187"}, {"frac14", "188"}, {"frac12", "189"}, {"frac34", "190"}, {"iquest", "191"}, {"Agrave", "192"}, {"Aacute", "193"}, {"Acirc", "194"}, {"Atilde", "195"}, {"Auml", "196"}, {"Aring", "197"}, {"AElig", "198"}, {"Ccedil", "199"}, {"Egrave", "200"}, {"Eacute", "201"}, {"Ecirc", "202"}, {"Euml", "203"}, {"Igrave", "204"}, {"Iacute", "205"}, {"Icirc", "206"}, {"Iuml", "207"}, {"ETH", "208"}, {"Ntilde", "209"}, {"Ograve", "210"}, {"Oacute", "211"}, {"Ocirc", "212"},
            {"Otilde", "213"}, {"Ouml", "214"}, {"times", "215"}, {"Oslash", "216"}, {"Ugrave", "217"}, {"Uacute", "218"}, {"Ucirc", "219"}, {"Uuml", "220"}, {"Yacute", "221"}, {"THORN", "222"}, {"szlig", "223"}, {"agrave", "224"}, {"aacute", "225"}, {"acirc", "226"}, {"atilde", "227"}, {"auml", "228"}, {"aring", "229"}, {"aelig", "230"}, {"ccedil", "231"}, {"egrave", "232"}, {"eacute", "233"}, {"ecirc", "234"}, {"euml", "235"}, {"igrave", "236"}, {"iacute", "237"}, {"icirc", "238"}, {"iuml", "239"}, {"eth", "240"}, {"ntilde", "241"}, {"ograve", "242"}, {"oacute", "243"}, {"ocirc", "244"}, {"otilde", "245"}, {"ouml", "246"}, {"divide", "247"}, {"oslash", "248"}, {"ugrave", "249"}, {"uacute", "250"}, {"ucirc", "251"}, {"uuml", "252"}, {"yacute", "253"}, {"thorn", "254"}, {"yuml", "255"}};
    private static final String[][] HTML40_ARRAY = {{"fnof", "402"}, {"Alpha", "913"}, {"Beta", "914"}, {"Gamma", "915"}, {"Delta", "916"}, {"Epsilon", "917"}, {"Zeta", "918"}, {"Eta", "919"}, {"Theta", "920"}, {"Iota", "921"}, {"Kappa", "922"}, {"Lambda", "923"}, {"Mu", "924"}, {"Nu", "925"}, {"Xi", "926"}, {"Omicron", "927"}, {"Pi", "928"}, {"Rho", "929"}, {"Sigma", "931"}, {"Tau", "932"}, {"Upsilon", "933"}, {"Phi", "934"}, {"Chi", "935"}, {"Psi", "936"}, {"Omega", "937"}, {"alpha", "945"}, {"beta", "946"}, {"gamma", "947"}, {"delta", "948"}, {"epsilon", "949"}, {"zeta", "950"}, {"eta", "951"}, {"theta", "952"}, {"iota", "953"}, {"kappa", "954"}, {"lambda", "955"}, {"mu", "956"}, {"nu", "957"}, {"xi", "958"}, {"omicron", "959"}, {"pi", "960"}, {"rho", "961"}, {"sigmaf", "962"}, {"sigma", "963"}, {"tau", "964"}, {"upsilon", "965"}, {"phi", "966"}, {"chi", "967"}, {"psi", "968"}, {"omega", "969"}, {"thetasym", "977"}, {"upsih", "978"}, {"piv", "982"}, {"bull", "8226"},
            {"hellip", "8230"}, {"prime", "8242"}, {"Prime", "8243"}, {"oline", "8254"}, {"frasl", "8260"}, {"weierp", "8472"}, {"image", "8465"}, {"real", "8476"}, {"trade", "8482"}, {"alefsym", "8501"}, {"larr", "8592"}, {"uarr", "8593"}, {"rarr", "8594"}, {"darr", "8595"}, {"harr", "8596"}, {"crarr", "8629"}, {"lArr", "8656"}, {"uArr", "8657"}, {"rArr", "8658"}, {"dArr", "8659"}, {"hArr", "8660"}, {"forall", "8704"}, {"part", "8706"}, {"exist", "8707"}, {"empty", "8709"}, {"nabla", "8711"}, {"isin", "8712"}, {"notin", "8713"}, {"ni", "8715"}, {"prod", "8719"}, {"sum", "8721"}, {"minus", "8722"}, {"lowast", "8727"}, {"radic", "8730"}, {"prop", "8733"}, {"infin", "8734"}, {"ang", "8736"}, {"and", "8743"}, {"or", "8744"}, {"cap", "8745"}, {"cup", "8746"}, {"int", "8747"}, {"there4", "8756"}, {"sim", "8764"}, {"cong", "8773"}, {"asymp", "8776"}, {"ne", "8800"}, {"equiv", "8801"}, {"le", "8804"}, {"ge", "8805"}, {"sub", "8834"}, {"sup", "8835"}, {"sube", "8838"}, {"supe", "8839"},
            {"oplus", "8853"}, {"otimes", "8855"}, {"perp", "8869"}, {"sdot", "8901"}, {"lceil", "8968"}, {"rceil", "8969"}, {"lfloor", "8970"}, {"rfloor", "8971"}, {"lang", "9001"}, {"rang", "9002"}, {"loz", "9674"}, {"spades", "9824"}, {"clubs", "9827"}, {"hearts", "9829"}, {"diams", "9830"}, {"OElig", "338"}, {"oelig", "339"}, {"Scaron", "352"}, {"scaron", "353"}, {"Yuml", "376"}, {"circ", "710"}, {"tilde", "732"}, {"ensp", "8194"}, {"emsp", "8195"}, {"thinsp", "8201"}, {"zwnj", "8204"}, {"zwj", "8205"}, {"lrm", "8206"}, {"rlm", "8207"}, {"ndash", "8211"}, {"mdash", "8212"}, {"lsquo", "8216"}, {"rsquo", "8217"}, {"sbquo", "8218"}, {"ldquo", "8220"}, {"rdquo", "8221"}, {"bdquo", "8222"}, {"dagger", "8224"}, {"Dagger", "8225"}, {"permil", "8240"}, {"lsaquo", "8249"}, {"rsaquo", "8250"}, {"euro", "8364"}};
    public static final Entities XML = new Entities();
    public static final Entities HTML40 = new Entities();
    private LookupEntityMap map = new LookupEntityMap();

    static{
        XML.addEntities(BASIC_ARRAY);
        XML.addEntities(APOS_ARRAY);
        HTML40.addEntities(BASIC_ARRAY);
        HTML40.addEntities(ISO8859_1_ARRAY);
        HTML40.addEntities(HTML40_ARRAY);
    }// end static block

    /**
     * This nested class is used to customize an map of values to refer to.
     *
     * @author Joseph Burris JCCC, jsb000is
     */
    static class LookupEntityMap {
        private Map<String, Integer> mapNameToValue = new HashMap<String, Integer>();
        private IntHashMap mapValueToName = new IntHashMap();
        private String[] lookupTable;
        private int lookupTableSize = 256;

        /**
         * This method is used to add a value to the map.
         *
         * @param name
         *        The name of the map to add.
         * @param value
         *        The value of the map to add.
         */
        public void add(String name, int value) {
            this.mapNameToValue.put(name, new Integer(value));
            this.mapValueToName.put(value, name);
        }// end add

        /**
         * This method is used to get the name of the parameter value.
         *
         * @param value
         *        The value to look up.
         * @return The name of the value in the map.
         */
        public String name(int value) {
            if(value < this.lookupTableSize){
                return lookupTable()[value];
            }// end if
            return ((String) this.mapValueToName.get(value));
        }// end name

        /**
         * @return The values of the map in an array.
         */
        private String[] lookupTable() {
            if(this.lookupTable == null){
                this.lookupTable = new String[this.lookupTableSize];
                for(int i = 0;i < this.lookupTableSize; ++i){
                    this.lookupTable[i] = ((String) this.mapValueToName.get(i));
                }// end for
            }// end if
            return this.lookupTable;
        }// end lookupTable

        /**
         * This method is used to get the value of the parameter name.
         *
         * @param name
         *        The name to look up.
         * @return The value fo the name in the map.
         */
        public int value(String name) {
            Object value = this.mapNameToValue.get(name);
            if(value == null){
                return -1;
            }// end if
            return ((Integer) value).intValue();
        }// end value
    }// end class

    /**
     * This method is used to add a two-dimensional array of values to the map.
     *
     * @param entityArray
     *        The array to add.
     */
    public void addEntities(String[][] entityArray) {
        for(int i = 0, j = entityArray.length;i < j; ++i){
            this.map.add(entityArray[i][0], Integer.parseInt(entityArray[i][1]));
        }// end for
    }// end addEntities

    /**
     * This method is used to escape characters in the parameter {@code String}.
     *
     * @param str
     *        The value to be escaped.
     * @return The escaped value.
     */
    public String escape(String str) {
        StringBuffer buf = new StringBuffer(str.length() * 2);
        for(int i = 0;i < str.length(); ++i){
            char ch = str.charAt(i);
            String entityName = this.map.name(ch);
            if(entityName == null){
                if(ch > ''){
                    int intValue = ch;
                    buf.append("&#");
                    buf.append(intValue);
                    buf.append(';');
                }else{
                    buf.append(ch);
                }// end if/else
            }else{
                buf.append('&');
                buf.append(entityName);
                buf.append(';');
            }// end if/else
        }// end for
        return buf.toString();
    }// end escape

    /**
     * This method is used to escape characters in the parameter {@code String} by adding them to the parameter {@link Writer}.
     *
     * @param writer
     *        The {@link Writer} to hold the escaped value.
     * @param str
     *        The value to be escaped.
     * @throws IOException
     *         An exception if the method logic fails.
     */
    public void escape(Writer writer, String str) throws IOException {
        int len = str.length();
        for(int i = 0;i < len; ++i){
            char c = str.charAt(i);
            String entityName = this.map.name(c);
            if(entityName == null){
                if(c > ''){
                    writer.write("&#");
                    writer.write(Integer.toString(c, 10));
                    writer.write(59);
                }else{
                    writer.write(c);
                }// end if/else
            }else{
                writer.write(38);
                writer.write(entityName);
                writer.write(59);
            }// end if/else
        }// end for
    }// end escape
}// end class

/**
 * This nested class is used to hold a map of {@code int} keys and {@code Object} values.
 *
 * @author Joseph Burris JCCC, jsb000is
 */
class IntHashMap {
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;

    /**
     * This nested class is used to model an array of values.
     *
     * @author Joseph Burris JCCC, jsb000is
     */
    private static class Entry {
        private int hash;
        // private int key;
        private Object value;
        private Entry next;

        /**
         * This overloaded constructor is used to instantiate this class.
         *
         * @param hash
         *        The hash value.
         * @param value
         *        The {@code Object} value.
         * @param next
         *        The next {@link Entry} value.
         */
        protected Entry(int hash, /* int key, */Object value, Entry next) {
            this.hash = hash;
            // this.key = key;
            this.value = value;
            this.next = next;
        }// end constructor
    }// end class

    /**
     * This default constructor is used to instantiate this class.
     */
    public IntHashMap() {
        super();
        this.loadFactor = 0.75F;
        this.table = new Entry[20];
        this.threshold = (int) (20 * loadFactor);
    }// end constructor

    /**
     * This method is used to get the value from the map for the parameter key.
     *
     * @param key
     *        The map key to search by.
     * @return The map value for the key.
     */
    public Object get(int key) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for(Entry e = tab[index];e != null;e = e.next){
            if(e.hash == hash){
                return e.value;
            }// end if
        }// end for
        return null;
    }// end get

    /**
     * This method is used to put an entry in the map.
     *
     * @param key
     *        The map key to be entered/updated.
     * @param value
     *        The map value for the key.
     * @return The entered {@code Object}.
     */
    public Object put(int key, Object value) {
        Entry[] tab = this.table;
        int hash = key;
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for(Entry e = tab[index];e != null;e = e.next){
            if(e.hash == hash){
                Object old = e.value;
                e.value = value;
                return old;
            }// end if
        }// end for
        if(this.count >= this.threshold){
            rehash();
            tab = this.table;
            index = (hash & 0x7FFFFFFF) % tab.length;
        }// end if
        Entry e = new Entry(hash, /* key, */value, tab[index]);
        tab[index] = e;
        this.count += 1;
        return null;
    }// end put

    /**
     * This method is used to re-hash the map values.
     */
    private void rehash() {
        int oldCapacity = this.table.length;
        Entry[] oldMap = this.table;
        int newCapacity = oldCapacity * 2 + 1;
        Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int) (newCapacity * this.loadFactor);
        this.table = newMap;
        for(int i = oldCapacity;i-- > 0;){
            for(Entry old = oldMap[i];old != null;){
                Entry e = old;
                old = old.next;
                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = newMap[index];
                newMap[index] = e;
            }// end for
        }// end for
    }// end rehash
}// end class
