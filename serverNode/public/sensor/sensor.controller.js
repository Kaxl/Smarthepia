(function() {
    'use strict';

    angular
        .module('app.sensor')
        .controller('SensorController', SensorController);


    SensorController.$inject = ['$log', 'SensorService', '$scope', '$filter'];

    function SensorController($log, SensorService, $scope, $filter) {
        var vm = this;

        vm.init = init;
        vm.loadTemperatures = loadTemperatures;
        vm.test = test;

        function init() {
            // $scope.myJson = {
            //     type: 'line',
            //     series: [{
            //         values: []
            //     }]
            // };
        }

        function loadTemperatures(sensorId, piId, startDate, endDate) {
            $log.debug('loadTemperatures called');
            SensorService.GetTemperature(sensorId, piId, startDate, endDate)
                .then(function(temperatures) {
                    var tmpValues = [];
                    var tmpLabels = [];
                    vm.temperatures = temperatures;
                    angular.forEach(temperatures, function(temp) {
                        // $log.info(temp);
                        tmpValues.push(temp.temperature);
                        $log.debug($filter('date')(temp.updateTime*1000, "dd/MM/yyyy"));
                        tmpLabels.push($filter('date')(temp.updateTime*1000, "dd/MM/yyyy"));

                    });

                    $scope.myJson = {
                        type: 'line',
                        "scale-x": {
                            label: {
                                text: "Temperatures"
                            },
                            labels: tmpLabels
                        },
                        "scale-y": {
                            "min-value": 20,
                            "max-value": 30
                        },
                        series: [{
                            values: tmpValues
                        }]
                    };
                })
        }

        function test() {
            $log.info("This is a test");
        }

        init();
    }
})();
