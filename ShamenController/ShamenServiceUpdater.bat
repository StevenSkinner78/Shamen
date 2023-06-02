REM This .bat file will update an existing instance of ShamenController.  
REM It is designed to modify the .jar name used by the ShamenController Service.

REM Stop the Service
ShamenController //SS//ShamenController

REM Output file indicating that the service stopped
net start > serviceStopping.txt

REM Using ping to delay subsequent steps to allow for large HSQLDB shutdown process to complete
ping localhost -n 20 -w 3000>nul

REM Update the jar name used by the Service. The jar name must be manually changed as required!
ShamenController //US//ShamenController --Classpath="%CLASSPATH%@filename@"

REM Start the Service
ShamenController //ES//ShamenController

REM Output files to verify the service started and basic information about the ShamenController Service
net start > serviceStarted.txt
sc qc ShamenController > serviceStartedQC.txt