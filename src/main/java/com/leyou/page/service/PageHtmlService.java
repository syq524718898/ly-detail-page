package com.leyou.page.service;

import com.leyou.item.pojo.Spu;
import com.leyou.page.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class PageHtmlService {

    @Autowired
    private TemplateEngine templateEngine;
    @Value("${ly.page.path}")
    String path;

    private static final Logger LOGGER = LoggerFactory.getLogger(PageHtmlService.class);

    /**
     * 创建html页面
     *
     * @param attributes
     * @throws Exception
     */
    public void createHtml(Map<String, Object> attributes) {

        PrintWriter writer = null;
        Spu spu = (Spu)attributes.get("spu");
        Long spuId = spu.getId();
        try {

            // 创建thymeleaf上下文对象
            Context context = new Context();
            // 把数据放入上下文对象
            context.setVariables(attributes);

            // 创建输出流
            File file = new File(path + spuId + ".html");
            writer = new PrintWriter(file);

            // 执行页面静态化方法
            templateEngine.process("item", context, writer);
        } catch (Exception e) {
            LOGGER.error("页面静态化出错：{}，"+ e, spuId);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 新建线程处理页面静态化
     * @param attributes
     */
    public void asyncExcute(Map<String, Object> attributes) {
        ThreadUtils.execute(()->createHtml(attributes));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }
}