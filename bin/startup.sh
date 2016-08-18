#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

# Set up application name
APP="Vertx-financial-demo"

# Set up the path to the Vert.x installation, needed for the "vertx" command.  The last known compatible version
# of Vert.x was 3.3.2.
# http://vertx.io/download/
VERTX=CHANGEME

# Set up the path to the java agent
AGENT=CHANGEME

WEBDELEGATETIER=WebDelegate
BASE_APPD="-javaagent:${AGENT}/javaagent.jar -Dappdynamics.agent.applicationName=\"${APP}\""
WEB_OPTS="${BASE_APPD} -Dappdynamics.agent.tierName=Web -Dappdynamics.agent.nodeName=webnode1"
WEBDELEGATE_OPTS="${BASE_APPD} -Dappdynamics.agent.tierName=WebDelegate -Dappdynamics.agent.nodeName=delegate1"
MICROTRADE_OPTS="${BASE_APPD} -Dappdynamics.agent.tierName=MicroTrader -Dappdynamics.agent.nodeName=micro1"
STANDARDTRADE_OPTS="${BASE_APPD} -Dappdynamics.agent.tierName=StandardTrader -Dappdynamics.agent.nodeName=std1"
PREMIERCLIENTS_OPTS="${BASE_APPD} -Dappdynamics.agent.tierName=PremierClients -Dappdynamics.agent.nodeName=premier1"

COMMON=${DIR}/../common/target/common-1.0-SNAPSHOT.jar

(export JAVA_OPTS="${WEB_OPTS}" && cd ${DIR}/../web && ${VERTX}/bin/vertx run -cp target/web-1.0-SNAPSHOT.jar:${COMMON} -conf ${DIR}/../src/main/resources/dataSet1.conf com.appdynamics.reactivetrade.TradingHandler -cluster) &

(export JAVA_OPTS="${WEBDELEGATE_OPTS}" && cd ${DIR}/../webdelegate && ${VERTX}/bin/vertx run -cp target/webdelegate-1.0-SNAPSHOT.jar:${COMMON} -conf ${DIR}/../src/main/resources/dataSet1.conf com.appdynamics.reactivetrade.HTTPListener -cluster) &

(export JAVA_OPTS="${MICROTRADE_OPTS}" && cd ${DIR}/../microtrader && ${VERTX}/bin/vertx run -cp target/microtrader-1.0-SNAPSHOT.jar:${COMMON} -conf ${DIR}/../src/main/resources/dataSet1.conf com.appdynamics.reactivetrade.MicroTradeHandler -cluster) &

(export JAVA_OPTS="${STANDARDTRADE_OPTS}" && cd ${DIR}/../standardtrader && ${VERTX}/bin/vertx run -cp target/standardtrader-1.0-SNAPSHOT.jar:${COMMON} -conf ${DIR}/../src/main/resources/dataSet1.conf com.appdynamics.reactivetrade.StandardTradeHandler -cluster) &

(export JAVA_OPTS="${PREMIERCLIENTS_OPTS}" && cd ${DIR}/../premierclients && ${VERTX}/bin/vertx run -cp target/premierclients-1.0-SNAPSHOT.jar:${COMMON} -conf ${DIR}/../src/main/resources/dataSet1.conf com.appdynamics.reactivetrade.PremierClientHandler -cluster) &
