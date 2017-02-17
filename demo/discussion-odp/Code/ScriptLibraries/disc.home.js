var app = angular.module('disc', [ 'ngResource', 'ui.router','ui.bootstrap','ngSanitize']);

app.config( function($stateProvider, $urlRouterProvider) {
	$urlRouterProvider.otherwise("/home");
	var homeState = {
		name : 'home',
		url : '/home',
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

app.controller('OverviewCtrl',['TopicService','$state', function(TopicService, $state) {
	var vm = this;
	vm.topics = TopicService.overview();
	vm.newTopic = function() {
		$state.go('newtopic');
	}
	vm.openTopic = function(id) {
		$state.go('topic', {'id':id});
	}
	
} ]);

app.controller('TopicCtrl',['TopicService','$state','$stateParams', function(TopicService, $state,$stateParams) {
	var vm = this;
	var id = $stateParams.id;
	console.log(id);
	vm.data = {};
	vm.newcomment = { parentid: id, topic:'', content:''};
	vm.data.topic = TopicService.getTopic(id);
	vm.comments = TopicService.getComments(id);
	vm.addComment = function () {
		var comment = TopicService.addComment(vm.newcomment);
		vm.newcomment = {parentid: id, topic:'', content:''};
		vm.comments.push(comment);
	}
	
} ]);


app.factory('TopicService', [ '$resource', function($resource) {
	var topicsService = {};
	topicsService.store = $resource("xsp/.xrest/topics/:id", {id:'@id'}, {	});
	topicsService.commentstore = $resource("xsp/.xrest/topics/:parentid/comments/:id", {id:'@id', parentid:'@parentid'}, {	});
	topicsService.overview = function() {
		return topicsService.store.query();
	}
	topicsService.getTopic = function(id) {
		return topicsService.store.get({'id':id});
	}
	topicsService.getComments = function(parentId) {
		return topicsService.commentstore.query({parentid:parentId});		
	}
	topicsService.addComment = function(comment) {
		comment.id = "@new";
		return topicsService.commentstore.save(comment);
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