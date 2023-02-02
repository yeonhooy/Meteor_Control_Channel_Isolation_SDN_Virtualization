#!/bin/bash
OVXCTL='../utils/ovxctl.py'

python $OVXCTL -n createNetwork tcp:10.0.0.3:10001 10.0.0.0 16
TENENT="2"
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:07
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:07

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 3

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 3

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 4


python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 1 00:a4:23:05:00:00:00:02 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 2 00:a4:23:05:00:00:00:03 1 spf 1

python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 2 00:a4:23:05:00:00:00:04 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 2 00:a4:23:05:00:00:00:05 1 spf 1

python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:04 2 00:00:00:00:00:02
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:05 2 00:00:00:00:00:0e

python $OVXCTL -n startNetwork $TENENT
