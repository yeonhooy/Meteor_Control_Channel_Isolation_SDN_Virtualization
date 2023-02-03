# Author: Laura Kulowski

import numpy as np
import matplotlib.pyplot as plt
import torch
import random

def plot_train_test_results(lstm_model, Xtrain, Ytrain, Xtest, Ytest, num_rows = 720):
  '''
  plot examples of the lstm encoder-decoder evaluated on the training/test data
  
  : param lstm_model:     trained lstm encoder-decoder
  : param Xtrain:         np.array of windowed training input data
  : param Ytrain:         np.array of windowed training target data
  : param Xtest:          np.array of windowed test input data
  : param Ytest:          np.array of windowed test target data 
  : param num_rows:       number of training/test examples to plot
  : return:               num_rows x 2 plots; first column is training data predictions,
  :                       second column is test data predictions
  '''

  # input window size
  iw = Xtrain.shape[0]
  ow = Ytest.shape[0]
  print("plot_iw,ow")
  print(iw,ow) #10,5 갯수

  # figure setup 
  num_cols = 2
  num_plots = num_rows * num_cols

  fig, ax = plt.subplots(1, num_cols, figsize = (13, 50))
  target_num = 0 #1feature
  print("22222222220")
  print(Xtrain)
  print(Xtrain.shape)   #(60,7791,4)
  print(ax)
  print(ax.shape)
  #input("xtrain1")

  # plot training/test predictions
  result_test = []
  result_pred = []
  rv_list = []
  for ii in range(num_rows):
      rv = ii*20
      #print(rv)
      rv_list.append(rv)
      # train set
      #print(Xtrain)
      X_train_plt = Xtrain[:, rv, :]
      #print("x-train-plt")
      #print(X_train_plt)
      #print(X_train_plt.shape)   #(60,4)
      #input("d")
      Y_train_pred = lstm_model.predict(torch.from_numpy(X_train_plt).type(torch.Tensor), target_len = ow)
      #print(Y_train_pred)  #그 다음 20개


      #))))))))
      #print("ii",ii,rv)
      # ax[ii, 0].plot(np.arange(0, iw), Xtrain[:, rv, 0], 'k', linewidth = 2, label = 'Input')
      # ax[ii, 0].plot(np.arange(iw - 1, iw + ow), np.concatenate([[Xtrain[-1, rv, target_num]], Ytrain[:, rv, target_num]]),
      #                color = (0.2, 0.42, 0.72), linewidth = 2, label = 'Target')
      # ax[ii, 0].plot(np.arange(iw - 1, iw + ow),  np.concatenate([[Xtrain[-1, rv, target_num]], Y_train_pred[:, target_num]]),
      #                color = (0.76, 0.01, 0.01), linewidth = 2, label = 'Prediction')
      # ax[ii, 0].set_xlim([0, iw + ow - 1])
      # ax[ii, 0].set_xlabel('$t$')
      # ax[ii, 0].set_ylabel('$y$')

      # test set

      X_test_plt = Xtest[:, rv, :]
      Y_test_plt = Ytest[:, rv, :]
      Y_test_pred = lstm_model.predict(torch.from_numpy(X_test_plt).type(torch.Tensor), target_len = ow)
      # ax[ii, 1].plot(np.arange(0, iw), Xtest[:, rv, target_num], 'k', linewidth = 2, label = 'Input')
      # ax[ii, 1].plot(np.arange(iw - 1, iw + ow), np.concatenate([[Xtest[-1, rv, target_num]], Ytest[:, rv, target_num]]),
      #                color = (0.2, 0.42, 0.72), linewidth = 2, label = 'Target')
      # ax[ii, 1].plot(np.arange(iw - 1, iw + ow), np.concatenate([[Xtest[-1, rv, target_num]], Y_test_pred[:, target_num]]),
      #                color = (0.76, 0.01, 0.01), linewidth = 2, label = 'Prediction')
      # ax[ii, 1].set_xlim([0, iw + ow - 1])
      # ax[ii, 1].set_xlabel('$t$')
      # ax[ii, 1].set_ylabel('$y$')

      # if ii == 0:
      #   ax[ii, 0].set_title('Train')
      #
      #   ax[ii, 1].legend(bbox_to_anchor=(1, 1))
      #   ax[ii, 1].set_title('Test')

      result_test.append(Y_test_plt)
      result_pred.append(Y_test_pred)

  # plt.suptitle('LSTM Encoder-Decoder(TAPFLOW) Predictions', x = 0.445, y = 1.)
  # plt.tight_layout()
  # plt.subplots_adjust(top = 0.95)
  # plt.savefig('plots/predictions.png')
  # plt.close()


      
  #return Y_test_plt, Y_test_pred
  return result_test, result_pred, rv_list



