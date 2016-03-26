 'use strict';

angular.module('versionmonitorApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-versionmonitorApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-versionmonitorApp-params')});
                }
                return response;
            }
        };
    });
