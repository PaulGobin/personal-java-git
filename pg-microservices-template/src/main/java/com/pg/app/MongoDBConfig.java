package com.pg.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

/***********************************************************
 * Configure a bean used to access MongoDB,all configurations are pulled from the Config service via the Serice Registry by simply including the
 * 
 * common-mongodb profile in bootstrap.yml file
 * 
 * @author pgobin
 *
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.pg.app.repository.mongo")
public class MongoDBConfig extends AbstractMongoConfiguration {

	private static final Logger log = Logger.getLogger(MongoDBConfig.class);

	@Value("${mongo.servers}")
	String mongoServerDef;

	@Value("${mongo.username}")
	String userName;

	@Value("${mongo.password}")
	String password;

	@Value("${mongo.db.name}")
	String mongoDBName;

	@Value("${mongo.socketConnectionTimeout:3600000}")
	int socketConnectionTimeout;

	@Value("${mongo.socketTimeout:3600000}")
	int socketTimeout;

	@Value("${mongo.connectionsPerhost:50}")
	int connectionsPerHost;

	@Value("${mongo.threadsAllowedToBlockForConnectionMultiplier:10}")
	int threadsAllowedToBlockForConnectionMultiplier;

	@Value("${spring.application.name}")
	String applicationName;

	@Override
	protected String getDatabaseName()
	{
		return mongoDBName;
	}

	@Override

	public Mongo mongo() throws Exception
	{

		if (StringUtils.isEmpty(mongoServerDef))
		{
			log.fatal("$$$$$$ NO MONGO SERVERS DEFINED !!! $$$$$$");
			throw new Exception("NO MONGO SERVERS DEFINED");
		}
		List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
		String[] mongoServers = StringUtils.split(mongoServerDef, ",");
		for (String mongoServer : mongoServers)
		{
			serverAddresses.add(new ServerAddress(mongoServer));
		}
		// @see
		// http://mongodb.github.io/mongo-java-driver/3.0/driver/reference/connecting/authenticating/

		// MongoClientURI uri = new
		// MongoClientURI("mongodb://user1:pwd1@host1/?authSource=db1");
		// MongoCredential mc = MongoCredential.createCredential("Bvnchat",
		// getDatabaseName(), "bvnchat".toCharArray());
		MongoCredential mc = MongoCredential.createMongoCRCredential(userName, getDatabaseName(), password.toCharArray());
		List<MongoCredential> lmc = new ArrayList<MongoCredential>();
		lmc.add(mc);

		// http://stackoverflow.com/questions/6520439/how-to-configure-mongodb-java-driver-mongooptions-for-production-use
		WriteConcern wc = WriteConcern.ACKNOWLEDGED;
		MongoClientOptions.Builder options = MongoClientOptions.builder();
		options.socketKeepAlive(true).applicationName(applicationName).connectionsPerHost(connectionsPerHost).connectTimeout(socketConnectionTimeout).socketTimeout(socketTimeout)
			.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier).writeConcern(wc);
		MongoClient mongoClient = new MongoClient(serverAddresses, options.build());

		return mongoClient;
	}

	@Override
	protected String getMappingBasePackage()
	{
		return "com.bvn.app.repository.mongo";
	}

	// @Override
	public MongoClient mongoClient()
	{
		if (StringUtils.isEmpty(mongoServerDef))
		{
			log.fatal("$$$$$$ NO MONGO SERVERS DEFINED !!! $$$$$$");
			return null;
		}
		List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
		String[] mongoServers = StringUtils.split(mongoServerDef, ",");
		for (String mongoServer : mongoServers)
		{
			serverAddresses.add(new ServerAddress(mongoServer));
		}
		MongoCredential mc = MongoCredential.createMongoCRCredential(userName, getDatabaseName(), password.toCharArray());
		List<MongoCredential> lmc = new ArrayList<MongoCredential>();
		lmc.add(mc);

		// http://stackoverflow.com/questions/6520439/how-to-configure-mongodb-java-driver-mongooptions-for-production-use
		WriteConcern wc = WriteConcern.ACKNOWLEDGED;
		MongoClientOptions.Builder options = MongoClientOptions.builder();

		options.applicationName(applicationName).connectionsPerHost(connectionsPerHost).connectTimeout(socketConnectionTimeout).socketTimeout(socketTimeout)
			.threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier).writeConcern(wc);
		MongoClient mongoClient = new MongoClient(serverAddresses, options.build());

		return mongoClient;
	}

}