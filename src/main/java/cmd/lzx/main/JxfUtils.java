package cmd.lzx.main;

import com.alibaba.fastjson.util.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author 1Zx.
 * @data 2019/11/21 9:45
 */
public final class JxfUtils {

    private static StringBuffer sbSend = new StringBuffer();

    private static void exportTxt(StringBuffer data,String path) {
        sbSend.append(data);
        File file = new File(path+".txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
            bw.write(data.toString());
            bw.flush();
            bw.close();
        } catch (IOException e) {
            System.out.println("write err");
        }
    }

    public static String getRequest(String httpurl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept","*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e1) {
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e1) {
                }
            }
        } catch (IOException e) {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e1) {
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e1) {
                }
            }
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

            connection.disconnect();// 关闭远程连接
        }

        return result;
    }

    public static void createData(String path) {
        if (!path.endsWith("\\")) {
            path = path + "\\";
        }
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            System.out.println("error directory");
            return;
        }
        String[] list = file.list();
        if (list.length != 2) {
            System.out.println("Wrong number of files in the directory");
            return;
        }
        String txtName = null,pathName = null;
        for (String s : list) {
            if (s.endsWith("txt")) {
                txtName = s;
            } else {
                pathName = s;
            }
        }
        if (null == txtName) {
            System.out.println("error textName");
            return;
        }
        if (null == pathName) {
            System.out.println("cant find pathName directory");
            return;
        }
        File files = new File(path + pathName);
        if (!files.isDirectory()) {
            System.out.println("error pathName");
            return;
        }
        /** 读取txt 内容*/
        Map<String, List<String>> textData = readTextData(new File(path+txtName));
        Map<String, Map<String,List<String>>> outData = new LinkedHashMap<>();
        Iterator<Map.Entry<String,List<String>>> iterator = textData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String,List<String>> entry = iterator.next();
            String titlePath = path + txtName.replace(".txt","") + "\\" + entry.getKey().trim();
            List<String> ids = entry.getValue();
            Map<String,List<String>> keyIdValueIdAndTitle = new LinkedHashMap<>();
            int j = 1;
            for (int i = 0; i < ids.size(); i++) {
                keyIdValueIdAndTitle.put(ids.get(i).trim(),createIdAndTitle(ids.get(i).trim(),new File(titlePath+"\\"+entry.getKey().trim()+j+".txt")));
                j++;
            }
            outData.put(entry.getKey(),keyIdValueIdAndTitle);

        }

        File outDic = new File(path + "out");
        if (!outDic.exists()) {
            outDic.mkdirs();
        }
        Iterator<Map.Entry<String,Map<String,List<String>>>> outIterator = outData.entrySet().iterator();
        while (outIterator.hasNext()) {
            Map.Entry<String,Map<String,List<String>>> nameEntry = outIterator.next();
            Iterator<Map.Entry<String,List<String>>> idAndTitleIterator = nameEntry.getValue().entrySet().iterator();
            int i = 1;
            while (idAndTitleIterator.hasNext()) {
                Map.Entry<String,List<String>> entry = idAndTitleIterator.next();
                List<String> out = entry.getValue();
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < out.size(); j++) {
                    sb.append(out.get(j));
                        sb.append(System.getProperty("line.separator"));
                }
                exportTxt(sb,outDic.getPath() + "\\" + i);
                i++;
            }
        }
        sendEmail(sbSend.toString());
        System.out.println("success!!!");
    }

    private static List<String> createIdAndTitle(String s, File file) {
        List<String> idAndTitle = new LinkedList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file),Charset.forName("GBK")));
            String line;
            while (null != (line = br.readLine())) {
                idAndTitle.add(s.trim()+"$$"+line.trim());
            }
        } catch (FileNotFoundException e) {
            System.out.println("cant find txt:" + file.getPath());
            System.exit(0);
        } catch (IOException e) {
            System.out.println("read file error" + file.getPath());
            System.exit(0);
        }
        return idAndTitle;
    }

    private static Map<String, List<String>> readTextData(File file) {
        Map<String,List<String>> result = new LinkedHashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("GBK")));
            String line;
            String key = null;
            while (null != (line = br.readLine())) {
                line = line.trim();
                if (StringUtils.isNotBlank(line)) {
                    if (!StringUtils.isNumeric(line)) {
                        key = line;
                        result.put(key,new LinkedList<>());
                    } else if (StringUtils.isNumeric(line)){
                        result.get(key).add(line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("txt file not found");
            return null;
        } catch (IOException e) {
            System.out.println("read error");
            return null;
        } finally {
            if (null != br) {
                IOUtils.close(br);
            }
        }
        return result;
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
