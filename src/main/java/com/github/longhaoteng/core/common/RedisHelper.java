package com.github.longhaoteng.core.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * RedisHelper
 *
 * @author mr.long
 */
@Component
public class RedisHelper {

    private RedisTemplate redis;

    private static RedisTemplate redisTemplate;

    @Autowired
    public RedisHelper(RedisTemplate redis) {
        this.redis = redis;
    }

    @PostConstruct
    public void init() {
        RedisHelper.redisTemplate = redis;
    }

    // 出异常，重复操作的次数
    private static final Integer TIMES = 5;

    /**
     * 获取Redis中所有的键的key
     *
     * @return Set
     */
    public static Set<String> getAllKeys() {
        return redisTemplate.keys("*");
    }

    /**
     * 获取所有的普通key-value
     *
     * @return Map
     */
    public static Map<String, Object> getAllString() {
        Set<String> stringSet = getAllKeys();
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.STRING) {
                map.put(k, get(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的Set key-value
     *
     * @return Map
     */
    public static Map<String, Set<Object>> getAllSet() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.SET) {
                map.put(k, getSet(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的ZSet正序  key-value 不获取权重值
     *
     * @return Map
     */
    public static Map<String, Set<Object>> getAllZSetReverseRange() {
        Set<String> stringSet = getAllKeys();
        Map<String, Set<Object>> map = new HashMap<String, Set<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.ZSET) {
                map.put(k, getZSetReverseRange(k));
            }
        }
        return map;
    }

    /**
     * 获取整个有序集合ZSET，倒序
     *
     * @param key key
     * @return Set
     */
    public static Set<Object> getZSetReverseRange(String key) {
        return getZSetReverseRange(key, 0, getZSetSize(key));
    }

    /**
     * 获取所有的List key-value
     *
     * @return Map
     */
    public static Map<String, List<Object>> getAllList() {
        Set<String> stringSet = getAllKeys();
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.LIST) {
                map.put(k, getList(k));
            }
        }
        return map;
    }

    /**
     * 获取所有的Map key-value
     *
     * @return Map
     */
    public static Map<String, Map<String, Object>> getAllMap() {
        Set<String> stringSet = getAllKeys();
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        Iterator<String> iterator = stringSet.iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (getType(k) == DataType.HASH) {
                map.put(k, getMap(k));
            }
        }
        return map;
    }

    /**
     * 添加一个list
     *
     * @param key        key
     * @param objectList objectList
     */
    public static void addList(String key, List<Object> objectList) {
        for (Object obj : objectList) {
            addList(key, obj);
        }
    }

    /**
     * 向list中增加值
     *
     * @param key key
     * @param obj obj
     * @return 返回在list中的下标
     */
    public static long addList(String key, Object obj) {
        return redisTemplate.boundListOps(key).rightPush(obj);
    }

    /**
     * 向list中增加值
     *
     * @param key key
     * @param obj obj
     * @return 返回在list中的下标
     */
    public static long addList(String key, Object... obj) {
        return redisTemplate.boundListOps(key).rightPushAll(obj);
    }

    /**
     * 输出list
     *
     * @param key List的key
     * @param s   开始下标
     * @param e   结束的下标
     * @return List
     */
    public static List<Object> getList(String key, long s, long e) {
        return redisTemplate.boundListOps(key).range(s, e);
    }

    /**
     * 输出完整的list
     *
     * @param key key
     * @return List
     */
    public static List<Object> getList(String key) {
        return redisTemplate.boundListOps(key).range(0, getListSize(key));
    }

    /**
     * 获取list集合中元素的个数
     *
     * @param key key
     * @return long
     */
    public static long getListSize(String key) {
        return redisTemplate.boundListOps(key).size();
    }

    /**
     * 移除list中某值
     * 移除list中 count个value为object的值,并且返回移除的数量,
     * 如果count为0,或者大于list中为value为object数量的总和,
     * 那么移除所有value为object的值,并且返回移除数量
     *
     * @param key    key
     * @param object object
     * @return 返回移除数量
     */
    public static long removeListValue(String key, Object object) {
        return redisTemplate.boundListOps(key).remove(0, object);
    }

    /**
     * 移除list中某值
     *
     * @param key     key
     * @param objects objects
     * @return 返回移除数量
     */
    public static long removeListValue(String key, Object... objects) {
        long r = 0;
        for (Object object : objects) {
            r += removeListValue(key, object);
        }
        return r;
    }

