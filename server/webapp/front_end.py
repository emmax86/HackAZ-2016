
def parse_docs(pebble_data=None, phone_data=None):
    if not pebble_data or not phone_data:
        raise Exception('Expected phone and pebble data, didn\'t receive both')

    pebble_datalayer = Data(pebble_data)
    phone_datalayer = Data(phone_data)

class Data(object):
    def __init__(self, data):
       self._data = data

    def setup(self):
        x_min_max = min_max_selector(data, lambda frame: frame[0]) # X
        self.x_min = x_min_max['minimum']
        self.x_max = x_min_max['maximum']
        y_min_max = min_max_selector(data, lambda frame: frame[1]) # Y
        self.y_min = y_min_max['minimum']
        self.y_max = y_min_max['maximum']
        z_min_max = min_max_selector(data, lambda frame: frame[2]) # Z
        self.z_min = z_min_max['minimum']
        self.z_max = z_min_max['maximum']

        self.range_x = self.x_max - self.x_min
        self.range_y = self.y_max - self.y_min
        self.range_z = self.z_max - self.z_min

    def data_array(self):
        data = []
        data.extend([self.x_min, self.x_max, self.y_min, self.y_max,
            self.z_min, self.z_max, self.range_x, self.range_y, self.range_z])
        return data


# def x_y_z_min_max(data):
    # x_min_max = min_max_selector(data, lambda frame: frame[0]) # X
    # y_min_max = min_max_selector(data, lambda frame: frame[1]) # Y
    # z_min_max = min_max_selector(data, lambda frame: frame[2]) # Z
    # return (x_min_max, y_min_max, z_min_max)


def min_max_selector(data, selector):
    if len(data) == 0:
        raise Exception('Min max selector was given data with length zero')

    minimum = selector(data[0])
    maximum =  selector(data[0])
    for frame in data[1:]:
        selected_data = selector(frame)
        if selected_data < minimum:
            minimum = selected_data
        if selected_data > maximum:
            maximum = selected_data
    return {'minimum': minimum, 'maximum': maximum)
