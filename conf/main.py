#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 24-08-2017
# @last_modify: Mon Sep 11 19:41:47 2017
##
########################################

import asyncio
import celery
import falcon
import goblin
import os
import importlib

from .settings import APPS
from apps.core.models import (
    Person,
    Candidate,
    Recruiter,
    Employee,
    Phone,
    Email,
    LinkedIn,
    Job,
    Company,
    Institute,
    Has,
    Knows,
    WorkedWith,
    StudiedWith,
    WorkedAt,
    StudiedAt,
    ProvidedBy,
    Posted,
    Likes,
    IsAMatchFor
)

# Initialize celery app
celery_app = celery.Celery('xonnect', broker='amqp://guest@localhost//')

# Initialize goblin app
loop = asyncio.get_event_loop()
goblin_app = loop.run_until_complete(goblin.Goblin.open(loop))
path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'goblin.yml')
goblin_app.config_from_file(path)

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
