CTREP@UHI Fedora web services client generation and unit tests
==============================================================

o Introduction
o What you'll need
o How to find out what there is to build
o Generating the web services clients
o Running the tests
o Everything at once
o Reporting problems

Introduction
------------
This directory contains everything required to generate and tests clients for the Fedora web services. Currently, this is only API-A and API-M. The clients are generated automatically using Axis2 and XMLBeans data binding, using the Fedora WSDL defintions available on the Fedora web site. The clients provide full domain level object oriented access to native Fedora objects and services. The unit tests exercise this functionality on a running Fedora instance.
There is a short tutorial on installing Fedora at:
http://www.weblogs.uhi.ac.uk/sm00ay/?p=325

What you'll need
----------------
Axis2 : http://ws.apache.org/axis2
Copy all jar files from AXIS2_HOME/lib to your maven2 repository. Suggested directory is:
.m2/repository/org/apache/axis/2

JUnit4.x : http://www.junit.org/
Copy junit-4.x.jar to your maven2 repository. Suggested directory is:
.m2/repository/junit/junit/4.1

How to find out what there is to build
--------------------------------------
In this directory use the following command:

ant

to see a list of available build targets.

Generating the web services clients
-----------------------------------
Open the file repository.properties and modify the settings indicated in the file.
Open the file build.properties and modify the settings indicated in the file.

In this directory use the following command:

ant clients

This will generate the client libraries in the build directory. It will also copy the jars to the maven2 repository.

Running the tests
-----------------
Before you can run the tests you must install and start Fedora. You must then open the file:

testapp/build.properties

and modify the settings indicated in the file.

In this directory use the following command:

ant run-tests

Everything at once
------------------
If you already have Fedora installed and running, you can generate web services clients and run the tests at the same time. To do so, modify the settings as described in "Generating the web services clients" and "Running the tests", then in this directory use the following command:

ant all

Reporting problems
------------------
If you find any problems, send an email to:

alistair@smo.uhi.ac.uk
