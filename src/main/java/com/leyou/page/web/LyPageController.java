package com.leyou.page.web;

import com.alibaba.fastjson.JSONObject;
import com.leyou.page.service.PageHtmlService;
import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author sunyuqi
 */
@Controller
public class LyPageController {

    @Autowired
    private PageService pageService;

    @Autowired
    PageHtmlService pageHtmlService;

    @GetMapping("item/{productId}")
    public String toItemPage(@PathVariable("productId") Long spuId, Model model) {
        Map<String, Object> attributes = JSONObject.parseObject(pageService.loadModel(spuId));
        model.addAllAttributes(attributes);
        // 页面静态化
//        this.pageHtmlService.asyncExcute(attributes);
        return "item";
    }

    @GetMapping("item")
    @ResponseBody
    public String GetProduct(@RequestParam("productId") Long spuId) {
        return pageService.loadModel(spuId);
    }
}
