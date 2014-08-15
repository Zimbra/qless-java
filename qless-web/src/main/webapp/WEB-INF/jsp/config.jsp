<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">

<div class="page-header">
  <h1>Configuration Settings <small>Including defaults</small></h1>
</div>

<c:forEach items="${options}" var="option">
<div class="row">
  <div class="span4">
    <h2>${option.key}</h2>
  </div>
  <div class="span8">
    <h2>${option.value}</h2>
  </div>
</div>
</c:forEach>

</tiles:putAttribute>
</tiles:insertDefinition>
