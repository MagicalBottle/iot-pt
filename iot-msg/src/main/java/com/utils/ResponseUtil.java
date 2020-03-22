package com.utils;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @desc : 响应xml数据
 * @auth : TYF
 * @date : 2019-06-28 - 16:33
 */
public class ResponseUtil {

    //返回str
    public static void ajaxOutputSTR(HttpServletResponse response, String text) {
        try {
            response.setContentType("text/html;charset=utf-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST,GET");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(text);
            printWriter.flush();
            printWriter.close();
        } catch (Exception e) {
            //do nothing
            e.printStackTrace();
        }
    }



}
