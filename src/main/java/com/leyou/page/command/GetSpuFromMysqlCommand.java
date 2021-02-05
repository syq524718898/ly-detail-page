package com.leyou.page.command;


import com.leyou.item.pojo.Spu;
import com.leyou.page.client.GoodsClient;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;


public class GetSpuFromMysqlCommand extends HystrixCommand<Spu> {

	private Long spuId;

	private GoodsClient goodsClient;
	
	public GetSpuFromMysqlCommand(Long spuId, GoodsClient goodsClient) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SpuRedisGroup"))
				.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
						.withCoreSize(10)
						.withMaximumSize(30) 
						.withAllowMaximumSizeToDivergeFromCoreSize(true) 
						.withKeepAliveTimeMinutes(1) 
						.withMaxQueueSize(50)
						.withQueueSizeRejectionThreshold(100)) 
				); 
		this.spuId = spuId;
		this.goodsClient = goodsClient;
	}
	
	@Override
	protected Spu run() throws Exception {

		Spu spu = this.goodsClient.querySpuBySpuId(this.spuId);
		return spu;
	} 
	
	@Override
	protected Spu getFallback() {
		return null;
	}
}
