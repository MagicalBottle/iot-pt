package com.utils;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @desc : 请求解析工具
 * @auth : TYF
 * @date : 2019-06-28 - 17:04
 */
public class RequestUtil {

    private static Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    /**
    *   @desc : request中解析参数到map
    *   @auth : TYF
    *   @date : 2019-06-28 - 17:14
    */
    public static TreeMap<String, String> parseXml(HttpServletRequest request){
        TreeMap<String, String> map = new TreeMap<>();
        try {
            InputStream inputStream = request.getInputStream();
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            @SuppressWarnings("unchecked")
            List<Element> elementList = root.elements();
            for (Element e : elementList)
                map.put(e.getName(), e.getText());
            inputStream.close();
        }
        catch (IOException e){
            logger.info("Xml转Map异常");
            return null;
        }
        catch (DocumentException e){
            logger.info("Xml转Map异常");
            return null;
        }
        return map;
    }

    /**
     *   @desc : 参数非空验证
     *   @auth : TYF
     *   @date : 2019-07-01 - 15:14
     */
    public static String checkParam(Map<String,String> params, String ... names){

        StringBuffer res = new StringBuffer();
        res.append("");
        for(int i=0;i<names.length;i++){
            String name = names[i];
            String value = params.get(name);
            if(value==null||value.equals("")){
                res.append(name+" is error ! ");
            }
        }
        return res.toString();
    }

    /**
    *   @desc : 请求map转为jsonObj
    *   @auth : TYF
    *   @date : 2019-10-10 - 14:25
    */
    public static JSONObject getJsonFromMap(HttpServletRequest request){
        JSONObject result = new JSONObject();
        request.getParameterMap().keySet().stream().forEach(n->result.put(n,request.getParameter(n)));
        return result;
    }

    public static Integer getInteger(HttpServletRequest request , String param, Integer defaultvalue){
        String value = processParams(request, param);
        if(value.equals(""))
            return defaultvalue;
        else {
            try {
                Integer integer = Integer.valueOf(value);
                return integer;
            } catch (Exception e) {
                return defaultvalue;
            }
        }
    }

    public static Long getLong(HttpServletRequest request ,String param,Long defaultvalue){
        String value = processParams(request, param);
        if(value.equals(""))
            return defaultvalue;
        else {
            try {
                Long lvalue = Long.valueOf(value);
                return lvalue;
            } catch (Exception e) {
                return defaultvalue;
            }
        }
    }


    public static String getString(HttpServletRequest request ,String param,String defaultvalue){
        String value= processParams(request, param);
        if(value.equals(""))
            return defaultvalue;
        else {
            try {
                return value;
            } catch (Exception e) {
                return defaultvalue;
            }
        }
    }
    public static String getString(HttpServletRequest request ,String param){
        return processParams(request, param);

    }

    public static Double getDouble(HttpServletRequest request ,String param,Double defaultvalue){
        String value = processParams(request, param);
        if(value.equals(""))
            return defaultvalue;
        else {
            try {
                Double dvalue = Double.valueOf(value);
                return dvalue;
            } catch (Exception e) {
                return defaultvalue;
            }
        }
    }


    public static String processParams(HttpServletRequest request,String param){
        if(request.getParameter(param)!=null)
            return request.getParameter(param);
        return "";
    }

}
