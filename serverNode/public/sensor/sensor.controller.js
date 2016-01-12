(function() {
    'use strict';

    angular
        .module('app.sensor')
        .controller('SensorController', SensorController);


    SensorController.$inject = ['$log', 'SensorService'];

    function SensorController($log, SensorService) {
        var vm = this;

        vm.loadTemperatures = loadTemperatures;
        vm.test = test;

        function loadTemperatures(sensorId, piId, startDate, endDate) {
            $log.debug('loadTemperatures called');
            SensorService.GetTemperature(sensorId, piId, startDate, endDate)
            .then(function(temperatures) {
                vm.temperatures = temperatures;
            })
        }

        function test() {
            $log.info("This is a test");
        }
    }
})();
