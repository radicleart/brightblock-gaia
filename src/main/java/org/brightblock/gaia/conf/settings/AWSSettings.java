package org.brightblock.gaia.conf.settings;

import java.io.Serializable;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "aws")
public class AWSSettings implements Serializable {

	private static final long serialVersionUID = 7788699227709773763L;
	private String configSecret;
	private String serverName;
	private String port;
	private String driver;
	private String bucket;
	private String readUrl;
	private Credentials credentials;
	private Proofs proofs;
	private ArgsTransport argsTransport;

	public AWSSettings() {
		super();
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getReadUrl() {
		return readUrl;
	}

	public void setReadUrl(String readUrl) {
		this.readUrl = readUrl;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public Proofs getProofs() {
		return proofs;
	}

	public void setProofs(Proofs proofs) {
		this.proofs = proofs;
	}

	public ArgsTransport getArgsTransport() {
		return argsTransport;
	}

	public void setArgsTransport(ArgsTransport argsTransport) {
		this.argsTransport = argsTransport;
	}

	public String getConfigSecret() {
		return configSecret;
	}

	public void setConfigSecret(String configSecret) {
		this.configSecret = configSecret;
	}

}
