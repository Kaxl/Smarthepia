angular
    .module('app.room', ['zingchart-angularjs', 'underscore'])

.constant('REST_SERVER', 'http://86.119.33.122:9000')

.run(function($rootScope, $log) {
    $rootScope.$log = $log;
});
