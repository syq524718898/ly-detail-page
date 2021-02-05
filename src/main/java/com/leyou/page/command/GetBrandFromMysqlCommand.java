package com.leyou.page.command;

import com.leyou.item.pojo.Brand;
import com.leyou.page.client.BrandClient;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class GetBrandFromMysqlCommand extends HystrixCommand<Brand> {

	private Long brandId;

	private BrandClient brandClient;
	
	public GetBrandFromMysqlCommand(Long brandId, BrandClient brandClient) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("BrandRedisGroup"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(100)
						.withCircuitBreakerRequestVolumeThreshold(1000)
						.withCircuitBreakerErrorThresholdPercentage(70)
						.withCircuitBreakerSleepWindowInMilliseconds(60 * 1000))
				);  
		this.brandId = brandId;
		this.brandClient = brandClient;
	}
	
	@Override
	protected Brand run() throws Exception {
		Brand brand = this.brandClient.queryById(this.brandId);
		return brand;
	}
	
	@Override
	protected Brand getFallback() {
		return null;
	}

}
