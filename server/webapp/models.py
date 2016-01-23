from __init__ import redis_db
from werkzeug.security import generate_password_hash, check_password_hash


class User(object):
    def __init__(self, username, password, phone_number):
        self.username = username
        self.password_hash = generate_password_hash(password, method="pbkdf2:sha256", salt_length=32)
        self.phone_number = phone_number

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

    def write_to_db(self):
        user_dict = {"password_hash": self.password_hash, "phone_number": self.phone_number}
        redis_db.hmset(self.username, user_dict)

    @classmethod
    def get_from_db(cls, username):
        redis_db.hmget(username, ["password_hash", "phone_number"])
