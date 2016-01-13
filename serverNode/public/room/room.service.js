(function() {
    'use strict';

    angular
        .module('app.room')
        .factory('RoomService', RoomService);


    RoomService.$inject = ['$http', '$q', '$log', 'REST_SERVER'];
    function RoomService($http, $q, $log, REST_SERVER) {
        return {
            GetTemperature: GetTemperature,
            GetHumidity: GetHumidity,
            GetLuminance: GetLuminance
        }

        function GetTemperature(roomId, startDate, endDate) {
            var deffered = $q.defer();

            $http.get(REST_SERVER + '/room/' + roomId + '/temperature?dteStart=' + startDate + '&dteEnd=' + endDate)
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

        function GetHumidity(roomId, startDate, endDate) {
            var deffered = $q.defer();

            $http.get(REST_SERVER + '/room/' + roomId + '/humidity?dteStart=' + startDate + '&dteEnd=' + endDate)
                .success(function(data) {
                    deffered.resolve(data);
                    $log.info('Humidity get successfully !');
                })
                .error(function(err) {
                    deffered.reject(data);
                    $log.error('Problem while getting humidity');
                });

            return deffered.promise;
        };

        function GetLuminance(roomId, startDate, endDate) {
            var deffered = $q.defer();

            $http.get(REST_SERVER + '/room/' + roomId + '/luminance?dteStart=' + startDate + '&dteEnd=' + endDate)
                .success(function(data) {
                    deffered.resolve(data);
                    $log.info('Luminance get successfully !');
                })
                .error(function(err) {
                    deffered.reject(data);
                    $log.error('Problem while getting luminance');
                });

            return deffered.promise;
        };
    }
})();
