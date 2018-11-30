package org.brightblock.gaia.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application")
public class ApplicationSettings {
	private String bitcoinBase;

	public String getBitcoinBase() {
		return bitcoinBase;
	}

	public void setBitcoinBase(String bitcoinBase) {
		this.bitcoinBase = bitcoinBase;
	}

}
