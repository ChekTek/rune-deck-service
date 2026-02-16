package com.chektek;

public class PluginSummary {
	private final String id;
	private final String name;
	private final boolean isActive;

	public PluginSummary(String id, String name, boolean isActive) {
		this.id = id;
		this.name = name;
		this.isActive = isActive;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return isActive;
	}
}