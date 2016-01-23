def parse_docs(pebble_data=None, phone_data=None):
    if not pebble_data or not phone_data:
        raise Exception('Expected phone and pebble data, didn\'t receive both')

    pebble_datalayer = Data(pebble_data)
    phone_datalayer = Data(phone_data)


class Data(object):
    def __init__(self, phone_data, pebble_data):
        self.phone_data = phone_data
        phone_x_data = select_data(self.phone_data, lambda frame: frame[0])  # X
        self.phone_x_min = phone_x_data['minimum']
        self.phone_x_max = phone_x_data['maximum']
        self.phone_x_average = phone_x_data['average']
        phone_y_data = select_data(self.phone_data, lambda frame: frame[1])  # Y
        self.phone_y_min = phone_y_data['minimum']
        self.phone_y_max = phone_y_data['maximum']
        self.phone_y_average = phone_y_data['average']
        phone_z_data = select_data(self.phone_data, lambda frame: frame[2])  # Z
        self.phone_z_min = phone_z_data['minimum']
        self.phone_z_max = phone_z_data['maximum']
        self.phone_z_average = phone_z_data['average']

        self.phone_range_x = self.phone_x_max - self.phone_x_min
        self.phone_range_y = self.phone_y_max - self.phone_y_min
        self.phone_range_z = self.phone_z_max - self.phone_z_min

        self.pebble_data = pebble_data
        pebble_x_data = select_data(self.pebble_data, lambda frame: frame[0])  # X
        self.pebble_x_min = pebble_x_data['minimum']
        self.pebble_x_max = pebble_x_data['maximum']
        self.pebble_x_average = pebble_x_data['average']
        pebble_y_data = select_data(self.phone_data, lambda frame: frame[1])  # Y
        self.pebble_y_min = pebble_y_data['minimum']
        self.pebble_y_max = pebble_y_data['maximum']
        self.pebble_y_average = pebble_y_data['average']
        pebble_z_data = select_data(self.phone_data, lambda frame: frame[2])  # Z
        self.pebble_z_min = pebble_z_data['minimum']
        self.pebble_z_max = pebble_z_data['maximum']
        self.pebble_z_average = pebble_z_data['average']

        self.pebble_range_x = self.pebble_x_max - self.pebble_x_min
        self.pebble_range_y = self.pebble_y_max - self.pebble_y_min
        self.pebble_range_z = self.pebble_z_max - self.pebble_z_min

    def data_array(self):
        data = []
        data.extend([self.phone_x_min, self.phone_x_max, self.phone_y_min, self.phone_y_max,
                     self.phone_z_min, self.phone_z_max, self.phone_range_x, self.phone_range_y,
                     self.phone_range_z, self.pebble_x_min, self.pebble_x_max, self.pebble_y_min, self.pebble_y_max,
                     self.pebble_z_min, self.pebble_z_max, self.pebble_range_x, self.pebble_range_y,
                     self.pebble_range_z])

        return data

        # def x_y_z_min_max(data):
        # x_min_max = min_max_selector(data, lambda frame: frame[0]) # X
        # y_min_max = min_max_selector(data, lambda frame: frame[1]) # Y
        # z_min_max = min_max_selector(data, lambda frame: frame[2]) # Z
        # return (x_min_max, y_min_max, z_min_max)


def select_data(data, selector):
    if len(data) == 0:
        raise Exception('Min max selector was given data with length zero')

    minimum = selector(data[0])
    maximum = selector(data[0])
    sum = selector(data[0])
    for frame in data[1:]:
        selected_data = selector(frame)
        sum += selected_data
        if selected_data < minimum:
            minimum = selected_data
        if selected_data > maximum:
            maximum = selected_data
    average = sum / float(len(data))
    return {'minimum': minimum, 'maximum': maximum, 'average': average}
