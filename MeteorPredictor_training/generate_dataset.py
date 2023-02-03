# Author: Laura Kulowski Copyright (c) 2020 Laura Kulowski

'''
#MIT License
#
#Copyright (c) 2020 Laura Kulowski
#
#Permission is hereby granted, free of charge, to any person obtaining a copy
#of this software and associated documentation files (the "Software"), to deal
#in the Software without restriction, including without limitation the rights
#to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#copies of the Software, and to permit persons to whom the Software is
#furnished to do so, subject to the following conditions:
#
#The above copyright notice and this permission notice shall be included in all
#copies or substantial portions of the Software.
#
'''

'''

Generate a synthetic dataset for our LSTM encoder-decoder
We will consider a noisy sinusoidal curve 

'''

import numpy as np
import torch

def synthetic_data(Nt = 2000, tf = 80 * np.pi):
    
    '''
    create synthetic time series dataset
    : param Nt:       number of time steps 
    : param tf:       final time
    : return t, y:    time, feature arrays
    '''

    t = np.linspace(0., tf, Nt)
    y = np.sin(2. * t) + 0.5 * np.cos(t) + np.random.normal(0., 0.2, Nt)

    return t, y

def train_test_split(t, y, split = 0.8):

  '''
  
  split time series into train/test sets
  
  : param t:                      time array
  : para y:                       feature array
  : para split:                   percent of data to include in training set 
  : return t_train, y_train:      time/feature training and test sets;  
  :        t_test, y_test:        (shape: [# samples, 1])
  
  '''
  #input("trainsplot")
  indx_split = int(split * len(y))
  indx_train = np.arange(0, indx_split)
  indx_test = np.arange(indx_split, len(y))

  t_train = t[indx_train]
  y_train = y[indx_train]
  y_train_origin = y_train
  y_train = y_train.reshape(-1, 1) #feature
  #y_train = y_train.reshape(-1, 2)
  
  t_test = t[indx_test]
  y_test = y[indx_test]
  y_test_origin = y_test
  y_test = y_test.reshape(-1, 1)#feature
  #y_test = y_test.reshape(-1, 2)

  return t_train, y_train, t_test, y_test, y_train_origin, y_test_origin


def windowed_dataset(y, input_window = 20, output_window = 20, stride = 20, num_features = 1):
  
    '''
    create a windowed dataset
    
    : param y:                time series feature (array)
    : param input_window:     number of y samples to give model 
    : param output_window:    number of future y samples to predict  
    : param stide:            spacing between windows   
    : param num_features:     number of features (i.e., 1 for us, but we could have multiple features)
    : return X, Y:            arrays with correct dimensions for LSTM
    :                         (i.e., [input/output window size # examples, # features])
    '''
  
    L = y.shape[0]
    
    num_samples = (L - input_window - output_window) // stride + 1
    X = np.zeros([input_window, num_samples, num_features])
    print(output_window, num_samples, num_features)
    Y = np.zeros([output_window, num_samples, num_features])    
    
    for ff in np.arange(num_features):
        for ii in np.arange(num_samples):
            start_x = stride * ii
            end_x = start_x + input_window
            X[:, ii, ff] = y[start_x:end_x, ff]

            start_y = stride * ii + input_window
            end_y = start_y + output_window 
            Y[:, ii, ff] = y[start_y:end_y, ff]

    print(X.shape, Y.shape)
    return X, Y


def numpy_to_torch(Xtrain, Ytrain, Xtest, Ytest):
    '''
    convert numpy array to PyTorch tensor
    
    : param Xtrain:                           windowed training input data (input window size, # examples, # features); np.array
    : param Ytrain:                           windowed training target data (output window size, # examples, # features); np.array
    : param Xtest:                            windowed test input data (input window size, # examples, # features); np.array
    : param Ytest:                            windowed test target data (output window size, # examples, # features); np.array
    : return X_train_torch, Y_train_torch,
    :        X_test_torch, Y_test_torch:      all input np.arrays converted to PyTorch tensors 

    '''
    print(Xtrain)
    X_train_torch = torch.from_numpy(Xtrain).type(torch.Tensor)
    print(X_train_torch)
    Y_train_torch = torch.from_numpy(Ytrain).type(torch.Tensor)

    X_test_torch = torch.from_numpy(Xtest).type(torch.Tensor)
    Y_test_torch = torch.from_numpy(Ytest).type(torch.Tensor)
    
    return X_train_torch, Y_train_torch, X_test_torch, Y_test_torch
