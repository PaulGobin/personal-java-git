package com.pg.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;

/****************************************************
 * This the the entry point of this service, this is where the service gets boot strapped and does all registration with our service registry and also uses the service registry to
 * discover to pull configurations etc.
 * 
 * @author pgobin
 *
 */
@SpringBootApplication
@EnableEurekaClient
@RefreshScope
public class ServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(ServiceApplication.class);

	// You can use this global context to create beans etc at runtime.
	// example KafkaProducerConfig kp = context.getBean(KafkaProducerConfig.class);
	public static ConfigurableApplicationContext context;

	public static void main(String[] args)
	{
		log.info("####### STARTING SERVICE ########");
		context = SpringApplication.run(ServiceApplication.class, args);
		log.info("####### SUCCESSFULLY STARTED [" + AppConfig.getEnv().getProperty("spring.application.name") + "] #######");
	}
}
