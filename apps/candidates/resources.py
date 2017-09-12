#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Tue Sep 12 11:51:12 2017
##
########################################


import asyncio
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
from utils.resources import ValidatedPostResource



class ImportContacts(ValidatedPostResource):

    required_fields = ['data']

    def post(self, req, resp, **params):
        loop = asyncio.get_event_loop()
        loop.run_until_complete(self.upload_contacts(req, resp, **params))
        loop.close()
        resp.status = falcon.HTTP_201
        resp.content_type = 'application/json'
        resp.body = json.dumps({'message': 'success'})

    def validate_post(self, req, resp, **params):
        if not (hasattr(req, 'data') and getattr(req, 'data', None)):
            raise ValidationError('No request data could be found.')


    async def upload_contacts(self, req, resp, **params):
        uid = params.get('uid')
        source = params.get('source')
        user_type = get_graph_label(params.get('user_type'))
        contacts_data = json.loads(
                clean_non_ascii(json.dumps(req.data.get('data', [])))
        )
        keymap = KEY_MAP.get(source.lower(), {})
        contacts = update_keys(contacts_data, keymap)
        await upload_contacts(uid, user_type, source, contacts)



import_contacts = ImportContacts()
