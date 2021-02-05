package com.leyou.page.command;

import com.leyou.item.pojo.Category;
import com.leyou.page.client.CategoryClient;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import java.util.List;

public class GetCategorysFromMysqlCommand extends HystrixCommand<List<Category> > {

	private List<Long> ids;

	private CategoryClient categoryClient;

	public GetCategorysFromMysqlCommand(List<Long> ids, CategoryClient categoryClient) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CategorysRedisGroup"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(100)
						.withCircuitBreakerRequestVolumeThreshold(1000)
						.withCircuitBreakerErrorThresholdPercentage(70)
						.withCircuitBreakerSleepWindowInMilliseconds(60 * 1000))
				);  
		this.ids = ids;
		this.categoryClient = categoryClient;
	}
	
	@Override
	protected List<Category>  run() throws Exception {
		List<Category> categories = this.categoryClient.queryByIds(this.ids);
		return categories;
	}
	
	@Override
	protected List<Category>  getFallback() {
		return null;
	}

}
