package com.paul.config;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**************
 * 
 * @author pgobin
 *
 *         See configuration options at https://cloud.spring.io/spring-cloud-config/multi/multi_spring-cloud-config.html
 */
@SpringBootApplication
@EnableConfigServer
@Configuration
@EnableAutoConfiguration
public class ServiceApplication {

	private static final Logger log = Logger.getLogger(ServiceApplication.class);

	public static ConfigurableApplicationContext context;

	@Autowired
	void setEnvironment(Environment e)
	{
		log.info("***** INITIALIZING " + this.getClass().getCanonicalName() + " =>" + e.getProperty("spring.application.name"));
	}

	public static void main(String[] args)
	{
		context = SpringApplication.run(ServiceApplication.class, args);
		String appName = context.getEnvironment().getProperty("app-name");
		String springAppName = context.getEnvironment().getProperty("spring.application.name");
		log.info(System.lineSeparator() + System.lineSeparator() + "***** [" + springAppName + "] successfully started ****" + System.lineSeparator() + System.lineSeparator());
	}

}
