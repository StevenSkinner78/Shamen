<%@ taglib uri="/WEB-INF/tlds/struts-logic.tld" prefix="logic" %>

<logic:redirect forward="logon"/>

<%--

Redirect default requests to logon global ActionForward.
By using a redirect, the user-agent will change address to match the path of our Logon ActionForward. 

--%>