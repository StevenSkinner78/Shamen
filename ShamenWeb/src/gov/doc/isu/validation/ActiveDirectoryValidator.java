package gov.doc.isu.validation;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.sun.jna.platform.win32.Secur32Util;
import com.sun.jna.platform.win32.Win32Exception;

import gov.doc.isu.exception.ActiveDirectoryValidationException;
import waffle.servlet.WindowsPrincipal;
import waffle.windows.auth.IWindowsAuthProvider;
import waffle.windows.auth.IWindowsIdentity;
import waffle.windows.auth.IWindowsImpersonationContext;
import waffle.windows.auth.impl.WindowsAuthProviderImpl;
import waffle.windows.auth.impl.WindowsComputerImpl;

/**
 * Utility class using Active Directory to authenticate valid user sign on.
 * 
 * @author <strong>Steven L. Skinner</strong>
 */

public class ActiveDirectoryValidator {

    private static Logger log = Logger.getLogger("gov.doc.isu.validation.ActiveDirectoryValidator");

    /**
     * This method verifies username and password are correct for this domain
     * 
     * @param userNameDomain
     *        the fully qualified Active Directory name
     * @param password
     *        the users password
     * @return String username
     * @throws ActiveDirectoryValidationException
     *         if an ActiveDirectoryValidationException occurred
     */
    public static String authenticateUser(String userNameDomain, String password) throws ActiveDirectoryValidationException {
        log.debug("Entering ActiveDirectoryValidator.authenticateUser");
        log.debug("Paramaters: userNameDomain=" + String.valueOf(userNameDomain) + ", password is prtoected");
        try{
            String domain = null;
            String userNameHold = null;
            // username@domain
            String[] nameArray = userNameDomain.split("@");
            if(nameArray.length == 2){
                userNameHold = nameArray[0];
                domain = nameArray[1];
            }// end tif
            log.debug("Authenticating user name and password through Active Directory: username=" + userNameDomain);
            IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
            prov.logonDomainUser(userNameHold, domain, password);
        }catch(Win32Exception e){
            log.error("Win32Exception occurred in ActiveDirectoryValidator.authenticateUser. e=" + e.getMessage());
            throw new ActiveDirectoryValidationException(e.getMessage());
        }// end try
        log.debug("Exiting ActiveDirectoryValidator.authenticateUser. userName=" + String.valueOf(userNameDomain));
        return userNameDomain;
    }// end method

    /**
     * This method verifies that the user name is a current user in domain
     * 
     * @param userNameDomain
     *        the fully qualified active directory name
     * @return String username
     * @throws ActiveDirectoryValidationException
     *         if an ActiveDirectoryValidationException occurred
     */
    public static String verifyUser(String userNameDomain) throws ActiveDirectoryValidationException {
        log.debug("Entering ActiveDirectoryValidator.verifyUser");
        log.debug("Paramaters: userNameDomain=" + String.valueOf(userNameDomain));
        try{
            IWindowsAuthProvider prov = new WindowsAuthProviderImpl();
            log.debug("verifying user is valid Active Directory User. user=" + String.valueOf(userNameDomain));
            prov.lookupAccount(userNameDomain);
            log.debug("User=" + String.valueOf(userNameDomain) + " is valid");
        }catch(Win32Exception e){
            log.error("Win32Exception occurred in ActiveDirectoryValidator.authenticateUser. e=" + e.getMessage());
            throw new ActiveDirectoryValidationException(e.getMessage());
        }// end try
        log.debug("Exiting ActiveDirectoryValidator.verifyUser. userName=" + String.valueOf(userNameDomain));
        return userNameDomain;
    }// end method

    /**
     * This method verifies that the computer name is a current computer in domain
     * 
     * @param computerName
     *        the name of the computer
     * @return boolean true|false
     * @throws ActiveDirectoryValidationException
     *         if an ActiveDirectoryValidationException occurred
     */
    public static boolean verifyComputer(String computerName) throws ActiveDirectoryValidationException {
        log.debug("Entering ActiveDirectoryValidator.verifyComputer");
        log.debug("Paramaters: computerName=" + String.valueOf(computerName));
        boolean result = false;
        try{
            new WindowsAuthProviderImpl();
            new WindowsComputerImpl(computerName);
        }catch(Win32Exception e){
            log.error("Win32Exception occurred in ActiveDirectoryValidator.verifyComputer. e=" + e.getMessage());
            throw new ActiveDirectoryValidationException(e.getMessage());
        }// end try
        log.debug("Exiting ActiveDirectoryValidator.verifyComputer. computerName=" + String.valueOf(computerName));
        return result;
    }// end method

    /**
     * This method gets the current logged in fully qualified Active Directory user name in username@domain format.
     * 
     * @param request
     *        HttpServletRequest
     * @return String
     * @throws ActiveDirectoryValidationException
     *         if an ActiveDirectoryValidationException occurred
     */
    public static String getCurrentLoggedInUser(HttpServletRequest request) throws ActiveDirectoryValidationException {
        log.debug("Entering ActiveDirectoryValidator.getCurrentLoggedInUser");
        log.debug("Paramaters: HttpServletRequest=" + (request == null ? "null" : request.toString()));
        String fullyQualifiedName = "";
        if(request != null){
            WindowsPrincipal principal = (WindowsPrincipal) request.getSession().getAttribute("waffle.servlet.NegotiateSecurityFilter.PRINCIPAL");
            log.debug("Getting the Windows Principal that was loaded from the security filter. principal=" + (principal == null ? "NULL" : principal.getName()));
            if(principal == null){
                log.error("Could not retrieve the Windows Principal.");
                throw new ActiveDirectoryValidationException("Could not retrieve your Windows Principal. You must log in with you user name and password.");
            }// end if
            try{
                IWindowsIdentity identity = principal.getIdentity();
                IWindowsImpersonationContext ctx = identity.impersonate();
                if(ctx != null){
                    fullyQualifiedName = Secur32Util.getUserNameEx(8);
                    ctx.revertToSelf();
                    if("null".equalsIgnoreCase(String.valueOf(fullyQualifiedName))){
                        log.error("Could not retrieve the Windows Log on.");
                        throw new ActiveDirectoryValidationException("Could not retrieve your Windows Credentials. You must log in with you user name and password.");
                    }// end if
                }// end if
            }catch(Exception e){
                log.error("Could not retrieve the Windows Log on.");
                throw new ActiveDirectoryValidationException("Could not retrieve your Windows Credentials. You must log in with you user name and password.");
            }// end try
        }
        log.debug("Getting the current Windows logged in user in fully qualified username format. username=" + (fullyQualifiedName == null ? "NULL" : fullyQualifiedName));
        log.debug("Exiting ActiveDirectoryValidator.getCurrentLoggedInUser. userId=" + fullyQualifiedName);
        return fullyQualifiedName;
    }// end method

}// end class
