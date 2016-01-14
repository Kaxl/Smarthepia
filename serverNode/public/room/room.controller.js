(function() {
    'use strict';

    angular
        .module('app.room')
        .controller('RoomController', RoomController);


    RoomController.$inject = ['$log', 'RoomService', '$scope', '$filter', '_'];

    function RoomController($log, RoomService, $scope, $filter, _) {
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
                roomId: 'A406',
                dteStart: $filter('date')(now, "yyyyMMdd"),
                dteEnd: $filter('date')(now.setDate(now.getDate() + 1), "yyyyMMdd")
            };

            $log.debug(vm.filter);
        }

        function loadAll(roomId, startDate, endDate) {
            loadHumidity(roomId, startDate, endDate);
            loadLuminance(roomId, startDate, endDate);
            loadTemperatures(roomId, startDate, endDate);
            loadMotion(roomId, startDate, endDate);
        }

        function loadMotion(roomId, startDate, endDate) {
            RoomService.GetInfo('motion', roomId, startDate, endDate)
                .then(function(motion) {
                    var tmpValues = [];
                    var tmpLabels = [];
                    var sensorList = [];

                    angular.forEach(motion, function(move) {
                        if (!tmpValues[move.sensor]) {
                            tmpValues[move.sensor] = [];
                            tmpLabels[move.sensor] = [];
                            sensorList.push(move.sensor);
                        }
                        tmpValues[move.sensor].push(move.motion);
                        tmpLabels[move.sensor].push($filter('date')(move.updateTime * 1000, "dd/MM/yyyy HH:mm"));
                    });

                    vm.motionChart = {
                        type: 'line',
                        "legend": {
                            "item": {
                                "font-size": 11
                            }
                        },
                        "plot": {
                            "stacked": false
                        },
                        "scale-x": {
                            label: {
                                text: "Motion"
                            },
                            labels: tmpLabels[sensorList[1]]
                        },
                        "scale-y": {
                            "min-value": _.min(_.values(tmpValues))[0],
                            "max-value": _.max(_.values(tmpValues))[0]
                        },
                        series: []
                    };

                    angular.forEach(sensorList, function(sensor) {
                        vm.motionChart.series.push({"values": tmpValues[sensor], "text": "Sensor : " + sensor})
                    });
                })
        };

        function loadHumidity(roomId, startDate, endDate) {
            RoomService.GetInfo('humidity', roomId, startDate, endDate)
                .then(function(humidity) {
                    var tmpValues = [];
                    var tmpLabels = [];
                    var sensorList = [];
                    vm.humidityChart = null;

                    angular.forEach(humidity, function(hum) {

                        if (!tmpValues[hum.sensor]) {
                            tmpValues[hum.sensor] = [];
                            tmpLabels[hum.sensor] = [];
                            sensorList.push(hum.sensor);
                        }

                        tmpValues[hum.sensor].push(hum.humidity);
                        tmpLabels[hum.sensor].push($filter('date')(hum.updateTime * 1000, "dd/MM/yyyy HH:mm"));

                    });

                    vm.humidityChart = {
                        type: 'line',
                        "legend": {
                            "item": {
                                "font-size": 11
                            }
                        },
                        "plot": {
                            "stacked": false
                        },
                        "scale-x": {
                            label: {
                                text: "Humidity"
                            },
                            labels: tmpLabels[sensorList[1]]
                        },
                        "scale-y": {
                            "min-value": _.min(_.values(tmpValues))[0],
                            "max-value": _.max(_.values(tmpValues))[0]
                        },
                        series: []
                    };

                    angular.forEach(sensorList, function(sensor) {
                        vm.humidityChart.series.push({"values": tmpValues[sensor], "text": "Sensor : " + sensor});
                    });
                })
        };

        function loadLuminance(roomId, startDate, endDate) {
            RoomService.GetInfo('luminance', roomId, startDate, endDate)
                .then(function(luminance) {
                    var tmpValues = [];
                    var tmpLabels = [];
                    var sensorList = [];

                    angular.forEach(luminance, function(lum) {

                        if (!tmpValues[lum.sensor]) {
                            tmpValues[lum.sensor] = [];
                            tmpLabels[lum.sensor] = [];
                            sensorList.push(lum.sensor);
                        }

                        tmpValues[lum.sensor].push(lum.luminance);
                        tmpLabels[lum.sensor].push($filter('date')(lum.updateTime * 1000, "dd/MM/yyyy HH:mm"));

                    });

                    vm.luminanceChart = {
                        type: 'line',
                        "legend": {
                            "item": {
                                "font-size": 11
                            }
                        },
                        "plot": {
                            "stacked": false
                        },
                        "scale-x": {
                            label: {
                                text: "Luminance"
                            },
                            labels: tmpLabels[sensorList[1]]
                        },
                        "scale-y": {
                            "min-value": _.min(_.values(tmpValues))[0],
                            "max-value": _.max(_.values(tmpValues))[0]
                        },
                        series: []
                    };

                    angular.forEach(sensorList, function(sensor) {
                        vm.luminanceChart.series.push({"values": tmpValues[sensor], "text": "Sensor : " + sensor});
                    });
                })
        };

        function loadTemperatures(roomId, startDate, endDate) {
            RoomService.GetInfo('temperature', roomId, startDate, endDate)
                .then(function(temperature) {
                    var tmpValues = [];
                    var tmpLabels = [];
                    var sensorList = [];

                    angular.forEach(temperature, function(temp) {

                        if (!tmpValues[temp.sensor]) {
                            tmpValues[temp.sensor] = [];
                            tmpLabels[temp.sensor] = [];
                            sensorList.push(temp.sensor);
                        }

                        tmpValues[temp.sensor].push(temp.temperature);
                        tmpLabels[temp.sensor].push($filter('date')(temp.updateTime * 1000, "dd/MM/yyyy HH:mm"));

                    });

                    vm.temperaturesChart = {
                        type: 'line',
                        "legend": {
                            "item": {
                                "font-size": 11
                            }
                        },
                        "plot": {
                            "stacked": false
                        },
                        "scale-x": {
                            label: {
                                text: "Temperatures"
                            },
                            labels: tmpLabels[sensorList[1]]
                        },
                        "scale-y": {
                            "min-value": _.min(_.values(tmpValues))[0],
                            "max-value": _.max(_.values(tmpValues))[0]
                        },
                        series: []
                    };

                    angular.forEach(sensorList, function(sensor) {
                        vm.temperaturesChart.series.push({"values": tmpValues[sensor], "text": "Sensor : " + sensor});
                    });
                })
        };

        init();
    }
})();
