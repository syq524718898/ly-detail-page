package com.leyou.page.client.fallback;

import com.leyou.item.pojo.Brand;
import com.leyou.page.client.BrandClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BrandClientFallback implements BrandClient {
    @Override
    public Brand queryById(Long id) {
        return null;
    }

    @Override
    public List<Brand> queryBrandsByIds(List<Long> ids) {
        return null;
    }
}
