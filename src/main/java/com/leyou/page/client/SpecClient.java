package com.leyou.page.client;

import com.leyou.item.api.SpecApi;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.page.client.fallback.SpecClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author sunyuqi
 */
@FeignClient(value = "item-service",fallback = SpecClientFallback.class)
public interface SpecClient {
    // 查询规格参数组，及组内参数
    @GetMapping("spec/{cid}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);

    @GetMapping("spec/params")
    List<SpecParam> querySpecParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching,
            @RequestParam(value = "generic", required = false) Boolean generic
    );

    @GetMapping("spec/groups/{cid}")
    List<SpecGroup> querySpecGroupByCid(@PathVariable("cid") Long cid);
}
