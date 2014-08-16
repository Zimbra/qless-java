<!-- erb :_pagination, :layout => false -->

<c:forEach items="${jobs}" var="job">
<%@include file="_job.jsp" %>
</c:forEach>

<!-- erb :_pagination, :layout => false -->

