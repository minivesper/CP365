import matplotlib.pyplot as plt
import numpy as np
np.random.seed(42) # Get the same random numbers every time


# Ugly code for thinking about linear regression with gradient descent

################################################################
### Load the dataset
my_data = np.genfromtxt('djia_temp.csv', delimiter=';', skip_header=1)[:10]
high_temp = my_data[:, 2]
djia_close = my_data[:, 1]
average_high_temp = my_data[:, 3]

################################################################
### Init the model parameters
weight = np.random.rand(1)
bias = np.random.rand(1)

temp = average_high_temp - high_temp
learning_rate = 0.01

################################################################
### How do we change the weight and the bias to make the line's fit better?
def linearRegression(indepv, depv, learning_rate):
    weight = np.random.rand(1)
    bias = np.random.rand(1)
    significantChange = True
    end_cost = 0
    init_cost = calcCost(indepv, depv, weight, bias)
    n = 0
    error = calcError(indepv, depv, weight, bias)
    l = ['a']
    print(l)
    l. remove('a')
    print(l)

    while(significantChange):
        n = n + 1
        if((init_cost - end_cost) > 0.001):
            init_cost = np.sum(np.power(error, 2))
            weight = weight - np.sum(learning_rate * error * temp / len(indepv))
            bias = bias - np.sum(learning_rate * error * 1.0 / len(indepv))
            error = calcError(indepv, depv, weight, bias)
            end_cost = np.sum(np.power(error, 2))  
        else:
            significantChange = False
    print("Final cost=")
    print(end_cost)

def calcError(indepv, depv, weight, bias):
    return (indepv*weight+bias) - depv

def calcCost(indepv, depv, weight, bias):
    return np.sum(np.power((indepv*weight+bias) - depv, 2))

linearRegression(temp, djia_close, learning_rate)

################################################################
### Graph the dataset along with the line defined by the model
#
# xs = np.arange(0, 5)
# ys = xs * weight + bias
#
# plt.plot(high_temp, djia_close, 'r+', xs, ys, 'g-')
# plt.show()
