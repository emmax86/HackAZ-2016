from __init__ import app
from flask import request, json
from models import *
from grumpy import generate_token, verify_token
from datetime import datetime
from counters import increment_counter, decrement_counter, get_counter
from front_end import Data
from sklearn.linear_model import LogisticRegression
import pickle


@app.route("/")
def hello():
    return "Guard Dog API v0.2.0"


@app.route("/sign_up", methods=["POST"])
def sign_up():
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


@app.route("/modify_emergency_contact", methods=["POST"])
def add_emergency_contact():
    obj = request.get_json(force=True)
    if obj and obj.get("token") and obj.get("phone_number"):
        token_elements = obj.get("token").split(":")
        user = User.get_from_db(token_elements[0])
        if user and verify_token(user, obj["token"]):
            user.emergency_contact = obj["emergency_contact"]
            user.write_to_db()
            return json.dumps(user.emergency_contact)
        else:
            return "Could not verify user", 401
    else:
        return "Malformed request", 401


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
            return "Could not verify user", 401
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
            return "Could not verify user", 401
    else:
        return "Malformed request", 401


@app.route("/classify", methods=["POST"])
def classify():
    obj = request.get_json(force=True)
    if obj and obj.get("token") and obj.get("batch-phone") and obj.get("batch-pebble"):
        token_elements = obj.get("token").split(":")
        user = User.get_from_db(token_elements[0])
        if user and verify_token(user, obj["token"]):
            batch_phone = obj["batch-phone"]
            batch_pebble = obj["batch-pebble"]
            data = Data(batch_phone, batch_pebble).data_array()
            past_data = redis_db.get(user.username + ":ml")
            if past_data:
                regression_engine = pickle.loads(past_data)
            else:
                regression_engine = LogisticRegression()
            d = regression_engine.predict(data)
            print d
            guess = bool(d)
            response_dict = {"guess": guess}
            return json.dumps(response_dict)
        else:
            return "Could not verify user", 401
    else:
        return "Malformed request", 401


@app.route("/correct", methods=["POST"])
def correct():
    obj = request.get_json(force=True)
    if obj and obj.get("token") and obj.get("batch-phone") and obj.get("batch-pebble") and obj.get("answer"):
        token_elements = obj.get("token").split(":")
        user = User.get_from_db(token_elements[0])
        if user and verify_token(user, obj["token"]):
            batch_phone = obj["batch-phone"]
            batch_pebble = obj["batch-pebble"]
            data = Data(batch_phone, batch_pebble).data_array()
            past_data = redis_db.get(user.username + ":ml")
            if past_data:
                regression_engine = pickle.loads(past_data)
            else:
                regression_engine = LogisticRegression()
            answer = int(obj["answer"])
            regression_engine.fit(data, [answer])
            redis_db.set(user.username + ":ml", pickle.dumps(regression_engine))
            return "Great success"
        else:
            return "Malformed request", 401
    else:
        return "Malformed request", 401


@app.route("/train", methods=["POST"])
def train():
    obj = request.get_json(force=True)
    print obj.get("batch-phone") + " " + obj.get("batch-pebble") + " " + obj.get("answer")
    if obj and obj.get("batch-phone") and obj.get("batch-pebble") and obj.get("answer"):
        batch_phone = obj["batch-phone"]
        batch_pebble = obj["batch-pebble"]
        data = Data(batch_phone, batch_pebble).data_array()
        past_data = redis_db.get("learning-data")
        if past_data:
            regression_engine = pickle.loads(past_data)
        else:
            regression_engine = LogisticRegression()
        answer = int(obj["answer"])
        regression_engine.fit(data, [answer])
        redis_db.set("learning-data")
        return "Great success"
    else:
        return "Malformed request", 401


@app.route("/stats", methods=["GET"])
def stats():
    public_stats = ['users-count', 'contacts-count']

    res_data = {}

    for stat_name in public_stats:
        res_data[stat_name] = get_counter(stat_name)

    return json.dumps(res_data), 200
