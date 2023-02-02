#!/bin/bash
OVXCTL='../utils/ovxctl.py'
python $OVXCTL -n createNetwork tcp:10.0.0.3:10000 10.0.0.0 16


python $OVXCTL -n createSwitch 1 00:00:00:00:00:00:00:01
python $OVXCTL -n createSwitch 1 00:00:00:00:00:00:00:02
python $OVXCTL -n createSwitch 1 00:00:00:00:00:00:00:03

python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:01 1
python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:01 2
python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:01 3

python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:02 1
python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:02 2
python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:02 3

python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:03 1
python $OVXCTL -n createPort 1 00:00:00:00:00:00:00:03 2


python $OVXCTL -n connectLink 1 00:a4:23:05:00:00:00:01 2 00:a4:23:05:00:00:00:02 2 spf 1
python $OVXCTL -n connectLink 1 00:a4:23:05:00:00:00:01 3 00:a4:23:05:00:00:00:03 1 spf 1
python $OVXCTL -n connectLink 1 00:a4:23:05:00:00:00:02 3 00:a4:23:05:00:00:00:03 2 spf 1

python $OVXCTL -n connectHost 1 00:a4:23:05:00:00:00:01 1 00:aa:aa:05:00:01
python $OVXCTL -n connectHost 1 00:a4:23:05:00:00:00:02 1 00:aa:aa:05:00:02


python $OVXCTL -n startNetwork 1
