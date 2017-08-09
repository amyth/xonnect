#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 04-08-2017
# @last_modify: Fri Aug  4 16:43:26 2017
##
########################################

import time

from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.structure.graph import Graph


class GremlinWebSocketClient(object):

    def __init__(self, host='172.16.65.133', port='8182', *args, **kwargs):

        self.graph = Graph()
        self.g = self.graph.traversal().withRemote(DriverRemoteConnection(
            'ws://{}:{}/gremlin'.format(host, port), 'g'))

    def __getattr__(self, name):

        def wrapper(*args, **kwargs):
            start_time = time.time()
