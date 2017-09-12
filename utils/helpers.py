#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 11-09-2017
# @last_modify: Mon Sep 11 19:43:20 2017
##
########################################


from utils.mappings import GRAPH_LABEL_MAP


def clean_non_ascii(value):
    """
    Removes all non-ascii characters from the given string.
    """

    return ''.join([i if ord(i) < 128 else '' for i in value])


def update_keys(obj, key_map):
    """
    Handles both dict and list objects to update
    keys.
    """

    if isinstance(obj, (list, tuple)):
        return _update_keys({"data": obj}, key_map)
    elif isinstance(obj, dict):
        return _update_keys(obj, key_map)
    else:
        error_message = "TypeMismatch: Data object is of type %s, "\
                "Expected either a list or a dict"
        print(error_message % type(obj))
	#TODO: Implement logging and log the exception
        #logger.error(error_message % type(obj))


def _update_keys(obj, key_map):
    """
    Recursively replaces the old keys in the given
    dictionary and replaces with the new keys using
    the given key_map.
    """

    results = {}

    try:
        for key, val in obj.iteritems():
            if isinstance(val, dict):
                val = _update_keys(val, key_map)

            if isinstance(val, (list, tuple)):
                nvals = []
                for x in val:
                    if isinstance(x, dict):
                        x = _update_keys(x, key_map)
                        nvals.append(x)
                val = nvals

            if key in key_map.keys():
                results[key_map[key]] = val
            else:
                results[key] = val
    except Exception as err:
        pass
	#TODO: Implement logging and log the exception
        #logger.error("Error shortening keys: %s" % str(err))

    return results


def get_graph_label(user_type):
    return GRAPH_LABEL_MAP.get(user_type)
