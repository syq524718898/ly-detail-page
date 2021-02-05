package com.leyou.page.service;


import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.Spu;
import com.leyou.page.LyPageApplication;
import com.leyou.page.client.CategoryClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyPageApplication.class)
public class PageServiceTest {

    @Autowired
    private PageService pageService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void createHtml() {
        pageService.createHtml(156L);
    }

    @Test
    public void testCateApi()
    {
        List<Category> categories = categoryClient.queryByIds(Arrays.asList(75L, 76L));
        System.out.println(categories);
    }

    @Test
    public void testLocalCache()
    {
//        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxTotal(100);
//        config.setMaxIdle(5);
//        config.setMaxWaitMillis(1000 * 10);
//        config.setTestOnBorrow(true);
//        JedisPool jedisPool = new JedisPool(config, "192.168.3.39", 1111, 10000);
//        Jedis resource = jedisPool.getResource();
//        resource.set("kv","v");
//        cacheService.saveTest("kv","vsd");

        Spu spuFromRedisCache = cacheService.getSpuFromLocalCache(57l);
        System.out.println(spuFromRedisCache);
    }
}