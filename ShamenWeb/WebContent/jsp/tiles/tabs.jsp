<%--Author:		James Eric Mansfield JEM01#IS
  - Date:		03/05/2013
  - Description: 	Tiles header
  - JavaScript:	Event handlers in the external file's ready() function.
--%>
<%@ include file="/jsp/common/tagdeps.jsp"%>
<bean:page id="requestVar"  property="request"/>
<script type="text/javascript" src="<bean:write name="requestVar" property="contextPath"/>/jsp/common/tabs.js"></script>
<isu:useMenuDisplayer name="TabbedMenu" permissions="permissionsAdapter" config="gov.doc.isu.shamen.resources.MessageResources">
	<isu:displayMenu name="OverviewLink" />
	<isu:displayMenu name="CalendarLink" />
	<isu:displayMenu name="SystemLink" />
	<isu:displayMenu name="SystemInfoLink" />
	<isu:displayMenu name="ApplicationLink" />
	<isu:displayMenu name="ControllerLink" />
	<isu:displayMenu name="ControllerInfoLink" />
	<isu:displayMenu name="BatchLink" />
	<isu:displayMenu name="BatchInfoLink" />
	<isu:displayMenu name="BatchCollectionLink" />
	<isu:displayMenu name="BatchCollectionInfoLink" />
	<isu:displayMenu name="StatusLink" />
	<isu:displayMenu name="AdminLink" />
	<isu:displayMenu name="StatLink" />
</isu:useMenuDisplayer>
