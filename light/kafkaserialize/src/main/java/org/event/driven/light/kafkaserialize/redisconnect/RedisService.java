package org.event.driven.light.kafkaserialize.redisconnect;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

public class RedisService {
    private static JedisPool pool;

    static {
        pool = RedisHelper.jedisPool;
    }

    public static boolean insert2Redis(String key, String value){
        String status = "";
        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            status = jedis.set(key, value);
        }catch(Exception e){
            System.out.println("Error in inserting key: {"+key+"}, value: {"+value+"} to Redis.");
            e.printStackTrace();
        }finally
        {
            if (jedis != null)
                jedis.close();
        }

        return status.equals("OK");
    }

    public static boolean insertListValue(String key, String value){
        Long result = 0L;
        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            result = jedis.lpush(key, value);
        }catch(Exception e){
            System.out.println("Error in inserting "+key+"-"+value+" to Redis List.");
           // e.printStackTrace();
        }finally
        {
            if (jedis != null)
                jedis.close();
        }

        return result>0;
    }

    public static String getValue(String key){
        String value = "";
        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            value = jedis.get(key);
        }catch(Exception e){
            System.out.println("Error in get value about key: {"+key+"} from Redis.");
            e.printStackTrace();
        }finally
        {
            if (jedis != null)
                jedis.close();
        }

        return value;
    }

    public static boolean delValue(String key) {
        Long result = 0L;
        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            result = jedis.del(key);
        }catch(Exception e){
            System.out.println("Error in delete key: {"+key+"} from Redis.");
           // e.printStackTrace();
        }finally
        {
            if (jedis != null)
                jedis.close();
        }

        return result > 0;
    }

    public static List<String> getListValue(String key) {
        List<String> result = new ArrayList<>();
        Jedis jedis = null;

        try{
            jedis = pool.getResource();
            result=jedis.lrange(key, 0, 1);
        }catch(Exception e){
            System.out.println("Error in get List of key{"+key+"} from Redis");
        }finally
        {
            if (jedis != null)
                jedis.close();
        }

        return result;
    }

    public static void main(String[] args){
        //insert2Redis("3", "4");
        //String value=getValue("3");
        //insertListValue("5", "5");
        //insertListValue("5", "6");
        //System.out.println(value);
        List<String> res=getListValue("3d54856c-2e0e-4998-a3e5-181f579b2250");
        System.out.println(res.toString());
    }
}
