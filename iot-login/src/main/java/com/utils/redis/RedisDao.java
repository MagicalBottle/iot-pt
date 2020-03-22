package com.utils.redis;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Repository
public class RedisDao {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
    *   @desc : 获取key的过期时间
    *   @auth : TYF
    *   @date : 2020/3/16 - 23:32
    */
    public Long getExpire(String key){
        //-2
        //-1
        return stringRedisTemplate.getExpire(key,TimeUnit.SECONDS);
    }


    /**
    *   @desc : 操作hash
    *   @auth : TYF
    *   @date : 2020/3/16 - 23:32
    */
    public void setHash(String key,Map<String,String> value){
        for(String v_key:value.keySet()){
            stringRedisTemplate.opsForHash().put(key,v_key,value.get(v_key));
        }
    }
    public void setHash(String key,Map<String,String> value,Integer expire){
        for(String v_key:value.keySet()){
            stringRedisTemplate.opsForHash().put(key,v_key,value.get(v_key));
            if(expire!=null){
                stringRedisTemplate.expire(key,expire,TimeUnit.SECONDS);
            }
        }
    }
    public Map<String,String> getHash(String key){
        Map <Object,Object> entrys =  stringRedisTemplate.opsForHash().entries(key);
        Map<String,String> values = new HashMap<>();
        for (Object obj:entrys.keySet()){
            values.put((String)obj,(String)entrys.get(obj));
        }
        return values;
    }
    public void setHashKV(String key,String v_key,String v_value){
        stringRedisTemplate.opsForHash().put(key,v_key,v_value);
    }

    public void incrementHashKV(String key,String v_key,Long delta){
        stringRedisTemplate.opsForHash().increment(key,v_key,delta);
    }

    public String getHashKV(String key,String v_key){
        String value = (String) stringRedisTemplate.opsForHash().get(key,v_key);
        return value ;
    }

    public boolean delHash(String key){
        boolean del = stringRedisTemplate.delete(key);
        return del;
    }



    /**
    *   @desc : 操作string
    *   @auth : TYF
    *   @date : 2020/3/16 - 23:33
    */
    public void setString(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }
    public void setString(String key,String value,Integer expire){
        stringRedisTemplate.opsForValue().set(key,value);
        if(expire==null){
            stringRedisTemplate.expire(key,5*60,TimeUnit.SECONDS);//默认5分钟
        }else {
            stringRedisTemplate.expire(key,expire,TimeUnit.SECONDS);
        }
    }
    public String getString(String key){
        return stringRedisTemplate.opsForValue().get(key);
    }
    public boolean delString(String key){
        boolean del = stringRedisTemplate.delete(key);
        return del;
    }


    //按照前缀模糊批量查询
    public JSONObject getStringByPrefix(String prefix){
        JSONObject res = new JSONObject();
        List<String> keys = new ArrayList<>(stringRedisTemplate.keys(prefix+"*"));
        List<String> values = stringRedisTemplate.opsForValue().multiGet(keys);
        for(int i=0;i<keys.size();i++){
            res.put(keys.get(i),values.get(i));
        }
        return res;
    }


}
