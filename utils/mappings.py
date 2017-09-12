#!/usr/bin/python
# -*- coding: utf-8 -*-
#
########################################
##
# @author:          Amyth
# @email:           mail@amythsingh.com
# @website:         www.techstricks.com
# @created_date: 11-09-2017
# @last_modify: Mon Sep 11 15:30:44 2017
##
########################################


PHONEBOOK_KEY_MAP = {
    "Phone Number: ": "pn",
    "Contact Ph Nums": "pns",
    "Display Name: ": "n",
    "Email": "e",
    "Contact Emails": "es",
    "Organization Title": "ot",
    "Organization Name": "on",
    "My Contacts": "contacts",
}

LINKEDIN_KEY_MAP = {
    "_start": "s",
    "_total": "t",
    "_count": "co",
    "values": "v",
    "firstName": "f",
    "lastName": "l",
    "pictureUrl": "i",
    "positions": "p",
    "company": "c",
    "name": "n",
    "publicProfileUrl": "pp",
}

GMAIL_KEY_MAP = {
    "name": "n",
    "email": "e",
    "phone": "p",
}

FORM_KEY_MAP = {
    "dummy_id": "did",
    "phone": "pn",
    "email": "e",
    "name": "n",
    "unique_referral_code": "urc"
}

KEY_MAP = {
    "phonebook": PHONEBOOK_KEY_MAP,
    "linkedin": LINKEDIN_KEY_MAP,
    "gmail": GMAIL_KEY_MAP,
    "form": FORM_KEY_MAP
}

GRAPH_LABEL_MAP = {
    'candidate': 'candidate',
    'candidates': 'candidate',
    'employee': 'employee',
    'employees': 'employee',
    'recruiters': 'recruiter',
    'recruiter': 'recruiter',
}
