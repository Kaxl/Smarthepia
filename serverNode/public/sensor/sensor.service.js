(function() {
    'use strict';

    angular
        .module('app.sensor')
        .factory('SensorService', SensorService);


    SensorService.$inject = ['$http', '$q', '$log', 'REST_SERVER'];
    function SensorService($http, $q, $log, REST_SERVER) {
        return {
            GetTemperature: GetTemperature
        }

        function GetTemperature(sensorId, piId, startDate, endDate) {
            var deffered = $q.defer();

            $http.get(REST_SERVER + '/sensor/' + sensorId + '/' + piId + '/temperature?dteStart=' + startDate + 'dteEnd=' + endDate)
                .success(function(data) {
                    deffered.resolve(data);
                    $log.info('Temperatures get successfully !');
                })
                .error(function(err) {
                    deffered.reject(data);
                    $log.error('Problem while getting temperatures');
                });

            return deffered.promise;
        };
    }
})();
