(function() {
    'use strict';

    angular
        .module('app.battery')
        .controller('BatteryController', BatteryController);

    BatteryController.$inject = ['$log', 'BatteryService', '$scope', '$filter', '_'];

    function BatteryController($log, BatteryService, $scope, $filter, _) {
        var vm = this;

        vm.init = init;
        vm.loadState = loadState;

        function init() {
            vm.filter = {
                piId: 'Pi 3',
                pourcentage: '100'
            };
            $log.debug(vm.filter);
        }

        function loadState(piId, pourcentage) {
            $log.debug('loadState called');
            BatteryService.GetState(piId, pourcentage)
                .then(function(state) {
                    var tmpValues = [];
                    var tmpLabels = [];
                    angular.forEach(state, function(bat) {
                        tmpValues.push(bat.battery);
                        tmpLabels.push(bat.sensor);
                        // tmpLabels.push($filter('date')(bat.updateTime * 1000, "dd/MM/yyyy HH:mm"));
                    });

                    vm.batteryChart = {
                        type: 'bar',
                        "plot": {
                            "stacked": true
                        },
                        "scale-x": {
                            label: {
                                text: "Battery states"
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

        init();
    }
})();
