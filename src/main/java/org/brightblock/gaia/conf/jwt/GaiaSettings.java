package org.brightblock.gaia.conf.jwt;

import java.util.ArrayList;
import java.util.List;

import org.brightblock.gaia.conf.settings.AWSSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "gaia")
public class GaiaSettings {
	private String challengeText;
	private String readUrlPrefix;
	private String readUrlShort;
	private String latestAuthVersion;
	private String storageRootDirectory;
	private List<AWSSettings> configs;

	public String getChallengeText() {
		return challengeText;
	}

	public void setChallengeText(String challengeText) {
		this.challengeText = challengeText;
	}

	public String getReadUrlPrefix() {
		return readUrlPrefix;
	}

	public void setReadUrlPrefix(String readUrlPrefix) {
		this.readUrlPrefix = readUrlPrefix;
	}

	public String getLatestAuthVersion() {
		return latestAuthVersion;
	}

	public void setLatestAuthVersion(String latestAuthVersion) {
		this.latestAuthVersion = latestAuthVersion;
	}

	public String getStorageRootDirectory() {
		return storageRootDirectory;
	}

	public void setStorageRootDirectory(String storageRootDirectory) {
		this.storageRootDirectory = storageRootDirectory;
	}

	public List<AWSSettings> getConfigs() {
		return configs;
	}

	public void setConfigs(List<AWSSettings> configs) {
		this.configs = configs;
	}

	public AWSSettings getConfig(String driver) {
		AWSSettings configModel = null;
		for (AWSSettings config : this.configs) {
			if (config.getDriver().equals(driver)) {
				configModel = config;
			}
		}
		return configModel;
	}

	public void addConfig(AWSSettings configModel) {
		if (this.configs == null) {
			this.configs = new ArrayList<>();
		}
		boolean update = false;
		for (AWSSettings config : this.configs) {
			if (config.getDriver().equals(configModel.getDriver())) {
				update = true;
				config = configModel;
			}
		}
		if (!update) {
			this.configs.add(configModel);
		}
	}

	public String getReadUrlShort() {
		return readUrlShort;
	}

	public void setReadUrlShort(String readUrlShort) {
		this.readUrlShort = readUrlShort;
	}

}
