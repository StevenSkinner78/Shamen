REM ************This bat file must be run as an administrator**************

REM ShamenController Initialization Section

REM Run the command to stop the service if it already exists and is running
ShamenController //SS//ShamenController

REM Run the command to delete the service if it already exists
ShamenController //DS//ShamenController

REM This argument is passed to the ShamenController Application to extract the WebSphereMQ Library as required.
REM It does not start the application.  Change as required for local system.
"M:\Utilities\java\jdk1.8.0_131\bin\java" -Xms128M -Xmx1024M -jar @filename@ prepMQ

REM Run the command to install the Shamen Service
ShamenController //IS//ShamenController --DisplayName="Shamen Controller" --Description="Shamen Controller Service" --StartMode=jvm --StopMode=jvm --StopMethod=stop --Classpath="%CLASSPATH%@filename@" --StartClass=gov.doc.isu.shamen.main.MainController --StopClass=gov.doc.isu.shamen.main.MainController --Startup=auto --Jvm="M:\Utilities\java\jdk1.8.0_131\jre\bin\client\jvm.dll" --StartPath="%CLASSPATH2%" --LogPath="M:\Batch\Logs\ShamenService" --LogPrefix="ShamenService" --JvmMs="1024" --JvmMx="1024"

REM Start the service
ShamenController //ES//ShamenController