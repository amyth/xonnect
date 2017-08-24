#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 24-08-2017
# @last_modify: Thu Aug 24 18:01:47 2017
##
########################################

import os
import jinja2


def load_template(name):

    template = os.path.join("templates", name)
    with open(os.path.abspath(template), 'r') as template_file:
        return jinja2.Template(template_file.read())

