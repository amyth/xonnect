#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 24-08-2017
# @last_modify: Thu Aug 24 18:30:26 2017
##
########################################


import falcon
import importlib

from .settings import APPS


xonnect_app = falcon.API()
FINAL_ROUTES = []

## Add all application routes to falcon app
for application in APPS:
    try:
        app_routes = importlib.import_module('apps.{}.routes'.format(application))
        for route in app_routes.ROUTES:
            FINAL_ROUTES.append(route)
            xonnect_app.add_route(*route)
    except ImportError as e:
        #TODO: implement logging
        print "Could not add routes for app {}. {}".format(application, str(e))
