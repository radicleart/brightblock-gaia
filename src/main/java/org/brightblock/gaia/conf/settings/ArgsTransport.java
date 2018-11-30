package org.brightblock.gaia.conf.settings;

import java.io.Serializable;

public class ArgsTransport implements Serializable {

	private static final long serialVersionUID = -2379538510831915184L;
	private String level;
	private boolean handleExceptions;
	private boolean stringify;
	private boolean timestamp;
	private boolean colorize;
	private boolean json = true;

	public ArgsTransport() {
		super();
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean isHandleExceptions() {
		return handleExceptions;
	}

	public void setHandleExceptions(boolean handleExceptions) {
		this.handleExceptions = handleExceptions;
	}

	public boolean isStringify() {
		return stringify;
	}

	public void setStringify(boolean stringify) {
		this.stringify = stringify;
	}

	public boolean isTimestamp() {
		return timestamp;
	}

	public void setTimestamp(boolean timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isColorize() {
		return colorize;
	}

	public void setColorize(boolean colorize) {
		this.colorize = colorize;
	}

	public boolean isJson() {
		return json;
	}

	public void setJson(boolean json) {
		this.json = json;
	}

}
