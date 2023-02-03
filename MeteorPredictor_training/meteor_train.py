# Author: Laura Kulowski

'''

Example of using a LSTM encoder-decoder to model a synthetic time series 

'''

import numpy as np
import pandas as pd
import matplotlib
import matplotlib.pyplot as plt
from importlib import reload
import sys
import argparse

import torch
import torch.utils
import os
import generate_dataset
import lstm_encoder_decoder
import plotting
import random
import sympy as sp
from sympy import Integral

from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error

def inverse_result(Xtest_origin, result_y, result_pred, rv_list):
    num_result = len(result_y)
    trans_x_list = []
    trans_tests = []
    trans_preds = []
    k_list = []
    kr_list = []
    target_num = 0

    for i in range(num_result):
        rv = rv_list[i]
        x_rv = Xtest_origin[:, rv, :]
        print("sshpae",x_rv.shape)
        print(x_rv)

        trans_x = scaler.inverse_transform(x_rv)
        trans_test = scaler.inverse_transform(result_y[i])
        trans_pred = scaler.inverse_transform(result_pred[i])
        trans_x_list.append(trans_x)
        trans_tests.append(trans_test)
        trans_preds.append(trans_pred)

        pred_read = trans_pred[:,target_num]
        real_read = trans_test[:, target_num]
        pred_len = len(pred_read)
        for t in range(pred_len):
            if pred_read[t] < 200:
                pred_read[t] = real_read[t]
        print(pred_read)

        print(real_read, pred_read)
        f=open("real-predict(1f-rnn).txt", "a")
        f.write("@\n")
        for x in range(len(pred_read)):
            f.write(str(real_read[x]))
            f.write(", ")
        f.write("\n")
        for y in range(len(pred_read)):
            f.write(str(pred_read[y]))
            f.write(", ")
        f.write("\n")


        k, k_real = integrage(i,20, real_read,pred_read)
        k_list.append(k)
        kr_list.append(k_real)

        filek = open("kvalue(1f-rnn).txt", "a")
        filek.write(str(k_real))
        filek.write(",")
        filek.write(str(k))
        filek.write("\n")
    return k_list, kr_list
    plotting.print_plot_list(trans_x_list, trans_tests,trans_preds, rv_list)
def apprroxy(x_t,y_t):
    x_np = np.array(x_t)
    y_np = np.array(y_t)

    fit = np.polyfit(x_np,y_np,5)
    print(fit)
    from sympy import poly
    x = sp.Symbol('x')  # Create a symbol x
    coefficients = fit  # Your coefficients a python list

    p1 = sum(coef * x ** i for i, coef in
             enumerate(reversed(coefficients)))  # expression to generate a polynomial from coefficients.
    print(p1)
    print(type(p1))

    fit_y = []
    #fit_y ==> 근사함수에 대한 그래프 데이터
    for i in range(len(y_t)):
        fitY = p1.subs(x,i)
        if fitY < 0:
            fitY = 0
        fit_y.append(fitY)
    print(fit_y)
    print(x_t)
    #plt.plot(x_t,fit_y,label="fit")
    plt.plot(x_t,fit_y,linestyle='dashed',label="fit_value")

    return p1,x,x_t

def integral_order(p1,x,x_t,plt):

    integralf = Integral(p1,x).doit()
    t = sp.Symbol('t')
    inte_20 = integralf.subs(x,20)
    inte_t = integralf.subs(x,t)
    inte_0 = integralf.subs(x,0)
    inte_area = inte_20-inte_t
    inte_area_inverse = inte_t-inte_0
    t_func = (1/(20-t))*inte_area - p1.subs(x,t)
    t_func_inverse = (1/t)*inte_area_inverse - p1.subs(x,t)

    sol = sp.solvers.solve(t_func_inverse,t)
    print("sol",sol)
    sol_list = []
    if sol:

        for j in range(len(sol)):
            a = sol[j]
            try:
                if a < 20 and a > 0:
                    sol_list.append(a)
            except:
                pass
        if sol_list:
            print(sol_list)
            sol_max = max(sol_list)
            for l in range(len(sol_list)):
                if l==len(sol_list)-1:
                    continue
                k = p1.subs(x,sol_list[l])
                ps = []
                label_l = "t="+str(sol_list[l])
                for p in range(20):
                    ps.append(k)
                plt.plot(x_t,ps,label=label_l)
            k_max = p1.subs(x,sol_max)
            plt.scatter(sol_max,0, s=100, c='r')
            ps_max = []
            for p in range(20):
                ps_max.append(k_max)


            label_max = "k_max="+str(sol_max)
            plt.plot(x_t, ps_max, linestyle='dashed',c='r',label=label_max)

    return sol_list

