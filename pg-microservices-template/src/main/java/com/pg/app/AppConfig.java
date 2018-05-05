package com.pg.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**************************************************
 * A helper class if you would like to read properties via AppConfig.getEnv().getProperty("prop.name", "defaultVal"); You can also use the {@value} annotated mechanism to auto
 * inject a property value.
 * 
 * @value ("${prop.name:defaultValue})
 * @author pgobin
 *
 */
@Component
@Configuration
@RefreshScope
public class AppConfig {

	private static Environment env;

	/*********************
	 * 
	 * @param e
	 */
	@Autowired
	void setEnvironment(Environment e)
	{
		env = e;
	}

	public static Environment getEnv()
	{
		return env;
	}

}
