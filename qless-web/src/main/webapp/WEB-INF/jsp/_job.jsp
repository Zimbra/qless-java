<c:choose>
<c:when test="${job.recurring}">
<%@include file="_jobRecurring.jsp" %>
</c:when>
<c:otherwise>
<%@include file="_jobNonRecurring.jsp" %>
</c:otherwise>
</c:choose>