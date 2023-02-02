
import numpy as np
import pandas as pd
import sys
import torch
import sympy as sp
from sympy import Integral

from sklearn.preprocessing import MinMaxScaler
from threading import Thread
from multiprocessing import Process
import os


def inferenceAll(wId, tId, swId, wSize):
    dirPath = os.path.dirname(os.path.realpath(__file__))
    windowId = wId
    wsize = wSize
    channelId = str(tId)+'_'+str(swId)
    datapath = dirPath+'/inverseData/' + 'inverse_' + str(windowId) + "_" + str(channelId) + ".txt"
    dataset = pd.read_csv(datapath, delimiter=',')
    sumbw = 0
    lencount = len(dataset)

    for l in range(lencount):
        sumbw = sumbw + dataset.loc[l][6]

    dataset.iloc[0:l + 1][['readThroughput']] = sumbw
    y = dataset.drop(labels=['Time'], axis=1) 
    origin_y = y
    t = dataset['Time']

    scaler = MinMaxScaler()
    scale_cols = ['hostNum', 'slink', 'packetin', 'stat', 'flowmod', 'readThroughput']

    scaler.fit(y)
    df_scaled = scaler.fit_transform(y[scale_cols])
    df_scaled = pd.DataFrame(df_scaled)
    df_scaled.columns = scale_cols
    y = df_scaled
    t = np.array(t)

    y = np.array(y)

    target_num = 5
    
    modelPath = dirPath+"/model/meteorPredictor.pt"
    model = torch.load(modelPath, map_location=torch.device('cpu'))

    # return list
    graph_id = str(windowId) + "_" + str(channelId)
    Y_test_pred = model.predict(torch.from_numpy(y).type(torch.Tensor), target_len=wsize)

    x_rv = y
    
    trans_x_list = []
    trans_preds = []
    trans_x = scaler.inverse_transform(x_rv)
    trans_pred = scaler.inverse_transform(Y_test_pred)
    trans_x_list.append(trans_x)
    trans_preds.append(trans_pred)

    input_target = trans_x[:, target_num]
    pred_read = trans_pred[:, target_num]
    pred_len = len(pred_read)
    for t in range(pred_len):
        if pred_read[t] < 100:
            pred_read[t] = 0
            
    iw = len(input_target)
    
    x_input = []
    x_output = []
    x_outputzero = []
    y = []
    ks = []

    for i in range(iw):
        x_input.append(i)
    for i in range(wsize):
        x_output.append(i + iw)
        x_outputzero.append(i)
        if pred_read[i] < 0:
            y.append(0)
        else:
            y.append(pred_read[i])
            
    area_pred = np.trapz(y=y, x=x_outputzero)
    k_square = area_pred / wsize

    for i in range(wsize):
        ks.append(k_square)

    print(tId,swId, int(k_square))

if __name__ == '__main__':
    windowId = sys.argv[1]
    tenantId = sys.argv[2]
    MaxswitchId = sys.argv[3]


    for k in range(int(MaxswitchId)):
        Process(target=inferenceAll,args=(windowId,int(tenantId),k+1,20,)).start()





