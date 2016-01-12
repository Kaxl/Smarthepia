(function() {
    'use strict';

    angular
        .module('app.sensor')
        .controller('SensorController', SensorController);


    SensorController.$inject = ['$log', 'SensorService', '$scope', '$filter', '_'];

    function SensorController($log, SensorService, $scope, $filter, _) {
        var vm = this;

        vm.init = init;
        vm.loadTemperatures = loadTemperatures;
        vm.loadHumidity = loadHumidity;
        vm.loadLuminance = loadLuminance;
        vm.loadAll = loadAll;
        vm.test = test;

        function init() {
            vm.filter = {
                sensorId: '8',
                piId: 'Pi 3',
                dteStart: '20160112',
                dteEnd: '20160113'
            };
            $log.debug(vm.filter);
        }

        function loadAll(sensorId, piId, startDate, endDate) {
            loadHumidity(sensorId, piId, startDate, endDate);
            loadLuminance(sensorId, piId, startDate, endDate);
            loadTemperatures(sensorId, piId, startDate, endDate);
        }

        function loadHumidity(sensorId, piId, startDate, endDate) {
            $log.debug('loadHumidity called');
            SensorService.GetHumidity(sensorId, piId, startDate, endDate)
                .then(function(humidity) {
                    var tmpValues = [];
                    var tmpLabels = [];
                    // vm.temperatures = humidity;
                    angular.forEach(humidity, function(hum) {
                        // $log.info(temp);
                        tmpValues.push(hum.humidity);
                        tmpLabels.push($filter('date')(hum.updateTime * 1000, "dd/MM/yyyy HH:mm"));

                    });

                    vm.humidityChart = {
                        type: 'line',
                        "plot": {
                            "stacked": true
                        },
                        "scale-x": {
                            label: {
                                text: "Humidity"
                            },
                            labels: tmpLabels
                        },
                        "scale-y": {
                            "min-value": _.min(tmpValues),
                            "max-value": _.max(tmpValues)
                        },
                        series: [{
                            values: tmpValues
                        }]
                    };
                })
        };

        function loadLuminance(sensorId, piId, startDate, endDate) {
            $log.debug('loadLuminance called');
            SensorService.GetLuminance(sensorId, piId, startDate, endDate)
                .then(function(luminance) {
                    var tmpValues = [];
                    var tmpLabels = [];

                    angular.forEach(luminance, function(lux) {
                        tmpValues.push(lux.luminance);
                        tmpLabels.push($filter('date')(lux.updateTime * 1000, "dd/MM/yyyy HH:mm"));
                    });

                    vm.luminanceChart = {
                        type: 'line',
                        "scale-x": {
                            label: {
                                text: "Luminance"
                            },
                            labels: tmpLabels
                        },
                        "scale-y": {
                            "min-value": _.min(tmpValues),
                            "max-value": _.max(tmpValues)+1
                        },
                        series: [{
                            values: tmpValues
                        }]
                    };
                })
        };

        function loadTemperatures(sensorId, piId, startDate, endDate) {
            $log.debug('loadTemperatures called');
            SensorService.GetTemperature(sensorId, piId, startDate, endDate)
                .then(function(temperatures) {
                    var tmpValues = [];
                    var tmpLabels = [];

                    angular.forEach(temperatures, function(temp) {
                        tmpValues.push(temp.temperature);
                        tmpLabels.push($filter('date')(temp.updateTime * 1000, "dd/MM/yyyy HH:mm"));
                    });

                    vm.temperaturesChart = {
                        type: 'line',
                        "scale-x": {
                            label: {
                                text: "Temperatures"
                            },
                            labels: tmpLabels
                        },
                        "scale-y": {
                            "min-value": _.min(tmpValues),
                            "max-value": _.max(tmpValues)
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
