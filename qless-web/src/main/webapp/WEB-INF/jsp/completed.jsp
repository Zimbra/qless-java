<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>

<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">


<c:choose>
<c:when test="${empty jobs}">
  <div class="page-header">
    <h1>No Completed Jobs<small>(yet)</small></h1>
  </div>
</c:when>
<c:otherwise>
  <div class="page-header">
    <h1>Completed Jobs <small>You must be doing something right!</small></h1>
  </div>
</c:otherwise>
</c:choose>

<%@include file="_jobList.jsp" %>


</tiles:putAttribute>
</tiles:insertDefinition>
