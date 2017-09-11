#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Thu Sep  7 17:49:27 2017
##
########################################

import json

import falcon

from .exceptions import ValidationError


class BaseValidatedResource(object):

    required_fields = []

    def validate(self, req, resp, **params):
        try:
            data = json.loads(req.stream.read())
        except Exception as e:
            raise ValidationError('Could not read request data: {}'.format(
                    str(e)))

        for field in self.required_fields:
            if field not in data:
                raise ValidationError('This field is required.', field=field)


class ValidatedGetResource(BaseValidatedResource):

    def on_get(self, req, resp, **params):
        try:
            self.validate(req, resp, **params)
            self.validate_get(req, resp, **params)
            self.get(req, resp, **params)
        except ValidationError as e:
            e.process_error_response(resp)

    def validate_get(self, req, resp, **params):
        raise NotImplementedError('Please define a `validate_get` method.')


class ValidatedPostResource(BaseValidatedResource):

    def on_post(self, req, resp, **params):
        try:
            self.validate(req, resp, **params)
            self.validate_post(req, resp, **params)
            self.post(req, resp, **params)
        except ValidationError as e:
            e.process_error_response(resp)

    def validate_post(self, req, resp, **params):
        raise NotImplementedError('Please define a `validate_post` method.')


class ValidatedPutResource(BaseValidatedResource):

    def on_put(self, req, resp, **params):
        try:
            self.validate(req, resp, **params)
            self.validate_put(req, resp, **params)
            self.put(req, resp, **params)
        except ValidationError as e:
            e.process_error_response(resp)

    def validate_put(self):
        raise NotImplementedError('Please define a `validate_put` method.')


class ValidatedPatchResource(BaseValidatedResource):

    def on_patch(self, req, resp, **params):
        try:
            self.validate(req, resp, **params)
            self.validate_patch(req, resp, **params)
            self.patch(req, resp, **params)
        except ValidationError as e:
            e.process_error_response(resp)

    def validate_patch(self, req, resp, **params):
        raise NotImplementedError('Please define a `validate_patch` method.')


class ValidatedDeleteResource(BaseValidatedResource):

    def on_delete(self, req, resp, **params):
        try:
            self.validate(req, resp, **params)
            self.validate_delete(req, resp, **params)
            self.patch(req, resp, **params)
        except ValidationError as e:
            e.process_error_response(resp)

    def validate_delete(self, req, resp, **params):
        raise NotImplementedError('Please define a `validate_delete` method.')