    /**
     * 批量删除key对应的value
     *
     * @param keys keys
     */
    public static void remove(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                remove(keys[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(keys));
            }
        }
    }

    /**
     * 删除缓存
     * 根据key精确匹配删除
     *
     * @param key key
     */
    public static void remove(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除map中的某个对象
     *
     * @param key   map对应的key
     * @param field map中该对象的key
     */
    public static void removeMapField(String key, Object... field) {
        redisTemplate.boundHashOps(key).delete(field);
    }

    /**
     * 设置Set的过期时间
     *
     * @param key  key
     * @param time time
     * @return Boolean
     */
    public static Boolean setSetExpireTime(String key, Long time) {
        return redisTemplate.boundSetOps(key).expire(time, TimeUnit.SECONDS);
    }

    /**
     * 设置ZSet的过期时间
     *
     * @param key  key
     * @param time time
     * @return Boolean
     */
    public static Boolean setZSetExpireTime(String key, Long time) {
        return redisTemplate.boundZSetOps(key).expire(time, TimeUnit.SECONDS);
    }

    /**
     * 判断缓存中是否有key对应的value
     *
     * @param key key
     * @return boolean
     */
    public static boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取String缓存 可以是对象
     *
     * @param key key
     * @return Object
     */
    public static Object get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    /**
     * 读取String缓存 可以是对象
     *
     * @param keys keys
     * @return List
     */
    public static List<Object> get(String... keys) {
        List<Object> list = new ArrayList<Object>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }

    /**
     * 读取缓存 可以是对象 根据正则表达式匹配
     *
     * @param regKey regKey
     * @return List
     */
    public static List<Object> getByRegular(String regKey) {
        Set<String> stringSet = getAllKeys();
        List<Object> objectList = new ArrayList<Object>();
        for (String s : stringSet) {
            if (Pattern.compile(regKey).matcher(s).matches() && getType(s) == DataType.STRING) {
                objectList.add(get(s));
            }
        }
        return objectList;
    }

    /**
     * 写入缓存 可以是对象
     *
     * @param key   key
     * @param value value
     */
    public static void set(String key, Object value) {
        redisTemplate.boundValueOps(key).set(value);
    }

    /**
     * 写入缓存
     *
     * @param key        key
     * @param value      value
     * @param expireTime 过期时间 -单位s
     */
    public static void set(String key, Object value, Long expireTime) {
        redisTemplate.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 设置一个key的过期时间（单位：秒）
     *
     * @param key        key
     * @param expireTime expireTime
     * @return boolean
     */
    public static boolean setExpireTime(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取key的类型
     *
     * @param key key
     * @return DataType
     */
    public static DataType getType(String key) {
        return redisTemplate.type(key);
    }

    /**
     * 获取map
     *
     * @param key key
     * @return map
     */
    public static Map<String, Object> getMap(String key) {
        return redisTemplate.boundHashOps(key).entries();
    }

    /**
     * 获取map size
     *
     * @param key key
     * @return Long
     */
    public static Long getMapSize(String key) {
        return redisTemplate.boundHashOps(key).size();
    }

    /**
     * 获取map缓存中的某个对象
     *
     * @param key   map对应的key
     * @param field map中该对象的key
     * @param <T>   T
     * @return T
     */
    public static <T> T getMapField(String key, String field) {
        return (T) redisTemplate.boundHashOps(key).get(field);
    }

    /**
     * 判断map中对应key的key是否存在
     *
     * @param key   map对应的key
     * @param field field
     * @return Boolean
     */
    public static Boolean hasMapKey(String key, String field) {
        return redisTemplate.boundHashOps(key).hasKey(field);
    }

    /**
     * 获取map对应key的value
     *
     * @param key map对应的key
     * @return List
     */
    public static List<Object> getMapFieldValue(String key) {
        return redisTemplate.boundHashOps(key).values();
    }

    /**
     * 获取map的key
     *
     * @param key map对应的key
     * @return Set
     */
    public static Set<Object> getMapFieldKey(String key) {
        return redisTemplate.boundHashOps(key).keys();
    }

    /**
     * 添加map
     *
     * @param key key
     * @param map map
     */
    public static void addMap(String key, Map<String, Object> map) {
        redisTemplate.boundHashOps(key).putAll(map);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param value 值
     */
    public static void addMap(String key, String field, Object value) {
        redisTemplate.boundHashOps(key).put(field, value);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param value 值
     * @param time  过期时间-整个MAP的过期时间
     */
    public static void addMap(String key, String field, Object value, long time) {
        redisTemplate.boundHashOps(key).put(field, value);
        redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }

    /**
     * 向set中加入对象
     *
     * @param key 对象key
     * @param obj 值
     */
    public static void addSet(String key, Object... obj) {
        redisTemplate.boundSetOps(key).add(obj);
    }

    /**
     * 处理事务时锁定key
     *
     * @param key key
     */
    public static void watch(String key) {
        redisTemplate.watch(key);
    }

    /**
     * 移除set中的某些值
     *
     * @param key 对象key
     * @param obj 值
     * @return long
     */
    public static long removeSetValue(String key, Object obj) {
        return redisTemplate.boundSetOps(key).remove(obj);
    }

    /**
     * 移除set中的某些值
     *
     * @param key 对象key
     * @param obj 值
     * @return long
     */
    public static long removeSetValue(String key, Object... obj) {
        if (obj != null && obj.length > 0) {
            return redisTemplate.boundSetOps(key).remove(obj);
        }
        return 0L;
    }

    /**
     * 获取set的对象数
     *
     * @param key 对象key
     * @return long
     */
    public static long getSetSize(String key) {
        return redisTemplate.boundSetOps(key).size();
    }

    /**
     * 判断set中是否存在这个值
     *
     * @param key 对象key
     * @param obj obj
     * @return Boolean
     */
    public static Boolean hasSetValue(String key, Object obj) {
        Boolean boo = null;
        int t = 0;
        while (true) {
            try {
                boo = redisTemplate.boundSetOps(key).isMember(obj);
                break;
            } catch (Exception e) {
                t++;
            }
            if (t > TIMES) {
                break;
            }
        }
        return boo;
    }

    /**
     * 获得整个set
     *
     * @param key 对象key
     * @return Set
     */
    public static Set<Object> getSet(String key) {
        return redisTemplate.boundSetOps(key).members();
    }

    /**
     * 获得set 并集
     *
     * @param key      key
     * @param otherKey otherKey
     * @return Set
     */
    public static Set<Object> getSetUnion(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).union(otherKey);
    }

    /**
     * 获得set 并集
     *
     * @param key key
     * @param set set
     * @return Set
     */
    public static Set<Object> getSetUnion(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).union(set);
    }

    /**
     * 获得set 交集
     *
     * @param key      key
     * @param otherKey otherKey
     * @return Set
     */
    public static Set<Object> getSetIntersect(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).intersect(otherKey);
    }

    /**
     * 获得set 交集
     *
     * @param key key
     * @param set set
     * @return Set
     */
    public static Set<Object> getSetIntersect(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).intersect(set);
    }

    /**
     * 模糊移除 支持*号等匹配移除
     *
     * @param blears blears
     */
    public static void removeBlear(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }

    /**
     * 修改key名 如果不存在该key或者没有修改成功返回false
     *
     * @param oldKey oldKey
     * @param newKey newKey
     * @return Boolean
     */
    public static Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 模糊移除 支持*号等匹配移除
     *
     * @param blear blear
     */
    public static void removeBlear(String blear) {
        redisTemplate.delete(redisTemplate.keys(blear));
    }

    /**
     * 根据正则表达式来移除key-value
     *
     * @param blears blears
     */
    public static void removeByRegular(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }

    /**
     * 根据正则表达式来移除key-value
     *
     * @param blears blears
     */
    public static void removeByRegular(String blears) {
        Set<String> stringSet = getAllKeys();
        for (String s : stringSet) {
            if (Pattern.compile(blears).matcher(s).matches()) {
                redisTemplate.delete(s);
            }
        }
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key    key
     * @param blears blears
     */
    public static void removeMapFieldByRegular(String key, String... blears) {
        for (String blear : blears) {
            removeMapFieldByRegular(key, blear);
        }
    }

    /**
     * 根据正则表达式来移除 Map中的key-value
     *
     * @param key   key
     * @param blear blear
     */
    public static void removeMapFieldByRegular(String key, String blear) {
        Map<String, Object> map = getMap(key);
        Set<String> stringSet = map.keySet();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.boundHashOps(key).delete(s);
            }
        }
    }

    /**
     * 移除key 对应的value
     *
     * @param key   key
     * @param value value
     * @return Long
     */
    public static Long removeZSetValue(String key, Object... value) {
        return redisTemplate.boundZSetOps(key).remove(value);
    }

    /**
     * 移除key ZSet
     *
     * @param key key
     */
    public static void removeZSet(String key) {
        removeZSetRange(key, 0L, getZSetSize(key));
    }

    /**
     * 获取key ZSet
     *
     * @param key key
     * @return long
     */
    public static long getZSetSize(String key) {
        return redisTemplate.boundZSetOps(key).size();
    }

    /**
     * 删除，键为K的集合，索引start-index-end的元素子集
     *
     * @param key   key
     * @param start start
     * @param end   end
     */
    public static void removeZSetRange(String key, Long start, Long end) {
        redisTemplate.boundZSetOps(key).removeRange(start, end);
    }

    /**
     * 并集 将key对应的集合和key1对应的集合合并到key2中
     * 如果分数相同的值，都会保留
     * 原来key2的值会被覆盖
     *
     * @param key  key
     * @param key1 key1
     * @param key2 key2
     */
    public static void setZSetUnionAndStore(String key, String key1, String key2) {
        redisTemplate.boundZSetOps(key).unionAndStore(key1, key2);
    }

    /**
     * 获取有序集合ZSET
     * 键为K的集合，索引start-index-end的元素子集，倒序
     *
     * @param key   key
     * @param start 开始位置
     * @param end   结束位置
     * @return Set
     */
    public static Set<Object> getZSetReverseRange(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRange(start, end);
    }

    /**
     * 通过分数(权值)获取ZSET集合 正序 -从小到大
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public static Set<Object> getZSetRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).rangeByScore(start, end);
    }

    /**
     * 通过分数(权值)获取ZSET集合 倒序 -从大到小
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public static Set<Object> getZSetReverseRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).reverseRangeByScore(start, end);
    }

    /**
     * 键为K的集合，索引start-index-end的元素子集
     * 返回泛型接口（包括score和value），正序
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public static Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).rangeWithScores(start, end);
    }

    /**
     * 键为K的集合，索引start-index-end的元素子集
     * 返回泛型接口（包括score和value），倒序
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public static Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key, long start,
                                                                                       long end) {
        return redisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end);
    }
}
