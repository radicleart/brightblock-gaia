package org.brightblock.gaia.conf.settings;

import java.io.Serializable;

public class Credentials implements Serializable {

	private static final long serialVersionUID = -5145221139104920770L;
	private String type;
	private String accessKeyId;
	private String secretAccessKey;

	public Credentials() {
		super();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}

}
