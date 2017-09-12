#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 25-08-2017
# @last_modify: Wed Sep 13 13:49:15 2017
##
########################################

from .resources import (
    fetch_connections,
    import_contacts
)


ROUTES = [
    ['/{user_type}/{uid}/contacts/{source}', import_contacts],
]
