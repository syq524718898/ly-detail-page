package com.leyou.page.client;

import com.leyou.item.pojo.Category;
import com.leyou.page.client.fallback.CategoryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author sunyuqi
 */
@FeignClient(value = "item-service",fallback = CategoryClientFallback.class)
public interface CategoryClient {
    @GetMapping("category/list/ids")
    List<Category> queryByIds(@RequestParam("ids") List<Long> ids);
}
