
class SensorCtrl

    constructor: (@$log, @SensorService) ->
        @$log.debug "constructing SensorController"
        @sensors = []
        # @getAllUsers()

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