def print_plot(Xtrain,Ytest,Ptest):
    num_rows = 4
    num_cols = 2

    fig, ax = plt.subplots(num_rows, num_cols, figsize=(13, 50))
    plt.savefig('plots/predictions_only.png')
    target_num = 6
    iw = Xtrain.shape[0]
    ow = Ytest.shape[0]
    print(iw,ow)
    print("0000000000000")
    print(Xtrain)
    print(Xtrain.shape)  #(60,1859,4)
    print(Ytest)
    print(Ytest.shape)
    for ii in range(num_rows):
        ii=0
        X_train_plt = Xtrain[:, ii, :]
        ax[ii, 0].plot(np.arange(0, iw), Xtrain[:, ii, 0], 'k', linewidth=2, label='Input')
        ax[ii, 0].plot(np.arange(iw - 1, iw + ow),
                       np.concatenate([[Xtrain[-1, ii, target_num]], Ytest[:, target_num]]),
                       color=(0.2, 0.42, 0.72), linewidth=2, label='Target')
        ax[ii, 0].plot(np.arange(iw - 1, iw + ow),
                       np.concatenate([[Xtrain[-1, ii, target_num]], Ptest[:, target_num]]),
                       color=(0.76, 0.01, 0.01), linewidth=2, label='Prediction')
        ax[ii, 0].set_xlim([0, iw + ow - 1])
        ax[ii, 0].set_xlabel('$t$')
        ax[ii, 0].set_ylabel('$y$')

        # # test set
        # X_test_plt = Xtest[:, ii, :]
        # ax[ii, 1].plot(np.arange(0, iw), Xtest[:, ii, target_num], 'k', linewidth=2, label='Input')
        # ax[ii, 1].plot(np.arange(iw - 1, iw + ow),
        #                np.concatenate([[Xtest[-1, ii, target_num]], Ytest[:, ii, target_num]]),
        #                color=(0.2, 0.42, 0.72), linewidth=2, label='Target')
        # ax[ii, 1].plot(np.arange(iw - 1, iw + ow),
        #                np.concatenate([[Xtest[-1, ii, target_num]], Y_test_pred[:, target_num]]),
        #                color=(0.76, 0.01, 0.01), linewidth=2, label='Prediction')
        # ax[ii, 1].set_xlim([0, iw + ow - 1])
        # ax[ii, 1].set_xlabel('$t$')
        # ax[ii, 1].set_ylabel('$y$')

        if ii == 0:
            ax[ii, 0].set_title('Test')

            #ax[ii, 1].legend(bbox_to_anchor=(1, 1))
            #ax[ii, 1].set_title('Test')


    plt.suptitle('TAPFLOW Predictions', x=0.445, y=1.)
    plt.tight_layout()
    plt.subplots_adjust(top=0.95)
    plt.savefig('plots/predictions_only.png')
    plt.close()
    print("ppp")

def print_plot_list(Xtest_list,Ytest_list,Ptest_list,rv_list):
    num_rows = 10
    num_cols = 2

    fig, ax = plt.subplots(num_rows, num_cols, figsize=(13, 50))
    plt.savefig('plots/predictions_originValue.png')
    target_num = 5

    for ii in range(num_rows):
        xi = rv_list[ii]
        print("xi",xi)

        Xtest = Xtest_list[ii]
        Ytest = Ytest_list[ii]
        Ptest = Ptest_list[ii]
        iw = Xtest.shape[0]
        ow = Ytest.shape[0]
        print(iw, ow)   # 60,60
        print("0000000000000")
        # print(Xtrain)
        # print(Xtrain.shape)  # (60,1859,4)
        # print(Ytest)
        # print(Ytest.shape)

        #print(Xtest[:,target_num])
        #input("xtest")
        ax[ii, 0].plot(np.arange(0, iw), Xtest[:, target_num], 'k', linewidth=2, label='Input throughput')
        ax[ii, 0].plot(np.arange(iw - 1, iw + ow),
                       np.concatenate([[Xtest[-1, target_num]], Ytest[:, target_num]]),
                       color=(0.2, 0.42, 0.72), linewidth=2, label='Target')
        ax[ii, 0].plot(np.arange(iw - 1, iw + ow),
                       np.concatenate([[Xtest[-1, target_num]], Ptest[:, target_num]]),
                       color=(0.76, 0.01, 0.01), linewidth=2, label='Prediction')

        # ax[ii, 0].plot(np.arange(0, iw), Xtrain[:, xi, 0], 'k', linewidth=2, label='Input throughput')
        # ax[ii, 0].plot(np.arange(iw - 1, iw + ow),
        #                np.concatenate([[Xtrain[-1, xi, target_num]], Ytest[:, target_num]]),
        #                color=(0.2, 0.42, 0.72), linewidth=2, label='Target')
        # ax[ii, 0].plot(np.arange(iw - 1, iw + ow),
        #                np.concatenate([[Xtrain[-1, xi, target_num]], Ptest[:, target_num]]),
        #                color=(0.76, 0.01, 0.01), linewidth=2, label='Prediction')


        ax[ii, 0].set_xlim([0, iw + ow - 1])
        ax[ii, 0].set_xlabel('$t$')
        ax[ii, 0].set_ylabel('$y$')

        # # test set
        # X_test_plt = Xtest[:, ii, :]
        # ax[ii, 1].plot(np.arange(0, iw), Xtest[:, ii, target_num], 'k', linewidth=2, label='Input')
        # ax[ii, 1].plot(np.arange(iw - 1, iw + ow),
        #                np.concatenate([[Xtest[-1, ii, target_num]], Ytest[:, ii, target_num]]),
        #                color=(0.2, 0.42, 0.72), linewidth=2, label='Target')
        # ax[ii, 1].plot(np.arange(iw - 1, iw + ow),
        #                np.concatenate([[Xtest[-1, ii, target_num]], Y_test_pred[:, target_num]]),
        #                color=(0.76, 0.01, 0.01), linewidth=2, label='Prediction')
        # ax[ii, 1].set_xlim([0, iw + ow - 1])
        # ax[ii, 1].set_xlabel('$t$')
        # ax[ii, 1].set_ylabel('$y$')

        if ii == 0:
            ax[ii, 0].set_title('Test')

            #ax[ii, 1].legend(bbox_to_anchor=(1, 1))
            #ax[ii, 1].set_title('Test')


    plt.suptitle('TAPFLOW Predictions', x=0.445, y=1.)
    plt.tight_layout()
    plt.subplots_adjust(top=0.95)
    plt.savefig('plots/predictions_originValue.png')
    plt.close()
    print("ppp")