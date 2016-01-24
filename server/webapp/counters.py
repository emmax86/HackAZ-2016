from __init__ import redis_db


def get_counter(name):
    a = redis_db.get(name)
    return int(a) if a else 0


def increment_counter(name):
    redis_db.incr(name, amount=1)


def decrement_counter(name):
    redis_db.decr(name, amount=1)
