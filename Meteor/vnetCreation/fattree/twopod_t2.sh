#!/bin/bash
OVXCTL='../../utils/ovxctl.py'

python $OVXCTL -n createNetwork tcp:$1:10001 172.0.0.0 16
TENENT="2"

#switch
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:01 #s1001 00:a4:23:05:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:03 #s1003 00:a4:23:05:00:00:00:02
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:01 #s2001 00:a4:23:05:00:00:00:03
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:02 #s2002 00:a4:23:05:00:00:00:04
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:07 #s2007 00:a4:23:05:00:00:00:05
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:08 #s2008 00:a4:23:05:00:00:00:06
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:01 #s3001 00:a4:23:05:00:00:00:07
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:02 #s3002 00:a4:23:05:00:00:00:08
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:07 #s3007 00:a4:23:05:00:00:00:09
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:08 #s3008 00:a4:23:05:00:00:00:0a

#Port
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 4 #s1001 p4

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 1 #s1003 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 4 #s1003 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 1 #s2001 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 3 #s2001 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 4 #s2001 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 1 #s2002 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 3 #s2002 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 4 #s2002 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 1 #s2007 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 3 #s2007 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 4 #s2007 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 1 #s2008 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 3 #s2008 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 4 #s2008 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 1 #s3001 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 2 #s3001 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 3 #s3001 p3

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 1 #s3002 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 2 #s3002 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 3 #s3002 p3

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 1 #s3007 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 2 #s3007 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 3 #s3007 p3

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 1 #s3008 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 2 #s3008 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 3 #s3008 p3

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
#python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:07 3 00:00:00:00:00:01 #h001
#python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:09 3 00:00:00:00:00:0d #h013
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:08 3 00:00:00:00:00:03 #h003
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:0a 3 00:00:00:00:00:0f #h015

python $OVXCTL -n startNetwork $TENENT
