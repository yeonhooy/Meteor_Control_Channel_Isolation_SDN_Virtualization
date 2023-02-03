#!/bin/bash

for var in $(seq $1)
do 
  echo $var
  under=_
  sudo sh linear_$var$under$2.sh 
done