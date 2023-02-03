#!/bin/bash
OVXCTL='../../utils/$OVXCTL'
python $OVXCTL -n createNetwork tcp:$1:10000 172.0.0.0 16
TENENT="1"
echo $TENENT
#switch
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:01 #s1001 00:a4:23:05:00:00:00:01
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:02 #s1002 00:a4:23:05:00:00:00:02
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:03 #s1003 00:a4:23:05:00:00:00:03
python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:00:04 #s1004 00:a4:23:05:00:00:00:04
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:01 #s2001 00:a4:23:05:00:00:00:05
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:02 #s2002 00:a4:23:05:00:00:00:06
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:03 #s2003 00:a4:23:05:00:00:00:07
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:04 #s2004 00:a4:23:05:00:00:00:08
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:05 #s2005 00:a4:23:05:00:00:00:09
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:06 #s2006 00:a4:23:05:00:00:00:0a
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:07 #s2007 00:a4:23:05:00:00:00:0b
python $OVXCTL -n createSwitch $TENENT 20:00:00:00:00:00:00:08 #s2008 00:a4:23:05:00:00:00:0c
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:01 #s3001 00:a4:23:05:00:00:00:0d
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:02 #s3002 00:a4:23:05:00:00:00:0e
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:03 #s3003 00:a4:23:05:00:00:00:0f
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:04 #s3004 00:a4:23:05:00:00:00:10
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:05 #s3005 00:a4:23:05:00:00:00:11
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:06 #s3006 00:a4:23:05:00:00:00:12
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:07 #s3007 00:a4:23:05:00:00:00:13
python $OVXCTL -n createSwitch $TENENT 30:00:00:00:00:00:00:08 #s3008 00:a4:23:05:00:00:00:14
#Port
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 1 #s1001 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 2 #s1001 p2
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 3 #s1001 p3
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:01 4 #s1001 p4
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 1 #s1003 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 2 #s1003 p2
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 3 #s1003 p3
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:03 4 #s1003 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 1 #s2001 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 2 #s2001 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 3 #s2001 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:01 4 #s2001 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 1 #s2002 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 2 #s2002 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 3 #s2002 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:02 4 #s2002 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:03 1 #s2003 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:03 2 #s2003 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:03 3 #s2003 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:03 4 #s2003 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:04 1 #s2004 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:04 2 #s2004 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:04 3 #s2004 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:04 4 #s2004 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:05 1 #s2005 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:05 2 #s2005 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:05 3 #s2005 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:05 4 #s2005 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:06 1 #s2006 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:06 2 #s2006 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:06 3 #s2006 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:06 4 #s2006 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 1 #s2007 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 2 #s2007 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 3 #s2007 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:07 4 #s2007 p4
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 1 #s2008 p1
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 2 #s2008 p2
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 3 #s2008 p3
python $OVXCTL -n createPort $TENENT 20:00:00:00:00:00:00:08 4 #s2008 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 1 #s3001 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 2 #s3001 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 3 #s3001 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:01 4 #s3001 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 1 #s3002 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 2 #s3002 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 3 #s3002 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:02 4 #s3002 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:03 1 #s3003 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:03 2 #s3003 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:03 3 #s3003 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:03 4 #s3003 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:04 1 #s3004 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:04 2 #s3004 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:04 3 #s3004 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:04 4 #s3004 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:05 1 #s3005 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:05 2 #s3005 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:05 3 #s3005 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:05 4 #s3005 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:06 1 #s3006 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:06 2 #s3006 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:06 3 #s3006 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:06 4 #s3006 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 1 #s3007 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 2 #s3007 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 3 #s3007 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:07 4 #s3007 p4
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 1 #s3008 p1
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 2 #s3008 p2
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 3 #s3008 p3
python $OVXCTL -n createPort $TENENT 30:00:00:00:00:00:00:08 4 #s3008 p4
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 1 #s1002 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 2 #s1002 p2
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 3 #s1002 p3
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:02 4 #s1002 p4
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 1 #s1004 p1
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 2 #s1004 p2
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 3 #s1004 p3
python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:00:04 4 #s1004 p4
#connect Link
#1 link for s1001
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 1 00:a4:23:05:00:00:00:05 1 spf 1  #s1001-s2001
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 2 00:a4:23:05:00:00:00:07 1 spf 1  #s1001-s2003
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 3 00:a4:23:05:00:00:00:09 1 spf 1  #s1001-s2005
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:01 4 00:a4:23:05:00:00:00:0b 1 spf 1  #s1001-s2007
#2 link for s1003
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 1 00:a4:23:05:00:00:00:06 1 spf 1  #s1003-s2002
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 2 00:a4:23:05:00:00:00:08 1 spf 1  #s1003-s2004
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 3 00:a4:23:05:00:00:00:0a 1 spf 1  #s1003-s2006
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:03 4 00:a4:23:05:00:00:00:0c 1 spf 1  #s1003-s2008
#1-2 link for s1002
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 1 00:a4:23:05:00:00:00:05 2 spf 1  #s1002-s2001
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 2 00:a4:23:05:00:00:00:07 2 spf 1  #s1002-s2003
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 3 00:a4:23:05:00:00:00:09 2 spf 1  #s1002-s2005
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:02 4 00:a4:23:05:00:00:00:0b 2 spf 1  #s1002-s2007
#2-2 link for s1004
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:04 1 00:a4:23:05:00:00:00:06 2 spf 1  #s1004-s2002
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:04 2 00:a4:23:05:00:00:00:08 2 spf 1  #s1004-s2004
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:04 3 00:a4:23:05:00:00:00:0a 2 spf 1  #s1004-s2006
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:04 4 00:a4:23:05:00:00:00:0c 2 spf 1  #s1004-s2008
#3 link for s2001
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:05 3 00:a4:23:05:00:00:00:0d 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:05 4 00:a4:23:05:00:00:00:0e 1 spf 1
#4 link for s2002
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:06 3 00:a4:23:05:00:00:00:0d 2 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:06 4 00:a4:23:05:00:00:00:0e 2 spf 1
#5 link for s2007
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:0b 3 00:a4:23:05:00:00:00:13 1 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:0b 4 00:a4:23:05:00:00:00:14 1 spf 1
#6 link for s2008
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:0c 3 00:a4:23:05:00:00:00:13 2 spf 1
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:0c 4 00:a4:23:05:00:00:00:14 2 spf 1
#7 link for s2003
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:07 3 00:a4:23:05:00:00:00:0f 1 spf 1  #s2003-s3003
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:07 4 00:a4:23:05:00:00:00:10 1 spf 1  #s2003-s3004
#8 link for s2004
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:08 3 00:a4:23:05:00:00:00:0f 2 spf 1  #s2004-s3003
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:08 4 00:a4:23:05:00:00:00:10 2 spf 1  #s2004-s3004
#8 link for s2005
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:09 3 00:a4:23:05:00:00:00:11 1 spf 1  #s2005-s3005
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:09 4 00:a4:23:05:00:00:00:12 1 spf 1  #s2005-s3006
#8 link for s2006
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:0a 3 00:a4:23:05:00:00:00:11 2 spf 1  #s2006-s3005
python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:00:0a 4 00:a4:23:05:00:00:00:12 2 spf 1  #s2006-s3006
#connect Host
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:0d 3 00:00:00:00:00:01 #h001
python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:13 3 00:00:00:00:00:0d #h013

python $OVXCTL -n startNetwork $TENENT
