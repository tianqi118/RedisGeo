package com.example.utils;

import com.example.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String KEY_PREFIX_GEO = Constants.GEO_PRREFIX;

    /**********************************************************************************
     * redis-地图GEO操作
     **********************************************************************************/

    /**
     * @Author Yuan
     * @Description 添加空间元素信息（纬度、经度、名字）,时间复杂度为O(log(N)),可设置过期时间
     * redis 命令：geoadd cityGeo 116.405285 39.904989 "北京"
     * @Date 2019/12/9 11:11
     * @param k key
     * @param point 定位信息 包含经纬度
     * @param m 具体地理位置名
     * @param time 过期时间 单位秒 <=0 不过期
     * @return
     **/
    public boolean addGeo(String k, Point point, Object m, long time) {
        String key = KEY_PREFIX_GEO  + k;
        try {
            Long add = redisTemplate.opsForGeo().geoAdd(key, point, m);
            if (add > 0) {
                if (time > 0) {
                    redisTemplate.expire(key, time, TimeUnit.SECONDS);
                }
                return true;
            } else {
//                log.info("缓存[" + key +"]" + "失败, point["+ point.getX() + "," +
//                        point.getY() + "], member[" + m + "]已存在");
                return false;
            }
        } catch (Exception e) {
//            log.error("缓存[" + key +"]" + "失败, point["+ point.getX() + "," +
//                    point.getY() + "], member[" + m + "]" +", error[" + e + "]");
            return false;
        }
    }

    /**
     * @Author Yuan
     * @Description 添加空间元素信息（纬度、经度、名字）,时间复杂度为O(log(N)),永不过期
     * redis 命令：geoadd cityGeo 116.405285 39.904989 "北京"
     * @Date 2019/12/9 11:11
     * @param k key
     * @param point 定位信息 包含经纬度
     * @param m 具体地理位置名
     * @return java.lang.Long
     **/
    public boolean addGeo(String k, Point point, Object m) {
        return addGeo(k, point, m, -1);
    }

    /**
     * @Author Yuan
     * @Description 添加空间元素信息（纬度、经度、名字）,时间复杂度为O(log(N))，可设置过期时间
     * @Date 2019/12/9 11:08
     * @param k key
     * @param x 经度
     * @param y 纬度
     * @param m 具体地理位置名
     * @param time 过期时间 单位秒 <=0 不过期
     * @return boolean
     **/
    public boolean addGeo(String k, double x, double y, Object m, long time) {
        Point point = new Point(x, y);
        return addGeo(k, point, m, time);
    }

    /**
     * @Author Yuan
     * @Description 添加空间元素信息（纬度、经度、名字）,时间复杂度为O(log(N))，永不过期
     * @Date 2019/12/9 14:02
     * @param k key
     * @param x 经度
     * @param y 纬度
     * @param m 具体地理位置名
     * @return boolean
     **/
    public boolean addGeo(String k, double x, double y, Object m) {
        return addGeo(k, x, y, m,-1);
    }

    /**
     * @Author Yuan
     * @Description 批量添加空间元素信息,可设置过期时间
     * @Date 2019/12/9 11:43
     * @param k
     * @param locations
     * @param time
     * @return boolean
     **/
    public boolean addGeoBatch(String k, List<GeoLocation<String>> locations, long time) {
        try {
            for (GeoLocation<String> location : locations) {
                removeGeoKey(k, location.getName());
                addGeo(k, location.getPoint().getX(), location.getPoint().getY(), location.getName(), time);
            }
            return true;
        } catch (Throwable e) {
//            log.error("缓存[" + KEY_PREFIX_GEO + k + "]" + "失败" +", error[" + e + "]");
            return false;
        }
    }

    /**
     * @Author Yuan
     * @Description 批量添加空间元素信息,永不过期
     * @Date 2019/12/9 11:43
     * @param k
     * @param locations
     * @return boolean
     **/
    public boolean addGeoBatch(String k, List<GeoLocation<String>> locations) {
        return addGeoBatch(k, locations, -1);
    }

    /**
     * @Author Yuan
     * @Description 根据key和member获取这些member的坐标信息
     * redis命令：geopos cityGeo 北京
     * @Date 2019/12/9 14:34
     * @param k
     * @param m
     * @return java.util.List<org.springframework.data.geo.Point>
     **/
    public List<Point> geoGet(String k, Object... m) {
        String key = KEY_PREFIX_GEO  + k;
        try {
            return redisTemplate.opsForGeo().geoPos(key, m);
        } catch (Exception e) {
//            log.error("获取坐标[" + key +"]" + "失败]" + ", error[" + e + "]");
        }
        return null;
    }


    /**
     * @Author Yuan
     * @Description 返回两个地方的距离，可以指定单位，如果两个位置之间的其中一个不存在， 那么命令返回空值
     * redis命令：geodist cityGeo 北京 上海
     * @Date 2019/12/9 14:21
     * @param k key
     * @param mk1 地理位置1
     * @param mk2 地理位置2
     * @param metric 单位（米m，千米km，英里mi，英尺ft）
     * @return org.springframework.data.geo.Distance
     **/
    public Distance geoDist(String k, Object mk1, Object mk2, Metric metric) {
        String key = KEY_PREFIX_GEO  + k;
        try {
            return redisTemplate.opsForGeo().geoDist(key, mk1, mk2, metric);
        } catch (Exception e) {
//            log.error("计算距离[" + key +"]" + "失败, member[" + mk1 + "," + mk2 +"], error[" + e + "]");
        }
        return null;
    }

    /**
     * @Author Yuan
     * @Description 返回两个地方的距离，默认使用米作为单位，如果两个位置之间的其中一个不存在， 那么命令返回空值
     * @Date 2019/12/9 14:21
     * @param k key
     * @param mk1 地理位置1
     * @param mk2 地理位置2
     * @return org.springframework.data.geo.Distance
     **/
    public Distance geoDist(String k, Object mk1, Object mk2) {
        return geoDist(k, mk1, mk2, DistanceUnit.METERS);
    }

    /**
     * @Author Yuan
     * @Description 返回某一经纬度地点距另一地方的距离，默认使用米作为单位，如果两个位置之间的其中一个不存在， 那么命令返回空值
     * @Date 2019/12/9 14:21
     * @param k key
     * @param mk1 地理位置1
     * @param mk2 地理位置2
     * @return org.springframework.data.geo.Distance
     **/
    public Distance geoDist(String k, double x, double y, Object mk1, Object mk2) {
        //保存mk1当前位置
        removeGeoKey(k, mk1);
        addGeo(k, x, y, mk1);

        //计算mk1和mk2距离
        return geoDist(k, mk1, mk2, DistanceUnit.METERS);
    }

    /**
     * @Author Yuan
     * @Description 根据给定的经纬度，返回半径不超过指定距离的元素,时间复杂度为O(N+log(M))，N为指定半径范围内的元素个数，M为要返回的个数
     * redis命令：georadius cityGeo 116.405285 39.904989 100 km WITHDIST WITHCOORD ASC COUNT 5
     * @Date 2019/12/9 14:55
     * @param k key
     * @param x 经度
     * @param y 纬度
     * @param distance 查询距离 单位：米
     * @param direction 距离排序规则，ASC-由近及远 DESC-由远及近
     * @param limit 查询数量
     * @return org.springframework.data.geo.GeoResults<org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation<java.lang.Object>>
     **/
    public GeoResults<GeoLocation<Object>> radiusGeo(String k, double x, double y, double distance, Direction direction, long limit) {
        String key = KEY_PREFIX_GEO  + k;
        try {
            //设置geo查询参数
            GeoRadiusCommandArgs geoRadiusArgs = GeoRadiusCommandArgs.newGeoRadiusArgs();
            geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();//查询返回结果包括距离和坐标
            if (Direction.ASC.equals(direction)) {//按查询出的坐标距离中心坐标的距离进行排序
                geoRadiusArgs.sortAscending();
            } else if (Direction.DESC.equals(direction)) {
                geoRadiusArgs.sortDescending();
            }
            if (limit != 0) {
                geoRadiusArgs.limit(limit);//限制查询数量
            }
            Distance dis = new Distance(distance, DistanceUnit.METERS);
            GeoResults<GeoLocation<Object>> radiusGeo = redisTemplate.opsForGeo().geoRadius(key, new Circle(new Point(x, y), dis), geoRadiusArgs);

            return radiusGeo;
        } catch (Exception e) {
//            log.error("通过坐标[" + x + "," + y +"]获取范围[" + distance + "m]的其它坐标失败!" + ", error[" + e + "]");
        }
        return null;
    }

    /**
     * @Author Yuan
     * @Description 根据给定的经纬度，返回半径不超过指定距离的元素,默认由近及远，不限制数量
     * redis命令：georadius cityGeo 116.405285 39.904989 100 km WITHDIST WITHCOORD ASC
     * @Date 2019/12/9 14:55
     * @param k key
     * @param x 经度
     * @param y 纬度
     * @param distance 查询距离 单位：米
     * @return org.springframework.data.geo.GeoResults<org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation<java.lang.Object>>
     **/
    public GeoResults<GeoLocation<Object>> radiusGeo(String k, double x, double y, double distance) {
        return radiusGeo(k, x, y, distance, Direction.ASC, 0);
    }

    /**
     * @Author Yuan
     * @Description 根据给定的地点，返回半径不超过指定距离的元素,时间复杂度为O(N+log(M))，N为指定半径范围内的元素个数，M为要返回的个数
     * redis命令：georadiusbymember Sicily Agrigento 100 km WITHDIST WITHCOORD ASC COUNT 5
     * @Date 2019/12/9 14:55
     * @param k key
     * @param m 具体地理位置名
     * @param distance 查询距离 单位：米
     * @param direction 距离排序规则，ASC-由近及远 DESC-由远及近
     * @param limit 查询数量
     * @return org.springframework.data.geo.GeoResults<org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation<java.lang.Object>>
     **/
    public GeoResults<GeoLocation<Object>> radiusGeoByMember(String k, Object m, double distance, Direction direction, long limit) {
        String key = KEY_PREFIX_GEO  + k;
        try {
            //设置geo查询参数
            GeoRadiusCommandArgs geoRadiusArgs = GeoRadiusCommandArgs.newGeoRadiusArgs();
            geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();//查询返回结果包括距离和坐标
            if (Direction.ASC.equals(direction)) {//按查询出的坐标距离中心坐标的距离进行排序
                geoRadiusArgs.sortAscending();
            } else if (Direction.DESC.equals(direction)) {
                geoRadiusArgs.sortDescending();
            }
            if (limit != 0) {
                geoRadiusArgs.limit(limit);//限制查询数量
            }

            Distance dis = new Distance(distance, DistanceUnit.METERS);
            GeoResults<GeoLocation<Object>> radiusGeo = redisTemplate.opsForGeo().geoRadiusByMember(key, m,dis, geoRadiusArgs);

            return radiusGeo;
        } catch (Exception e) {
            log.error("通过位置[" + m +"]获取范围[" + distance + "m]的其它坐标失败!" + ", error[" + e + "]");
        }
        return null;
    }

    /**
     * @Author Yuan
     * @Description 根据给定的地点，返回半径不超过指定距离的元素,默认由近及远，不限制数量
     * redis命令：georadiusbymember Sicily Agrigento 100 km WITHDIST WITHCOORD ASC
     * @Date 2019/12/9 14:55
     * @param k key
     * @param m 具体地理位置名
     * @param distance 查询距离 单位：米
     * @return org.springframework.data.geo.GeoResults<org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation<java.lang.Object>>
     **/
    public GeoResults<GeoLocation<Object>> radiusGeoByMember(String k, Object m, double distance) {
        return radiusGeoByMember(k, m, distance, Direction.ASC, 0);
    }


    /**
     * 返回的是geohash值,查找一个位置的时间复杂度为O(log(N))
     * redis命令：geohash cityGeo 北京
     * @param k
     * @param mks
     * @return
     */
    public List geoHash(Object k, Object... mks) {
        String key = KEY_PREFIX_GEO  + k;
        List<String> results = redisTemplate.opsForGeo().geoHash(key, mks);
        return results;
    }

    /**
     * @Author Yuan
     * @Description 判断缓存是否存在
     * @Date 2019/12/9 15:23
     * @param k
     * @return boolean
     **/
    public boolean containsGeoKey(String k, Object m) {
        String key = KEY_PREFIX_GEO  + k;

        return exists(key);
    }

    public boolean removeGeoKey(String k, Object m) {
        String key = KEY_PREFIX_GEO  + k;
        Long remove = redisTemplate.opsForZSet().remove(key, m);
        if (remove > 0) {
            return true;
        }
        return false;
    }


    /**********************************************************************************
     * redis-加锁操作
     **********************************************************************************/

    /**
     * 获取锁
     * 获取失败，return false
     *
     * @param redisKey
     * @return
     */
    public boolean lock(String redisKey, int expireTime) {
        Boolean setResult = redisTemplate.opsForValue().setIfAbsent(redisKey, "true");
        if (!setResult) {
            return setResult;
        }
        redisTemplate.opsForValue().getOperations().expire(redisKey, expireTime, TimeUnit.SECONDS);
        return setResult;
    }

    /**
     * 获取锁
     * 获取失败，return false
     *
     * @param redisKey
     * @return
     */
    public boolean lock(String redisKey) {
        return lock(redisKey, 30);
    }

    public void unlock(String redisKey) {
        redisTemplate.opsForValue().getOperations().delete(redisKey);
    }

    /**********************************************************************************
     * redis-公共操作
     **********************************************************************************/

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis：指定缓存失效时间-异常】", e);
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效;如果该key已经过期,将返回"-2";
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("【redis：判断{}是否存在-异常】", key, e);
            return false;
        }
    }


    /**********************************************************************************
     * redis-String类型的操作
     **********************************************************************************/

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("【redis：普通缓存放入-异常】", e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis：普通缓存放入并设置时间-异常】", e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }


    /**
     * 获取缓存
     *
     * @param key   redis的key
     * @param clazz value的class类型
     * @param <T>
     * @return value的实际对象
     */
    public <T> T get(String key, Class<T> clazz) {
        Object obj = key == null ? null : redisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        if (!obj.getClass().isAssignableFrom(clazz)) {
            throw new ClassCastException("类转化异常");
        }
        return (T) obj;
    }

    /**
     * 获取泛型
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }


    /**********************************************************************************
     * redis-Map类型的操作
     **********************************************************************************/

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */

    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }


    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet - 存储Map对象
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("【redis：HashSet - 存储Map对象-异常】", e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis：HashSet - 存储Map对象并设置时间-异常】", e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */

    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("【redis：向一张hash表中放入数据,如果不存在将创建-异常】", e);
            return false;
        }
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */

    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis：向一张hash表中放入数据,如果不存在将创建-异常】", e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }


    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }


    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */

    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }


    /**********************************************************************************
     * redis-set操作
     **********************************************************************************/

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */

    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return null;
        }
    }


    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return false;
        }
    }


    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return 0;
        }
    }


    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return 0;
        }
    }


    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return 0;
        }
    }

    /**********************************************************************************
     * redis-list操作
     **********************************************************************************/


    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return null;
        }
    }


    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return 0;
        }
    }


    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头， 第二个元素，依次类推；index<0时，-，表尾，-倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return false;
        }

    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("【redis-异常】", e);
            return 0;
        }
    }

}
