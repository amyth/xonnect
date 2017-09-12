#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 11-09-2017
# @last_modify: Wed Sep 13 13:53:30 2017
##
########################################

from celery import Task
from conf.main import celery_app, graph_traversal


class UploadContacts(Task):

    name = 'upload_contacts'

    def run(self, uid, user_type, source, data):
        upload_method = getattr(self, 'upload_{}'.format(source.lower()))
        upload_method(uid, user_type, source, data)

    def upload_phonebook(self, uid, user_type, source, data):
        try:
            exists = []
            for contact in data:
                print('Processing contacts!')
            return True
        except Exception as err:
            #TODO implement retry & logging
            print(str(err))
            return False

upload_contacts = UploadContacts()
celery_app.tasks.register(upload_contacts)
