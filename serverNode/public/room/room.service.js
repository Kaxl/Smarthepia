(function() {
    'use strict';

    angular
        .module('app.room')
        .factory('RoomService', RoomService);


    RoomService.$inject = ['$http', '$q', '$log', 'REST_SERVER'];
    function RoomService($http, $q, $log, REST_SERVER) {
        return {
            GetInfo: GetInfo
            // GetTemperature: GetTemperature,
            // GetMotion: GetMotion,
            // GetHumidity: GetHumidity,
            // GetLuminance: GetLuminance
        }

        function GetInfo(type, roomId, startDate, endDate) {
            var deffered = $q.defer();

            $http.get(REST_SERVER + '/room/' + roomId + '/' + type + '?dteStart=' + startDate + '&dteEnd=' + endDate)
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
        //
        // function GetMotion(roomId, startDate, endDate) {
        //     var deffered = $q.defer();
        //
        //     $http.get(REST_SERVER + '/room/' + roomId + '/motion?dteStart=' + startDate + '&dteEnd=' + endDate)
        //         .success(function(data) {
        //             deffered.resolve(data);
        //             $log.info('Motion get successfully !');
        //         })
        //         .error(function(err) {
        //             deffered.reject(err);
        //             $log.error('Problem while getting motion');
        //         });
        //
        //     return deffered.promise;
        // };
        //
        // function GetTemperature(roomId, startDate, endDate) {
        //     var deffered = $q.defer();
        //
        //     $http.get(REST_SERVER + '/room/' + roomId + '/temperature?dteStart=' + startDate + '&dteEnd=' + endDate)
        //         .success(function(data) {
        //             deffered.resolve(data);
        //             $log.info('Temperatures get successfully !');
        //         })
        //         .error(function(err) {
        //             deffered.reject(err);
        //             $log.error('Problem while getting temperatures');
        //         });
        //
        //     return deffered.promise;
        // };
        //
        // function GetHumidity(roomId, startDate, endDate) {
        //     var deffered = $q.defer();
        //
        //     $http.get(REST_SERVER + '/room/' + roomId + '/humidity?dteStart=' + startDate + '&dteEnd=' + endDate)
        //         .success(function(data) {
        //             deffered.resolve(data);
        //             $log.info('Humidity get successfully !');
        //         })
        //         .error(function(err) {
        //             deffered.reject(err);
        //             $log.error('Problem while getting humidity');
        //         });
        //
        //     return deffered.promise;
        // };
        //
        // function GetLuminance(roomId, startDate, endDate) {
        //     var deffered = $q.defer();
        //
        //     $http.get(REST_SERVER + '/room/' + roomId + '/luminance?dteStart=' + startDate + '&dteEnd=' + endDate)
        //         .success(function(data) {
        //             deffered.resolve(data);
        //             $log.info('Luminance get successfully !');
        //         })
        //         .error(function(err) {
        //             deffered.reject(err);
        //             $log.error('Problem while getting luminance');
        //         });
        //
        //     return deffered.promise;
        // };
    }
})();
