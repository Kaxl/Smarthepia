(function() {
    'use strict';

    angular
        .module('app.sensor')
        .factory('SensorService', SensorService);


    SensorService.$inject = ['$http', '$q', '$log', 'REST_SERVER'];
    function SensorService($http, $q, $log, REST_SERVER) {
        return {
            GetInfo: GetInfo
        }

        function GetInfo(type, sensorId, piId, startDate, endDate) {
            var deffered = $q.defer();

            $http.get(REST_SERVER + '/sensor/' + sensorId + '/' + piId + '/' + type + '?dteStart=' + startDate + '&dteEnd=' + endDate)
                .success(function(data) {
                    deffered.resolve(data);
                    $log.info(type + ' get successfully !');
                })
                .error(function(err) {
                    deffered.reject(err);
                    $log.error('Problem while getting ' + type);
                });

            return deffered.promise;
        };
    }
})();
