tabs = angular.module("tabselector",[])

tabs.controller "tabParentCtrl" ,($scope) ->
	$scope.test = ""

tabs.directive "animatedTabs", ['$compile','$timeout',($compile,$timeout) ->
	restrict: "E"
	scope :
		duration : '@'
		selected : '='
		selectedClass : '@'
		tabActive : '='
	controller : ($scope,$element) ->
		$scope.$watch (->
			$scope.selected 
		), (newval,oldval) ->
			prevel = $($element).find '#'+oldval
			newel = $($element).find '#'+newval
			selectedClass = $scope.selectedClass
			el = angular.element $element
			prevpos = {}
			prevpos = prevel.position()
			prevpos.width = prevel.outerWidth()
			newpos = {}
			newpos = newel.position()
			newpos.width = newel.outerWidth()

			$scope.highstyle = 
				left :prevpos.left+'px',
				width :prevpos.width+'px',
			if newval == oldval
				return
			#$scope.showtransition = true
			$scope.hidehighlighter  = false
			$scope.tabActive = ""
			$scope.highstyle =
				left :newpos.left+'px',
				width :newpos.width+'px',
			$timeout (->
			  #$scope.hidehighlighter = true
			  $scope.tabActive = newval
			  #$scope.showtransition = false
			  return
			), $scope.duration
			return
		return
	link : (scope,element,attr) ->
		scope.tabActive = scope.selected;
		scope.highstyle = {left : '1px',width : '1px',height : '1px'}
		scope.hidehighlighter = true
		#scope.showtransition = false
		el = $(element)
		node = "<div class='tab-highlighter' data-ng-class='{\"highlighter-hidden\" : hidehighlighter }' data-ng-style='highstyle' />"
		el.prepend(node)
		newnode = el.find('.tab-highlighter')
		$compile(newnode)(scope)
]
