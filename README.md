What is cxReader?
=================

cxReader is a news aggregator written in Java.
It uses the Java Platform, Enterprise Edition.
The goal of this software is to provide a single source for all your subscribed feeds
and make them available for different platforms like desktop, mobile phone or web browser.

How do i build/test it?
=======================

This project uses maven as build system.
Assuming that you've already installed [Apache Maven][maven],
the following line executed in the main directory will unit test the software

	mvn test

To unit test and creating a deployable archive run the following command

	mvn package

To unit test, creating a deployable archive and integration test the software run the following command

	mvn verify

Please note that these commands wont build the desktop frontend.
Also note that the integration test currently creates a new GlassFish domain in the temporary folder of the operating system
on every run which isn't deleted afterwards.
If you haven't mounted tmpfs to /tmp (or if you stuck windows ;-) you may want to clean this folder from time to time.


How do i deploy/run it?
=======================

The cxReader needs a Java EE 6 full profile compliant application server as runtime environment.
Great care was taken to only use Java EE standards instead of proprietary extensions.
The software is however developed and tested on [GlassFish Server][glassfish] 3.1.1 only
so it maybe only work on this application server.

In order to be able to deploy the software, you must provide a JDBC resource with the JNDI name `jdbc/cxReaderDb`.
Only users in the role `users` can login to the software so you should map some groups/users to this role.
If you use GlassFish you can activate the [Default Principal to Role Mapping][role-mapping] and create some users with the group `users`.

When you have build the software you can find the EAR archive containing the cxReader in the folder `assembly/target/`.
When you have deployed the software successfully, the web frontend will be available under the following address

	http://<your-server>/frontend-web

The web service will have the following address

	http://<your-server>/connector-rest

This is what you put into the start up dialog of the desktop frontend.

How do i build the desktop frontend?
====================================

Since the desktop frontend is written in C++ and uses [Qt][qt] you must have a C++ compiler and the Qt SDK installed. 
To compile it as part of the main build process you have to enable the `frontend-qt` profile.
On the console the following command would do that

	mvn -Pfrontend-qt package
	
Since the maven configuration for the desktop frontend is merely a wrapper around qmake and make you can also build it separately
by issuing the following commands 

	cd frontend-qt
	qmake
	make


[maven]: http://maven.apache.org/ "the web site of the Apache Maven Projekt"
[glassfish]: http://glassfish.java.net/ "the GlassFish web site"
[qt]: http://qt.nokia.com/products/ "the Qt web site"
[role-mapping]: https://blogs.oracle.com/bobby/entry/simplified_security_role_mapping "Simplified Security Role Mapping"