<!doctype html>
<html lang="en" data-ng-app="myooApp">
<head>
<title>/myoo/</title>
<link rel="stylesheet" href="libs/bootstrap/3.2.0/css/bootstrap.min.css" />
<link rel="stylesheet" href="css/myoo.css" />

<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />

<!-- http://www.luster.io/blog/9-29-14-mobile-web-checklist.html -->
<!-- android -->
<meta name="mobile-web-app-capable" content="yes">
<!-- iOS -->
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="translucent-black">
<meta name="apple-mobile-web-app-title" content="/myoo/">
<meta http-equiv="cleartype" content="on">
</head>
<body data-ng-controller="rootCtrl">
	<div class="container-fluid">
		<h1>
			<a>/myoo/</a> <small class="hidden-xs">Ask for it by name</small>
		</h1>
		<div class="panel panel-info">
			<div class="panel-heading">
				<div class="pull-right" data-ng-cloak>
					Welcome, <a data-ng-click="showPreferences()">${currentUserNickname}!</a>
					<span class="badge"
						title="You have {{ userState.totalPoints }}&mu;." data-ng-cloak>{{
						userState.totalPoints }}&mu;</span> <a href="${logoutUrl}">Sign out</a>
				</div>
				<div class="clearfix"></div>
			</div>
			<div class="panel-body">
				<div class="row">
					<div
						class="col-lg-offset-2 col-md-offset-3 col-sm-offset-4 col-lg-10 col-md-9 col-sm-8 padding-md">
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
					<div class="col-lg-2 col-md-3 col-sm-4">
						<ul class="nav nav-pills nav-stacked hidden-xs" data-ng-cloak>
							<li
								data-ng-repeat="project in userState.projects | orderBy : 'id' | filter : isSubscribed"
								data-ng-class="{ 'active' : project.id === userState.currentProjectId }">
								<a
								href="#/project/{{ project.id }}/section/{{ userState.currentSectionId }}"
								title="{{ project.description }}">{{ project.name }}</a>
							</li>
						</ul>
						<select class="form-control visible-xs" data-ng-if="userState.projects.length === 0">
							<option value="">No projects</option>
						</select>
						<select class="form-control visible-xs" data-ng-if="userState.projects.length > 0" data-ng-model="userState.mobileCurrentProjectId" data-ng-options="project.id as project.name for project in userState.projects | filter : isSubscribed">
							<option value="">Select a project</option>							
						</select>
						<div class="text-center padding-md">
							<button data-ng-click="createProject()" class="btn btn-default">+
								Create project</button>
						</div>
					</div>
					<div class="col-lg-10 col-md-9 col-sm-8 padding-md">
						<div id="alertMsg" class="alert alert-danger" role="alert" style="display: none;">
							<button type="button" class="close">
								<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
							</button>
							The last request didn't work correctly. If you keep experiencing
							issues, you might need to sign out and sign back in.
						</div>
						<div data-ng-view></div>
					</div>
				</div>
			</div>
			<div class="panel-footer">
				<p class="small">&copy; 2014 Osha Fuangkasae</p>
			</div>
		</div>
	</div>
	<div id="preferencesBox" class="modal fade" tabindex="-1" role="dialog"
		aria-labelledby="Preferences" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
					</button>
					<h4 id="preferencesBoxTitle" class="modal-title">Preferences</h4>
				</div>
				<div id="preferencesBoxBody" class="modal-body">
					<table data-ng-if="userState.projects.length > 0"
						class="table table-bordered table-striped">
						<thead>
							<tr>
								<th colspan="3">Subscriptions</th>
							</tr>
						</thead>
						<thead>
							<tr>
								<td colspan="3"><input type="text"
									placeholder="Search projects" class="form-control"
									data-ng-model="projectSearchQuery" /></td>
						<thead>
							<tr>
								<th></th>
								<th>Project name</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<tr
								data-ng-repeat="project in userState.projects | filter : { name : projectSearchQuery } | orderBy : 'name'">
								<td width="1%"><input type="checkbox"
									data-ng-model="userState.subscriptions[project.id]" /></td>
								<td>{{ project.name }}</td>
								<td width="1%"><button type="button"
										class="btn btn-default"
										data-ng-click="confirmClearPoints(project)">Clear
										points</button></td>
							</tr>
						</tbody>
					</table>
					<p data-ng-if="userState.projects.length === 0">There are
						currently no projects to subscribe to.</p>
				</div>
				<div id="preferencesBoxFooter" class="modal-footer">
					<button type="button" class="btn btn-primary" data-dismiss="modal">Done</button>
				</div>
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
	<script src="libs/jquery/2.1.1/jquery-2.1.1.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.0-rc.2/angular.min.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.0-rc.2/angular-route.min.js"></script>
	<script src="libs/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<script src="https://www.google.com/jsapi"></script>
	<script>
		google.load('visualization', '1.0', {
			'packages' : [ 'corechart' ]
		});
	</script>
	<script src="libs/fastclick/1.0.3-7/fastclick.js"></script>
	<script src="myoo.js"></script>
	<script src="filters/dash.js"></script>
	<script src="directives/save-on-event.js"></script>
	<script src="directives/resizing-chart.js"></script>
	<script src="directives/badge-selector.js"></script>
	<script src="controllers/record.js"></script>
	<script src="controllers/review.js"></script>
	<script src="controllers/config.js"></script>
	<script>
		angular.module('myooApp').run(function($rootScope) {
			$rootScope.isUserAdmin = ${isUserAdmin};
			$rootScope.currentUserNickname = '${currentUserNickname}';
		});
	</script>
</body>
</html>