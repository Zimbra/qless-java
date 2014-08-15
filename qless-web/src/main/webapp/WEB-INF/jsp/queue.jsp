<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:putAttribute name="title" value="Overview"/>
<tiles:insertDefinition name="layout">
<tiles:putAttribute name="body">

<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
  google.load("visualization", "1", {packages:["corechart"]});
  google.setOnLoadCallback(drawCharts);

  function drawCharts() {
    drawChart(${waitStatsHistogram}, 'wait-chart', 'Jobs / Minute');
    drawChart(${runStatsHistogram}, 'run-chart' , 'Jobs / Minute');
  }

  function drawChart(d, id, title) {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'Time');
    data.addColumn('number', title);

    var _data = [];
    for (var i = 0; i < 60; ++i) {
      _data.push([i + ' seconds', d[i] * 60]);
    }
    for (var i = 1; i < 60; ++i) {
      _data.push([i + ' minutes', d[59 + i]]);
    }
    for (var i = 1; i < 24; ++i) {
      _data.push([i + ' hours', d[118 + i] / 60]);
    }
    for (var i = 1; i < 7; ++i) {
      _data.push([i + ' days', d[141 + i] / 1440]);
    }
    data.addRows(_data);

    var options = {
      legend: {position: 'none'},
      chartArea: { width:"80%", height:"80%" }
    };

    var chart = new google.visualization.SteppedAreaChart(document.getElementById(id));
    chart.draw(data, options);
  }
</script>

<div class="subnav subnav-fixed">
  <ul class="nav nav-pills">
    <li class="<c:if test="${tab == 'stats'}">active</c:if>"><a href="<c:url value="/queues/${queue.name}/stats"/>">Stats</a></li>
    <li class="<c:if test="${tab == 'running'}">active</c:if>"><a href="<c:url value="/queues/${queue.name}/running"/>">Running</a></li>
    <li class="<c:if test="${tab == 'waiting'}">active</c:if>"><a href="<c:url value="/queues/${queue.name}/waiting"/>">Waiting</a></li>
    <li class="<c:if test="${tab == 'scheduled'}">active</c:if>"><a href="<c:url value="/queues/${queue.name}/scheduled"/>">Scheduled</a></li>
    <li class="<c:if test="${tab == 'stalled'}">active</c:if>"><a href="<c:url value="/queues/${queue.name}/stalled"/>">Stalled</a></li>
    <li class="<c:if test="${tab == 'depends'}">active</c:if>"><a href="<c:url value="/queues/${queue.name}/depends"/>">Depends</a></li>
    <li class="<c:if test="${tab == 'recurring'}">active</c:if>"><a href="<c:url value="/queues/${queue.name}/recurring"/>">Recurring</a></li>
  </ul>
</div>

<div id="alerts" style="margin-top: 40px"></div>

<div class="row">
  <div class="span8">
    <h2><a href="<c:url value="/queues/${queue.name}"/>"">${queue.name}</a> |
      ${counts.running} /
      ${counts.waiting} /
      ${counts.scheduled} /
      ${counts.stalled} /
      ${counts.depends} <small>(running / waiting / scheduled / stalled / depends)</small>
    </h2>
  </div>

  <div class="span4">
    <div style="float:right">
      <h2>
        ${stats.failed} /
        ${stats.failures} /
        ${stats.retries} <small>(failed / failures / retries)</small>
      </h2>
    </div>
  </div>
</div>

<c:choose>
<c:when test="${tab != 'stats'}">
  <hr/>
  <!-- TODO erb :_job_list, :locals => { :jobs => jobs, :queues => queues }  -->
</c:when>
<c:otherwise>
  <div class="row" style="margin-top: 15px">
    <div class="span6">
      <div class="well">
        <div class="row">
          <div class="span12">
            <h3>Waiting</h3>
          </div>
        </div>
        <div class="row">
          <div class="span12">
            <h3>
              ${stats.wait.count} /
              <fmt:formatNumber type="number" maxFractionDigits="3" value="${stats.wait.mean}"/> /  
              <fmt:formatNumber type="number" maxFractionDigits="3" value="${stats.wait.std}"/> <small>Total / Mean / Std. Deviation</small>
            </h3>
          </div>
        </div>
        <div id="wait-chart" class="queue-stats-time-histogram-wait" style="height: 500px"></div>
      </div>
    </div>

    <div class="span6">
      <div class="well">
        <div class="row">
          <div class="span12">
            <h3>Running</h3>
          </div>
        </div>
        <div class="row">
          <div class="span12">
            <h3>
              ${stats.run.count} /
              <fmt:formatNumber type="number" maxFractionDigits="3" value="${stats.run.mean}"/> /  
              <fmt:formatNumber type="number" maxFractionDigits="3" value="${stats.run.std}"/> <small>Total / Mean / Std. Deviation</small>
            </h3>
          </div>
        </div>

        <div id="run-chart"  class="queue-stats-time-histogram-run"  style="height: 500px"></div>
      </div>
    </div>
  </div>
</c:otherwise>
</c:choose>

</tiles:putAttribute>
</tiles:insertDefinition>
