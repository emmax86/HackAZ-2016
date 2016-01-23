from __init__ import redis_db
from werkzeug.security import generate_password_hash, check_password_hash


class User(object):

    def __init__(self):
        self.username = ""
        self.password_hash = ""
        self.phone_number = ""

    def set_password(self, password):
        self.password_hash = generate_password_hash(password, method="pbkdf2:sha256", salt_length=32)

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

    def write_to_db(self):
        user_dict = {"password_hash": self.password_hash, "phone_number": self.phone_number}
        redis_db.hmset(self.username, user_dict)

    @classmethod
    def get_from_db(cls, username):
        user_dict = redis_db.hmget(username, ["password_hash", "phone_number"])
        fetched_user = User()
        fetched_user.username = username
        fetched_user.password_hash = user_dict["password_hash"]
        fetched_user.phone_number = user_dict["phone_number"]
        return fetched_user

