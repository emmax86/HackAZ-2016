from __init__ import app, db
from util import verify_structure
from flask import request
from flask import abort
from flask import json
from models import *
from counters import get_counter


@app.route("/")
def hello():
    return "Guard Dog API v0.1.2"

@app.route("/stats", methods=['POST'])
def stats():

    if not verify_structure(request.get_json(), ['names']):
        return 'Bad JSON structure', 400

    names = request.get_json().get('names')

    # Potential security flaw, check keys that are being chcked before grabbing the data
    # Would like an 'approved' list of keys that they should have access to
    # I don't know what data will be accessed from here yet
    counter_data = []
    for name in names:
        counter_data.append(get_counter(name))

    res_data = {
        'names_data' : counter_data,
        'names' : names
    }

    return json.dumps(res_data), 200


@app.route("/create")
def create():
    db.create_all()
    return "Created", 200
