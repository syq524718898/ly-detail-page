package com.leyou.page.client;

import com.leyou.item.pojo.Brand;
import com.leyou.page.client.fallback.BrandClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author sunyuqi
 */
@FeignClient(value = "item-service",fallback = BrandClientFallback.class)
public interface BrandClient{
    @GetMapping("brand/{id}")
    Brand queryById(@PathVariable("id") Long id);

    @GetMapping("brand/list")
    List<Brand> queryBrandsByIds(@RequestParam("ids") List<Long> ids);
}
