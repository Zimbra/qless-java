<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">

<div class="subnav subnav-fixed">
  <ul class="nav nav-pills">
    <li><a href="#Running">Running</a></li>
    <li><a href="#Stalled">Stalled</a></li>
  </ul>
</div>

<div id="alerts" style="margin-top: 40px"></div>

<c:if test="${fn:length(running) > 0}">
  <div class="page-header">
    <h1>${name} <small>Running Jobs</small></h1>
  </div>

  <c:forEach items="${running}" var="job">
  <%@include file="_job.jsp" %>
  </c:forEach>
</c:if>

<c:if test="${fn:length(stalled) > 0}">
  <div class="page-header">
    <h1>${name} <small>Stalled Jobs</small></h1>
  </div>

  <c:forEach items="${stalled}" var="job">
  <%@include file="_job.jsp" %>
  </c:forEach>
</c:if>

<c:if test="${fn:length(running) == 0 and fn:length(stalled) == 0}">
  <div class="page-header">
    <h1>${name} <small>Isn't Doing Anything</small></h1>
  </div>
</c:if>

</tiles:putAttribute>
</tiles:insertDefinition>
