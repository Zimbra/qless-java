<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row" id="job-${job.jid}">
  <div class="span12">
    <div class="row">
      <div class="span6">
        <h2 style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden">
          <a href="<c:url value="/jobs/${job.jid}"/>">${job.jid}</a> | ${job.klassName}
        </h2>
      </div>
      <div class="span3">
        <h2 style="text-overflow: ellipsis; white-space: nowrap; overflow: hidden">
          <strong>
            | ${job.state} / <a href="<c:url value="/queues/${job.queueName}"/>">${job.queueName}</a> / ${job.workerName}
          </strong>
        </h2>
      </div>
      <div class="span3">
        <div style="float:right; margin-top: 4px">
          <div class="btn-group">
            <c:if test="job.state != 'complete'">
            <button title="delete" class="btn btn-danger" onclick="confirmation(this, 'Delete?', function() { cancel('${job.jid}', fade) })"><i class="icon-remove"></i></button>
            </c:if>
            <c:if test="job.state == 'running'">
            <button title="Time out job" class="btn btn-danger" onclick="confirmation(this, 'Time out job?', function() { timeout('${job.jid}') })"><i class="icon-time"></i></button>
            </c:if>
            <button title="track" class="btn<c:if test='${job.tracked}'> active</c:if>" data-toggle="button" onclick="$(this).hasClass('active') ? untrack('${job.jid}', fade) : track('${job.jid}', [], fade)"><i class="icon-flag"></i></button>
            <c:if test="job.state == 'failed'">
            <button title="requeue" class="btn btn-success" onclick="retry('${job.jid}', fade)"><i class="icon-repeat"></i></button>
            </c:if>
            <button title="move" class="btn dropdown-toggle btn-success" data-toggle="dropdown">
              <i class="caret"></i>
            </button>
            <ul class="dropdown-menu">
              <c:forEach items="${queues}" var="queue">
              <a href="#" onclick="move('${job.jid}', '${queue.name}', fade)">${queue.name}</a>
              </c:forEach>
            </ul>
          </div>
        </div>
        <div style="float:right; margin-right: 12px; margin-top: 4px">
          <div class="btn-group">
            <input class="span1 priority" type="text" placeholder="Pri ${job.priority}" onchange="priority('${job.jid}', $(this).val())"></input>
				    <button class="btn dropdown-toggle" data-toggle="dropdown">
					    <i class="caret"></i>
            </button>
            <ul class="dropdown-menu">
	    	  <a href="#" onclick="priority('${job.jid}',  25)">high</a>
              <a href="#" onclick="priority('${job.jid}',  0 )">normal</a>
              <a href="#" onclick="priority('${job.jid}', -25)">low</a>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- if not job.dependencies.empty? >
    <div class="row">
      <div class="span12" style="margin-bottom: 10px">
        <div style="float:left; margin-right: 10px"><h3>Dependencies:</h3></div>
        < job.dependencies.each do |jid| >
        <div class="btn-group" style="float:left; margin-right: 10px" id="<= sanitize_attr("${job.jid}-dependson-${jid}") >">
          <button class="btn" onclick="window.open('<= u "/jobs/${jid}" >', '_blank')"><= jid[0...8] >...</button>
          <button class="btn dropdown-toggle" onclick="confirmation(this, 'Undepend?', function() { undepend('${job.jid}', '${jid}', function() { $('#<= sanitize_attr("${job.jid}-dependson-${jid}") >').remove()} ); })">
            <i class="icon-remove"></i>
          </button>
        </div>
        < end >
      </div>
    </div>
    < end -->

    <!-- if not job.dependents.empty? >
    <div class="row">
      <div class="span12" style="margin-bottom: 10px">
        <div style="float:left; margin-right: 10px"><h3>Dependents:</h3></div>
        < job.dependents.each do |jid| >
        <div class="btn-group" style="float:left; margin-right: 10px" id="<= sanitize_attr("${job.jid}-dependents-${jid}") >">
          <button class="btn" onclick="window.open('<= u "/jobs/${jid}" >', '_blank')">${jid}</button>
          <button class="btn dropdown-toggle" onclick="confirmation(this, 'Undepend?', function() { undepend('${jid}', '${job.jid}', function() { $('#<= sanitize_attr("${job.jid}-dependents-${jid}") >').remove()} ); })">
            <i class="icon-remove"></i>
          </button>
        </div>
        < end >
      </div>
    </div>
    < end -->

    <div class="row">
      <div class="span12 tags" style="margin-bottom: 3px;">
        <c:forEach items="${job.tags}" var="tag">
        <div class="btn-group" style="float:left">
          <span class="tag">${tag}</span>
			    <button class="btn" onclick="untag('${job.jid}', '${tag}')">
            <i class="icon-remove"></i>
          </button>
        </div>
        </c:forEach>
       
        <!-- One for adding new tags -->
        <div class="btn-group" style="float:left">
          <input class="span1 add-tag" type="text" placeholder="Add Tag" onchange="tag('${job.jid}', $(this).val())"></input>
			    <button class="btn" onclick="tag('${job.jid}', $(this).parent().siblings().val())">
            <i class="icon-plus"></i>
          </button>
        </div>
      </div>
    </div>

    <!-- if not defined? brief -->
    <c:if test="true">
    <div class="row">
      <div class="span6">
        <h3><small>Data</small></h3>
        <pre style="overflow-y:scroll; height: 200px"><!-- JSON.pretty_generate(job.data) -->${job.data}</pre>
      </div>
      <div class="span6">
        <h3><small>History</small></h3>
        <div style="overflow-y:scroll; height: 200px">
          <!-- job.queue_history.reverse.each do |h| -->
          <c:forEach items="${job.history}" var="h">
          <c:if test="h.what == 'put'">
          	<pre><strong>${h.what}</strong> at ${h.when} in queue <strong>${h.q}</strong></pre>
          </c:if>
          <c:if test="h.what == 'popped'">
          	<pre><strong>${h.what}</strong> at ${h.when} by <strong>${h.worker}</strong></pre>
          </c:if>
          <c:if test="h.what == 'done'">
          	<pre><strong>completed</strong> at ${h.when}</pre>
          </c:if>
          <c:choose>
          <c:when test="h.what == 'failed'">
            <c:choose>
          	<c:when test="choose">
          	  <pre><strong>${h.what}</strong> at ${h.when}
          	  by <strong>${h.worker}</strong> in group <strong>${h.group}</strong></pre>
          	</c:when>
          	<c:otherwise>
          	  <pre><strong>${h.what}</strong> at ${h.when} in group <strong>${h.group}</strong></pre>
          	</c:otherwise>
          	</c:choose>
          </c:when>
          <c:otherwise>
          	<pre><strong>${h.what}</strong> at ${h.when}</pre>
          </c:otherwise>
          </c:choose>
          </c:forEach>
        </div>
      </div>
    </div>
    </c:if>

    <!-- if job.failure.length > 0 >
    <div class="row">
      <div class="span12">
        <div class="alert alert-error">
          <p>In <strong>${job.queueName}</strong> on <strong><= job.failure['worker'] ></strong>
            about <= strftime(Time.at(job.failure['when'])) ></p>
          <pre><= job.failure['message'].gsub('>', '&gt;').gsub('<', '&lt;') ></pre>
        </div>
      </div>
    </div>
    < end -->
    <hr/>
  </div>
</div>
