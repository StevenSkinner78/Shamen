<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.*,java.io.*,org.apache.log4j.*" %>
<%@ page session="true" %>
<!DOCTYPE HTML>
<%

    String message = "";
    StringBuffer buffer = new StringBuffer();    
    FileAppender appLogger = (FileAppender)Logger.getRoot().getAppender("appLog");    
    String appLogPath = null;
    if (appLogger!=null){
    appLogPath = appLogger.getFile();
    }
 
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Stats</title>
</head>
<body>
<p align=right><%=new Date()%></p>

<h1 align=center>Shamen Web Application Log</h1>

<pre>
<%
    RandomAccessFile raf = null;
    try {
        File logFile = new File(appLogPath);
        if (logFile.exists() && logFile.canRead()) {
            raf = new RandomAccessFile(logFile.getAbsolutePath(), "r");
            if (raf.length() > 5000){
                raf.seek(raf.length()-5000);
            }
            while (raf.getFilePointer() < raf.length()) {
                out.println(raf.readLine());
            }
            message = buffer.toString();
        } else {
            message = "File can't be read (" + appLogPath + ")";
        }
    } catch(Exception e) {
        out.println("Exception: " + e.toString());
    } finally {
        out.flush();
        try {
            raf.close();
        } catch(Exception e) {}
    }
%>
</pre>
</body>
</html>