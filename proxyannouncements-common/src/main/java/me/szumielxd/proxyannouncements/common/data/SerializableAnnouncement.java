package me.szumielxd.proxyannouncements.common.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;

public class SerializableAnnouncement implements Cloneable {
	
	
	private int delay;
	private int period;
	private boolean blacklist;
	private @NotNull List<String> serverList;
	private @NotNull List<String> messages;
	private @NotNull List<String> legacyMessages;
	
	
	public SerializableAnnouncement() {}
	
	public SerializableAnnouncement(int delay, int period, boolean blacklist, @NotNull List<String> serverList, @NotNull List<String> messages, @NotNull List<String> legacyMessages) {
		this.delay = delay;
		this.period = period;
		this.blacklist = blacklist;
		this.serverList = Objects.requireNonNull(serverList, "serverList cannot be null");
		this.messages = Objects.requireNonNull(messages, "messages cannot be null");
		this.legacyMessages = Objects.requireNonNull(legacyMessages, "legacyMessages cannot be null");
	}
	
	public SerializableAnnouncement(@NotNull ConfigurationSection configuration) {
		Objects.requireNonNull(configuration, "configuration cannot be null");
		this.delay = configuration.getInt("delay", 60);
		this.period = configuration.getInt("period", 60);
		this.blacklist = configuration.getBoolean("blacklist", true);
		this.serverList = configuration.getStringList("serverList");
		this.messages = configuration.getStringList("messages");
		this.legacyMessages = !configuration.getStringList("legacyMessages").isEmpty() ? configuration.getStringList("legacyMessages") : new ArrayList<>(this.messages);
	}
	
	@SuppressWarnings("unchecked")
	public SerializableAnnouncement(@NotNull Map<String, Object> configuration) {
		Objects.requireNonNull(configuration, "configuration cannot be null");
		this.delay = (int) configuration.getOrDefault("delay", 60);
		this.period = (int) configuration.getOrDefault("period", 60);
		this.blacklist = (boolean) configuration.getOrDefault("blacklist", true);
		this.serverList = (@NotNull List<String>) configuration.getOrDefault("serverList", Collections.emptyList());
		this.messages = (@NotNull List<String>) configuration.getOrDefault("messages", Collections.emptyList());
		this.legacyMessages = (@NotNull List<String>) configuration.getOrDefault("legacyMessages", new ArrayList<>(this.messages));
	}
	
	
	public int getDelay() {
		return this.delay;
	}
	public int getPeriod() {
		return this.period;
	}
	public boolean isBlacklistMode() {
		return this.blacklist;
	}
	public List<String> getServerList() {
		return this.serverList;
	}
	public List<String> getMessages() {
		return this.messages;
	}
	public List<String> getLegacyMessages() {
		return this.legacyMessages;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public void setLegacyMessages(List<String> legacyMessages) {
		this.legacyMessages = legacyMessages;
	}
	
	public Map<String, Object> toConfiguration() {
		Map<String, Object> cfg = new HashMap<>();
		cfg.put("delay", this.delay);
		cfg.put("messages", this.messages);
		if (!this.messages.equals(this.legacyMessages)) cfg.put("legacyMessages", this.legacyMessages);
		return cfg;
	}
	
	public SerializableAnnouncement clone() {
		return new SerializableAnnouncement(this.delay, this.period, this.blacklist, new ArrayList<>(this.serverList), new ArrayList<>(this.messages), new ArrayList<>(this.legacyMessages));
	}

}
