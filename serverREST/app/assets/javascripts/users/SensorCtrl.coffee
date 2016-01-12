
class SensorCtrl

    constructor: (@$log, @SensorService) ->
        @$log.debug "constructing SensorController"
        @sensors = []
        @temperatures = []
        @filterSensor =
            room: ''
            pi: 'Pi 3'
            sensor: ''
            dteStart: ''
            dteEnd: ''
        @options =
            chart:
                type: 'discreteBarChart'
                height: 450
                margin:
                    top: 20
                    right: 20
                    bottom: 60
                    left: 55
                x: (data) -> data.temperature
                # y: (data) -> d.value
                showValues: true
                valueFormat: (d) -> d3.format(',.4f')(d)
                transitionDuration: 500
                xAxis:
                    axisLabel: 'X Axis'
                yAxis:
                    axisLabel: 'Y Axis'
                    axisLabelDistance: 30

    getTemperatureFromSensor: (sensorId, piId, startDate, endDate) ->
        @$log.debug "getTemperatureFromSensor()"

        @SensorService.getTemperatureFromSensor(sensorId, piId, startDate, endDate)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Temperature"
                @temperatures = data
                @chartData =
                    key: "Temperature"
                    values: data
            ,
            (error) =>
                @$log.error "Unable to get Temperatures: #{error}"
            )

    # getAllUsers: () ->
    #     @$log.debug "getAllUsers()"
    #
    #     @UserService.listUsers()
    #     .then(
    #         (data) =>
    #             @$log.debug "Promise returned #{data.length} Users"
    #             @users = data
    #         ,
    #         (error) =>
    #             @$log.error "Unable to get Users: #{error}"
    #         )

controllersModule.controller('SensorCtrl', ['$log', 'SensorService', SensorCtrl])
