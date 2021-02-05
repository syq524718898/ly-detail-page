package com.leyou.page.client.fallback;

import com.leyou.item.pojo.Category;
import com.leyou.page.client.CategoryClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryClientFallback implements CategoryClient {
    @Override
    public List<Category> queryByIds(List<Long> ids) {
        return null;
    }
}
