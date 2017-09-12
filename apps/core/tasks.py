#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 11-09-2017
# @last_modify: Tue Sep 12 11:54:18 2017
##
########################################

import asyncio
import goblin

from conf.main import celery_app
from conf.settings import GREMLIN_PATH, DB_NAME



async def upload_contacts(uid, user_type, source, data):
    remote_connection = await goblin.DriverRemoteConnection.open(
            GREMLIN_PATH, DB_NAME)
