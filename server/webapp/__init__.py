from flask import Flask
import config
from flask.ext.sqlalchemy import SQLAlchemy
import redis

app = Flask(__name__)
app.config.from_object(config)
db = SQLAlchemy(app)

redis_db = redis.StrictRedis(host='localhost', port=6379, db=0)

from views import *
