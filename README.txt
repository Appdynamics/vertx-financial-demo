
INTRODUCTION

Stock trading demo.

This is a Vert.x-based app.  Since there is no OOTB Vert.x support in AppDynamics (yet), POJO
entrypoints should be used, and will be provided in the app export.

*** It is recommended that you import the app from App_Export.xml before starting ***

This demo uses Vert.x as a web and reactive app platform.  Dependencies will be managed by Maven.

PREREQUISITES

Vert.x.  3.3.2 is the current version in the Maven POM files.  http://vertx.io/download/
Maven.  http://maven.apache.org/download.cgi

APP CONFIG

Import App_Export.xml

CORRELATION CONFIG

Try to work it out yourself!

However, if you get stuck, the config is in conf/custom-activity-correlation.xml

OTHER CONFIG

This app uses a faked database, which is achieved by stubbing out JDBC driver methods.  Basically we are going to
use an unsupported JDBC driver, so the standard configuration for an unsupported driver must be used.
In app-agent-config.xml:

Properties for jdbc:

                <property name="jdbc-statements" value="com.appdynamics.reactivetrade.persist.SqlStatement"/>
                <property name="jdbc-connections" value="com.appdynamics.reactivetrade.persist.SqlConnection"/>

There is no PreparedStatement or CallableStatement used, so it is not necessary to declare a property for prepared or callable statements.

fork-config: see https://singularity.jira.com/browse/CORE-75838
                    <excludes filter-type="STARTSWITH" filter-value="io.netty/"/>
                    <excludes filter-type="STARTSWITH" filter-value="io.vertx/"/>
                    <excludes filter-type="STARTSWITH" filter-value="javassist/"/>
                    <excludes filter-type="STARTSWITH" filter-value="com.fasterxml/"/>
                    <excludes filter-type="STARTSWITH" filter-value="com.hazelcast/"/>

BUILD/DEPLOY/RUN

To build:
% mvn clean package (Maven must be in your path)

To stage for your environment:
Edit bin/startup.sh.  The only variables you should have to change are VERTX and BASE_APPD.  You might also
want to change APP (application name -- this app was configured using the "-D" options for app, tier and node
instead of putting that info in controller-info.xml).

Ports used:
9001, 9002 -- both HTTP.  The TCP-based messaging will also use a dynamic port number.

To run:
% bin/startup.sh

Do not try to put load on the app until it's ready.  You will know it's ready when you see all of these
lines output to stdout:
READYALERT: StandardTradeHandler
READYALERT: PremierClientTradeHandler
READYALERT: MicroTradeHandler

To generate constant load:
% bin/constantLoad.sh

BTs AND APP FLOW

Web.doLogin and Web.doLogout have simple flows that stay in the "Web" tier.

All other BTs have complex async flows that touch every tier:
Web->WebDelegate->{MicroTrader, StandardTrader->PremierClients}

The HTTP implementation is async, so the response comes back in a different thread from the request.

RELATED BUGS

https://singularity.jira.com/browse/CORE-31225

OOTB Vert.x support

