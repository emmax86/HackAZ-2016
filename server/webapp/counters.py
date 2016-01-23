from __init__ import redis_db

def get_counter(name):
    redis_db.get(name)

def increment_counter(name):
    redis_db.incr(name, amount=1)
