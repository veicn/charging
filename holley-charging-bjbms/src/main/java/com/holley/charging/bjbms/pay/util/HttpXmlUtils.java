package com.holley.charging.bjbms.pay.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.beanutils.BeanUtils;

import com.holley.common.util.StringUtil;

/**
 * post提交xml格式的参数
 * 
 * @author iYjrg_xiebin
 * @date 2015年11月25日下午3:33:38
 */
public class HttpXmlUtils {

    /**
     * 开始post提交参数到接口 并接受返回
     * 
     * @param url
     * @param xml
     * @param method
     * @param contentType
     * @return
     */
    public static String xmlHttpProxy(String url, String xml, String method, String contentType) {
        InputStream is = null;
        OutputStreamWriter os = null;

        try {
            URL _url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "text/xml");
            conn.setRequestProperty("Pragma:", "no-cache");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestMethod("POST");
            os = new OutputStreamWriter(conn.getOutputStream());
            os.write(new String(xml.getBytes(contentType)));
            os.flush();

            // 返回值
            is = conn.getInputStream();
            return getContent(is, "utf-8");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解析返回的值
     * 
     * @param is
     * @param charset
     * @return
     */
    public static String getContent(InputStream is, String charset) {
        String pageString = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            isr = new InputStreamReader(is, charset);
            br = new BufferedReader(isr);
            sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            pageString = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb = null;
        }
        return pageString;
    }

    /**
     * 构造xml参数
     * 
     * @param xml
     * @return
     */
    public static String xmlInfo(Unifiedorder unifiedorder) {
        // 构造xml参数的时候，至少又是个必传参数
        /*
         * <xml> <appid>wx2421b1c4370ec43b</appid> <attach>支付测试</attach> <body>JSAPI支付测试</body>
         * <mch_id>10000100</mch_id> <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
         * <notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url>
         * <openid>oUpF8uMuAJO_M2pxb1Q9zNjWeS6o</openid> <out_trade_no>1415659990</out_trade_no>
         * <spbill_create_ip>14.23.150.211</spbill_create_ip> <total_fee>1</total_fee> <trade_type>JSAPI</trade_type>
         * <sign>0CB01533B8C1EF103065174F50BCA001</sign> </xml>
         */

        if (unifiedorder != null) {
            StringBuffer bf = new StringBuffer();

            Field[] fields = unifiedorder.getClass().getDeclaredFields();
            String fieldname;
            String fieldvalue;
            bf.append("<xml>");
            for (int i = 0; i < fields.length; i++) {
                // System.out.println(fields[i].getType() + " " + fields[i].getName());
                fieldname = fields[i].getName();
                try {
                    fieldvalue = BeanUtils.getProperty(unifiedorder, fieldname);
                    if (StringUtil.isEmpty(fieldvalue)) {
                        continue;
                    }
                    bf.append("<" + fieldname + "><![CDATA[");
                    bf.append(fieldvalue);
                    bf.append("]]></" + fieldname + ">");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            bf.append("</xml>");
            return bf.toString();
        }

        return "";
    }

    /**
     * post请求并得到返回结果
     * 
     * @param requestUrl
     * @param requestMethod
     * @param output
     * @return
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String output) {
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(requestMethod);
            if (null != output) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(output.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            connection.disconnect();
            return buffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public static String backWeixin(String returncode, String returnmsg) {
        String resXml = "<xml>" + "<return_code><![CDATA[" + returncode + "]]></return_code>" + "<return_msg><![CDATA[" + returnmsg + "]]></return_msg>" + "</xml> ";
        return resXml;
    }

}
