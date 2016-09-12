import matplotlib.pyplot as plt
import numpy as np
np.random.seed(42)

def sigmoid(x):
    return 1 / (1 + np.exp(-x))

def dsigmoid(x):
    return x * (1 - x)

class Model:

    def __init__(self, layers, learning_rate=0.01):
        self.layers = layers
        self.learning_rate = learning_rate

    def iteration(self, X, y):
        out = X
        i = 0
        while i < len(self.layers):
            # print(i)
            out = layers[i].forward(out)
            i += 1
        i -= 1
        # print(layers[i].calculateError(y, out))
        deriv_err = layers[i].calculateDerivError(y, out)
        while(i >= 0):
            deriv_err = layers[i].backward(deriv_err).T
            i -= 1

    def train(self, X, y, number_epochs):
        for i in range(number_epochs):
            self.iteration(X, y)
            self.reportAccuracy(X, y)

    def reportAccuracy(self, X, y):
        out = X
        i = 0
        while i < len(self.layers):
            out = layers[i].forward(out)
            i += 1
        out = np.round(out)
        count = np.count_nonzero(y - out)
        correct = len(X) - count
        print("%.4f" % (float(correct)*100.0 / len(X)))

class Layer:

    def __init__(self, sizeofin, sizeofout, learning_rate):
        self.sizeofin = sizeofin
        self.sizeofout = sizeofout
        self.weights = np.random.rand(sizeofin, sizeofout)
        self.learning_rate = learning_rate

    def forward(self, X):
        self.incoming = X
        # print(X.shape, self.weights.shape)
        act = X.dot(self.weights)
        act = sigmoid(act)
        self.outputs = act
        return act

    def backward(self, err):
        # print(err[0])
        err = err * dsigmoid(self.outputs)
        update =  self.learning_rate * self.incoming.T.dot(err)
        sum_to_pass = self.weights.dot(err.T)
        self.weights += update
        return sum_to_pass

    def calculateDerivError(self, y, pred):
        return 2*(y - pred)

    def calculateError(self, y, pred):
        return (np.sum(np.power((y - pred), 2)))

def loadDataset(filename='breast_cancer.csv'):
    my_data = np.genfromtxt(filename, delimiter=',', skip_header=1)

    # The labels of the cases
    # Raw labels are either 4 (cancer) or 2 (no cancer)
    # Normalize these classes to 0/1
    y = (my_data[:, 10] / 2) - 1

    # Case features
    X = my_data[:, :10]

    # Normalize the features to (0, 1)
    X_norm = np.round(X / X.max(axis=0))

    return X_norm, y



def gradientChecker(model, X, y):
    epsilon = 1E-5

    model.weights[1] += epsilon
    out1 = model.forward(X)
    err1 = model.calculateError(y, out1)

    model.weights[1] -= 2*epsilon
    out2 = model.forward(X)
    err2 = model.calculateError(y, out2)

    numeric = (err2 - err1) / (2*epsilon)
    print(numeric)

    model.weights[1] += epsilon
    out3 = model.forward(X)
    err3 = model.calculateDerivError(y, out3)
    derivs = model.backward(err3)
    print(derivs[1])

if __name__=="__main__":
    X, y = loadDataset()

    # X = X
    print(X)
    print(y)
    y = y.reshape(683, 1)
    # print(X.shape, y.shape)
    learning_rate = 0.001
    layers = []
    layer1 = Layer(10, 25, learning_rate)
    layer2 = Layer(25, 1, learning_rate)
    layers.append(layer1)
    layers.append(layer2)
    model = Model(layers, learning_rate)
    # gradientChecker(model, X, y)
    model.train(X, y, 1000)
