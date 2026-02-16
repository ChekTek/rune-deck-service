package com.chektek;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;

public class PluginControlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PluginControlService.class);

	private final PluginManager pluginManager;

	@Inject
	public PluginControlService(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	public List<RuneDeckSocketServer.PluginSummary> getPluginSummaries() {
		return pluginManager.getPlugins().stream()
				.map(plugin -> new RuneDeckSocketServer.PluginSummary(
						getPluginId(plugin),
						getPluginName(plugin),
						pluginManager.isPluginActive(plugin)))
				.sorted(Comparator.comparing(RuneDeckSocketServer.PluginSummary::getName,
						String.CASE_INSENSITIVE_ORDER))
				.collect(Collectors.toList());
	}

	public RuneDeckSocketServer.PluginSummary getPluginSummary(Plugin plugin, boolean isActive) {
		if (plugin == null) {
			return null;
		}

		return new RuneDeckSocketServer.PluginSummary(
				getPluginId(plugin),
				getPluginName(plugin),
				isActive);
	}

	public void togglePlugin(String pluginId, Boolean isActive) {
		if (isActive == null) {
			LOGGER.warn("togglePlugin request missing isActive for plugin: " + pluginId);
			return;
		}

		Plugin plugin = pluginManager.getPlugins().stream()
				.filter(candidate -> getPluginId(candidate).equals(pluginId))
				.findFirst()
				.orElse(null);

		if (plugin == null) {
			LOGGER.warn("Could not find plugin with id: " + pluginId);
			return;
		}

		try {
			if (isActive) {
				pluginManager.setPluginEnabled(plugin, true);
				pluginManager.startPlugin(plugin);
			} else {
				pluginManager.stopPlugin(plugin);
				pluginManager.setPluginEnabled(plugin, false);
			}
		} catch (Exception e) {
			LOGGER.warn("Failed to toggle plugin {}: {}", pluginId, e.getMessage());
		}
	}

	private String getPluginId(Plugin plugin) {
		String canonicalName = plugin.getClass().getCanonicalName();
		if (canonicalName != null) {
			return canonicalName;
		}
		return plugin.getClass().getName();
	}

	private String getPluginName(Plugin plugin) {
		PluginDescriptor descriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
		if (descriptor != null && descriptor.name() != null && !descriptor.name().isEmpty()) {
			return descriptor.name();
		}
		return plugin.getClass().getSimpleName();
	}
}