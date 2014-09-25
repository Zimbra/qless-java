<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">

<div class="page-header">
  <h1>Current Workers <small>And their job counts</small></h1>
</div>

<c:forEach items="${workers}" var="worker">
<div class="row">
  <div class="span4">
    <h3><a href="/workers/<c:url value="${worker.name}"/>">${worker.name}</a></h3>
  </div>
  <div class="span8">
    <h3> |
      ${worker.jobs} /
      ${worker.stalled}
      <small>(running / stalled)</small>
    </h3>
  </div>
</div>
</c:forEach>

</tiles:putAttribute>
</tiles:insertDefinition>
