#!/bin/bash
OVXCTL='../utils/ovxctl.py'

python $OVXCTL -n createNetwork tcp:10.0.0.3:10001 10.0.0.0 16
TENENT="2"

#switch
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:02 #s1002 00:a4:23:05:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:04 #s1003 00:a4:23:05:00:00:00:02
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:01 #s2001 00:a4:23:05:00:00:00:03
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:02 #s2002 00:a4:23:05:00:00:00:04
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:03 #s2003 00:a4:23:05:00:00:00:05
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:04 #s2004 00:a4:23:05:00:00:00:06
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:01 #s3001 00:a4:23:05:00:00:00:07
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:02 #s3002 00:a4:23:05:00:00:00:08
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:03 #s3003 00:a4:23:05:00:00:00:09
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:04 #s3004 00:a4:23:05:00:00:00:0a

#Port
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 1 #s1002 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 2 #s1002 p2

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 1 #s1004 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 2 #s1004 p2

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 2 #s2001 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 3 #s2001 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 4 #s2001 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 2 #s2002 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 3 #s2002 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 4 #s2002 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:03 2 #s2003 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:03 3 #s2003 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:03 4 #s2003 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:04 2 #s2004 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:04 3 #s2004 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:04 4 #s2004 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 1 #s3001 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 2 #s3001 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 4 #s3001 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 1 #s3002 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 2 #s3002 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 4 #s3002 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:03 1 #s3003 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:03 2 #s3003 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:03 4 #s3003 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:04 1 #s3004 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:04 2 #s3004 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:04 4 #s3004 p4

#connect Link
#1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 1 00:a4:23:05:00:00:00:03 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 2 00:a4:23:05:00:00:00:05 1 spf 1
#2
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 1 00:a4:23:05:00:00:00:04 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 2 00:a4:23:05:00:00:00:06 1 spf 1
#3
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 2 00:a4:23:05:00:00:00:07 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 3 00:a4:23:05:00:00:00:08 1 spf 1
#4
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:04 2 00:a4:23:05:00:00:00:07 2 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:04 3 00:a4:23:05:00:00:00:08 2 spf 1
#5
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:05 2 00:a4:23:05:00:00:00:09 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:05 3 00:a4:23:05:00:00:00:0a 1 spf 1
#6
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:06 2 00:a4:23:05:00:00:00:09 2 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:06 3 00:a4:23:05:00:00:00:0a 2 spf 1

#connect Host
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:07 3 00:00:00:00:00:02 #h002
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:09 3 00:00:00:00:00:06 #h006
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:08 3 00:00:00:00:00:04 #h004
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:0a 3 00:00:00:00:00:08 #h008

python $OVXCTL -n startNetwork $TENENT
