<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
  <meta charset="utf-8">
  <title>Qless Web</title>

  <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link href="//netdna.bootstrapcdn.com/bootstrap/2.3.2/css/bootstrap.min.css" rel="stylesheet">
  <link href="//cdnjs.cloudflare.com/ajax/libs/prettify/r224/prettify.css" rel="stylesheet">

  <!--
  IMPORTANT:
  This is Heroku specific styling. Remove to customize.
  -->
  <link href="http://heroku.github.com/template-app-bootstrap/heroku.css" rel="stylesheet">
  <style type="text/css">
    .instructions {
      display: none;
    }

    .instructions li {
      margin-bottom: 10px;
    }

    .instructions h2 {
      margin: 18px 0;
    }

    .instructions blockquote {
      margin-top: 10px;
    }

    .screenshot {
      margin-top: 10px;
      display: block;
    }

    .screenshot a {
      padding: 0;
      line-height: 1;
      display: inline-block;
      text-decoration: none;
    }

    .screenshot img, .tool-choice img {
      border: 1px solid #ddd;
      -webkit-border-radius: 4px;
      -moz-border-radius: 4px;
      border-radius: 4px;
      -webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
      -moz-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
      box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075);
    }
  </style>
  <!-- /// -->
  <script type="text/javascript">
    <!--
    function appname() {
      return location.hostname.substring(0, location.hostname.indexOf("."));
    }
    // -->
  </script>
</head>

<body onload="prettyPrint();">
<div class="navbar navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <a href="/" class="brand">Qless Web</a>
      <!--
      IMPORTANT:
      This is Heroku specific markup. Remove to customize.
      -->
      <a href="/" class="brand" id="heroku">by <strong>Zimbra</strong></a>
      <!-- /// -->
    </div>
  </div>
</div>

<div class="container" id="getting-started">
<div class="row">
<div class="span8 offset2">
<h1 class="alert alert-success">Connected to Redis</h1>

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
		<c:forEach items="${queueList}" var="queue">
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


<script src="//code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="//netdna.bootstrapcdn.com/bootstrap/2.3.2/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/prettify/r224/prettify.min.js"></script>
</body>
</html>
