from flask import Flask
import config
import redis

app = Flask(__name__)
app.config.from_object(config)

redis_db = redis.StrictRedis(host='localhost', port=6379, db=0)

from views import *
