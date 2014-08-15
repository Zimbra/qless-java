<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:putAttribute name="title" value="Overview"/>
<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">

<h3>Queues <small>And their job counts</small></h3>
<table class="table table-bordered table-striped">
	<thead>
		<tr>
			<th>Queue</th>
			<th>running</th>
			<th>waiting</th>
			<th>scheduled</th>
			<th>stalled</th>
			<th>depends</th>
			<th>recurring</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${queues}" var="queue">
		<tr>
			<td>${queue.name}</td>
			<td>${queue.running}</td>
			<td>${queue.waiting}</td>
			<td>${queue.scheduled}</td>
			<td>${queue.stalled}</td>
			<td>${queue.depends}</td>
			<td>${queue.recurring}</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

</tiles:putAttribute>
</tiles:insertDefinition>
