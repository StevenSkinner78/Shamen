/**
 *
 */
package gov.doc.isu.shamen.main;

import gov.doc.isu.gtv.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Quickly threw this class together for extracting all need IBM websphere and IBM MQ jars for JMS to function in J2SE.
 * 
 * @author Richard Salas
 * @author Steven Skinner - modification 11/2/2016
 */
public class JarExtractor {

    private static final String MY_CLASS_NAME = "gov.doc.isu.shamen.main.JarExtractor";
    private static Logger myLogger = Logger.getLogger(MY_CLASS_NAME);

    boolean isJar;
    private String[] types;

    /**
     *
     */
    public JarExtractor(boolean isJar, final String... fileTypes) {
        this.isJar = isJar;
        types = fileTypes;
    }

    public void extractInternalFilesToExternalDirectory(File externalDir) {
        myLogger.entering(MY_CLASS_NAME, "extractInternalFilesToExternalDirectory(...)");
        myLogger.finest("checking to see if this is being ran as a jar file or as an expanded java project (ie in eclipse)");

        if(isJar){
            myLogger.fine("This application is being ran as a jar file going to start exacting file files");
            extractFilesFromJarToDestination(externalDir);
        }else{
            // this section runs when this is ran as a java project
            try{
                myLogger.fine("This application is being ran as an expanded java project (probably from an ide such as eclipse)");
                FilenameFilter filenameFilter = new FilenameFilter(){
                    @Override
                    public boolean accept(File dir, String name) {
                        return acceptName(name);
                    }// end method
                };
                File rootDir = new File(getClass().getResource("/").getPath()); // might have to add the prefix /
                String[] pathToInternalFiles = new String[1];
                String mask = "";
                do{
                    pathToInternalFiles[0] = null;
                    findFile(rootDir, ".jar", pathToInternalFiles, mask);
                    if(pathToInternalFiles[0] != null){
                        File file = new File(pathToInternalFiles[0]);
                        mask += ".*" + file.getParentFile().getName() + ".*|";
                        File[] files = file.getParentFile().listFiles(filenameFilter);
                        File extDir = new File(externalDir.getPath());
                        extDir.mkdirs();
                        for(int i = 0, j = files.length;i < j;i++){
                            myLogger.fine("Copying " + files[i] + " to destination directory " + extDir.getPath());
                            String pathToInternal = files[i].getPath();
                            byte[] bytes = FileUtil.getFileInBytes(getClass().getClassLoader().getResourceAsStream(pathToInternal.substring(pathToInternal.indexOf("src") + 4)), files[i].length());
                            FileUtil.writeFile(bytes, extDir.getPath() + "/", files[i].getName());
                        }// end for
                    }// end if
                }while(pathToInternalFiles[0] != null);
            }catch(Exception e){
                myLogger.log(Level.SEVERE, "Exception occurred while trying to locate internal files.  Error Message is: " + e.getMessage(), e);
                throw new IllegalArgumentException("Developers error occured due to the internal location does not exist. Please fix!");
            }// end truy...catch
        }// end if
        myLogger.exiting(MY_CLASS_NAME, "extractInternalFilesToExternalDirectory(...)");
    }// end extractInternalFilesToExternalDirectory()

    private static void findFile(File rootDirectory, String suffix, String[] pathToRequestedFile, String mask) {
        myLogger.entering(MY_CLASS_NAME, "findFile()", new Object[]{rootDirectory, suffix, pathToRequestedFile});
        File[] dirlist = rootDirectory.listFiles();
        for(int i = 0, j = dirlist.length;i < j;i++){
            if(pathToRequestedFile[0] != null){
                break;
            }// end if
            if(dirlist[i].isDirectory() && !dirlist[i].getName().contains("CVS")){
                findFile(dirlist[i], suffix, pathToRequestedFile, mask);
            }else if(dirlist[i].isFile() && dirlist[i].getName().endsWith(suffix) && !dirlist[i].getParent().matches(mask)){
                pathToRequestedFile[0] = dirlist[i].getPath();
                break;
            }// end if
        }// end for
        myLogger.exiting(MY_CLASS_NAME, "copyFileToDirectory");
    }

    private void extractFilesFromJarToDestination(File externalDir) {
        myLogger.entering(MY_CLASS_NAME, "extractFilesFromJarToDestination()");

        myLogger.finest("initializing local variables to null here for use in this method.");
        JarFile jar = null;
        JarEntry entry = null;
        InputStream in = null;
        OutputStream out = null;

        try{
            myLogger.finest("create an instance of this jar file");
            String fileLocation = URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8");
            jar = new JarFile(fileLocation);
            Enumeration<JarEntry> entries = jar.entries();
            String fileName = null;
            File destinationFile = null;
            myLogger.finest("looping through the entries in the jar filtering out the files that are being requested");
            while(entries.hasMoreElements()){
                entry = entries.nextElement();

                if(acceptName(entry.getName())){
                    myLogger.finest("make sure to close streams to free up resources before next iteration.");
                    myLogger.info(externalDir.getPath() + "/" + entry.getName().substring(0, entry.getName().lastIndexOf("/")));
                    if(in != null){
                        in.close();
                    }// end if
                    if(out != null){
                        out.close();
                    }// end if

                    myLogger.finest("start coping internal file to destination");
                    fileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                    destinationFile = new File(externalDir.getPath() + "/" + fileName);
                    if(!destinationFile.getParentFile().exists()){
                        myLogger.info("directory does not exist therefore we are going to make it here");
                        destinationFile.getParentFile().mkdirs();
                    }// end if
                    destinationFile.createNewFile();
                    in = new BufferedInputStream(jar.getInputStream(entry));
                    out = new BufferedOutputStream(new FileOutputStream(destinationFile));
                    byte[] buffer = new byte[2048];
                    int i = 0;
                    while((i = in.read(buffer)) != -1){
                        out.write(buffer, 0, i);
                    } // end while
                    out.flush();// flush the output.
                }// end if
            }// end while
        }catch(IOException e){
            myLogger.log(Level.SEVERE, "IOException while extracting files from jar." + e.getMessage(), e);
        }finally{
            try{
                myLogger.finest("close all the input/output streams.");
                if(in != null){
                    in.close();
                }// end if
                if(out != null){
                    out.close();
                }// end if
                if(jar != null){
                    jar.close();
                }// end if
            }catch(IOException e){
                myLogger.log(Level.SEVERE, "IOException while extracting files from jar." + e.getMessage(), e);
            }// end try...catch
        }// end try...catch
        myLogger.exiting(MY_CLASS_NAME, "extractFilesFromJarToDestination");
    } // end method

    public boolean acceptName(String name) {
        boolean accept = false;
        for(int i = 0, j = types.length;i < j;i++){
            if(name.endsWith(types[i])){
                accept = true;
                break;
            }// end if
        }// end for
        return accept;
    }// end method

}
