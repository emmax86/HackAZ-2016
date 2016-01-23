from __init__ import app
from models import *


@app.route("/")
def hello():
    return "Guard Dog API v0.2.0"


@app.route("/sign_up", methods=["GET", "POST"])
def sign_up():
    return "WOLOL"


@app.route("/log_in", methods=["GET", "POST"])
def log_in():
    return "WOLOL"


@app.route("/create")
def create():
    db.create_all()
    return "Created", 200
