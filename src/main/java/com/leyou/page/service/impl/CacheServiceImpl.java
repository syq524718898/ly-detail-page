package com.leyou.page.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.Spu;
import com.leyou.page.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.alibaba.fastjson.JSONObject;
import java.util.List;

/**
 * 缓存Service实现类
 */
@Service("cacheService")
public class CacheServiceImpl implements CacheService {
	
	public static final String CACHE_NAME = "local";
	
	@Autowired
	private JedisPool jedisPool;

	@Override
	@CachePut(value = CACHE_NAME, key = "'spu_info_'+#spu.getId()")
	public Spu saveSpu2LocalCache(Spu spu) {
		return spu;
	}


	@Override
	@Cacheable(value = CACHE_NAME, key = "'spu_info_'+#spuId")
	public Spu getSpuFromLocalCache(Long spuId) {
		return null;
	}

	@Override
	@CacheEvict(value = CACHE_NAME, key =  "'spu_info_'+#spuId")
	public Boolean deleteSpuFromLocalCache(Long spuId) {
		return true;
	}

	@Override
	@CachePut(value = CACHE_NAME, key = "'band_info_'+#band.getId()")
	public Brand saveBrand2LocalCache(Brand band) {
		return band;
	}

	@Override
    @Cacheable(value = CACHE_NAME, key = "'band_info_'+#brandId")
	public Brand getBrandFromLocalCache(Long brandId) {
		return null;
	}

	@Override
	@CacheEvict(value = CACHE_NAME, key = "'band_info_'+#brandId")
	public Boolean deleteBrandFromLocalCache(Long brandId) {
		return true;
	}

	@Override
	@CachePut(value = CACHE_NAME, key = "'categorie_info_'+#categorieIds")
	public List<Category> saveCategorys2LocalCache(List<Category> categories,String categorieIds) {
		return categories;
	}

	@Override
    @Cacheable(value = CACHE_NAME, key = "'categorie_info_'+#categorieIds")
	public List<Category> getCategorysFromLocalCache(String categorieIds) {
		return null;
	}

	@Override
	@CacheEvict(value = CACHE_NAME, key = "'categorie_info_'+#categorieIds")
	public Boolean deleteCategorysFromLocalCache(String categorieIds) {
		return true;
	}

	@Override
	@CachePut(value = CACHE_NAME, key = "'specGroups_info_'+#cid")
	public List<SpecGroup> saveSpecGroups2LocalCache(List<SpecGroup> specGroups,Long cid) {
		return specGroups;
	}

	@Override
    @Cacheable(value = CACHE_NAME, key = "'specGroups_info_'+#cid")
	public List<SpecGroup> getSpecGroupsFromLocalCache(Long cid) {
		return null;
	}

	@Override
	@CacheEvict(value = CACHE_NAME, key = "'specGroups_info_'+#cid")
	public Boolean deleteSpecGroupsFromLocalCache(Long cid) {
		return true;
	}

	@Override
	public void saveSpu2RedisCache(Spu spu) {
		Jedis jedis = jedisPool.getResource();
		jedis.set("spu_info_"+spu.getId(),JSONObject.toJSONString(spu));
		jedis.close();
	}

	@Override
	public Spu getSpuFromRedisCache(Long spuId) {
		Jedis jedis = jedisPool.getResource();
		Spu spu = JSONObject.parseObject(jedis.get("spu_info_" + spuId), Spu.class);
		jedis.close();
		return spu;
	}

	@Override
	public Boolean deleteSpuFromRedisCache(Long spuId) {
		Jedis jedis = jedisPool.getResource();
		jedis.del("spu_info_"+spuId);
		jedis.close();
		return true;
	}

	@Override
	public void saveBrand2RedisCache(Brand brand) {
		Jedis jedis = jedisPool.getResource();
		jedis.set("brand_info_"+brand.getId(),JSONObject.toJSONString(brand));
		jedis.close();
	}

	@Override
	public Brand getBrandFromRedisCache(Long brandId) {
		Jedis jedis = jedisPool.getResource();
		Brand brand = JSONObject.parseObject(jedis.get("brand_info_" + brandId), Brand.class);
		jedis.close();
		return brand;
	}

	@Override
	public Boolean deleteBrandFromRedisCache(Long brandId) {
		Jedis jedis = jedisPool.getResource();
		jedis.del("brand_info_"+brandId);
		jedis.close();
		return true;
	}

	@Override
	public void saveCategorys2RedisCache(List<Category> categories) {
	    String key = "categorie_info_";
	    if (categories!=null || categories.size()!=0)
        {
            int i;
            for ( i = 0;i < categories.size()-1;i++) {
                key += categories.get(i).getId()+",";
            }
            key+=categories.get(i).getId();
			Jedis jedis = jedisPool.getResource();
			jedis.set(key, JSON.toJSONString(categories));
			jedis.close();
        }
	}

	@Override
	public List<Category> getCategorysFromRedisCache(Long... categorieIds) {
	    if(categorieIds.length!=0 || categorieIds!=null)
        {
            String key = "categorie_info_";
            int i;
            for (i=0;i<categorieIds.length-1;i++)
            {
                key += categorieIds[i]+",";
            }
            key += categorieIds[i];
			Jedis jedis = jedisPool.getResource();
			String categorieJSON = jedis.get(key);
			jedis.close();
            if (categorieJSON==null || "".equals(categorieJSON))
            {
                return null;
            }
            return JSONArray.parseArray(categorieJSON,Category.class);
        }
        return null;
	}

	@Override
	public Boolean deleteCategorysFromRedisCache(Long... categorieIds) {
		if(categorieIds.length!=0 || categorieIds!=null)
		{
			String key = "categorie_info_";
			int i;
			for (i=0;i<categorieIds.length-1;i++)
			{
				key += categorieIds[i]+",";
			}
			key += categorieIds[i];
			Jedis jedis = jedisPool.getResource();
			jedis.del(key);
			jedis.close();
		}
		return true;
	}

	@Override
	public void saveSpecGroups2RedisCache(List<SpecGroup> specGroups,Long cid) {
		Jedis jedis = jedisPool.getResource();
		jedis.set("specGroups_info_"+cid, JSON.toJSONString(specGroups));
		jedis.close();
	}

	@Override
	public List<SpecGroup> getSpecGroupsFromRedisCache(Long cid) {
		Jedis jedis = jedisPool.getResource();
		String specGroupsJSON = jedis.get("specGroups_info_" + cid);
		jedis.close();
        if (specGroupsJSON==null || "".equals(specGroupsJSON))
        {
            return null;
        }
        return JSONArray.parseArray(specGroupsJSON,SpecGroup.class);
	}

	@Override
	public Boolean deleteSpecGroupsFromRedisCache(Long cid) {
		Jedis jedis = jedisPool.getResource();
		jedis.del("specGroups_info_" + cid);
		jedis.close();
		return true;
	}

	@Override
    public void saveTest(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		jedis.set(key,value);
		jedis.close();
    }

    @Override
    public String getTest(String key) {
		Jedis jedis = jedisPool.getResource();
		String s = jedis.get(key);
		jedis.close();
		return s;
	}
}
