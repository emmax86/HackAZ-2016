from __init__ import db
from werkzeug.security import generate_password_hash, check_password_hash


class User(db.Model):
    __tablename__ = "user"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True, index=True)
    username = db.Column(db.String(64), index=True)
    phone_number = db.Column(db.String(15))
    password_hash = db.Column(db.String(120))
    warranty = db.Column(db.Boolean, default=False)
    incidents = db.relationship("Incident", backref="user")

    def __init__(self, username, phone, password, warranty):
        self.username = username
        self.phone_number = phone
        self.password_hash = generate_password_hash(password, "pbkdf2:sha256:10000")
        self.warranty = warranty

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)


class Incident(db.Model):
    __tablename__ = "incident"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    real = db.Column(db.Boolean, default=True)
    frames = db.relationship("AccelerationFrame", backref="incident")
    user_id = db.Column(db.Integer, db.ForeignKey("user.id"))

    def __init__(self, real, user):
        self.real = real
        self.user_id = user.id

    def serialize(self):
        return {
            "id": self.id,
            "user": self.user.phone_number,
            "real": self.real,
            "frames": [each.serialize() for each in self.frames]
        }


class AccelerationFrame(db.Model):
    __tablename__ = "frame"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    batch_order = db.Column(db.Integer)
    accel_x = db.Column(db.Float)
    accel_y = db.Column(db.Float)
    accel_z = db.Column(db.Float)
    incident_id = db.Column(db.Integer, db.ForeignKey("incident.id"))

    def __init__(self, batch_order, accel_x, accel_y, accel_z, incident):
        self.batch_order = batch_order
        self.accel_x = accel_x
        self.accel_y = accel_y
        self.accel_z = accel_z
        self.incident_id = incident.id

    def serialize(self):
        return {
            "id": self.id,
            "accel_x": self.accel_x,
            "accel_y": self.accel_y,
            "accel_z": self.accel_z,
            "incident_id": self.incident_id
        }


class Counters(db.Model):
    __tablename__ = "counters"
    id = db.Column(db.Integer, primary_key=True, autoincrement=True, default=1)
    count_users = db.Column(db.Integer)
    count_incidents = db.Column(db.Integer)
    count_lives_saved = db.Column(db.Integer)

    def __init__(self):
        self.count_users = 0
        self.count_incidents = 0
        self.count_users = 0

    def increment_users(self):
        self.count_users += 1

    def increment_incidents(self):
        self.count_incidents += 1

    def incrememnt_lives_saved(self):
        self.count_lives_saved += 1
