package com.card.ccuop.batch.util;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.omg.CORBA.SystemException;

import java.io.*;
import java.util.Properties;

@Slf4j
public class SFTPUtil{
    private Session session;
    private Channel channel;
    private ChannelSftp chSftp;

    public SFTPUtil(String ftpHost, int ftpPort, String ftpUserName, String password, String priKeyFile,String passphrase) throws Exception{
        session = getSftpSession(ftpHost, ftpPort, ftpUserName, password, priKeyFile, passphrase);
        channel = session.openChannel("sftp");
        channel.connect();
        chSftp = (ChannelSftp) channel;
    }

    /*
     * 多文件上传
     */
    public void uploadSftpFile(String remoteFilepath, String localFilePath) throws JSchException, SftpException {
            createDir(remoteFilepath.substring(0,remoteFilepath.lastIndexOf('/')),chSftp);
            chSftp.put(localFilePath, remoteFilepath);
    }

    public void closeSFTP(){
        chSftp.quit();
        channel.disconnect();
        session.disconnect();
    }

    /**
     * 创建一个文件目录
     * @throws SystemException
     */
    public static void createDir(String createpath, ChannelSftp sftp) throws SftpException {
        try {
            if (isDirExist(createpath,sftp)) {
            }else{
                String pathArry[] = createpath.split("/");
                if (createpath.startsWith("/")){
                    sftp.cd("/");
                }

                for (String path : pathArry) {
                    if (path.equals("")) {
                        continue;
                    }

                    if (isDirExist(path,sftp)) {
                        sftp.cd(path);
                    /*    if (isDirExist(createpath, sftp)) {

                        }*/
                    } else {
                        // 建立目录
                        sftp.mkdir(path);
                        // 进入并设置为当前目录
                        sftp.cd(path);
                        log.info("sftp目录创建成功:"+path);
                    }
                }
                for (String path : pathArry) {
                    if (path.equals("")) {
                        continue;
                    }else{
                        sftp.cd("../");
                    }
                }
            }

            //System.out.print(sftp.pwd());
            //sftp.cd(createpath);
            //sftp.cd("/");
        } catch (SftpException e) {
            log.error("创建sftp目录异常"+ createpath,e);
            throw e;
        }

    }


