#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 24-08-2017
# @last_modify: Thu Aug 24 17:40:12 2017
##
########################################


import falcon
import importlib

from .settings import APPS


xonnect_app = falcon.API()

## Add all application routes to falcon app
for application in APPS:
    try:
        app_routes = importlib.import_module('apps.{}.routes'.format(application))
        for route in app_routes.ROUTES:
            xonnect_app.add_route(*route)
    except ImportError as e:
        #TODO: implement logging
        print "Could not add routes for app {}. {}".format(application, str(e))
