#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Fri Aug 25 12:51:22 2017
##
########################################

from .resources import import_contacts


ROUTES = [
    ['/candidates/{uid}/contacts/{source}', import_contacts]
]
