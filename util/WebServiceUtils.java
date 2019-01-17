package com.card.ccuop.batch.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

public class WebServiceUtils {

    /**
     * 发起webservice请求
     * @param url 请求地址
     * @param methodName 请求的方法
     * @param value 请求参数内容
     * @return
     * @throws Exception
     */
    public static Object[] getWebServiceClient(String url,String methodName,Object... value) throws Exception {
        if (StringUtils.isEmpty(url)){
            return null;
        }
        JaxWsDynamicClientFactory dcf =JaxWsDynamicClientFactory.newInstance();
        //获取webService请求客户端
        Client client = dcf.createClient(url);
        //发送请求
        Object[] objects = client.invoke(methodName,value);
        return objects;
    }

}
