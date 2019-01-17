package com.card.ccuop.batch.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;

/**
 * @ClassName:Test
 * @Description: TODO
 * @Author: SHOCKBLAST
 * @Date: 2018-12-03 17:20
 * @Version: 1.0.0
 **/
public class Test {

    private static void tarFile(File[] file, TarArchiveOutputStream taos) throws Exception {

        for (File file1 : file) {
            TarArchiveEntry tae = new TarArchiveEntry(file1);
            tae.setSize(file1.length());//大小
            tae.setName(new String(file1.getName().getBytes("gbk"), "ISO-8859-1"));//不设置会默认全路径
            taos.putArchiveEntry(tae);

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file1));
            int count;
            byte data[] = new byte[1024];
            while ((count = bis.read(data, 0, 1024)) != -1) {
                taos.write(data, 0, count);
            }
            bis.close();

            taos.closeArchiveEntry();
        }
    }

    private static void deTarFile(String destPath, TarArchiveInputStream tais) throws Exception {

        TarArchiveEntry tae = null;
        while ((tae = tais.getNextTarEntry()) != null) {

            String dir = destPath + File.separator + tae.getName();//tar档中文件
            File dirFile = new File(dir);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dirFile));

            int count;
            byte data[] = new byte[1024];
            while ((count = tais.read(data, 0, 1024)) != -1) {
                bos.write(data, 0, count);
            }

            bos.close();
        }

    }

    public static void main(String[] args) {
        try {
            TarArchiveInputStream taos = new TarArchiveInputStream(new FileInputStream(new File("/Users/SHOCKBLAST/Desktop/20181211/sssss.tar.gz")));
            deTarFile("/Users/SHOCKBLAST/Desktop/20181211/", taos);

            System.out.println("解压成功");
//            TarArchiveOutputStream taoss = new TarArchiveOutputStream(new FileOutputStream(new File("/Users/SHOCKBLAST/Desktop/20181211/sssss.tar.gz")));
//            File[] files = new File[2];
//            files[1] = new File("/Users/SHOCKBLAST/Desktop/20181211/1/0.png");
//            files[0] = new File("/Users/SHOCKBLAST/Desktop/20181211/1/1.png");
//            tarFile(files, taoss);
//            System.out.println("压缩成功");


        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        XStream xStream =new XStream(new Dom4JDriver());
//        RequestBody body = new RequestBody();
//        RequestData data = new RequestData();
//        data.setBackpicna("1111");
//        body.setRequest(data);
//        xStream.alias("Body",RequestBody.class);
//        String s = xStream.toXML(body).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>","").trim();
//        System.out.println(s);

    }
}
