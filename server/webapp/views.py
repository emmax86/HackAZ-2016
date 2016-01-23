from __init__ import app
from flask import request, json
from models import *
from grumpy import generate_token, verify_token
from datetime import datetime
from counters import increment_counter, decrement_counter, get_counter


@app.route("/")
def hello():
    return "Guard Dog API v0.2.0"


@app.route("/sign_up", methods=["POST"])
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

                increment_counter('users-count')

                dump = {"token": generate_token(new_user.username, new_user.secret_key, datetime.now())}
                return json.dumps(dump)
            else:
                return "You are already a registered user", 401
        else:
            return "Malformed request", 401


@app.route("/log_in", methods=["POST"])
def log_in():
    if request.method == "POST":
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


@app.route("/log_out", methods=["POST"])
def logout():
    if request.method == "POST":
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


@app.route("/add_contact", methods=["POST"])
def add_contact():
    obj = request.get_json(force=True)
    if obj and obj.get("token") and obj.get("phone_number"):
        token_elements = obj.get("token").split(":")
        user = User.get_from_db(token_elements[0])
        if user and verify_token(user, obj["token"]):
            user.contacts.add(obj["phone_number"])
            user.write_to_db()
            increment_counter('contacts-count')
            return json.dumps(list(user.contacts))
        else:
            return "Malformed request", 401
    else:
        return "Malformed request", 401


@app.route("/remove_contact", methods=["POST"])
def remove_contact():
    obj = request.get_json(force=True)
    if obj and obj.get("token") and obj.get("phone_number"):
        token_elements = obj.get("token").split(":")
        user = User.get_from_db(token_elements[0])
        if user and verify_token(user, obj["token"]):
            if obj["phone_number"] in user.contacts:
                user.contacts.remove(obj["phone_number"])
                user.write_to_db()
                decrement_counter('contacts-count')
            return json.dumps(list(user.contacts))
        else:
            return "Malformed request", 401
    else:
        return "Malformed request", 401


@app.route("/stats")
def stats():
    public_stats = ['users-count', 'contacts-count']

    res_data = {}

    for stat_name in public_stats:
        res_data[stat_name] = get_counter(stat_name)

    return json.dumps(res_data), 200
