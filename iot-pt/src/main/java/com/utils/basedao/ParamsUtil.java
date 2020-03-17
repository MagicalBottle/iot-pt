package com.utils.basedao;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*   @desc : 从json对象中获取查询条件
*   @auth : TYF
*   @date : 2019-10-31 - 16:56
*/
public class ParamsUtil {

    private static Logger logger = LoggerFactory.getLogger(ParamsUtil.class);

    /**
    *   @desc : 解析分页条件
    *   @auth : TYF
    *   @date : 2019-10-31 - 16:57
    */
    public static PageOrderConfig getPageConfig(JSONObject params){
        PageOrderConfig config = new PageOrderConfig();
        if(params!=null){
            JSONObject pageConfig = params.getJSONObject("pageConfig");
            if(pageConfig!=null){
                Integer pageNum = pageConfig.getInteger("pageNum");//当前页
                Integer pageSize = pageConfig.getInteger("pageSize");//每条条数
                if(pageNum!=null&&pageSize!=null&&pageNum>=1&&pageSize>=1){
                    config.setPageInfo(pageNum,pageSize);
                }
                String orderField = pageConfig.getString("orderField");//排序字段
                String orderType = pageConfig.getString("orderType");//排序方式
                if(orderField!=null&&!"".equals(orderField)&&orderType!=null&&!"".equals(orderType)){
                    config.setOrderInfo(orderField,orderType);
                }
            }
        }
        return config;
    }


    /**
    *   @desc : 解析高级查询条件
    *   @auth : TYF
    *   @date : 2019-10-31 - 17:09
    */
    public static List<SearchBean> getSearchBeanList(JSONObject params, Class clazz){
        List<SearchBean> res = new ArrayList<>();
        if(params!=null){
            JSONArray searchBeans = params.getJSONArray("searchBeans");
            if(searchBeans!=null){
                //遍历多个SearchBean
                searchBeans.stream().forEach(n->{
                    if(!"".equals(n)){
                        JSONObject nObj = JSONObject.parseObject(n.toString());
                        SearchBean sb = new SearchBean();
                        //实体字段名
                        String fieldName = nObj.getString("fieldName");
                        if(fieldName!=null&&!"".equals(fieldName)){
                            sb.setFieldName(fieldNameToColumName(fieldName));//实体字段名转数据库字段名
                        }
                        //大于(等于)时开始值
                        String startValue = nObj.getString("startValue");
                        if(startValue!=null&&!"".equals(startValue)){
                            sb.setStartValue(transforFieldType(startValue,fieldName,clazz));
                        }
                        //小于(等于)时结束值
                        String endValue = nObj.getString("endValue");
                        if(endValue!=null&&!"".equals(endValue)){
                            sb.setEndValue(transforFieldType(endValue,fieldName,clazz));
                        }
                        //操作符
                        String operator = nObj.getString("operator");
                        if(operator!=null&&!"".equals(operator)){
                            sb.setOperator(getOperator(operator));
                        }
                        //基础值
                        String basicValue = nObj.getString("basicValue");
                        if(basicValue!=null&&!"".equals(basicValue)&&fieldName!=null&&clazz!=null){
                            //in时传list(逗号分隔)
                            if("CONTAINS".equals(operator)){
                                String[] bvArry = basicValue.split(",");
                                List<Object> bvList = new ArrayList<>();
                                if(bvArry!=null&&bvArry.length>=1){
                                    Arrays.stream(bvArry).forEach(bvlaue->{
                                        Object value = transforFieldType(bvlaue,fieldName,clazz);
                                        bvList.add(value);
                                    });
                                }
                                sb.setBasicValue(bvList);
                            }
                            //like时传单个object
                            else if("LIKE".equals(operator)){
                                Object value = transforFieldType(basicValue,fieldName,clazz);
                                sb.setBasicValue(value);
                            }
                        }
                        res.add(sb);
                    }
                });
            }
        }
        return res;
    }

