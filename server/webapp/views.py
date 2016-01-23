from __init__ import app, db
from flask import request
from flask import abort
from flask import json
from models import *
from svm import SVM


@app.route("/")
def hello():
    return "Guard Dog API v0.1.2"


@app.route("/create")
def create():
    db.create_all()
    return "Great Success", 200


@app.route("/login", methods=["POST"])
def login():
    obj = request.get_json()
    username = obj["username"]
    password = obj["password"]

    user = User.query.filter_by(username=username).first()

    if not user:
        return "Great Failure", 401

    if user.verify_password(password):
        return "Great Success", 200
    else:
        return "Great Failure", 401


@app.route("/data", methods=["POST"])
def data():
    def verify_structure(obj):
        if not obj:
            return False
        elif ("real" not in obj) or ("frames" not in obj) or ("username" not in obj):
            return False
        return True

    if not verify_structure(request.get_json()):
        print "verify structure"
        abort(401)

    real = bool(request.get_json()["real"])

    user = User.query.filter_by(username=request.get_json()["username"]).first()

    if not user:
        abort(401)

    happening = Incident(real, user)
    db.session.add(happening)
    db.session.commit()

    frames = request.get_json()["frames"]
    for each in sorted(frames, key=lambda frame: frame["batch_order"]):
        frame = AccelerationFrame(each["batch_order"], each["accel_x"], each["accel_y"], each["accel_z"], happening)
        db.session.add(frame)

    db.session.commit()
    return "Great Success", 200


@app.route("/debug_db")
def debug_db():
    return json.dumps(
        {
            "Incidents": [each.serialize() for each in Incident.query.all()]
        }
    ), 200


@app.route("/register", methods=["POST"])
def register():
    obj = request.get_json()
    username = obj["username"]
    phone = obj["phone_number"]
    password = obj["password"]
    warranty = obj["warranty"]

    if User.query.filter_by(username=username).first():
        return "Error, phone number already registered", 401

    user = User(username, phone, password, warranty)

    db.session.add(user)
    db.session.commit()

    return "", 200


@app.route("/analyze", methods=["POST"])
def analyze():
    def verify_structure(obj):
        if not obj:
            return False
        elif ("frames" not in obj) or ("username" not in obj):
            return False
        return True

    if not verify_structure(request.get_json()):
        print "verify structure"
        abort(401)

    user = User.query.filter_by(username=request.get_json()["username"]).first()

    if not user:
        abort(401)

    incidents = user.incidents

    machine = SVM(user.username, event_list_flatten(incidents), event_list_result_flatten(incidents))

    event_list = [0.0] * 75

    frames = request.get_json()["frames"]
    for each in sorted(frames, key=lambda frame: frame["batch_order"]):
        batch_order = each["batch_order"]
        event_list[batch_order * 3] = each["accel_x"]
        event_list[batch_order * 3 + 1] = each["accel_y"]
        event_list[batch_order * 3 + 2] = each["accel_z"]

    machine.classify(event_list)
    guess = bool(machine.labeled_new_data())

    return json.dumps({"guess": guess, "content": request.get_json()}), 200


@app.route("/correct", methods=["POST"])
def correct():
    obj = request.get_json()
    real = obj["real"]
    incident_id = obj["incident_id"]

    incident = Incident.query.filter_by(id=incident_id).first()

    if not incident:
        abort(401)

    incident.real = real
    db.session.add(incident)
    db.session.commit()

    return "Great Success", 200


# PASS THIS METHOD AN INCIDENT AND IT WILL GIVE YOU THE VECTOR FOR THE INCIDENT
def event_flatten(event):
    x = []
    y = [[each.accel_x, each.accel_y, each.accel_z] for each in event.frames]
    for each in y:
        x.extend(each)
    return x


def event_list_flatten(events):
    x = []
    for each in events:
        x.append(event_flatten(each))
    return x


def event_list_result_flatten(events):
    return [int(x.real) for x in events]

