package com.ucg.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
 /**
 * 
 * @author IBM
 * 
 */ 
 public class FileUtil {

 public static String dirSplit = "\\";//linux windows
 /**
  * save file accroding to physical directory infomation
  * 
  * @param physicalDir
  *            physical directory
  * @param fname
  *            file name of destination
  * @param istream
  *            input stream of destination file
  * @return
  */ 
  public static boolean SaveFileByPhysicalDir(String physicalPath,
   InputStream istream) {

  boolean flag = false;
  try {
   OutputStream os = new FileOutputStream(physicalPath);
   int readBytes = 0;
   byte buffer[] = new byte[8192];
   while ((readBytes = istream.read(buffer, 0, 8192)) != -1) {
    os.write(buffer, 0, readBytes);
   }
   os.close();
   flag = true;
  } catch (FileNotFoundException e) {

   e.printStackTrace();
  } catch (IOException e) {

   e.printStackTrace();
  }
  return flag;
 }

 public static boolean CreateDirectory(String dir){
  File f = new File(dir);
  if (!f.exists()) {
   f.mkdirs();
  }
  return true;
 }
 
 public static boolean CreateFile(String fileName){
	  File f = new File(fileName);
	 try {
		f.createNewFile();
	} catch (IOException e) {
		e.printStackTrace();
		return false;
	}
	  return true;
 }

 
 /**     
* 创建人：陈永培   
* 创建时间：2017-5-16 上午8:47:10
* 功能说明:覆盖    
*/
 public static void saveFile(String physicalPath,String content){
	 saveAsFileOutputStream(physicalPath,content,false); 
 }
 
 /**     
* 创建人：陈永培   
* 创建时间：2017-5-16 上午8:47:10
* 功能说明:追加    
*/
 public static void appendFile(String physicalPath,String content){
	 saveAsFileOutputStream(physicalPath,content,true); 
 }
 
 public static void saveAsFileOutputStream(String physicalPath,String content,boolean isAppend) {
    File file = new File(physicalPath);
    boolean b = file.getParentFile().isDirectory();
    if(!b){
     File tem = new File(file.getParent());
     // tem.getParentFile().setWritable(true);
     tem.mkdirs();// 创建目录
    }
    //Log.info(file.getParent()+";"+file.getParentFile().isDirectory());
    FileOutputStream foutput =null;
    try {
     foutput = new FileOutputStream(physicalPath,isAppend);

     foutput.write(content.getBytes("UTF-8"));
     //foutput.write(content.getBytes());
    }catch(IOException ex) {
     ex.printStackTrace();
     throw new RuntimeException(ex);
    }finally{
     try {
      foutput.flush();
      foutput.close();
     }catch(IOException ex){
      ex.printStackTrace();
      throw new RuntimeException(ex);
     }
    }
     //Log.info("文件保存成功:"+ physicalPath);
   } 
 

  /**
     * COPY文件
     * @param srcFile String
     * @param desFile String
     * @return boolean
     */
    public static boolean copyToFile(String srcFile, String desFile) {
        File scrfile = new File(srcFile);
        if (scrfile.isFile() == true) {
            int length;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(scrfile);
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            File desfile = new File(desFile); 
             FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(desfile, false);
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            desfile = null;
            length = (int) scrfile.length();
            byte[] b = new byte[length];
            try {
                fis.read(b);
                fis.close();
                fos.write(b);
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            scrfile = null;
            return false;
        }
        scrfile = null;
        return true;
    } 
     /**
     * COPY文件夹
     * @param sourceDir String
     * @param destDir String
     * @return boolean
     */
    public static boolean copyDir(String sourceDir, String destDir) {
        File sourceFile = new File(sourceDir);
        String tempSource;
        String tempDest;
        String fileName;
        File[] files = sourceFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            fileName = files[i].getName();
            tempSource = sourceDir + "/" + fileName;
            tempDest = destDir + "/" + fileName;
            if (files[i].isFile()) {
                copyToFile(tempSource, tempDest);
            } else {
                copyDir(tempSource, tempDest);
            }
        }
        sourceFile = null;
        return true;
    } 
     /**
     * 删除指定目录及其中的所有内容。
     * @param dir 要删除的目录
     * @return 删除成功时返回true，否则返回false。
     */
    public static boolean deleteDirectory(File dir) {
        File[] entries = dir.listFiles();
        int sz = entries.length;
        for (int i = 0; i < sz; i++) {
            if (entries[i].isDirectory()) {
                if (!deleteDirectory(entries[i])) {
                    return false;
                }
            } else {
                if (!entries[i].delete()) {
                    return false;
                }
            }
        }
        if (!dir.delete()) {
            return false;
        }
        return true;
    }

    

    /**
     * File exist check
     *
     * @param sFileName File Name
     * @return boolean true - exist<br>
     *                 false - not exist
     */
    public static boolean checkExist(String sFileName) {

     boolean result = false;

       try {
        File f = new File(sFileName);

        //if (f.exists() && f.isFile() && f.canRead()) {
    if (f.exists() && f.isFile()) {
      result = true;
    } else {
      result = false;
    }
    } catch (Exception e) {
         result = false;
    }

        /* return */
        return result;
    }
    
    /**
     * File exist check
     *
     * @param sFileName File Name
     * @return boolean true - exist<br>
     *                 false - not exist
     */
    public static boolean checkDirExist(String dirName) {

     boolean result = false;

       try {
        File f = new File(dirName);
	    if (f.exists() ) {
	      result = true;
	    } else {
	      result = false;
	    }
	    } catch (Exception e) {
	         result = false;
	    }

        /* return */
        return result;
    }

    /**
     * Get File Size
     *
     * @param sFileName File Name
     * @return long File's size(byte) when File not exist return -1
     */
    public static long getSize(String sFileName) {

        long lSize = 0;

        try {
      File f = new File(sFileName);

              //exist
      if (f.exists()) {
       if (f.isFile() && f.canRead()) {
        lSize = f.length();
       } else {
        lSize = -1;
       }
              //not exist
      } else {
          lSize = 0;
      }
    } catch (Exception e) {
         lSize = -1;
    }
    /* return */
    return lSize;
    }

 /**
  * File Delete
  *
  * @param sFileName File Nmae
  * @return boolean true - Delete Success<br>
  *                 false - Delete Fail
  */
    public static boolean deleteFromName(String sFileName) {

        boolean bReturn = true;

        try {
            File oFile = new File(sFileName);

           //exist
           if (oFile.exists()) {
            //Delete File
            boolean bResult = oFile.delete();
            //Delete Fail
            if (bResult == false) {
                bReturn = false;
            }

            //not exist
           } else {

           }

   } catch (Exception e) {
    bReturn = false;
   }

   //return
   return bReturn;
    }

 /**
  * File Unzip
  *
  * @param sToPath  Unzip Directory path
  * @param sZipFile Unzip File Name
  */
 @SuppressWarnings("rawtypes")
public static void releaseZip(String sToPath, String sZipFile) throws Exception {

  if (null == sToPath ||("").equals(sToPath.trim())) {
    File objZipFile = new File(sZipFile);
    sToPath = objZipFile.getParent();
  }
  ZipFile zfile = new ZipFile(sZipFile);
  Enumeration zList = zfile.entries();
  ZipEntry ze = null;
  byte[] buf = new byte[1024];
  while (zList.hasMoreElements()) { 
     ze = (ZipEntry) zList.nextElement();
    if (ze.isDirectory()) {
     continue;
    }

    OutputStream os =
    new BufferedOutputStream(
    new FileOutputStream(getRealFileName(sToPath, ze.getName())));
    InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
    int readLen = 0;
    while ((readLen = is.read(buf, 0, 1024)) != -1) {
     os.write(buf, 0, readLen);
    }
    is.close();
    os.close();
  }
  zfile.close();
 }

 /**
  * getRealFileName
  *
  * @param  baseDir   Root Directory
  * @param  absFileName  absolute Directory File Name
  * @return java.io.File     Return file
  */
 @SuppressWarnings({ "rawtypes", "unchecked" })
private static File getRealFileName(String baseDir, String absFileName) throws Exception {

  File ret = null; 
   List dirs = new ArrayList();
  StringTokenizer st = new StringTokenizer(absFileName, System.getProperty("file.separator"));
  while (st.hasMoreTokens()) {
   dirs.add(st.nextToken());
  } 
   ret = new File(baseDir);
  if (dirs.size() > 1) {
   for (int i = 0; i < dirs.size() - 1; i++) {
    ret = new File(ret, (String) dirs.get(i));
   }
  }
  if (!ret.exists()) {
   ret.mkdirs();
  }
  ret = new File(ret, (String) dirs.get(dirs.size() - 1));
  return ret;
 } 
  /**
  * copyFile
  *
  * @param  srcFile   Source File
  * @param  targetFile   Target file
  */
 @SuppressWarnings("resource")
 public static  void copyFile(String srcFile , String targetFile) throws IOException
  {

   FileInputStream reader = new FileInputStream(srcFile);
   FileOutputStream writer = new FileOutputStream(targetFile); 
    byte[] buffer = new byte [4096];
   int len; 
    try
   {
    reader = new FileInputStream(srcFile);
    writer = new FileOutputStream(targetFile);

    while((len = reader.read(buffer)) > 0)
    {
     writer.write(buffer , 0 , len);
    }
   }
   catch(IOException e)
   {
    throw e;
   }
   finally
   {
    if (writer != null)writer.close();
    if (reader != null)reader.close();
   }
  } 
  /**
  * renameFile
  *
  * @param  srcFile   Source File
  * @param  targetFile   Target file
  */
 public static  void renameFile(String srcFile , String targetFile) throws IOException
  {
   try {
    copyFile(srcFile,targetFile);
    deleteFromName(srcFile);
   } catch(IOException e){
    throw e;
   }
  } 
 
 public static void write(String tivoliMsg,String logFileName) {
  try{
    byte[]  bMsg = tivoliMsg.getBytes();
    FileOutputStream fOut = new FileOutputStream(logFileName, true);
    fOut.write(bMsg);
    fOut.close();
  } catch(IOException e){
   //throw the exception      
  } 
  }
 /**
 * This method is used to log the messages with timestamp,error code and the method details
 * @param errorCd String
 * @param className String
 * @param methodName String
 * @param msg String
 */
 public static void writeLog(String logFile, String batchId, String exceptionInfo) {

  DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.JAPANESE);

  Object args[] = {df.format(new Date()), batchId, exceptionInfo};

  String fmtMsg = MessageFormat.format("{0} : {1} : {2}", args);

  try {

   File logfile = new File(logFile);
   if(!logfile.exists()) {
    logfile.createNewFile();
   }

      FileWriter fw = new FileWriter(logFile, true);
      fw.write(fmtMsg);
      fw.write(System.getProperty("line.separator")); 
       fw.flush();
      fw.close(); 
   } catch(Exception e) {
  }
 }

 public static String readTextFile(String realPath) throws Exception {
  File file = new File(realPath);
   if (!file.exists()){
    System.out.println("File not exist!");
    return null;
   }
   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(realPath)));   
   String temp = "";
   String txt="";
   while((temp = br.readLine()) != null){
    txt+=temp+"\r\n";
    }   
   br.close();
  return txt;
 }
 
 public static  HashMap<String, ArrayList<String>> refreshFileList(String strPath) { 
	 
	 HashMap<String, ArrayList<String>> hashMap=new HashMap<String, ArrayList<String>>();
	 
	 ArrayList<String> filelistpath = new ArrayList<String>(); 
	 ArrayList<String> filelistname = new ArrayList<String>(); 
     File dir = new File(strPath); 
     File[] files = dir.listFiles(); 
     
     if (files == null) 
         return null; 
     for (int i = 0; i < files.length; i++) { 
         if (files[i].isDirectory()) { 
             refreshFileList(files[i].getAbsolutePath()); 
         } else { 
     
        	 filelistname.add(files[i].getName()); 
             filelistpath.add(files[i].getAbsolutePath());                    
         } 
     }
     
     hashMap.put("filelistpath", filelistpath);
     hashMap.put("filelistname", filelistname);
	return hashMap; 
 }
 
 public static void main(String[] args) {
	 HashMap<String, ArrayList<String>> map = refreshFileList("E:\\任务管理系统系统\\apache-tomcat-7.0.29-windows-x86\\apache-tomcat-7.0.29\\webapps\\TaskSystem\\file");
	 ArrayList<String> filelistpath = map.get("filelistpath");
	 ArrayList<String> filelistname = map.get("filelistname");
	for (String string : filelistpath) {
		System.out.println(string);
	}
	
	for (String string : filelistname) {
		System.out.println(string);
	}
}
}
 