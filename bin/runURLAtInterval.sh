#!/bin/bash

triggerVal=$(expr $2 % $3)

if [ $triggerVal -eq 0 ];
then
    #echo "calling URL $1"
    curl "$1" > /dev/null 2>&1
fi
