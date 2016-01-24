from webapp import app
from datetime import datetime, timedelta
import time
import hmac
from hashlib import sha256
import base64


expire_timedelta = timedelta(days=120)


def generate_token(username, user_secret, dt):
    unix_timestamp = int(time.mktime(dt.timetuple()))
    token_prefix = username + ":" + str(unix_timestamp)
    token_hmac = base64.b64encode(hmac.new(app.secret_key + "@" + user_secret, token_prefix, digestmod=sha256).digest())
    token_id = token_prefix + ":" + token_hmac
    return token_id


def verify_token(user, token):
    tokens_elements = token.split(":")
    if user.username != tokens_elements[0]:
        return False

    creation_datetime = datetime.fromtimestamp(int(tokens_elements[1]))
    now = datetime.utcnow()
    if now - creation_datetime.utcnow() > expire_timedelta:
        return False
    return generate_token(user.username, user.secret_key, creation_datetime) == token
