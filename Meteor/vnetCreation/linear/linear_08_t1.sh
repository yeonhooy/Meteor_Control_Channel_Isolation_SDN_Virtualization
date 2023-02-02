#!/bin/bash
OVXCTL='../../utils/ovxctl.py'

python $OVXCTL -n createNetwork tcp:10.0.0.3:10000 10.0.0.0 16
TENENT="1"

#switch

python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:01 #s1001 00:a4:23:05:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:02 #s1001 00:a4:23:05:00:00:00:02
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:03 #s1001 00:a4:23:05:00:00:00:03
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:04 #s1001 00:a4:23:05:00:00:00:04
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:05 #s1001 00:a4:23:05:00:00:00:05
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:06 #s1001 00:a4:23:05:00:00:00:06
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:07 #s1001 00:a4:23:05:00:00:00:07
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:08 #s1001 00:a4:23:05:00:00:00:08


#Port
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 2 #s1001 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 2 #s1001 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 2 #s1001 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 2 #s1001 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:05 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:05 2 #s1001 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:06 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:06 2 #s1001 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:07 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:07 2 #s1001 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:08 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:08 2 #s1001 p2



#connect Link
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 2 00:a4:23:05:00:00:00:02 1 spf 1  
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 2 00:a4:23:05:00:00:00:03 1 spf 1  
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 2 00:a4:23:05:00:00:00:04 1 spf 1  
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:04 2 00:a4:23:05:00:00:00:05 1 spf 1  
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:05 2 00:a4:23:05:00:00:00:06 1 spf 1  
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:06 2 00:a4:23:05:00:00:00:07 1 spf 1  
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:07 2 00:a4:23:05:00:00:00:08 1 spf 1   


#connect Host
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:01 1 00:00:00:00:00:07 #h001
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:08 2 00:00:00:00:00:08 #h002


python $OVXCTL -n startNetwork $TENENT
