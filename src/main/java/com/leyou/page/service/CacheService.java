package com.leyou.page.service;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.Spu;


import java.util.List;


/**
 * 缓存service接口
 */

public interface CacheService {

	
	/**
	 * 将商品信息保存到本地的ehcache缓存中
	 * @param spu
	 */
	Spu saveSpu2LocalCache(Spu spu);
	
	/**
	 * 从本地ehcache缓存中获取商品信息
	 * @param spuId
	 * @return
	 */
	Spu getSpuFromLocalCache(Long spuId);

    /**
     * 从本地ehcache缓存中删除商品信息
     * @param spuId
     * @return
     */
    Boolean deleteSpuFromLocalCache(Long spuId);
	
	/**
	 * 将品牌信息保存到本地的ehcache缓存中
	 * @param band
	 */
	Brand saveBrand2LocalCache(Brand band);
	
	/**
	 * 从本地ehcache缓存中获取品牌信息
	 * @param brandId
	 * @return
	 */
	Brand getBrandFromLocalCache(Long brandId);

    /**
     * 从本地ehcache缓存中删除品牌信息
     * @param brandId
     * @return
     */
    Boolean deleteBrandFromLocalCache(Long brandId);

    /**
     * 将分类信息保存到本地的ehcache缓存中
     * @param categories
     * @return
     */
	List<Category> saveCategorys2LocalCache(List<Category> categories,String categorieIds);

    /**
     * 从本地ehcache缓存中获取分类信息
     * @param categorieIds
     * @return
     */
    List<Category> getCategorysFromLocalCache(String categorieIds);

    /**
     * 从本地ehcache缓存中删除分类信息
     * @param categorieIds
     * @return
     */
    Boolean deleteCategorysFromLocalCache(String categorieIds);

    /**
     * 将规格信息保存到本地的ehcache缓存中
     * @param specGroups
     * @return
     */
    List<SpecGroup> saveSpecGroups2LocalCache(List<SpecGroup> specGroups,Long cid);
    /**
     * 从本地ehcache缓存中获取规格信息
     * @param cid
     * @return
     */
    List<SpecGroup> getSpecGroupsFromLocalCache(Long cid);

    /**
     * 从本地ehcache缓存中删除规格信息
     * @param cid
     * @return
     */
    Boolean deleteSpecGroupsFromLocalCache(Long cid);

    /**
     * 将商品信息保存到Redis缓存中
     * @param spu
     */
    void saveSpu2RedisCache(Spu spu);

    /**
     * 从Redis缓存中获取商品信息
     * @param spuId
     * @return
     */
    Spu getSpuFromRedisCache(Long spuId);

    /**
     * 从Redis缓存中删除商品信息
     * @param spuId
     * @return
     */
    Boolean deleteSpuFromRedisCache(Long spuId);

    /**
     * 将品牌信息保存到Redis缓存中
     * @param band
     */
    void saveBrand2RedisCache(Brand band);

    /**
     * 从Redis缓存中获取品牌信息
     * @param brandId
     * @return
     */
    Brand getBrandFromRedisCache(Long brandId);

    /**
     * 从Redis缓存中删除品牌信息
     * @param brandId
     * @return
     */
    Boolean deleteBrandFromRedisCache(Long brandId);

    /**
     * 将分类信息保存到Redis缓存中
     * @param categories
     * @return
     */
    void saveCategorys2RedisCache(List<Category> categories);

    /**
     * 从Redis缓存中获取分类信息
     * @param categorieIds
     * @return
     */
    List<Category> getCategorysFromRedisCache(Long... categorieIds);


    /**
     * 从Redis缓存中删除分类信息
     * @param categorieIds
     * @return
     */
    Boolean deleteCategorysFromRedisCache(Long... categorieIds);

    /**
     * 将规格信息保存到Redis缓存中
     * @param specGroups
     * @return
     */
    void saveSpecGroups2RedisCache(List<SpecGroup> specGroups,Long cid);
    /**
     * 从Redis缓存中获取规格信息
     * @param cid
     * @return
     */
    List<SpecGroup> getSpecGroupsFromRedisCache(Long cid);

    /**
     * 从Redis缓存中删除规格信息
     * @param cid
     * @return
     */
    Boolean deleteSpecGroupsFromRedisCache(Long cid);

    void saveTest(String key,String value);

    String getTest(String key);
}
