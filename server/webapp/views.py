from __init__ import app, db
from flask import request
from flask import abort
from flask import json
from models import *


@app.route("/")
def hello():
    return "Guard Dog API v0.1.2"


@app.route("/create")
def create():
    db.create_all()
    return "Created", 200
