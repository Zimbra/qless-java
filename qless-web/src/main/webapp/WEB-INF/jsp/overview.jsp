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
	    <tr class="queue-row">
    	  <td class="queue-column large-text">
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
	      </td>
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
