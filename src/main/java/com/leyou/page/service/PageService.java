package com.leyou.page.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecClient;
import com.leyou.page.command.GetBrandFromMysqlCommand;
import com.leyou.page.command.GetCategorysFromMysqlCommand;
import com.leyou.page.command.GetSpecGroupsFromMysqlCommand;
import com.leyou.page.command.GetSpuFromMysqlCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author sunyuqi
 */
@Slf4j
@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${ly.page.path}")
    private String dest;

    public String loadModel(Long spuId) {
        Map<String, Object> model = new HashMap<>();

        Spu spu = cacheService.getSpuFromRedisCache(spuId);
        if (spu == null)
        {
            spu = cacheService.getSpuFromLocalCache(spuId);
            if (spu == null)
            {
                spu = new GetSpuFromMysqlCommand(spuId,goodsClient).execute();
                cacheService.saveSpu2LocalCache(spu);
            }
            cacheService.saveSpu2RedisCache(spu);
        }

        //上架未上架，则不应该查询到商品详情信息，抛出异常
        if (!spu.getSaleable()) {
            throw new LyException(ExceptionEnum.GOODS_NOT_SALEABLE);
        }

        SpuDetail detail = spu.getSpuDetail();
        List<Sku> skus = spu.getSkus();


        Brand brand = cacheService.getBrandFromRedisCache(spu.getBrandId());
        if (brand == null)
        {
            brand = cacheService.getBrandFromLocalCache(spu.getBrandId());
            if (brand == null)
            {
                brand = new GetBrandFromMysqlCommand(spu.getBrandId(),brandClient).execute();
                cacheService.saveBrand2LocalCache(brand);
            }
            cacheService.saveBrand2RedisCache(brand);
        }


        //查询三级分类
        List<Category> categories = new ArrayList<>();

        categories = cacheService.getCategorysFromRedisCache(spu.getCid1(),spu.getCid2(),spu.getCid3());
        if (CollectionUtils.isEmpty(categories))
        {
            String ids = spu.getCid1()+","+spu.getCid2()+","+spu.getCid3();
            categories = cacheService.getCategorysFromLocalCache(ids);
            if (CollectionUtils.isEmpty(categories))
            {
                categories = new GetCategorysFromMysqlCommand(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()), categoryClient).execute();
                cacheService.saveCategorys2LocalCache(categories,ids);
            }
            cacheService.saveCategorys2RedisCache(categories);
        }

        //查询规格参数
        List<SpecGroup> specs = cacheService.getSpecGroupsFromRedisCache(spu.getCid3());
        if (CollectionUtils.isEmpty(specs))
        {
            specs = cacheService.getSpecGroupsFromLocalCache(spu.getCid3());
            if (CollectionUtils.isEmpty(specs))
            {
                specs = new GetSpecGroupsFromMysqlCommand(spu.getCid3(),specClient).execute();
                cacheService.saveSpecGroups2LocalCache(specs,spu.getCid3());
            }
            cacheService.saveSpecGroups2RedisCache(specs,spu.getCid3());
        }

        model.put("brand", brand);
        model.put("categories", categories);
        model.put("spu", spu);
        model.put("skus", skus);
        model.put("detail", detail);
        model.put("specs", specs);
        String jsonString = JSON.toJSONString(model);
        return jsonString;
    }

    public  void createHtml(Long spuId) {
        Context context = new Context();
        Map<String, Object> map = JSONObject.parseObject(loadModel(spuId));
        context.setVariables(map);

        File file = new File(this.dest, spuId + ".html");
        //如果页面存在，先删除，后进行创建静态页
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter writer = new PrintWriter(file, "utf-8")) {
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            log.error("【静态页服务】生成静态页面异常", e);
        }
    }

    public void deleteHtml(Long id) {
        File file = new File(this.dest + id + ".html");
        if (file.exists()) {
            boolean flag = file.delete();
            if (!flag) {
                log.error("删除静态页面失败");
            }
        }
    }
}
