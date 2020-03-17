package com.utils.basedao;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*   @desc : 实体/Map 转化
*   @auth : TYF
*   @data : 2019-01-25 - 14:26
*/
@Component
public class OrmUtil<T> {

    /**
    *   @desc : POJO转MSP
    *   @auth : TYF
    *   @data : 2019-01-25 - 14:27
    */
    public Map<String, Object> pojoToMap(T t){
        if(t==null){
            return null;
        }
        Field[] declaredFields = t.getClass().getDeclaredFields();
        Map<String, Object> map = new HashMap<String, Object>();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                Object value =  field.get(t);
                if(value!=null){
                    String key = camel2Underline(field.getName());
                    map.put(key, field.get(t));
                }
            }  catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
    *   @desc : MAP转POJO
    *   @auth : TYF
    *   @data : 2019-01-25 - 14:28
    */
    public void mapToPojo(Map<String, Object> map, T t) {
        Field[] declaredFields = t.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                String key = camel2Underline(field.getName());
                field.set(t, map.get(key));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
    *   @desc : list<MAP>转list<POJO>
    *   @auth : TYF
    *   @data : 2019-01-25 - 14:28
    */
    public List<T> getPojoList(List<Map<String,Object>> list, Class<T> c){
        List<T> result = new ArrayList<T>();
        for(int i=0;i<list.size();i++){
            try {
                T t = c.newInstance();
                Map<String, Object> map = list.get(i);
                mapToPojo(map, t);
                result.add(t);
            } catch (InstantiationException |IllegalAccessException e) {
                throw new RuntimeException("Create Pojo Error!");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
    *   @desc : 根据实体类型获取表名称
    *   @auth : TYF
    *   @data : 2019-01-25 - 14:29
    */
    public String getTableName(Class<?> modelClass){
        String simpleName = modelClass.getSimpleName();
        return camel2Underline(simpleName);
    }

    /**
     * 检查高级自定义条件是否符合规范
     * @param searchBeans
     */
    public void checkSearchBeans(List<SearchBean> searchBeans){
        if(searchBeans!=null&&!searchBeans.isEmpty()){
            for (SearchBean searchBean : searchBeans) {
                String operator = searchBean.getOperator();
                Object basicValue = searchBean.getBasicValue();
                Object startValue = searchBean.getStartValue();
                Object endValue = searchBean.getEndValue();
                if("like".equals(operator)){//like将String转为List<String>
                    if(basicValue instanceof String) {
                        List<String> likeList = new ArrayList<>();
                        likeList.add((String)basicValue);
                        searchBean.setBasicValue(likeList);
                    }
                }else if("in".equals(operator)){
                    if(!(basicValue instanceof List)){//in必须为List
                        throw new RuntimeException("'in' operator needs List basicValue!");
                    }
                }else if("gt".equals(operator)||"ge".equals(operator)){//大于等于
                    if(startValue==null){
                        throw new RuntimeException("'gt' or 'gr' operator needs startValue!");
                    }
                }else if("lt".equals(operator)||"le".equals(operator)){
                    if(endValue==null){
                        throw new RuntimeException("'lt' or 'le' operator needs endValue!");
                    }
                }else if("bt".equals(operator)){
                    if(startValue==null||endValue==null){
                        throw new RuntimeException("'bt' operator needs startValue and endValue!");
                    }
                }
            }
        }
    }
    public static String camel2Underline(String line){
        if(line==null||"".equals(line)){
            return "";
        }
        line= String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuffer sb=new StringBuffer();
        Pattern pattern= Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher=pattern.matcher(line);
        while(matcher.find()){
            String word=matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end()==line.length()?"":"_");
        }
        return sb.toString().toLowerCase();
    }
}