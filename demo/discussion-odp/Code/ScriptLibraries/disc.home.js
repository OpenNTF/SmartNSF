var app = angular.module('disc', [ 'ngResource', 'ui.router','ui.bootstrap','ngSanitize','ngQuill']);

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

app.config(['ngQuillConfigProvider', function (ngQuillConfigProvider) {
    ngQuillConfigProvider.set(null, null, 'custom placeholder');
}]);

app.controller('OverviewCtrl',['TopicService','$state', function(TopicService, $state) {
	var vm = this;
	vm.topics = [];
	vm.start = 0;
	vm.count = 10;
	vm.total = 0;
	vm.pages = [];
	vm.packages = [];
	TopicService.overview(vm.start,vm.count,function(err,result){
		if (err) {
			alert(err);
			return;
		}
		vm.topics = result.entries;
		vm.total = result.total;
		vm.updatePages();
	});
	vm.newTopic = function() {
		$state.go('newtopic');
	}
	vm.openTopic = function(id) {
		$state.go('topic', {'id':id});
	}
	vm.updatePages = function () {
		console.log("Called..");
		var entrieCount = vm.total;
		var pageSize = vm.count;
		var pages = Math.floor(entrieCount/pageSize);
		var remainder = entrieCount % pageSize;
		var result = [];
		for (var i = 0; i < pages; i++) {
			var p = {};
			p.title = i +1;
			p.start = i * pageSize;
			p.count = pageSize;
			p.cssclass = vm.calcClass(i,pageSize,vm.start);
			console.dir(p);
			result.push(p);
		}
		if (remainder > 0) {
			var p = {};
			p.title = pages+1;
			p.start = pages * pageSize;
			p.count = pageSize;
			p.cssclass = vm.calcClass(i,pageSize,vm.start);
			result.push(p)
		}
		vm.pages= result;
		vm.packages = [{start:vm.start, count:10, title:'10', cssclass: vm.count==10?'active':'' },
		               {start:vm.start, count:25, title:'25', cssclass: vm.count==25?'active':''},
		               {start:vm.start, count:50, title:'50', cssclass: vm.count==50?'active':''},
		               {start:vm.start, count:vm.total, title:'All', cssclass: vm.count==vm.total?'active':''}];
		
	}
	vm.calcClass = function(page, size, position) {
		var start = page *size;
		var end = start +size;
		console.log(position + " - "+ start +" "+ end);
		if ((position >= start) && (position < end)) {
			return "active";
		}
		return "";
	}
	vm.gotoPage = function(page) {
		vm.start = page.start;
		vm.count = page.count;
		TopicService.overview(vm.start,vm.count,function(err,result){
			if (err) {
				alert(err);
				return;
			}
			vm.topics = result.entries;
			vm.total = result.total;
			vm.updatePages();
		});
	}
} ]);

app.controller('NewTopicCtrl',['TopicService','$state', function(TopicService, $state) {
	var vm = this;
	vm.data = { id:"@new"};
	vm.inputCategories = "";
	vm.saveTopic = function() {
		if (vm.inputCategories != '') {
			vm.data.categories = vm.inputCategories.split(',');
		}
		TopicService.saveTopic(vm.data);
		$state.go('home');

	}
	vm.postDisabled = function() {
		return vm.topicForm.$invalid;
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
	topicsService.store = $resource("xsp/.xrest/topics/:id", {id:'@id'}, {query:{isarry:false}	});
	topicsService.commentstore = $resource("xsp/.xrest/topics/:parentid/comments/:id", {id:'@id', parentid:'@parentid'}, {	});
	topicsService.overview = function(start, count, cb) {
		var payload= {start:start, count:count};
		topicsService.store.query(payload, function(result){
			cb(null,result);
		});
	}
	topicsService.getTopic = function(id) {
		return topicsService.store.get({'id':id});
	}
	topicsService.saveTopic = function(topic) {
		return topicsService.store.save(topic);
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