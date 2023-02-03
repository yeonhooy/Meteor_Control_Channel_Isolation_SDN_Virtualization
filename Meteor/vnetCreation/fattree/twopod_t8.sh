#!/bin/bash
OVXCTL='../../utils/ovxctl.py'

python $OVXCTL -n createNetwork tcp:$1:10007 172.0.0.0 16
TENENT="8"

#switch
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:02 #s1002 00:a4:23:05:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:04 #s1003 00:a4:23:05:00:00:00:02
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:05 #s2005 00:a4:23:05:00:00:00:03
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:06 #s2006 00:a4:23:05:00:00:00:04
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:07 #s2007 00:a4:23:05:00:00:00:05
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:08 #s2008 00:a4:23:05:00:00:00:06
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:05 #s3005 00:a4:23:05:00:00:00:07
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:06 #s3006 00:a4:23:05:00:00:00:08
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:07 #s3007 00:a4:23:05:00:00:00:09
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:08 #s3008 00:a4:23:05:00:00:00:0a

#Port
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 3 #s1002 p3
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 4 #s1002 p4

python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 3 #s1004 p3
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 4 #s1004 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:05 2 #s2005 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:05 3 #s2005 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:05 4 #s2005 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:06 2 #s2006 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:06 3 #s2006 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:06 4 #s2006 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 2 #s2007 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 3 #s2007 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 4 #s2007 p4

python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 2 #s2008 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 3 #s2008 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 4 #s2008 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:05 1 #s3005 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:05 2 #s3005 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:05 4 #s3005 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:06 1 #s3006 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:06 2 #s3006 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:06 4 #s3006 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 1 #s3007 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 2 #s3007 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 4 #s3007 p4

python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 1 #s3008 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 2 #s3008 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 4 #s3008 p4

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
#python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:07 3 00:00:00:00:00:0a #h010
#python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:09 3 00:00:00:00:00:0e #h014
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:08 3 00:00:00:00:00:0c #h012
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:0a 3 00:00:00:00:00:10 #h016

python $OVXCTL -n startNetwork $TENENT
