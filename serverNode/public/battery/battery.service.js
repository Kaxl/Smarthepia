(function() {
    'use strict';

    angular
        .module('app.battery')
        .factory('BatteryService', BatteryService);


    BatteryService.$inject = ['$http', '$q', '$log', 'REST_SERVER'];
    function BatteryService($http, $q, $log, REST_SERVER) {
        return {
            GetState: GetState
        }

        function GetState(piId, pourcentage) {
            var deffered = $q.defer();

            $http.get(REST_SERVER + '/battery/' + piId + '/' + pourcentage)
                .success(function(data) {
                    deffered.resolve(data);
                    $log.info('Batteries state get successfully !');
                })
                .error(function(err) {
                    deffered.reject(data);
                    $log.error('Problem while getting batteries state');
                });

            return deffered.promise;
        };

    }
})();
