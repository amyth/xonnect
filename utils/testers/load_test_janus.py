#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 03-08-2017
# @last_modify: Tue Aug 22 12:25:12 2017
##
########################################

from __future__ import print_function

import json
import random
import time
import uuid

from gremlin_python.driver.driver_remote_connection import DriverRemoteConnection
from gremlin_python.process.traversal import T
from gremlin_python.structure.graph import Graph
from locust import Locust, events, TaskSet, task


#GREMLIN_SERVER = '172.16.65.133:8182'
GREMLIN_SERVER = '35.198.251.227:8182'
GRAPH = Graph()
G = GRAPH.traversal().withRemote(DriverRemoteConnection('ws://{}/gremlin'.format(GREMLIN_SERVER), 'g'))


class LoadTestJanusTaskSet(TaskSet):


    def __init__(self, *args, **kwargs):
        super(LoadTestJanusTaskSet, self).__init__(*args, **kwargs)

    def on_start(self):

        with open('/tmp/uids.json', 'r') as json_file:
            self.uids = json.loads(json_file.read())

    @task(1)
    def create_person_with_phone(self):
        print('create_person_with_phone')
        start_time = time.time()
        try:
            properties = {
                'from':{'uid':uuid.uuid4().hex},
                'to':{'number':uuid.uuid4().hex},
                'relation_p':{}
            }
            self.create_vertex_with_edge('person', 'phone', 'has', **properties)
            total_time = int((time.time() - start_time) * 1000)
            events.request_success.fire(request_type="websocket", name='create_person_with_phone', response_time=total_time, response_length=0)
        except Exception as err:
            total_time = int((time.time() - start_time) * 1000)
            events.request_failure.fire(request_type="websocket", name='create_person_with_phone', response_time=total_time, exception=err)


    @task(1)
    def create_person_with_email(self):
        print('create_person_with_email')
        start_time = time.time()
        try:
            properties = {
                'from':{'uid': uuid.uuid4().hex},
                'to':{'email': '{}@loadtest.com'.format(uuid.uuid4().hex)},
                'relation_p':{}
            }
            self.create_vertex_with_edge('person', 'email', 'has', **properties)
            total_time = int((time.time() - start_time) * 1000)
            events.request_success.fire(request_type="websocket", name='create_person_with_email', response_time=total_time, response_length=0)
        except Exception as err:
            total_time = int((time.time() - start_time) * 1000)
            events.request_failure.fire(request_type="websocket", name='create_person_with_email', response_time=total_time, exception=err)

    @task(3)
    def create_knows_connections(self):
        print('create_knows_connections')
        start_time = time.time()
        try:
            i = 0
            tconn = []
            tvert = []

            while i < 50:
                tconn.append(uuid.uuid4().hex);
                i += 1

	    uid = random.choice(self.uids)
	    fromV = G.V().has('uid', uid).next()
	    for touid in tconn:
                G.addV('person', 'uid', touid).addE('knows').from_(fromV)
            total_time = int((time.time() - start_time) * 1000)
            events.request_success.fire(request_type="websocket", name='create_knows_connections', response_time=total_time, response_length=0)
        except Exception as err:
            total_time = int((time.time() - start_time) * 1000)
            events.request_failure.fire(request_type="websocket", name='create_knows_connections', response_time=total_time, exception=err)

    @task(7)
    def fetch_known_connections(self):
        print('fetch_known_connections')
        start_time = time.time()
        try:
            uid = random.choice(self.uids)
            results = G.V().has('uid', uid).out('knows').limit(100).values().toList()
            total_time = int((time.time() - start_time) * 1000)
            events.request_success.fire(request_type="websocket", name='fetch_known_connections', response_time=total_time, response_length=0)
            return results
        except Exception as err:
            total_time = int((time.time() - start_time) * 1000)
            events.request_failure.fire(request_type="websocket", name='fetch_known_connections', response_time=total_time, exception=err)

    @task(8)
    def fetch_working_connections(self):
        print('fetch_working_connections')
        start_time = time.time()
        try:
            uid = random.choice(self.uids)
            results = G.V().has('uid', uid).outE("knows").inV().hasLabel('person').dedup().as_("friends").outE('worked_at').inV().hasLabel('company').dedup().select('friends').toList()
            #results = G.V().has('uid', uid).out("knows").as_("friends").out("worked_at").select("friends").dedup().limit(50).values().toList()
            print(results)
            total_time = int((time.time() - start_time) * 1000)
            events.request_success.fire(request_type="websocket", name='fetch_working_connections', response_time=total_time, response_length=0)
            return results
        except Exception as err:
            total_time = int((time.time() - start_time) * 1000)
            events.request_failure.fire(request_type="websocket", name='fetch_working_connections', response_time=total_time, exception=err)

    def create_vertex(self, label, check_existence=False, existence_properties=None,
            **properties):
        """ Creates vertex with the given label and properties.
        """

        vertex = None

        # Check the given vertex for existence
        if (check_existence and existence_properties == None):
            print('Can not check if vertex already exists. Please provide '\
                    '`existence_properties`')
        elif (check_existence and existence_properties and (
                not isinstance(existence_properties, dict))):
            print('`existence_properties` should be a dictionary '\
                    '(key/value pairs) got {} instead.'.format(
                        type(existence_properties)))
        elif (check_existence and existence_properties and isinstance(
            existence_properties, dict)):
            query_results = G.V().has(**existence_properties)
            exists = query_results.hasNext()

            if exists:
                query_results.next()

        # Create the vertex if it does not exist.
        if vertex is None:
            vertex = G.addV(label).next()
            for key, value in properties.iteritems():
                G.V(vertex.id).property(key, value).next()

        return G.V(vertex.id)

    def create_edge(self, fromV, toV, relation, **properties):
        """ Creates an edge between the given from and to vertex
            arguments with the given relation labela and
            properties.
        """

        edge = fromV.addE(relation).to(toV).next()
        for key, value in properties.iteritems():
            G.E(edge.id).property(key, value).next()

        return True, edge

    def create_vertex_with_edge(self, from_label, to_label, relation, **properties):
        """ Creates vertexes with the given relation and properties.
        """

        from_props = properties.get('from', {})
        to_props = properties.get('to', {})
        relation_props = properties.get('relation_p', {})

        fromV = self.create_vertex(from_label, **from_props)
        toV = self.create_vertex(to_label, **to_props)
        edge = self.create_edge(fromV, toV, relation, **relation_props)

        return [fromV, edge, toV]

    def create_edge_for_vertex(self, vertex, to_label, relation, **properties):
        """ Creates vertexes with the given relation and properties.
        """

        from_props = properties.get('from', {})
        to_props = properties.get('to', {})
        relation_props = properties.get('relation_p', {})

        fromV = self.create_vertex(from_label, **from_props)
        toV = self.create_vertex(to_label, **to_props)
        edge = self.create_edge(fromV, toV, relation, **relation_props)

        return [fromV, edge, toV]

class JanusLoadTester(Locust):

    task_set = LoadTestJanusTaskSet
    min_wait = 100
    max_wait = 800
