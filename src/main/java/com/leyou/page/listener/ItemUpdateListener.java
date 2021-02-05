package com.leyou.page.listener;

import com.leyou.item.pojo.*;
import com.leyou.page.client.BrandClient;
import com.leyou.page.client.CategoryClient;
import com.leyou.page.client.GoodsClient;
import com.leyou.page.client.SpecClient;
import com.leyou.page.service.CacheService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
public class ItemUpdateListener {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private CacheService cacheService;


    //    @KafkaListener(id="consumer1",topicPartitions= {@TopicPartition(topic = "offsettest",partitions= {"1","2","3"})})
    @KafkaListener(topics = {"brand_update"})
    public void processBrandUpdate(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        try {
            Long brandId = Long.valueOf(String.valueOf(record.value()));
            Brand brand = brandClient.queryById(brandId);
            cacheService.saveBrand2RedisCache(brand);
            cacheService.saveBrand2LocalCache(brand);
            ack.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"brand_delete"})
    public void processBrandDelete(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        try {
            Long brandId = Long.valueOf(String.valueOf(record.value()));
            cacheService.deleteBrandFromRedisCache(brandId);
            cacheService.deleteBrandFromLocalCache(brandId);
            ack.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = {"spu_update"})
    public void processSpuUpdate(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        try {
            Long spuId = Long.valueOf(String.valueOf(record.value()));
            Spu spu = goodsClient.querySpuBySpuId(spuId);
            cacheService.saveSpu2RedisCache(spu);
            cacheService.saveSpu2LocalCache(spu);
            ack.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"spu_delete"})
    public void processSpuDelete(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        try {
            Long spuId = Long.valueOf(String.valueOf(record.value()));
            cacheService.deleteSpuFromLocalCache(spuId);
            cacheService.deleteSpuFromRedisCache(spuId);
            ack.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = {"specgroups_update"})
    public void processSpecGroupsUpdate(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        try {
            Long spuId = Long.valueOf(String.valueOf(record.value()));
            Spu spu = goodsClient.querySpuBySpuId(spuId);
            List<SpecGroup> specGroups = specClient.querySpecGroupByCid(spu.getCid3());
            cacheService.saveSpecGroups2RedisCache(specGroups,spu.getCid3());
            cacheService.saveSpecGroups2LocalCache(specGroups,spu.getCid3());
            ack.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @KafkaListener(topics = {"specgroups_delete"})
    public void processSpecGroupsDelete(ConsumerRecord<?, ?> record, Acknowledgment ack) {
        try {
            Long spuId = Long.valueOf(String.valueOf(record.value()));
            cacheService.deleteSpecGroupsFromLocalCache(spuId);
            cacheService.deleteSpecGroupsFromRedisCache(spuId);
            ack.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