    /**
    *   @desc : 解析基础查询实体
    *   @auth : TYF
    *   @date : 2019-11-01 - 9:46
    */
    public  static <T> T getBaseBean(JSONObject params,Class<T> clazz){
        T tObj = null;
        if(params!=null){
            JSONObject baseBean = params.getJSONObject("baseBean");
            if(baseBean!=null){
                Set<String> keys = baseBean.keySet();
                try {
                    tObj = clazz.newInstance();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(keys.size()>=1){
                    for (String key:keys){
                        try {
                            Field f = clazz.getDeclaredField(key);
                            f.setAccessible(true);
                            f.set(tObj,transforFieldType(baseBean.getString(key),key,clazz));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if(tObj==null){
            try {
                tObj = clazz.newInstance();
            }catch (Exception e){
                logger.info("clazz.newInstance()失败 e="+e.getCause());
            }
        }
        return tObj;
    }



    /**
    *   @desc : 操作符转换
    *   @auth : TYF
    *   @date : 2019-11-01 - 9:17
    */
    public static FieldOperator getOperator(String operator){
        FieldOperator fieldOperator = null;
        if(operator!=null&&!"".equals(operator)){
            switch (operator){
                case "GREATER_THAN":
                    fieldOperator = FieldOperator.GREATER_THAN;
                    break;
                case "LESS_THAN":
                    fieldOperator = FieldOperator.LESS_THAN;
                    break;
                case "GREATER_THAN_AND_EQUAL":
                    fieldOperator = FieldOperator.GREATER_THAN_AND_EQUAL;
                    break;
                case "LESS_THAN_AND_EQUAL":
                    fieldOperator = FieldOperator.LESS_THAN_AND_EQUAL;
                    break;
                case "CONTAINS":
                    fieldOperator = FieldOperator.CONTAINS;
                    break;
                case "NOT":
                    fieldOperator = FieldOperator.NOT;
                    break;
                case "LIKE":
                    fieldOperator = FieldOperator.LIKE;
                    break;
                case "BETWEEN":
                    fieldOperator = FieldOperator.BETWEEN;
                    break;
                default:
                    fieldOperator = null;
            }
        }
        return fieldOperator;
    }



    /**
    *   @desc : 字符串到任意属性类型转换
    *   @auth : TYF
    *   @date : 2019-11-01 - 10:38
    */
    public static Object transforFieldType(String value, String name, Class clazz){
        if (value==null||name==null||"".equals(value)||"".equals(name)){
            return null;
        }
        try {
            Field f = clazz.getDeclaredField(name);
            f.setAccessible(true);
            String type = f.getType().toString();
            switch (type){
                case "class java.lang.Integer":
                    return Integer.valueOf(value);
                case "class java.lang.Long":
                    return Long.valueOf(value);
                case "class java.lang.String":
                    return String.valueOf(value);
                default:
                    return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
    *   @desc : 实体字段名称转数据库字段名称(大写转下划线加小写)
    *   @auth : TYF
    *   @date : 2019-11-01 - 13:32
    */
    public static String fieldNameToColumName(String fieldName){
        if(fieldName==null||"".equals(fieldName)){
            return "";
        }
        fieldName= String.valueOf(fieldName.charAt(0)).toUpperCase().concat(fieldName.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern= Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(fieldName);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end()==fieldName.length()?"":"_");
        }
        return sb.toString().toLowerCase();
    }


    public static void main(String[] args) {

        /*
        {
              "pageConfig": {
                "orderType": "desc",
                "pageSize": "80",
                "orderField": "id",
                "pageNum": "2"
              },
              "searchBeans": [
                {
                  "fieldName": "createTime",
                  "startValue": "1572493580",
                  "operator": "GREATER_THAN_AND_EQUAL"
                },
                {
                  "fieldName": "createTime",
                  "startValue": "1572493580",
                  "endValue": "1572493580",
                  "operator": "BETWEEN"
                },
                {
                  "fieldName": "mobile",
                  "operator": "LIKE",
                  "basicValue": "133"
                },
                {
                  "fieldName": "remark",
                  "operator": "CONTAINS",
                  "basicValue": "无,未知"
                }
              ],
              "baseBean": {
                "id": "30222",
                "state": "1"
              }
           }
         */


    }

}
