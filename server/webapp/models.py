from __init__ import redis_db
from werkzeug.security import generate_password_hash, check_password_hash
from os import urandom
from base64 import b64encode


class User(object):

    def __init__(self):
        self.username = "" # required
        self.password_hash = "" # required
        self.phone_number = "" # required
        self.emergency_contact = "" # not required
        self.secret_key = b64encode(urandom(64)).decode("utf-8")
        self.contacts = set() # can be empty

    def set_password(self, password):
        self.password_hash = generate_password_hash(password, method="pbkdf2:sha256", salt_length=32)

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

    def write_to_db(self):
        user_dict = {"password_hash": self.password_hash, "phone_number": self.phone_number,
                "secret_key": self.secret_key, "emergency_contact": self.emergency_contact}
        redis_db.hmset(self.username, user_dict)
        redis_db.delete(self.username + ":contacts")
        if len(self.contacts):
            redis_db.sadd(self.username + ":contacts", *self.contacts)

    def deauthenticate(self):
        self.secret_key = b64encode(urandom(64)).decode("utf-8")

    @classmethod
    def get_from_db(cls, username):
        user_dict = redis_db.hmget(username, ["password_hash", "phone_number", "secret_key", "emergency_contact"])
        fetched_user = User()
        fetched_user.username = username
        fetched_user.password_hash = user_dict[0]
        fetched_user.phone_number = user_dict[1]
        fetched_user.secret_key = user_dict[2]
        fetched_user.emergency_contact = user_dict[3]
        if not fetched_user.password_hash or not fetched_user.phone_number or not fetched_user.secret_key:
            return None
        else:
            fetched_user.contacts = redis_db.smembers(fetched_user.username + ":contacts")
            return fetched_user

