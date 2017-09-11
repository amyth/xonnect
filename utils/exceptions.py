#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Thu Sep  7 17:49:29 2017
##
########################################

import json

import falcon


class ValidationError(Exception):

    def __init__(self, message, field=None, *args, **kwargs):
        super (ValidationError, self).__init__(message)
        self.field = field

    def process_error_response(self, resp):
        error_dict = {}
        resp.status = falcon.HTTP_404

        if self.field:
            error_dict[self.field] = self.message
        else:
            error_dict['non_field_errors'] = [self.message]

        resp.content_type = 'application/json'
        resp.body = json.dumps(error_dict)
