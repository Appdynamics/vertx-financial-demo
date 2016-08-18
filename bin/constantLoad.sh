#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

PFX="http://localhost:9001"
counter=1
idx=0

tickers=("AAPL" "ORCL" "FB" "GOOG" "IBM" "MSFT" "ADBE" "BAC" "XOM" "BRKB")
loBids=(96 36 70 575 185 43 68 12 95 130)
hiBids=(103 43 77 590 195 50 75 19 105 142)
cutoffs=(25 32 52 70 76 79 83 85 95 100)
netMicro=(0 0 0 0 0 0 0 0 0 0)
netStd=(0 0 0 0 0 0 0 0 0 0)
netPrem=(0 0 0 0 0 0 0 0 0 0)

function getIndex {
    # Index into tickers
    picker=$RANDOM
    let "picker %= 100"
    iter=0
    idx=0
    while [ $iter -lt 10 ];
    do
        if [ ${cutoffs[$iter]} -gt $picker ];
        then
            idx=$iter
            iter=100
        else
            iter=$(expr $iter + 1)
        fi
    done
}

function getQuantity {
    # Index into quantity bucket
    # Three basic patterns of quantity:
    #   25-150 shares
    #   500-5000 shares
    #   50000-1000000 shares
    qtyBucket=$RANDOM
    let "qtyBucket %= 100"
    qty=$RANDOM

    if [ $qtyBucket -lt 85 ];
    then
        # Micro trade: 25-125 shares
        let "qty %= 125"
        let "qty += 25"
    elif [ $qtyBucket -lt 97 ];
    then
        # Standard trade
        let "qty %= 4500"
        let "qty += 500"
    else
        # Big trade
        let "qty %= 950000"
        let "qty += 50000"
    fi
}

function getOp {
    if [ $qty -lt 300 ];
    then
        net=${netMicro[$idx]}
        threshold=3000
    elif [ $qty -lt 6000 ];
    then
        net=${netStd[$idx]}
        threshold=30000
    else
        net=${netPrem[$idx]}
        threshold=3000000
    fi

    op=""
    if [ $net -lt 0 ];
    then
        if [ $(expr $net + $threshold) -lt 0 ];
        then
            # Must buy
            op="buy"
        fi
    else
        if [ $(expr $net - $threshold) -gt 0 ];
        then
            # Must sell
            op="sell"
        fi
    fi

    if [ "" = "$op" ];
    then
        buySell=$RANDOM
        let "buySell %= 2"
        if [ $buySell -eq 0 ]; then
            op="buy"
        else
            op="sell"
        fi
    fi
}

function getPrice {
    diff=$(expr ${hiBids[$idx]} - ${loBids[$idx]})
    rand=$RANDOM
    incr=$(expr $rand % $diff)
    randPrice=$(expr ${loBids[$idx]} + $incr)

    if [ "$op" = "buy" ];
    then
        price=$(expr $randPrice + 2)
    else
        price=$(expr $randPrice - 2)
    fi
}

function reportTrade {
    echo "$1: t=${tickers[$idx]}, o=$op, q=$qty, nm=${netMicro[$idx]}, ns=${netStd[$idx]}, np=${netPrem[$idx]}, p=$price" > /dev/null
}

function executeTrade {
    if [ $qty -lt 300 ];
    then
        if [ "$op" = "buy" ];
        then
            netMicro[$idx]=$(expr ${netMicro[$idx]} + $qty)
        else
            netMicro[$idx]=$(expr ${netMicro[$idx]} - $qty)
        fi
    elif [ $qty -lt 6000 ];
    then
        if [ "$op" = "buy" ];
        then
            netStd[$idx]=$(expr ${netStd[$idx]} + $qty)
        else
            netStd[$idx]=$(expr ${netStd[$idx]} - $qty)
        fi
    else
        if [ "$op" = "buy" ];
        then
            netPrem[$idx]=$(expr ${netPrem[$idx]} + $qty)
        else
            netPrem[$idx]=$(expr ${netPrem[$idx]} - $qty)
        fi
    fi

	curl "${PFX}/trade/${op}/sym/${tickers[$idx]}/qty/${qty}/price/${price}" > /dev/null 2>&1
}

function doTrade {
    getIndex
    getQuantity
    getOp
    getPrice
    reportTrade BEFORE
    executeTrade
    reportTrade AFTER
}

sleepVal=2

# Intervals to run urls for non-trade operations
homeInterval=40
loginInterval=5
logoutInterval=7
fillsInterval=4
openOrdersInterval=10
closedOrdersInterval=9
scorecardInterval=12

while [ true ]; do
    sleep $sleepVal
    #echo "counter=" $counter

    ${DIR}/runURLAtInterval.sh ${PFX}/ ${counter} $homeInterval
    ${DIR}/runURLAtInterval.sh ${PFX}/login ${counter} $loginInterval
    ${DIR}/runURLAtInterval.sh ${PFX}/logout ${counter} $logoutInterval
    ${DIR}/runURLAtInterval.sh ${PFX}/fills ${counter} $fillsInterval
    ${DIR}/runURLAtInterval.sh ${PFX}/orders/open ${counter} $openOrdersInterval
    ${DIR}/runURLAtInterval.sh ${PFX}/orders/closed ${counter} $closedOrdersInterval
    ${DIR}/runURLAtInterval.sh ${PFX}/scorecard ${counter} $scorecardInterval

    doTrade
    doTrade
    doTrade
    doTrade
    doTrade
    doTrade
    doTrade
    doTrade
    doTrade
    doTrade

    counter=`expr $counter + 1`
    if [ $counter -ge 1000 ];
    then
        counter=1
    fi
done
