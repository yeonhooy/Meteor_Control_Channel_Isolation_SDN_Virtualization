#!/bin/bash
OVXCTL='../../utils/ovxctl.py'

python $OVXCTL -n createNetwork tcp:10.0.0.3:10000 10.0.0.0 16
TENENT="1"

#switch
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:05 
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:06 

#Port
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:05 1 
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:05 2
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:06 1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:06 2

#connect Link
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 2 00:a4:23:05:00:00:00:02 1 spf 1 

#connect Host
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:01 1 00:00:00:00:00:05 #h05
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:02 2 00:00:00:00:00:06 #h06

python $OVXCTL -n startNetwork $TENENT
