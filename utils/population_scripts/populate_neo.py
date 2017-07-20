#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 20-07-2017
# @last_modify: Thu Jul 20 16:10:30 2017
##
########################################


import json

from neo4j.v1 import GraphDatabase


class GraphPopulator(object):

    def __init__(self, neoip, username, password, json_path='/tmp/dummy.json',
            bolt_port=7687, *args, **kwargs):

        self.driver = GraphDatabase.driver("bolt://{}:{}".format(
            neoip, bolt_port), auth=(username, password))
        self.data = None

        ## update self.data from file
        with open(json_path, 'r') as json_file:
            data = json_file.read()
            self.data = json.loads(data)

    def create_schema(self):
        print "Preparing indexes."
        indexes = {
            'person': ['uid'],
            'candidate': ['uid'],
            'recruiter': ['uid'],
            'employee': ['uid'],
            'job': ['uid'],
            'company': ['uid'],
            'institute': ['uid']
        }
        with self.driver.session() as session:
            for label, properties in indexes.iteritems():
                session.write_transaction(create_index,
                        label, properties)
        print "Indexes created successfully."

    def create_vertexes(self, vertexes):
        print "Preparing vertexes."
        with self.driver.session() as session:
            for vertex in vertexes:
                session.write_transaction(create_vertex,
                        vertex.get('labels')[0],
                        vertex.get('properties', {}))
        print "Vertexes created successfully."

    def create_edges(self, edges):
        print "Preparing edges."
        with self.driver.session() as session:
            for edge in edges:
                session.write_transaction(create_edge,
                        edge.get('nodes')[0],
                        edge.get('nodes')[1],
                        edge.get('edge'),
                        edge.get('properties', {}))
        print "Edges created successfully."


    def populate(self):
        self.create_vertexes(self.data.get('vertexes'))
        self.create_edges(self.data.get('edges'))
        self.create_schema()


def create_vertex(tx, label, properties):
    prop_string = ""
    for name, value in properties.iteritems():
        prop_string += name
        prop_string += ": "
        prop_string += "\"%s\", " % value
    props = "{%s}" % prop_string.strip(", ")
    tx.run("create (x:{} {})".format(label, props))


def create_edge(tx, from_node, to_node, relation, properties):
    props = ""
    if properties:
        prop_string = ""
        for name, value in properties.iteritems():
            prop_string += name
            prop_string += ": "
            prop_string += "\"value\", "
        props = "{%s}" % prop_string.strip(", ")
    tx.run("match (a{uid: $from_uid}), (b{uid: $to_uid})"
            "create (a)-[c:%s%s]->(b)" % (relation, props),
            from_uid=from_node, to_uid=to_node
    )

def create_index(tx, label, properties):
    properties = ','.join(properties)
    tx.run("create index on :%s(%s)" % (label, properties))

populator = GraphPopulator('localhost', 'neo4j', 'Puresy307')
populator.populate()
