/**
 * 
 */
package com.bvn.paul.server.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.JdbcEnvironmentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author pgobin IF YOU NEED TO CREATE YOUR OWN EnvironmentRepository, follow this example.
 * 
 *         REMEMBER TO COMMENT OUT THE @Component and @ConfigurationProperties below
 * 
 *         Lets use this implementation vs the one that comes with SpringBoot JdbcEnvironmentRepository because the JdbcEnvironmentRepository does not handle the default label
 *         defined in the application.properties if the client does not provide one, it hard code it to master.
 * 
 *         Also see the application.yaml for bvnjdbc definition
 */
@Component
@ConfigurationProperties("spring.cloud.config.server.bvnjdbc")
public class PaulJdbcEnvironmentRepository extends JdbcEnvironmentRepository /* implements EnvironmentRepository, Ordered */ {

	private static final String DEFAULT_SQL = "SELECT KY, VALUE from PROPERTIES where APPLICATION=? and PROFILE=? and LABEL=?";

	@Value("${spring.cloud.config.server.default-label:master}")
	private String DEFAULT_LABEL;

	@Value("${spring.cloud.config.server.bvnjdbc.sql}")
	private String DEFINED_SQL;

	public PaulJdbcEnvironmentRepository(JdbcTemplate jdbc)
	{
		super(jdbc);
		setSQL(DEFINED_SQL);
	}

	/************************************
	 * 
	 * @param sql
	 */
	private void setSQL(String sql)
	{
		if (StringUtils.isEmpty(DEFINED_SQL))
		{
			DEFINED_SQL = DEFAULT_SQL;
		}
		super.setSql(DEFINED_SQL);
	}

	@Override
	public Environment findOne(String application, String profile, String label)
	{
		if (StringUtils.isEmpty(label))
		{
			label = DEFAULT_LABEL;
		}
		this.setSQL(DEFINED_SQL);
		//System.out.println("LABEL IS [" + label + "]" );
		return super.findOne(application, profile, label);
	}
}