def integral_inverse(p1,x,x_t,k_sq):

    integralf = Integral(p1,x).doit()
    t = sp.Symbol('t')
    inte_20 = integralf.subs(x,20)
    inte_t = integralf.subs(x,t)
    inte_0 = integralf.subs(x,0)
    inte_area = inte_20-inte_t
    inte_area_inverse = inte_t-inte_0
    t_func = (1/(20-t))*inte_area - p1.subs(x,t)

    sol = sp.solvers.solve(t_func,t)
    sols = []
# 20-t ~ 20
    for j in range(len(sol)):
        try:
            if sol[j] < 20 and sol[j] > 0:
                sols.append(sol[j])
        except:
            pass
    print(sols)
    sol_min = min(sols)
    for l in range(len(sols)):
        k = p1.subs(x,sols[l])
        ps = []
        for p in range(20):
            ps.append(k)
        plt.plot(x_t,ps)
    k = p1.subs(x,sol_min)
    ps = []
    for p in range(20):
        ps.append(k)
    plt.plot(x_t,ps,label='k_min')
    plt.legend()
    plt.show()

def integrage(graph_id,iw,real,pred):
    x = []
    y = []
    ks =[]
    kr = []
    for i in range(iw):
        x.append(i)
        if pred[i] < 0:
            y.append(0)
        else:
            y.append(pred[i])

    area_pred = np.trapz(y=y, x=x)
    area_real = np.trapz(y=real, x=x)
    k_square = area_pred / 20
    k_real_square = area_real / 20
    for i in range(iw):
        ks.append(k_square)
        kr.append(k_real_square)

    plt.suptitle('Meteor Predictions area=%s' % area_pred, x=20, y=20)
    plt.axis([-1,20,-1,20000])

    plt.plot(x, y, label='prediction')
    plt.plot(x, real, label='real value')
    plt.plot(x, ks, label='k_pred_square')
    plt.plot(x, kr, label='k_real_square')
    #근사함수
    p1,x_symbol,x_p1 = apprroxy(x,y)
    # 교차점
    sols = integral_order(p1, x_symbol, x_p1, plt)

    plt.rc('legend',fontsize=7)
    plt.legend(loc='center left', bbox_to_anchor=(0.5,1))
    #plt.savefig('plots/prediction_graph_%s.png' %graph_id)
    plt.cla()

    return k_square, k_real_square



parser = argparse.ArgumentParser(description='Trainig Meteor')
parser.add_argument('--dataset', help='put dataset path')
parser.add_argument('--model', help='put model path')
parser.add_argument('--io', help='input window size')
parser.add_argument('--wo', help='output window size')
args = parser.parse_args()


matplotlib.rcParams.update({'font.size': 17})

a=torch.cuda.is_available()
os.environ["CUDA_VISIBLE_DEVICES"]="0"
device = torch.device('cuda' if a else 'cpu')
print(device)
print(torch.cuda.get_device_name(0))



#----------------------------------------------------------------------------------------------------------------
# generate dataset for LSTM
dataset = pd.read_csv(args.dataset)

y = dataset.drop(labels=['Time'], axis=1) #4개의 Input data / label
origin_y = y
t = dataset['Time']


scaler = MinMaxScaler()
scale_cols = ['hostNum','slink','packetin','stat','flowmod','readThroughput']
scale_cols = ['readThroughput']
scaler.fit(y)
df_scaled = scaler.fit_transform(y[scale_cols])
df_scaled = pd.DataFrame(df_scaled)
df_scaled.columns = scale_cols
y = df_scaled

