
class SensorService

    @headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
    @defaultConfig = { headers: @headers }

    constructor: (@$log, @$http, @$q) ->
        @$log.debug "constructing SensorService"

    getTemperatureFromSensor: (sensorId, piId, startDate, endDate) ->
        @$log.debug "getTemperatureFromSensor()"
        deferred = @$q.defer()

        @$http.get("/sensor/" + sensorId + "/" + piId + "/temperature?dteStart=" + startDate + "&dteEnd=" + endDate)
        .success((data, status, headers) =>
                @$log.info("Successfully get temperature - status #{status}")
                @$log.info(data)
                deferred.resolve(data)
            )
        .error((data, status, headers) =>
                @$log.error("Failed to list Users - status #{status}")
                deferred.reject(data)
            )
        deferred.promise

    # listUsers: () ->
    #     @$log.debug "listUsers()"
    #     deferred = @$q.defer()
    #
    #     @$http.get("/users")
    #     .success((data, status, headers) =>
    #             @$log.info("Successfully listed Users - status #{status}")
    #             deferred.resolve(data)
    #         )
    #     .error((data, status, headers) =>
    #             @$log.error("Failed to list Users - status #{status}")
    #             deferred.reject(data)
    #         )
    #     deferred.promise
    #
    # createUser: (user) ->
    #     @$log.debug "createUser #{angular.toJson(user, true)}"
    #     deferred = @$q.defer()
    #
    #     @$http.post('/user', user)
    #     .success((data, status, headers) =>
    #             @$log.info("Successfully created User - status #{status}")
    #             deferred.resolve(data)
    #         )
    #     .error((data, status, headers) =>
    #             @$log.error("Failed to create user - status #{status}")
    #             deferred.reject(data)
    #         )
    #     deferred.promise
    #
    # updateUser: (firstName, lastName, user) ->
    #   @$log.debug "updateUser #{angular.toJson(user, true)}"
    #   deferred = @$q.defer()
    #
    #   @$http.put("/user/#{firstName}/#{lastName}", user)
    #   .success((data, status, headers) =>
    #           @$log.info("Successfully updated User - status #{status}")
    #           deferred.resolve(data)
    #         )
    #   .error((data, status, header) =>
    #           @$log.error("Failed to update user - status #{status}")
    #           deferred.reject(data)
    #         )
    #   deferred.promise

servicesModule.service('SensorService', ['$log', '$http', '$q', SensorService])
