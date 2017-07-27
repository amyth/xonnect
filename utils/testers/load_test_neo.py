#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 20-07-2017
# @last_modify: Thu Jul 20 19:07:19 2017
##
########################################

import uuid
import random

from locust import Locust, TaskSet, task
from neo4j.v1 import GraphDatabase


class NeoBoltTasks(TaskSet):

    HOST = "127.0.0.1"
    PORT = 7687
    USERNAME = "neo4j"
    PASSWORD = "shine@123"

    def on_start(self):
        """ Initialize graph etc.
        """
        self.driver = GraphDatabase.driver("bolt://{}:{}".format(
            self.HOST, self.PORT), auth=(self.USERNAME, self.PASSWORD))

    @task(1)
    def create_vertex(self):
        labels = ['person', 'candidate', 'employee', 'recruiter', 'company', 'institute', 'job', 'phone', 'email']
        label = random.choice(labels)
        uid = uuid.uuid4().hex
        with self.driver.session() as session:
            session.write_transaction(create_vertex_tx, label, uid)

    @task(1)
    def create_edge(self):
        relations = [
            'candidate:has:email',
            'candidate:has:phone',
            'candidate:knows:candidate',
            'recruiter:posted:job',
            'candidate:worked_with:candidate',
            'candidate:studied_with:candidate'
        ]
        relation = random.choice(relations).split(':')
        with self.driver.session() as session:
            session.write_transaction(create_edge_tx, relation)

    @task(3)
    def get_friends(self):
        uids = ['0b7c4eae0679451fa444d31f54f941bb']
        uid = random.choice(uids)

        with self.driver.session() as session:
            session.write_transaction()

    @task(3)
    def get_friends_conditional(self):
        pass


class NeoLoadTester(Locust):
    task_set = NeoBoltTasks



def create_vertex_tx(tx, label, pname, pvalue):
    tx.run("merge (x:$label {$pname: $pvalue})", label=label,
            pname=pname, pvalue=pvalue)

def create_edge_tx(tx, rel):
    tx.run("merge (x1:$label1 {$pname1: $pvalue1}),"
            "merge (x2:$label2 {$pname1: $pvalue2})"
            "create (x1)-[x3:$label3]->(x2)",
            label1=rel[0], label2=rel[1], label=rel[2],
            pname1='uid', pname2='uid', pvalue1=uuid.uuid4().hex,
            pvalue2=uuid.uuid4().hex)
