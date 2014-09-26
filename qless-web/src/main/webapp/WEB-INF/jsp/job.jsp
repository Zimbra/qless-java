<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">

<script>
var fade = function(jid, type) {
  if (type == 'cancel') {
    $('#job-' + jid).slideUp();
  }
}
</script>

<c:choose>
<c:when test="${job == null}">
  <div class="row">
    <div class="span12">
      <h2>${jid} doesn't exist, was canceled, or expired</h2>
    </div>
  </div>
</c:when>
<c:otherwise>
  <%@include file="_job.jsp" %>
</c:otherwise>
</c:choose>

</tiles:putAttribute>
</tiles:insertDefinition>
