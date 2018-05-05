/**
 * 
 */
package com.pg.app;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configure a Feing/Ribbon load balances and auto-discovery of services. When communicating with other services internal (inter-communication) Avoid using the APIGateway because
 * the APIGateway main purpose is to sit at the edge and expose endpoints to external clients, no DMZ.
 * 
 * Both Internal and APIGateway does autodiscovery and loadbalance client side
 * 
 * If you cannot use this loadbalanced/auto-discover mechanism because the rest api you are calling is not part of a registry, then create a new rest templlate and use that on
 * 
 * RestTemplate restTemplate = new RestTemplate();
 * 
 * @author pgobin
 *
 */
@Configuration
public class FeingLoadBalanceRestTemplateBean {

	/****************************************************
	 * Create a LoadBalanced restTemplate and add it to the app context, now whenever you are using a RestTemplate to call a service endpoint, it will automatically used this load
	 * balanced template, along with service discovery of the service
	 * 
	 * @return
	 */

	@LoadBalanced
	@Bean
	RestTemplate restTemplate()
	{
		return new RestTemplate();
	}

}
