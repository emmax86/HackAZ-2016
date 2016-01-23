def verify_structure(obj, fields):
    if not obj:
        return False
    for field in fields:
	if not field in obj:
	    return False
    return True
