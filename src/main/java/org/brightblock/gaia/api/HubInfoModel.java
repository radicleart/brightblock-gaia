package org.brightblock.gaia.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HubInfoModel implements Serializable {
	private static final long serialVersionUID = -1800755507031671701L;
	@JsonProperty("challenge_text")
	private String challengeText;
	@JsonProperty("read_url_prefix")
	private String readUrlPrefix;
	@JsonProperty("latest_auth_version")
	private String latestAuthVersion;

	public HubInfoModel(String challengeText, String readUrlPrefix, String latestAuthVersion) {
		super();
		this.challengeText = challengeText;
		this.readUrlPrefix = readUrlPrefix;
		this.latestAuthVersion = latestAuthVersion;
	}

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

}
