package org.brightblock.gaia.api;

import java.io.Serializable;

public class StoreResponseModel implements Serializable {

	private static final long serialVersionUID = 8518732405971428502L;
	private String publicUrl;

	public StoreResponseModel(String publicUrl) {
		super();
		this.publicUrl = publicUrl;
	}

	public StoreResponseModel() {
		super();
	}

	public String getPublicUrl() {
		return publicUrl;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}

}
