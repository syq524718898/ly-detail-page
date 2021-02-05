package com.leyou.page.command;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.page.client.SpecClient;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import java.util.List;

public class GetSpecGroupsFromMysqlCommand extends HystrixCommand<List<SpecGroup>> {
	
	private Long cid;

	private SpecClient specClient;
	
	public GetSpecGroupsFromMysqlCommand(Long cid, SpecClient specClient) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SpecGroupsRedisGroup"))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(100)
						.withCircuitBreakerRequestVolumeThreshold(1000)
						.withCircuitBreakerErrorThresholdPercentage(70)
						.withCircuitBreakerSleepWindowInMilliseconds(60 * 1000))
				);  
		this.cid = cid;
		this.specClient = specClient;
	}
	
	@Override
	protected List<SpecGroup> run() throws Exception {
		List<SpecGroup> specGroups = this.specClient.querySpecsByCid(cid);
		return specGroups;
	} 
	
	@Override
	protected List<SpecGroup> getFallback() {
		return null;
	}

}
