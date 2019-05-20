package com.example.demo.download;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownLoadMain {
    private static final String HTML_SRC = "src";
    private static final String HTML_BING_IMG_SRC = "img[src]";
    private static final String HTML_ZTO_IMG_SRC = "img[id=bigImg]";

    private static final String BING_BING_IMG_URL = "https://bing.ioliu.cn/?p=";
    private static final String BING_ZTO_IMG_URL = "http://desk.zol.com.cn/bizhi/376_ZTONum_2.html";
    private static final String LOCAL_DIR = "F:\\my\\图片\\桌面壁纸\\";
    private static final String LOCAL_IMG_SUBFIX = "xinsiji";
    private static final int BING_PAGENUM = 98;
    private static final int ZTO_IMG_NAME = 3000;


    public static void main(String[] args) {
        List<String> list = new ArrayList();


        downLoadZOLChineseStyleImgToLocal(list);
        downLoadBingImgToLocal(list);
        list.parallelStream().forEach(item -> downLoadImgToLocal(item));
    }


    /**
     * 中国风桌面壁纸欣赏-ZOL桌面壁纸
     * @param list
     */
    public static void downLoadZOLChineseStyleImgToLocal(List<String> list) {
        for (int i = 0; i < 1000; i++) {
            String urlPath = BING_ZTO_IMG_URL.replace("ZTONum",String.valueOf(ZTO_IMG_NAME + i));
            String s = HttpUtil.get(urlPath);
            Document doc = Jsoup.parse(s);
            Elements elements = doc.select(HTML_ZTO_IMG_SRC);
            for (int j = 0; j < elements.size(); j++) {
                String src = elements.get(j).attr(HTML_SRC);
                list.add(src);
            }
        }
    }

    /**
     * 下载bing壁纸
     * @param list
     */
    public static void downLoadBingImgToLocal(List<String> list) {
        for (int i = 2; i < BING_PAGENUM; i++) {
            String result = HttpUtil.get(BING_BING_IMG_URL + i);
            Document doc = Jsoup.parse(result);
            Elements elements = doc.select(HTML_BING_IMG_SRC);
            for (int j = 0; j < elements.size(); j++) {
                String src = elements.get(j).attr(HTML_SRC);
                list.add(src);
            }
        }
    }

    /**
     * 爬取图片到本地
     *
     * @param urlPath
     */
    public static void downLoadImgToLocal(String urlPath) {
        DataInputStream dataInputStream = null;
        ByteArrayOutputStream output = null;
        DataOutputStream dataOutputStream = null;
        HttpURLConnection connection = null;
        String fileName = LOCAL_DIR + LOCAL_IMG_SUBFIX + "_" + RandomUtil.randomNumbers(32) + "." + urlPath.substring(urlPath.lastIndexOf(".") + 1);
        try {
            URL url = new URL(urlPath);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            dataInputStream = new DataInputStream(connection.getInputStream());
            dataOutputStream = new DataOutputStream(new FileOutputStream(fileName));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                dataOutputStream.write(buffer, 0, length);
            }
            System.out.println("图片：" + urlPath + ";已下载成功；本地地址名：" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭，删除临时文件
            try {
                if (output != null) {
                    output.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
