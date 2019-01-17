package com.card.ccuop.batch.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件操作工具类
 * 
 * @author lzeus Liu
 * 
 */
public class FileTools {
	
	private static Log log = LogFactory.getLog(FileTools.class);
	/**
	 * 建立一个新的FILE 如果存在，就删除后新建立 或者直接建立一个新文件
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public static File createNewFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		return  file;
	}

	/**
	 * 建立一个新文件 如果存在不做任何逻辑 不存在，新建立一个文件
	 * 
	 * @param filePath
	 * @throws IOException
	 */
	public static void createNewFileNoExists(String filePath)
			throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	/**
	 * 删除一个文件
	 * 
	 * @param filePath
	 */
	public static void delFile(String filePath) {
		log.info("删除文件delFile方法开始,待删除的文件是" + filePath);
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			file.delete();
			log.info("删除文件delFile方法执行成功,删除的文件是" + filePath);
		}
	}
	
	public static void delFile(File fileObject){
		if(fileObject == null){
			log.debug("参数fileObject为null该方法直接退出");
			return;
		}
		
		if(fileObject.exists()&&fileObject.isFile()){
			String fileName = null;
			try {
				fileName = fileObject.getCanonicalPath();
			} catch (Exception e) {
				fileName = fileObject.getName();
			}
			
			fileObject.delete();
			log.debug("删除文件delFile方法执行成功,删除的文件是" + fileName);
		}
		
	}

	/**
	 * 删除一个空文件 如果文件不为空，不删除
	 * 
	 * @param folderName
	 * @param fileName
	 */
	public static void delNullFolder(String folderName, String fileName) {
		File file = new File(folderName);
		File groupFile = new File(fileName);
		if (file.isDirectory()) {
			if (groupFile.length() == 0) {
				groupFile.delete();
			}
			int fileNum = file.listFiles().length;
			if (fileNum == 0) {
				file.delete();
			}
		}
	}
	
	public static File[] getFileList(File dir,final String regex){
		
		File[] files = dir.listFiles(new FilenameFilter() {
			
			private Pattern patern = Pattern.compile(regex);
			
			@Override
			public boolean accept(File dir, String name) {
				Matcher matcher = patern.matcher(name);
				boolean rslt = matcher.matches();
				return rslt;
			}
		});
		return files;
	}
	/**
	 * 剪切
	 * @param fromFilePath 待剪切的文件路径字符串描述
	 * @param toFilePath 剪切到的文件路径字符串描述
	 */
	public static void move(String fromFilePath,String toFilePath){
		File sourceFile = new File(fromFilePath);
		move(sourceFile, toFilePath);
	}
	/**
	 * 剪切
	 * @param sourceFile 待剪切的文件对象
	 * @param toFilePath 剪切到的文件路径字符串描述
	 */
	public static void move(File sourceFile,String toFilePath){

		if(!sourceFile.exists()){
			log.debug("待剪切的文件不存在");
			return;
		}
		
		File targetFile = new File(toFilePath);
		
		if(targetFile.exists()){
			delFile(toFilePath);
		}
		try {
			targetFile.createNewFile();
		} catch (IOException e) {
			log.error("建立目标文件"+toFilePath+"时发生异常");
			return;
		}
		
		copy(sourceFile,targetFile);
		delFile(sourceFile);
	}
	/**
	 * 复制文件
	 * @param src 待复制的文件
	 * @param dst 复制到的文件
	 */
	public static void copy(File src, File dst) {
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new BufferedInputStream(new FileInputStream(src),
						BUFFER_SIZE);
				out = new BufferedOutputStream(new FileOutputStream(dst),
						BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				int _continue = -1;
				do{
					_continue = in.read(buffer);
					if(_continue > -1){
						out.write(buffer,0,_continue);
					}
				}while(_continue > -1);
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
			String srcFileName = null;
			String dstFileName = null;
			try {
				srcFileName = src.getCanonicalPath();
				dstFileName = dst.getCanonicalPath();
			} catch (Exception e) {
				srcFileName = src.getName();
				dstFileName = src.getName();
			}
			log.debug("将文件"+srcFileName+"复制到"+dstFileName+"成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 文件缓存区大小（个字节）
	 */
	public static final int BUFFER_SIZE = 1024;

	/**
	 * 清理文件
	 * @param fromDir
	 * @param intervalDays 多少天之前的文件
	 * @return
	 */
	public static Integer cleanFiles(String fromDir,int intervalDays){
		File srcDir = new File(fromDir);
		if (!srcDir.exists()) {
			return 0;
		}
		File[] files = srcDir.listFiles();
		if (files == null || files.length <= 0) {
			return 0;
		}
		int l = 0;
		Date today = new Date();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				try {
					File ff = files[i];
					long time=ff.lastModified();
					Calendar cal=Calendar.getInstance();
					cal.setTimeInMillis(time);
					Date lastModified = cal.getTime();
					//(int)(today.getTime() - lastModified.getTime())/86400000;
					long days = getDistDates(today, lastModified);
					if(days>=intervalDays){
						files[i].delete();
						l++;
					}
				} catch (Exception e) {
					log.error("清理文件异常",e);
				}
			}
		}
		return l;
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws
	 */
	public static long getDistDates(Date startDate,Date endDate)
	{
		long totalDate = 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		long timestart = calendar.getTimeInMillis();
		calendar.setTime(endDate);
		long timeend = calendar.getTimeInMillis();
		totalDate = Math.abs((timeend - timestart))/(1000*60*60*24);
		return totalDate;
	}


}
