from webapp import app
from datetime import datetime, timedelta
import time
import hmac
from hashlib import sha256
import base64


expire_timedelta = timedelta(days=120)


def generate_session_id(username, dt):
    unix_timestamp = int(time.mktime(dt.timetuple()))
    session_prefix = username + ":" + str(unix_timestamp)
    session_hmac = base64.b64encode(hmac.new(app.secret_key, session_prefix, digestmod=sha256).digest())
    session_id = session_prefix + ":" + session_hmac
    return session_id


def verify_session(username, session_id):
    tokens = session_id.split(":")
    if username != tokens[0]:
        return False

    creation_datetime = datetime.fromtimestamp(int(tokens[1]))
    now = datetime.utcnow()
    if now - creation_datetime.utcnow() > expire_timedelta:
        return False
    return generate_session_id(username, creation_datetime) == session_id
