from __init__ import app, db
from util import verify_structure
from flask import request, json
from models import *
from counters import get_counter
from grumpy import generate_token, verify_token
from datetime import datetime


@app.route("/")
def hello():
    return "Guard Dog API v0.2.0"


@app.route("/sign_up", methods=["GET", "POST"])
def sign_up():
    if request.method == "GET":
        return "WOLOL"
    elif request.method == "POST":
        obj = request.get_json(force=True)
        if obj and obj.get("username") and obj.get("phone_number") and obj.get("password"):
            if not User.get_from_db(obj["username"]):
                new_user = User()
                new_user.username = obj["username"]
                new_user.phone_number = obj["phone_number"]
                new_user.set_password(obj["password"])
                new_user.write_to_db()
                dump = {"token": generate_token(new_user.username, new_user.secret_key, datetime.now())}
                return json.dumps(dump)
            else:
                return "You are already a registered user", 401
        else:
            return "Malformed request", 401


@app.route("/log_in", methods=["GET", "POST"])
def log_in():
    if request.method == "GET":
        return "WOLOL"
    elif request.method == "POST":
        obj = request.get_json(force=True)
        if obj and obj.get("username") and obj.get("password"):
            user = User.get_from_db(obj["username"])
            if user and user.verify_password(obj["password"]):
                dump = {"token": generate_token(user.username, user.secret_key, datetime.now())}
                return json.dumps(dump)
            else:
                return "Invalid username/password combo", 401
        else:
            return "Malformed request", 401


@app.route("/log_out", methods=["GET", "POST"])
def logout():
    if request.method == "GET":
        return "WOLOL"
    elif request.method == "POST":
        obj = request.get_json(force=True)
        if obj and obj.get("token"):
            token_elements = obj.get("token").split(":")
            user = User.get_from_db(token_elements[0])
            if user and verify_token(user, obj["token"]):
                user.deauthenticate()
                user.write_to_db()
                return "Great success"
            else:
                return "Malformed request", 401
        else:
            print "Malformed request", 401


@app.route("/add_contact", methods=["GET", "POST"])
def add_contact():
    if request.method == "GET":
        return "WOLOL"
    elif request.method == "POST":
        obj = request.get_json(force=True)
        if obj and obj.get("token") and obj.get("phone_number"):
            token_elements = obj.get("token").split(":")
            user = User.get_from_db(token_elements[0])
            if user and verify_token(user, obj["token"]):
                user.contacts.add(obj["phone_number"])
                user.write_to_db()
                return "Great success"
            else:
                return "Malformed request", 401
        else:
            return "Malformed request", 401


@app.route("/stats", methods=['POST'])
def stats():

    if not verify_structure(request.get_json(), ['names']):
        return 'Bad JSON structure', 400

    names = request.get_json().get('names')

    # Potential security flaw, check keys that are being checked before grabbing the data
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
