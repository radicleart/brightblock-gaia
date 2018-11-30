package org.brightblock.gaia.conf.settings;

import java.io.Serializable;

public class Proofs implements Serializable {

	private static final long serialVersionUID = 7746775550785675069L;
	private int proofsRequired;

	public Proofs() {
		super();
	}

	public int getProofsRequired() {
		return proofsRequired;
	}

	public void setProofsRequired(int proofsRequired) {
		this.proofsRequired = proofsRequired;
	}

}
