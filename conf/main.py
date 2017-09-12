#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 24-08-2017
# @last_modify: Tue Sep 12 19:12:49 2017
##
########################################

import celery
import falcon
import os
import importlib

from gremlin_python.structure.graph import Graph
from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from .settings import APPS, GREMLIN_PATH, GREMLIN_TRAVERSAL_SOURCE

# Initialize celery app
celery_app = celery.Celery('xonnect', broker='amqp://guest@localhost//')

# Initialize gremlin client
graph = Graph()
connection = DriverRemoteConnection(GREMLIN_PATH, GREMLIN_TRAVERSAL_SOURCE)
graph_traversal = graph.traversal().withRemote(connection)

# Initialize xonnect apis
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
        print("Could not add routes for app {}. {}".format(application, str(e)))
