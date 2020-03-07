package cmd.lzx.main;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.*;

/**
 * @author 1Zx.
 * @data 2019/11/21 9:45
 */
public final class JxfUtils {

    private static void exportTxt(StringBuffer data,String path) {
        File file = new File(path+".txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(data.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("write err");
        }
    }

    public static String getRequest(String url) {
        // 输入流
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        // 创建httpClient实例
        HttpClient httpClient = new HttpClient();
        // 设置http连接主机服务超时时间：15000毫秒
        // 先获取连接管理器对象，再获取参数对象,再进行参数的赋值
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);
        DefaultHttpParams.getDefaultParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
        // 创建一个Get方法实例对象
        GetMethod getMethod = new GetMethod(url);
        // 设置get请求超时为60000毫秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        // 设置请求重试机制，默认重试次数：3次，参数设置为true，重试机制可用，false相反
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, true));
        try {
            // 执行Get方法
            int statusCode = httpClient.executeMethod(getMethod);
            // 判断返回码
            if (statusCode != HttpStatus.SC_OK) {
                // 如果状态码返回的不是ok,说明失败了,打印错误信息
                System.err.println("Method faild: " + getMethod.getStatusLine());
            } else {
                // 通过getMethod实例，获取远程的一个输入流
                is = getMethod.getResponseBodyAsStream();
                // 包装输入流
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                // 读取封装的输入流
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp).append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            System.out.println("getRequest err");
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 释放连接
            getMethod.releaseConnection();
        }
        return result;
    }

    public static void createData(String path) {

        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            System.out.println("error directory");
            return;
        }
        String[] list = file.list();
        for (String s : list) {
            System.out.println(s);
        }

        //sendEmail("hello");
        System.out.println("success!!!");
    }

    public static void sendEmail(String content) {
        SendEmailByQQ sendEmailByQQ = new SendEmailByQQ();
        sendEmailByQQ.setContent(content);
        sendEmailByQQ.setAuthorizationCode(JxfApp.properties.getProperty(Constants.AuthorizationCode));
        sendEmailByQQ.setProtocol(JxfApp.properties.getProperty(Constants.EMAILPROTOCOL));
        sendEmailByQQ.setHost(JxfApp.properties.getProperty(Constants.EMAILHOST));
        sendEmailByQQ.setAuth(JxfApp.properties.getProperty(Constants.EMAILAUTH));
        sendEmailByQQ.setPort(Integer.valueOf(JxfApp.properties.getProperty(Constants.EMAILPORT)));
        sendEmailByQQ.setSslEnable(JxfApp.properties.getProperty(Constants.EMAILSSLENABLE));
        sendEmailByQQ.setDebug(JxfApp.properties.getProperty(Constants.EMAILDEBUG));
        sendEmailByQQ.setReceiveEmail(JxfApp.properties.getProperty(Constants.EMAILRECEIVEURL));
        sendEmailByQQ.setFromEmail(JxfApp.properties.getProperty(Constants.EMAILFROMURL));
        new Thread(sendEmailByQQ).run();
    }
}
