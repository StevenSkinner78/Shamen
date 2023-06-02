/**
 * @(#)ShamenApplicationCipher.java 1.0.0 Copyright (c) 2018 Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit All Rights Reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: -Redistribution of source code must retain the above copyright notice, this list of conditions and the following disclaimer. -Redistribution in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of Missouri Department of Corrections, Jefferson City Correctional Center, Information Systems Unit or the names of contributors may be used to endorse or promote products derived from this software without specific prior written permission. This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR IMPLIED
 *                                  CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF MISSOURI DEPARTMENT OF CORRECTIONS, JEFFERSON CITY CORRECTIONAL CENTER, INFORMATION SYSTEMS UNIT HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. You
 *                                  acknowledge that this software is not designed, licensed or intended for use in the design, construction, operation or maintenance of any nuclear facility.
 */
package gov.doc.isu.shamen.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.ibm.ws.util.Base64;

/**
 * This class handles encryption and decryption for Shamen JMS messages. It uses a org.jasypt.encryption.pbe.StandardPBEStringEncryptor encryption and a generated Pseudo Random Number Generator that is hashed as its password.
 * 
 * @see java.security.MessageDigest
 * @see org.jasypt.encryption.pbe.StandardPBEStringEncryptor
 * @author <strong>Shane Duncan</strong> 4/14/2016
 */
public class ShamenApplicationCipher {

    private String password;
    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.core.ShamenApplicationCipher";
    private static Log log = LogFactory.getLog(MY_CLASS_NAME);
    private static final String PASSWORD_DEFAULT = "#superMAN%IS*FROM&krypton+";
    // Encryption algorithm for application message encryption.
    private static final String ENCRYPTION_ALGORITHM = "PBEWithMD5AndDES";

    /**
     * Constructor that instantiates cipher with provided password.
     * 
     * @param password
     *        rolling encryption password
     */
    public ShamenApplicationCipher(String password) {
        super();
        log.debug("Entering method ShamenApplicationCipher");
        this.setPassword(password);
        // create the encryptor here. it's better that it blow up now rather than later if there is a problem.
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        log.debug("Set up the password for this Cipher.");
        encryptor.setPassword(password);
        encryptor.setAlgorithm(ENCRYPTION_ALGORITHM);
        log.debug("Exiting method ShamenApplicationCipher");
    }// end ShamenApplicationCipher

    /**
     * Constructor that allows cipher to instantiate with default password.
     * 
     * @throws Exception
     *         when algorithm is not recognized.
     */
    public ShamenApplicationCipher() throws Exception {
        super();
        log.debug("Entering method ShamenApplicationCipher");
        MessageDigest md;

        try{
            log.debug("Generate a default password for this application.");
            md = MessageDigest.getInstance("SHA-1");
            md.update(PASSWORD_DEFAULT.getBytes());
            this.setPassword(new String(Base64.encode(md.digest())));
        }catch(NoSuchAlgorithmException e){
            log.error("Unable to establish default password hashing.");
            throw (e);
        }// end try-catch
        log.info("Default password was successfully generated.");
        // create the encryptor here. it's better that it blow up now rather than later if there is a problem.
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(ENCRYPTION_ALGORITHM);
        log.debug("Exiting method ShamenApplicationCipher");
    }// end ShamenApplicationCipher

    /**
     * This method will encrypt a string of raw text using the already loaded password.
     * 
     * @param rawText
     *        text to encrypt
     * @return encrypted text
     */
    public String encrypt(String rawText) {
        log.debug("Entering method encrypt");
        log.trace("This method will encrypt a string of raw text using the already loaded password.");
        String encryptedText = null;
        log.debug("Initialize the encryptor.");
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(ENCRYPTION_ALGORITHM);
        log.debug("Encrypt the raw text.");
        encryptedText = encryptor.encrypt(rawText);
        log.debug("Text was successfully encrypted.");
        log.debug("Exiting method encrypt");
        return encryptedText;
    }// end encrypt

    /**
     * This method decrypts a string of encrypted text using the already loaded password.
     * 
     * @param encryptedText
     *        text to encrypt
     * @return decrypted text
     */
    public String decrypt(String encryptedText) {
        log.debug("Entering method decrypt");
        log.trace("This method decrypts a string of encrypted text using the already loaded password.");
        log.debug("Initialize the decryptor.");
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm(ENCRYPTION_ALGORITHM);
        log.debug("Decrypt the encrypted text.");
        String decryptedText = encryptor.decrypt(encryptedText);
        log.debug("Text was successfully decrypted.");
        log.debug("Exiting method decrypt");
        return decryptedText;
    }// end decrypt

