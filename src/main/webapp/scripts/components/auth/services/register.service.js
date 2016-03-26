'use strict';

angular.module('versionmonitorApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


