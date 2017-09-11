#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Thu Sep  7 17:49:24 2017
##
########################################


import falcon
import json

from utils.resources import ValidatedPostResource



class ImportContacts(ValidatedPostResource):

    required_fields = ['source', 'shine_id', 'data']

    def post(self, req, resp, **params):
        resp.status = falcon.HTTP_201
        resp.content_type = 'application/json'
        resp.body = json.dumps({'message': 'success'})

    def validate_post(self, *args, **kwargs):
        pass




import_contacts = ImportContacts()