    /**
     * 判断目录是否存在
     */
    public static boolean isDirExist(String directory,ChannelSftp sftp) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    /**
     * 判断文件是否存在
     */
    public boolean isFileExist(String filepath) {
        boolean isFileExistFlag = false;
        try {
            SftpATTRS sftpATTRS = chSftp.lstat(filepath);
            isFileExistFlag = true;
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isFileExistFlag = false;
            }
        }
        return isFileExistFlag;
    }


    public static void uploadSftpFile(int ftpPort,String ftpHost,  String ftpUserName, String remoteFilepath, String localFilePath, String privateKeyFile,String passphrase) throws JSchException, SftpException  {
        Session session = getSftpSession(ftpHost, ftpPort, ftpUserName, null, privateKeyFile,passphrase);
        uploadSftpFile(remoteFilepath, localFilePath, session);
    }

    public static void uploadSftpFile(int ftpPort, String ftpHost, String ftpUserName, String remoteFilepath, String localFilePath, String password) throws JSchException, SftpException {
        Session session = getSftpSession(ftpHost, ftpPort, ftpUserName, password, null,null);
        uploadSftpFile(remoteFilepath, localFilePath, session);
    }




    private static void uploadSftpFile(String remoteFilepath, String localFilePath, Session session) throws JSchException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp chSftp = (ChannelSftp) channel;

        try {
            createDir(remoteFilepath.substring(0,remoteFilepath.lastIndexOf('/')),chSftp);
            chSftp.put(localFilePath, remoteFilepath);
        }finally{
            chSftp.quit();
            channel.disconnect();
            session.disconnect();
        }
    }

    public static void uploadFtpFile(int ftpPort, String ftpHost, String ftpUserName, String remoteFilepath, String localFilePath, String password) throws JSchException, SftpException,Exception {
        FTPClient ftpClient = null;
        InputStream in = null;
        try {
            ftpClient = getFtpClient(ftpHost, ftpPort, ftpUserName, password);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            in = new FileInputStream(localFilePath);
            boolean storeFile = ftpClient.storeFile(remoteFilepath, in);
            if(!storeFile) {
                in.close();
                ftpClient.disconnect();
                throw new Exception("upload failed!");
            }
        } finally {
            if(in != null) in.close();
            if(ftpClient != null)
                ftpClient.disconnect();
        }

    }

    /**
     * 通过sftp下载文件数据
     * @param ftpHost
     * @param ftpUserName
     * @param ftpPort
     * @param remoteFilepath
     * @param localFilePath
     * @throws JSchException
     * @throws SftpException
     */
    public static void downloadSftpFile(String ftpHost, int ftpPort, String ftpUserName, String remoteFilepath, String localFilePath,  String privateKeyFile,String passphrase) throws JSchException, SftpException {
        Session session = getSftpSession(ftpHost, ftpPort, ftpUserName, null,  privateKeyFile,passphrase);
        downloadSftpFile(remoteFilepath, localFilePath, session);
    }

    public static void downloadSftpFile(int ftpPort, String ftpHost, String ftpUserName, String remoteFilepath, String localFilePath, String password) throws JSchException, SftpException {
        Session session = getSftpSession(ftpHost, ftpPort, ftpUserName, password, null,null);
        downloadSftpFile(remoteFilepath, localFilePath, session);
    }
    /**
     * 通过ftp下载文件数据
     * @param ftpPort
     * @param ftpHost
     * @param ftpUserName
     * @param remoteFilepath
     * @param localFilePath
     * @param password
     * @throws JSchException
     * @throws SftpException
     * @throws IOException
     */
    public static void downloadFtpFile(int ftpPort, String ftpHost, String ftpUserName, String remoteFilepath, String localFilePath, String password) throws JSchException, SftpException, IOException, Exception {
        FTPClient ftpClient = null;
        OutputStream out = null;
        try {
            ftpClient = getFtpClient(ftpHost, ftpPort, ftpUserName, password);
            FTPFile[] listFiles = ftpClient.listFiles(remoteFilepath);
            if(listFiles.length == 0) {
                throw new Exception(String.format("下载文件不存在，文件路径：%s", remoteFilepath));
            }
            out =new FileOutputStream(localFilePath);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            ftpClient.retrieveFile(remoteFilepath, out);
        } finally {
            if(out != null) out.close();
            if(ftpClient != null) ftpClient.disconnect();
        }
    }
    /**
     * 下载文件
     * */
    private static void downloadSftpFile(String remoteFilepath, String localFilePath, Session session) throws JSchException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp chSftp = (ChannelSftp) channel;

        try {
            if (remoteFilepath.startsWith("/")) {
                chSftp.cd("/");
            }
            chSftp.get(remoteFilepath, localFilePath);
        } finally {
            chSftp.quit();
            channel.disconnect();
            session.disconnect();
        }
    }
    /**
     * 获取sftp连接session
     * */
    private static Session getSftpSession(String ftpHost, int ftpPort, String ftpUserName, String password, String priKeyFile,String passphrase) throws JSchException {
        JSch jsch = new JSch();
        if (priKeyFile != null && !"".equals(priKeyFile)) {
            if (passphrase != null && !"".equals(passphrase)) {
                jsch.addIdentity(priKeyFile, passphrase);
            } else {
                jsch.addIdentity(priKeyFile);
            }
        }
        Session session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
        session.setTimeout(100000);
        if(StringUtils.isNotBlank(password)) {
            session.setPassword(password);
        }

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();

        return session;
    }

    /**
     * 获取ftp客服端
     */
    private static FTPClient getFtpClient(String ftpHost, int ftpPort, String ftpUserName, String password) throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpHost,ftpPort);
        ftpClient.login(ftpUserName, password);
        int reply = ftpClient.getReplyCode();
        ftpClient.setDataTimeout(60000);
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new Exception("FTP server refuse connect！");
        }
        return ftpClient;
    }

}