t = np.array(t)
y = np.array(y)
target_num=0  #output feature
t_train, y_train, t_test, y_test, y_train_origin, y_test_origin = generate_dataset.train_test_split(t, y, split = 0.9)

print(np.transpose(y))
# plot time series 
plt.figure(figsize = (18, 6))
plt.plot(t, np.transpose(y)[target_num], color = 'k', linewidth = 2)
plt.xlim([t[0], t[-1]])
plt.xlabel('$t$')
plt.ylabel('$y$')
plt.title('Synthetic Time Series')
plt.savefig('plots/synthetic_time_series.png')

# plot time series with train/test split
plt.figure(figsize = (18, 6))
plt.plot(t_train, np.transpose(y_train)[target_num], color = '0.4', linewidth = 2, label = 'Train')
y_trans_train=np.transpose(y_train)[target_num]
y_trans_test=np.transpose(t_test)[target_num]
plt.plot(np.concatenate([[t_train[-1]], t_test]), np.concatenate([[y_train[-1][target_num]], np.transpose(y_test)[target_num]]),
         color = (0.74, 0.37, 0.22), linewidth = 2, label = 'Test')
plt.xlim([t[0], t[-1]])
plt.xlabel(r'$t$')
plt.ylabel(r'$y$')
plt.title('Time Series Split into Train and Test Sets')
plt.legend(bbox_to_anchor=(1, 1))
plt.tight_layout
plt.savefig('plots/train_test_split.png')

#----------------------------------------------------------------------------------------------------------------
# window dataset

# set size of input/output windows 
iw = int(args.io)
ow = int(args.wo)
s = 1
num_feautre = target_num+1

# generate windowed training/test datasets
Xtrain, Ytrain= generate_dataset.windowed_dataset(y_train, input_window = iw, output_window = ow, stride = s)
Xtest, Ytest = generate_dataset.windowed_dataset(y_test, input_window = iw, output_window = ow, stride = s)


# plot example of windowed data  
plt.figure(figsize = (10, 6)) 
plt.plot(np.arange(0, iw), Xtrain[:, 0, target_num], 'k', linewidth = 2.2, label = 'Input')
plt.plot(np.arange(iw - 1, iw + ow), np.concatenate([[Xtrain[-1, 0, target_num]], Ytrain[:, 0, target_num]]),
         color = (0.2, 0.42, 0.72), linewidth = 2.2, label = 'Target')
plt.xlim([0, iw + ow - 1])
plt.xlabel(r'$t$')
plt.ylabel(r'$y$')
plt.title('Example of Windowed Training Data')
plt.legend(bbox_to_anchor=(1.3, 1))
plt.tight_layout() 
plt.savefig('plots/windowed_data.png')
plt.cla()

#----------------------------------------------------------------------------------------------------------------
# LSTM encoder-decoder
# convert windowed data from np.array to PyTorch tensor
X_train, Y_train, X_test, Y_test = generate_dataset.numpy_to_torch(Xtrain, Ytrain, Xtest, Ytest)

# ---------------------------------------
# model save
PATH = args.model

# specify model parameters and train
print(X_train.shape)
print("Xtrain_shape[1]",X_train.shape[1])
print("Xtrain_shape[2]",X_train.shape[2])

print("PATH: ",PATH)
X_train = X_train.to(device)
Y_train = Y_train.to(device)
model = lstm_encoder_decoder.lstm_seq2seq(input_size = X_train.shape[2], hidden_size = 64).cuda()
loss = model.train_model(X_train, Y_train, n_epochs = 1000, target_len = ow, batch_size = 720, training_prediction = 'mixed_teacher_forcing', teacher_forcing_ratio = 0.5, learning_rate = 0.01, dynamic_tf = False)
#mixed_teacher_forcing
#save model
torch.save(model, PATH)

# return list
test_list, pred_list, rv_list = plotting.plot_train_test_results(model, Xtrain, Ytrain, Xtest, Ytest)
inverse_result(Xtest, test_list, pred_list, rv_list)

plt.close('all')