    /**
     * This method encrypts an entire <code>HashMap</code> using the already loaded password.
     * 
     * @param rawMap
     *        map to encrypt
     * @return encrypted HashMap
     */
    public HashMap<String, String> encryptApplicationMap(HashMap<String, String> rawMap) {
        log.debug("Entering method encryptApplicationMap");
        log.trace("This method encrypts an entire HashMap using the already loaded password.");
        HashMap<String, String> encryptedMap = new HashMap<String, String>();
        Set<String> keys = rawMap.keySet();
        log.info("Loop through the map and encrypt its keys and values.");
        for(Iterator<String> iterator = keys.iterator();iterator.hasNext();){
            String key = (String) iterator.next();
            if(rawMap.containsKey(key)){
                encryptedMap.put(encrypt(key), encrypt(rawMap.get(key)));
            }// end if
        }// end if
        log.info("Successfully encrypted the map.");
        log.debug("Exiting method encryptApplicationMap");
        return encryptedMap;
    }// end encryptApplicationMap

    /**
     * This method decrypts the entire <code>HashMap</code> using the already loaded password.
     * 
     * @param encryptedMap
     *        encrypted HashMap to decrypt.
     * @return decrypted HashMap
     */
    public HashMap<String, String> decryptApplicationMap(HashMap<String, String> encryptedMap) {
        log.debug("Entering method decryptApplicationMap");
        log.trace("This method decrypts the entire HashMap using the already loaded password.");
        HashMap<String, String> decryptedMap = new HashMap<String, String>();
        Set<String> keys = encryptedMap.keySet();
        log.info("Loop through the map and decrypt its keys and values.");
        for(Iterator<String> iterator = keys.iterator();iterator.hasNext();){
            String key = (String) iterator.next();
            if(encryptedMap.containsKey(key)){
                decryptedMap.put(decrypt(key), decrypt(encryptedMap.get(key)));
            }// end if
        }// end if
        log.info("Successfully decrypted the map.");
        log.debug("Exiting method decryptApplicationMap");
        return decryptedMap;
    }// end decryptApplicationMap

    /**
     * This method decrypts the entire <code>HashMap</code> using the already loaded password.
     * 
     * @param encryptedMap
     *        encrypted HashMap to decrypt.
     * @return decrypted HashMap
     */
    public HashMap<String, Object> decryptApplicationObjectMap(HashMap<String, Object> encryptedMap) {
        log.debug("Entering method decryptApplicationObjectMap");
        log.trace("This method decrypts the entire HashMap using the already loaded password.");
        HashMap<String, Object> decryptedMap = new HashMap<String, Object>();
        Set<String> keys = encryptedMap.keySet();
        log.debug("Loop through the map and decrypt its keys and values.");
        for(Iterator<String> iterator = keys.iterator();iterator.hasNext();){
            String key = (String) iterator.next();
            if(encryptedMap.containsKey(key)){
                if(encryptedMap.get(key) instanceof String){
                    decryptedMap.put(decrypt(key), decrypt((String) encryptedMap.get(key)));
                }else{
                    decryptedMap.put(key, encryptedMap.get(key));
                }// end if/else
            }// end if
        }// end if
        log.info("Successfully decrypted the map.");
        log.debug("Exiting method decryptApplicationObjectMap");
        return decryptedMap;
    }// end decryptApplicationObjectMap

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }// end getPassword

    /**
     * @param password
     *        the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }// end setPassword

    /**
     * This method generates a random password and then hash it using SHA-1.
     * 
     * @return password the hashed password
     */
    public static String generatePassword() {
        log.debug("Entering method generatePassword");
        log.trace("This method generates a random password.");
        log.info("Generate the password.");
        SecureRandom sr = new SecureRandom();
        log.debug("Generated the SecureRandom seed.");
        sr.setSeed(new SecureRandom().generateSeed(10));
        MessageDigest md;
        String returnPassword = null;
        try{
            md = MessageDigest.getInstance("SHA-1");
            md.update(String.valueOf(sr.nextLong()).getBytes());
            log.debug("Hash the password.");
            returnPassword = Base64.encode(md.digest());
        }catch(NoSuchAlgorithmException e){
            log.warn("Unable to hash a generated password.  Using unhashed(but still secure) password instead.  Exception is: " + e);
            returnPassword = String.valueOf(sr.nextLong());
        }// end try-catch
        log.debug("Exiting method generatePassword");
        return returnPassword;
    }// end generatePassword

}// end class
