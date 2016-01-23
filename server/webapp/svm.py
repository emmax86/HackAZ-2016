from sklearn import svm
from sklearn.externals import joblib


class SVM:
    """
    Initialization of the Support Vector Machine.
    """
    def __init__(self, username, input_vectors, output_labels):
        self.person_name = username
        self.machine = svm.SVC()
        self.machine.fit(input_vectors, output_labels)
        self.new_output_label = -1

    """
    Use of trained Support Vector Machine to classify new
    data and insert it to new_output_label
    """
    def classify(self, new_data):
        self.new_output_label = self.machine.predict(new_data)

    """
    Return array of the newly labeled output values
    1 -> Defined as user NOT OK
    0 -> Defined as user IS OK
    """
    def labeled_new_data(self):
        return self.new_output_label

    """
    Dump contents of SVM to disk
    """
    def dump(self):
        joblib.dump(self.machine, self.person_name + ".pkl")
