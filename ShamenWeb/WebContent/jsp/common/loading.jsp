<%--
	Author: Shane Duncan SBD00#IS JCCC
	06/08/2011
--%>
<%@ page language="java" 
		 contentType="text/html; charset=UTF-8"%>
<%@ include file="/jsp/common/tagdeps.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/transitional.dtd">
<html>
	<head>
		<jsp:include page="/jsp/common/common.jsp" flush="true" />
		<title>Loading</title>
	</head>
	<body >
		<table>
			<tr>
				<td>
					<html:messages id="message" message="true">
			  			<logic:present name="message">
			  	  			<h4 align="left" class="messageRed">
			          			&nbsp;<bean:write name="message" filter="false"/>
			 	    		</h4>
			  			</logic:present>
					</html:messages>
				</td>
			</tr>
			<tr>
				<td>
					<center><p>Loading, please wait...</p></center>
				</td>
			</tr>
		</table>
	</body>
</html>