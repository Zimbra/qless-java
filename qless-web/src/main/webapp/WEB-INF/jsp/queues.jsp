<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">

<c:choose>
<c:when test="queues.empty()">
  <div class="page-header">
    <h1>No Queues <small>I wish I had some queues :-/</small></h1>
  </div>
</c:when>
<c:otherwise>
  <div class="page-header">
    <h1>Queues <small>And their job counts</small></h1>
  </div>

  <c:forEach items="${queues}" var="queue">
  <div class="row">
    <div class="span4">
      <h3>
        <c:choose>
        <c:when test="queue.paused">
          <button
            id="${queue.name}-pause"
            title="Unpause"
            class="btn btn-success"
            onclick="unpause('${queue.name}')"><i class="icon-play"></i>
          </button>
        </c:when>
        <c:otherwise>
          <button
            id="${queue.name}-pause"
            title="Pause"
            class="btn btn-warning"
            onclick="pause('${queue.name}')"><i class="icon-pause"></i>
          </button>
        </c:otherwise>
        </c:choose>
        <a href="/queues/<c:url value="${queue.name}"/>">${queue.name}</a>
      </h3>
    </div>
    <div class="span8">
      <h3> |
        ${queue.running} /
        ${queue.waiting} /
        ${queue.scheduled} /
        ${queue.stalled} /
        ${queue.depends} <small>(running / waiting / scheduled / stalled / depends)</small>
      </h3>
    </div>
  </div>
  </c:forEach>
</c:otherwise>
</c:choose>

</tiles:putAttribute>
</tiles:insertDefinition>
