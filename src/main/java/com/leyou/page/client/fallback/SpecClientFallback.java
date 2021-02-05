package com.leyou.page.client.fallback;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.page.client.SpecClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecClientFallback implements SpecClient {
    @Override
    public List<SpecGroup> querySpecsByCid(Long cid) {
        return null;
    }

    @Override
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        return null;
    }

    @Override
    public List<SpecGroup> querySpecGroupByCid(Long cid) {
        return null;
    }
}
