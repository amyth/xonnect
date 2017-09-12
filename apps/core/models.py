#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 11-09-2017
# @last_modify: Mon Sep 11 19:31:47 2017
##
########################################


import goblin


class Person(goblin.Vertex):
    uid = goblin.Property(goblin.String)


class Candidate(goblin.Vertex):
    uid = goblin.Property(goblin.String)


class Recruiter(goblin.Vertex):
    uid = goblin.Property(goblin.String)
    name = goblin.Property(goblin.String)


class Employee(goblin.Vertex):
    uid = goblin.Property(goblin.String)


class Phone(goblin.Vertex):
    number = goblin.Property(goblin.String)


class Email(goblin.Vertex):
    email = goblin.Property(goblin.String)


class LinkedIn(goblin.Vertex):
    linkedin_id = goblin.Property(goblin.String)
    linkedin_url = goblin.Property(goblin.String)
    image_url = goblin.Property(goblin.String)


class Job(goblin.Vertex):
    uid = goblin.Property(goblin.String)


class Company(goblin.Vertex):
    uid = goblin.Property(goblin.String)
    name = goblin.Property(goblin.String)


class Institute(goblin.Vertex):
    uid = goblin.Property(goblin.String)
    name = goblin.Property(goblin.String)


## Define Edge classes

class Has(goblin.Edge):
    pass


class Knows(goblin.Edge):
    name = goblin.Property(goblin.String)


class WorkedWith(goblin.Edge):
    pass


class StudiedWith(goblin.Edge):
    pass


class WorkedAt(goblin.Edge):
    from_date = goblin.Property(goblin.String)
    to_date = goblin.Property(goblin.String)


class StudiedAt(goblin.Edge):
    from_date = goblin.Property(goblin.String)
    to_date = goblin.Property(goblin.String)


class ProvidedBy(goblin.Edge):
    pass


class Posted(goblin.Edge):
    pass


class Likes(goblin.Edge):
    pass


class IsAMatchFor(goblin.Edge):
    score = goblin.Property(goblin.Float)
