from flask import Flask
import config
from flask.ext.sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config.from_object(config)
db = SQLAlchemy(app)

from views import *
