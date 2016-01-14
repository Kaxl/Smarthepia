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
        vm.loadMotion = loadMotion;
        vm.loadAll = loadAll;

        function init() {
            var now = new Date;
            vm.filter = {
                sensorId: '8',
                piId: 'Pi 3',
                dteStart: $filter('date')(now, "yyyyMMdd"),
                dteEnd: $filter('date')(now.setDate(now.getDate() + 1), "yyyyMMdd")
            };
            $log.debug(vm.filter);
        }

        function loadAll(sensorId, piId, startDate, endDate) {
            loadHumidity(sensorId, piId, startDate, endDate);
            loadLuminance(sensorId, piId, startDate, endDate);
            loadTemperatures(sensorId, piId, startDate, endDate);
            loadMotion(sensorId, piId, startDate, endDate);
        }

        function loadMotion(sensorId, piId, startDate, endDate) {
            SensorService.GetInfo('motion', sensorId, piId, startDate, endDate)
                .then(function(motion) {
                    var tmpValues = [];
                    var tmpLabels = [];

                    angular.forEach(motion, function(move) {
                        tmpValues.push(move.motion);
                        tmpLabels.push($filter('date')(move.updateTime * 1000, "dd/MM/yyyy HH:mm"));

                    });

                    vm.motionChart = {
                        type: 'line',
                        "plot": {
                            "stacked": true
                        },
                        "scale-x": {
                            label: {
                                text: "Motion"
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

        function loadHumidity(sensorId, piId, startDate, endDate) {
            SensorService.GetInfo('humidity', sensorId, piId, startDate, endDate)
                .then(function(humidity) {
                    var tmpValues = [];
                    var tmpLabels = [];

                    angular.forEach(humidity, function(hum) {
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
            SensorService.GetInfo('luminance', sensorId, piId, startDate, endDate)
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
            SensorService.GetInfo('temperature', sensorId, piId, startDate, endDate)
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

        init();
    }
})();
