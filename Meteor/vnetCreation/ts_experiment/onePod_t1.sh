#!/bin/bash
OVXCTL='../../utils/ovxctl.py'

python $OVXCTL -n createNetwork tcp:10.0.0.3:10000 10.0.0.0 16
TENENT="1"

#switch
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:01 #s3001 00:a4:23:05:00:00:00:01

#Port
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 3 #s3001 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 4 #s3001 p4

#connect Link

#connect Host
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:01 1 00:00:00:00:00:03 #h017
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:01 2 00:00:00:00:00:13 #h018

python $OVXCTL -n startNetwork $TENENT
