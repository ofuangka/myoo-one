<!doctype html>
<html lang="en" data-ng-app="myooApp">
<head>
<title>/myoo/</title>
<link rel="stylesheet"
	href="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.common.min.css" />
<link
	href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.default.min.css"
	rel="stylesheet" />
<link
	href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.dataviz.min.css"
	rel="stylesheet" />
<link
	href="http://cdn.kendostatic.com/2014.1.318/styles/kendo.dataviz.default.min.css"
	rel="stylesheet" />
<link rel="stylesheet" href="css/myoo.css" />

<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
</head>
<body data-ng-controller="rootCtrl">
	<div class="container-fluid">
		<h1>
			<a>/myoo/</a> <small>Ask for it by name</small>
		</h1>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="pull-right" data-ng-cloak>
					Welcome, ${currentUser.nickname}! <span class="badge"
						title="You have {{ userState.totalPoints }}&mu;." data-ng-cloak>{{
						userState.totalPoints }}&mu;</span> <a href="${logoutUrl}">Sign out</a>
				</div>
				<div class="clearfix"></div>
			</div>
			<div class="panel-body">
				<div class="row">
					<div
						class="col-md-offset-2 col-sm-offset-4 col-md-10 col-sm-8 padding-md">
						<ul class="nav nav-pills">
							<li data-ng-repeat="section in userState.sections"
								data-ng-class="{ 'active' : section.id === userState.currentSectionId }">
								<a
								href="#/project/{{ userState.currentProjectId }}/section/{{ section.id }}"
								title="{{ section.description }}" data-ng-cloak>{{
									section.name }}</a>
							</li>
						</ul>
					</div>
				</div>
				<div class="row" data-ng-cloak>
					<div class="col-md-2 col-sm-4">
						<ul class="nav nav-pills nav-stacked" data-ng-cloak>
							<li data-ng-repeat="project in userState.projects"
								data-ng-class="{ 'active' : project.id === userState.currentProjectId }">
								<a
								href="#/project/{{ project.id }}/section/{{ userState.currentSectionId }}"
								title="{{ project.description }}">{{ project.name }}</a>
							</li>
						</ul>
						<div class="text-center padding-md">
							<button data-ng-click="createProject()" class="btn btn-default">+
								Create project</button>
						</div>
					</div>
					<div class="col-md-10 col-sm-8 padding-md">
						<div data-ng-view></div>
					</div>
				</div>
			</div>
			<div class="panel-footer">
				<p class="small">&copy; 2014 Osha Fuangkasae</p>
			</div>
		</div>
	</div>
	<div id="alertBox" class="modal fade" tabindex="-1" role="dialog"
		aria-labelledby="Alert" aria-hidden="true">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 id="alertBoxTitle" class="modal-title"></h4>
				</div>
				<div id="alertBoxBody" class="modal-body"></div>
				<div id="alertBoxFooter" class="modal-footer"></div>
			</div>
		</div>
	</div>
	<script src="http://code.jquery.com/jquery-2.1.1.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.0-beta.16/angular.min.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.0-beta.16/angular-route.min.js"></script>
	<script
		src="//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script src="https://www.google.com/jsapi"></script>
	<script src="myoo.js"></script>
	<script src="filters/dash.js"></script>
	<script src="directives/blur-save.js"></script>
	<script src="controllers/record.js"></script>
	<script src="controllers/review.js"></script>
	<script src="controllers/config.js"></script>
</body>
</html>