package com.leyou.page.client.fallback;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.CartDto;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.page.client.GoodsClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoodsClientFallback implements GoodsClient {
    @Override
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        return null;
    }

    @Override
    public SpuDetail querySpuDetailById(Long id) {
        return null;
    }

    @Override
    public List<Sku> querySkuBySpuId(Long id) {
        return null;
    }

    @Override
    public List<Sku> querySkusByIds(List<Long> ids) {
        return null;
    }

    @Override
    public Spu querySpuBySpuId(Long spuId) {
        return null;
    }

    @Override
    public void decreaseStock(List<CartDto> cartDTOS) {

    }
}
