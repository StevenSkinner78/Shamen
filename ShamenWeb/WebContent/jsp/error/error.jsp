<!DOCTYPE HTML>
<%@ page contentType="text/html;charset=UTF-8"
 		 language="java" 
 		 isErrorPage="true"
 		 import="java.util.*,gov.doc.isu.sdk.JSPErrorHandler" %>
<html>
<head>
<meta charset="utf-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<title>Application Error</title>
<jsp:include page="/jsp/common/common.jsp" flush="true" />
</head>
<body>
	<div class="container-fluid">
		<div class="row w-100">
			<div class="col-lg-6 mx-auto">
				<div class="error">
					<div class="error__content">
						<h3>Error in Displaying Page</h3>
						<%
							String ex = "";
							if (exception != null) {
								if (exception.getClass() != null) {
									ex = exception.getClass().toString();
										if (exception.getClass().getName() != null) {
											ex = exception.getClass().getName();
										}//end if
								}//end if
							}//end if
						%>
						<p>There was an error displaying this page: <em><%=ex%></em></p>
						<%
							String str = JSPErrorHandler.error(request, exception);
						%>
						<p>Please notify your administrator of this error code: <strong><%=str%></strong>.</p>
						<button class="btn btn-accent btn-pill" onclick="javascript:history.back()" name="back">← Go Back</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
