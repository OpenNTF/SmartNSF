var app = angular.module('disc', [ 'ngResource', 'ui.router','ui.bootstrap']);

app.config( function($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise("/home");
	var homeState = {
		name : 'home',
		url : '/home',
		//template : '<h1>Ich bin die Home</h1>'
		templateUrl : 'overview.html',
		controller	: 'OverviewCtrl as ovCtrl'			
	};
	var topicState = {
			name : 'topic',
			url : '/topic/:id',
			templateUrl : 'topic.html',
			controller	: 'TopicCtrl as topCtrl'			
		};

	var newTopicState = {
		name : 'newtopic',
		url : '/newtopic',
		templateUrl : 'newtopic.html',
		controller : 'NewTopicCtrl as ntCtrl'
	};
	$stateProvider.state(homeState);
	$stateProvider.state(topicState);
	$stateProvider.state(newTopicState);
});

app.controller('OverviewCtrl',['TopicService', function(TopicService) {
	var vm = this;
	console.log("loading...")
	vm.topics = TopicService.overview();
	
} ])


app.factory('TopicService', [ '$resource', function($resource) {
	var topicsService = {};
	topicsService.store = $resource("xsp/.xrest/topics", null, {
	});
	topicsService.overview = function() {
		console.log("executing overview");
		return topicsService.store.query();
	}
	return topicsService;
} ]);

app.filter('join', function () {
    return function join(array, separator, prop) {
        if (!Array.isArray(array)) {
            return array; // if not array return original - can also throw error
        }

        return (!!prop ? array.map(function (item) {
            return item[prop];
        }) : array).join(separator);
    };
});

app.filter('commonName', function () {
    return function cn(value) {
        if (value && value.indexOf('/') > -1) {
            return value.split("/")[0];
        } else {
            return value;
        }
    };
});