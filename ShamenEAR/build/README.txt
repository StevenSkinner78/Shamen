INSTRUCTIONS on how to run these files in this directory
=========================================================

=========================================================
Required Compiler Level
		Java 1.8 (jdk1.8.0_131)
=========================================================

=======================================================================
**Java Memory**
		If you receive a "java.lang.OutOfMemoryError: Java heap space"
		you may need to set the ANT_OPTS environment variable for
		boosting the java virtual machine memory. You should only need
		to set it up to the following values:  -Xms128m -Xmx256m

			EXAMPLE
					 Variable name:  ANT_OPTS
					Variable value:  -Xms128m -Xmx256m
=======================================================================

Current files in this directory (ShamenEAR/build/)
==============================================
build.buildnumber
build.email.doc.list
build.email.jccc.list
build.local.properties
build.module.list
build.properties
build.xml
lib/j2ee.jar
==============================================

============================================================================================================
build.buildnumber
build.email.doc.list
build.email.jccc.list
build.local.properties
build.module.list
build.properties
build.xml
		These files are used to build the application for deployment on test/production servers based on
		switch property settings in the 'build.properties'. (DO NOT RUN these scripts from inside of this
		Working Directory. This script should be copied to your local machine into a directory such as
		C:\build_apps for example.
============================================================================================================

============================================================================================================
lib/j2ee.jar
		The 'j2ee.jar' is used to compile the application and is required to get a successful build.
============================================================================================================

============================================================================================================
ADDITIONAL INFORMATION
		More Information/Instructions about running the above scripts can be found inside of each individual
		script file listed above. PLEASE READ before running any if you are uncertain what the script is for.

Steps to build EAR file using ANT Script
1. Download and Install Ant.
2. Create new environment variables for ANT_HOME, JAVA_HOME and add the environment variables to PATH environment variable.
3. Copy build.xml, build.properties, build.buildnumber, build.email.doc.list, build.email.jccc.list, build.local.properties, build.module.list to a local builds directory.
4. Copy build.local.properties file to your c:\Users\<User _Id> folder and update the location of Java JDK and specify CVS user id and password.
	Please note that the path to Java should point to Java 8 JDK.
5. Update build.properties file as required.
6. From the builds directory run build script using "ant" command.