from __init__ import app
from flask import request
from models import *


@app.route("/")
def hello():
    return "Guard Dog API v0.2.0"


@app.route("/sign_up", methods=["GET", "POST"])
def sign_up():
    if request.method == "GET":
        return "WOLOL"
    elif request.method == "POST":
        obj = request.get_json(force=True)
        if obj and obj.get("username") and obj.get("email") and obj.get("password"):
            return obj["username"] + obj["email"] + obj["password"]
        else:
            return "DEAD"


@app.route("/log_in", methods=["GET", "POST"])
def log_in():
    if request.method == "GET":
        return "WOLOL"
    elif request.method == "POST":
        obj = request.get_json(force=True)
        if obj and obj.get("username") and obj.get("password"):
            return obj["username"] + obj["password"]
        else:
            return "DEAD"


@app.route("/create")
def create():
    db.create_all()
    return "Created", 200
