(function() {
  var tabs;

  tabs = angular.module("tabselector", []);

  tabs.controller("tabParentCtrl", function($scope) {
    return $scope.test = "";
  });

  tabs.directive("animatedTabs", [
    '$compile', '$timeout', function($compile, $timeout) {
      return {
        restrict: "E",
        scope: {
          duration: '@',
          selected: '=',
          selectedClass: '@',
          tabActive: '='
        },
        controller: function($scope, $element) {
          $scope.$watch((function() {
            return $scope.selected;
          }), function(newval, oldval) {
            var el, newel, newpos, prevel, prevpos, selectedClass;
            prevel = $($element).find('#' + oldval);
            newel = $($element).find('#' + newval);
            selectedClass = $scope.selectedClass;
            el = angular.element($element);
            prevpos = {};
            prevpos = prevel.position();
            prevpos.width = prevel.outerWidth();
            newpos = {};
            newpos = newel.position();
            newpos.width = newel.outerWidth();
            $scope.highstyle = {
              left: prevpos.left + 'px',
              width: prevpos.width + 'px'
            };
            if (newval === oldval) {
              return;
            }
            $scope.hidehighlighter = false;
            $scope.tabActive = "";
            $scope.highstyle = {
              left: newpos.left + 'px',
              width: newpos.width + 'px'
            };
            $timeout((function() {
              $scope.tabActive = newval;
            }), $scope.duration);
          });
        },
        link: function(scope, element, attr) {
          var el, newnode, node;
          scope.tabActive = scope.selected;
          scope.highstyle = {
            left: '1px',
            width: '1px',
            height: '1px'
          };
          scope.hidehighlighter = true;
          el = $(element);
          node = "<div class='tab-highlighter' data-ng-class='{\"highlighter-hidden\" : hidehighlighter }' data-ng-style='highstyle' />";
          el.prepend(node);
          newnode = el.find('.tab-highlighter');
          return $compile(newnode)(scope);
        }
      };
    }
  ]);

}).call(this);
