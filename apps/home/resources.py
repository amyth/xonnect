#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 24-08-2017
# @last_modify: Thu Aug 24 18:34:07 2017
##
########################################


import falcon

from conf.main import FINAL_ROUTES
from utils.jinja import load_template


class HomeResource(object):
    def on_get(self, req, resp):
        template = load_template('home.html')

        resp.status = falcon.HTTP_200
        resp.content_type = 'text/html'
        resp.body = template.render(app_name='Xonnect', routes=FINAL_ROUTES)


home = HomeResource()
