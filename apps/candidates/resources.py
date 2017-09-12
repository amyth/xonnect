#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Wed Sep 13 12:34:06 2017
##
########################################


import falcon
import json


from apps.core.tasks import upload_contacts
from utils.exceptions import ValidationError
from utils.helpers import (
    clean_non_ascii,
    get_graph_label,
    update_keys
)
from utils.mappings import KEY_MAP
from utils.resources import (
    ValidatedGetResource,
    ValidatedPostResource
)


class ImportContacts(ValidatedPostResource):

    required_fields = ['data']

    def post(self, req, resp, **params):
        self.upload_contacts(req, resp, **params)
        resp.status = falcon.HTTP_201
        resp.content_type = 'application/json'
        resp.body = json.dumps({'message': 'success'})

    def validate_post(self, req, resp, **params):
        if not (hasattr(req, 'data') and getattr(req, 'data', None)):
            raise ValidationError('No request data could be found.')


    def upload_contacts(self, req, resp, **params):
        uid = params.get('uid')
        source = params.get('source')
        user_type = get_graph_label(params.get('user_type'))
        contacts_data = json.loads(
                clean_non_ascii(json.dumps(req.data.get('data', [])))
        )
        keymap = KEY_MAP.get(source.lower(), {})
        contacts = update_keys(contacts_data, keymap)
        upload_contacts.apply_async(args=(
                uid, user_type, source, contacts))


class FetchConnections(ValidatedGetResource):
    def get(self, req, resp, **params):
        pass


import_contacts = ImportContacts()
fetch_connections = FetchConnections()
