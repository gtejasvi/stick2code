'use strict';
angular.module('sliderapp',[]);
angular.module('sliderapp').controller('sliderAppCtrl', ['$scope',
	function() {
	}
]);
angular.module('sliderapp').directive('underlineSlider',['$compile','$timeout',function($compile,$timeout){
	return{
		restrict :'EA',
		scope : {
			sliderClass : '=',
			selector : '=',
			selectorMap : '=',
			sliderStyle : '=',
			duration : '='

		},
		link : function($scope,$element, $attrs){
			
			var children = $element;
			var parent = $element;
			var iter = 0;
			var sliderParent ;
			var sliderElements = [];
			var checkClass = function(el){
				angular.forEach(el,function(child){
					var childEl = angular.element(child);
					if(childEl.hasClass($scope.sliderClass)){
						childEl.addClass('ng-hide');
						sliderElements.push(childEl);
						return childEl;
					}
					
					if(!angular.isUndefined(child)){
						var children = angular.element(child).children();
						var retClass = checkClass(children);
					}
				});
			}
			checkClass(parent);
			
			var getPosition = function(elem){
		      if (typeof elem == 'string' || elem instanceof String) {
		        elem = document.getElementById(elem);
		      } else {
		        var elm = angular.element(elem);
		        if ('undefined' == typeof(elm)) {
		          elm = elem;
		        }
		      }
		      if ('undefined' == typeof(elm)) {
		        return {
		          left: 0,
		          top: 0
		        };
		      }
		      var rawDom = elm[0];
		      var _x = 0;
		      var _y = 0;
		      var body = document.documentElement || document.body;
		      var scrollX = window.pageXOffset || body.scrollLeft;
		      var scrollY = window.pageYOffset || body.scrollTop;
		      var position = rawDom.getBoundingClientRect();
		      return position;

			}

			//console.log(sliderEl);
			var slideCompleteFunc;
			var promise;
			var prevElement;
			var newElement;
			$scope.$watch(function(){return $scope.selector},function(newValue,oldValue){
				if(angular.isDefined(promise)){
					var promiseRet = $timeout.cancel(promise);
					if(promiseRet){
						slideCompleteFunc();
						promise = null;
						console.log('Promise Fuc');
					}
				}

				var prevChange = angular.element(document.getElementById(oldValue));
				var curChange = angular.element(document.getElementById(newValue));
				var prevElPos = $scope.selectorMap[oldValue];
				var newElPos = $scope.selectorMap[newValue];
				//console.log(prev);
				//console.log(cur);

				var sliderPrevEl = sliderElements[prevElPos];
				var sliderNewEl = sliderElements[newElPos];

				if(oldValue == newValue){
					sliderPrevEl.removeClass('ng-hide');
					return;
				}
				
				var curElPos = getPosition(curChange);
				var prevElPos = getPosition(prevChange);
				var sliderPrevElPos = getPosition(sliderPrevEl);
				
				
				//$scope.$parent.$digest();
				
				console.log(sliderPrevElPos);
				var moveLeft = curElPos.left - prevElPos.left;
				console.log(moveLeft);
					sliderNewEl.attr('style','');
					//Style it with position Absolute and float left and width 100% in case the slider does not have it already
					//To ensure that the animation is not edgy the slider should be floated and the size should be controlled using the 
					//width of the parent
					sliderPrevEl.attr('style','left:'+moveLeft+'px;');
					//Finish execution 
					prevElement = sliderPrevEl;
					newElement = sliderNewEl;
					slideCompleteFunc = function(){
						prevElement.removeClass('ng-hide');
						prevElement.addClass('ng-hide');
						prevElement.attr('style','');
						newElement.removeClass('ng-hide');

					}

					promise = $timeout(function(){
						slideCompleteFunc();
						},
						$scope.duration * 1000);

			});

		}
	};
}]);
