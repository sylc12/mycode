package com.card.ccuop.batch.util;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * @ClassName:JasyptUtils
 * @Description: TODO
 * @Author: SHOCKBLAST
 * @Date: 2018-12-27 11:05
 * @Version: 1.0.0
 **/
public class JasyptUtils {
    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //加密所需的salt(盐)
        textEncryptor.setPassword("ccuop2018");
        //要加密的数据（数据库的用户名或密码）
        String username = textEncryptor.encrypt("root");
        String password = textEncryptor.encrypt("ccuopusr");
        System.out.println("username:"+username);
        System.out.println("password:"+password);
    }
}
