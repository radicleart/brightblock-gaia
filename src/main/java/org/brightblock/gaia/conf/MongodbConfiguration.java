package org.brightblock.gaia.conf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;

@Configuration
public class MongodbConfiguration extends AbstractMongoConfiguration {

	@Autowired private ApplicationSettings applicationSettings;
	private static final Logger logger = LogManager.getLogger(MongodbConfiguration.class);

	@Override
	protected String getDatabaseName() {
		return "radstore";
	}

	@Override
	protected String getMappingBasePackage() {
		return "org.brightblock";
	}

	@Override
	public MongoClient mongoClient() {
		logger.info("MONGODB: Attempting to connect on IP: " + applicationSettings.getMongoIp());
		return new MongoClient(applicationSettings.getMongoIp(), 27017);
	}
}
