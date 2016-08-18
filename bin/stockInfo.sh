#!/bin/bash

if [ "ping" = "$2" ];
then
	if [ "AAPL" = "$1" ];
	then
		echo "success"
	elif [ "ORCL" = "$1" ];
	then
		echo "success"
	elif [ "FB" = "$1" ];
	then
		echo "success"
	elif [ "GOOG" = "$1" ];
	then
		echo "success"
	elif [ "IBM" = "$1" ];
	then
		echo "success"
	elif [ "MSFT" = "$1" ];
	then
		echo "success"
	elif [ "ADBE" = "$1" ];
	then
		echo "success"
	elif [ "BAC" = "$1" ];
	then
		echo "success"
	elif [ "XOM" = "$1" ];
	then
		echo "success"
	elif [ "BRKB" = "$1" ];
	then
		echo "success"
	else
		echo "failure"
	fi
elif [ "lowPrice" = "$2" ];
then
	if [ "AAPL" = "$1" ];
	then
		echo "96"
	elif [ "ORCL" = "$1" ];
	then
		echo "38"
	elif [ "FB" = "$1" ];
	then
		echo "71"
	elif [ "GOOG" = "$1" ];
	then
		echo "575"
	elif [ "IBM" = "$1" ];
	then
		echo "185"
	elif [ "MSFT" = "$1" ];
	then
		echo "43"
	elif [ "ADBE" = "$1" ];
	then
		echo "68"
	elif [ "BAC" = "$1" ];
	then
		echo "12"
	elif [ "XOM" = "$1" ];
	then
		echo "95"
	elif [ "BRKB" = "$1" ];
	then
		echo "130"
	else
		echo "failure"
	fi
elif [ "hiPrice" = "$2" ];
then
	if [ "AAPL" = "$1" ];
	then
		echo "102"
	elif [ "ORCL" = "$1" ];
	then
		echo "42"
	elif [ "FB" = "$1" ];
	then
		echo "77"
	elif [ "GOOG" = "$1" ];
	then
		echo "590"
	elif [ "IBM" = "$1" ];
	then
		echo "195"
	elif [ "MSFT" = "$1" ];
	then
		echo "48"
	elif [ "ADBE" = "$1" ];
	then
		echo "75"
	elif [ "BAC" = "$1" ];
	then
		echo "17"
	elif [ "XOM" = "$1" ];
	then
		echo "105"
	elif [ "BRKB" = "$1" ];
	then
		echo "140"
	else
		echo "failure"
	fi
fi

