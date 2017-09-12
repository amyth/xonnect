#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Mon Sep 11 15:34:34 2017
##
########################################

from .resources import import_contacts


ROUTES = [
    ['/{user_type}/{uid}/contacts/{source}', import_contacts]
]
