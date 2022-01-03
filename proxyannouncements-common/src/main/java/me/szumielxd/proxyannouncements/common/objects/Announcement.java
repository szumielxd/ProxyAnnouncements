package me.szumielxd.proxyannouncements.common.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import me.szumielxd.proxyannouncements.common.ProxyAnnouncements;
import me.szumielxd.proxyannouncements.common.data.SerializableAnnouncement;
import me.szumielxd.proxyannouncements.common.utils.MiscUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Announcement implements Runnable {

	
	private final @NotNull ProxyAnnouncements plugin;
	private final @NotNull String id;
	private final @NotNull List<Component> messages = new ArrayList<>();
	private final @NotNull List<Component> legacyMessages = new ArrayList<>();
	private final @NotNull List<String> serverList;
	private final boolean blacklist;
	private int counter = -1;
	
	
	public Announcement(@NotNull ProxyAnnouncements plugin, @NotNull String id, @NotNull SerializableAnnouncement announcement) {
		this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
		this.id = Objects.requireNonNull(id, "id cannot be null");
		this.blacklist = Objects.requireNonNull(announcement, "announcement cannot be null").isBlacklistMode();
		this.serverList = announcement.getServerList().stream().map(String::toLowerCase).collect(Collectors.toList());
		
		LegacyComponentSerializer altSerializer = LegacyComponentSerializer.legacySection().toBuilder().extractUrls().hexColors().build();
		LegacyComponentSerializer altLegacySerializer = LegacyComponentSerializer.legacySection().toBuilder().extractUrls().build();
		int i = 0;
		for (String msg : announcement.getMessages()) {
			i++;
			try {
				// JSON
				this.messages.add(GsonComponentSerializer.gson().deserializeFromTree(new Gson().fromJson(msg, JsonObject.class)));
			} catch (Exception e) {
				Component comp = MiniMessage.get().deserialize(msg);
				String str = altSerializer.serialize(comp);
				// MiniMessage
				if (!str.equalsIgnoreCase(msg.replace("\\n", "\n"))) this.messages.add(comp);
				// Legacy
				else this.messages.add(altSerializer.deserializeOr(MiscUtil.translateAlternateColorCodes('&', msg).replace("\\n", "\n"), Component.text("INVALID("+i+")").color(NamedTextColor.RED)));
			}
		}
		i = 0;
		for (String msg : announcement.getLegacyMessages()) {
			i++;
			try {
				// JSON
				this.legacyMessages.add(GsonComponentSerializer.colorDownsamplingGson().deserializeFromTree(new Gson().fromJson(msg, JsonObject.class)));
			} catch (Exception e) {
				Component comp = this.downsampleComponent(MiniMessage.get().deserialize(msg));
				String str = altSerializer.serialize(comp);
				// MiniMessage
				if (!str.equalsIgnoreCase(msg.replace("\\n", "\n"))) this.legacyMessages.add(comp);
				// Legacy
				else this.legacyMessages.add(altLegacySerializer.deserializeOr(MiscUtil.translateAlternateColorCodes('&', msg).replace("\\n", "\n"), Component.text("INVALID("+i+")").color(NamedTextColor.RED)));
			}
		}
	}
	
	
	@Override
	public void run() {
		Collection<CommonPlayer> players = this.plugin.getProxyServer().getPlayers();
		this.counter++;
		if (this.counter >= this.messages.size() || this.counter >= this.legacyMessages.size()) this.counter = 0;
		if (players.isEmpty() || this.messages.isEmpty() || this.legacyMessages.isEmpty()) return;
		for (CommonPlayer player : players) {
			if (player.getWorldName() == null) return;
			if (this.blacklist != this.serverList.contains(player.getWorldName().toLowerCase())) {
				if (player.hasPermission("bungeeannouncements.announce."+this.id)) {
					TextReplacementConfig pcfg = TextReplacementConfig.builder().matchLiteral("{player}").replacement(player.getName()).build();
					TextReplacementConfig scfg = TextReplacementConfig.builder().matchLiteral("{server}").replacement(player.getWorldName()).build();
					if (player.getVersion() > 734) {
						player.sendMessage(this.messages.get(this.counter).replaceText(pcfg).replaceText(scfg));
					} else {
						player.sendMessage(this.legacyMessages.get(this.counter).replaceText(pcfg).replaceText(scfg));
					}
				}
			}
		}
	}
	
	
	public @NotNull String getId() {
		return this.id;
	}
	public boolean isBlacklisted() {
		return this.blacklist;
	}
	public @NotNull List<String> getServerList() {
		return Collections.unmodifiableList(this.serverList);
	}
	public @NotNull Component[] getNext() {
		int c = this.counter + 1;
		if (c >= messages.size() || c >= this.legacyMessages.size()) c = 0;
		return new Component[] { this.messages.get(c), this.legacyMessages.get(c) };
	}
	public int getCounter() {
		return this.counter;
	}
	public @NotNull Component[] getMessage(int index) {
		int c = index;
		if (c >= messages.size() || c >= this.legacyMessages.size()) c = 0;
		return new Component[] { this.messages.get(c), this.legacyMessages.get(c) };
	}
	
	
	private @NotNull Component downsampleComponent(@NotNull Component comp) {
		
		Objects.requireNonNull(comp, "component cannot be null");
		
		// self
		if (comp.color() != null && !(comp.color() instanceof NamedTextColor)) {
			comp = comp.color(NamedTextColor.nearestTo(comp.color()));
		}
		
		// children
		if (!comp.children().isEmpty()) {
			comp = comp.children(comp.children().stream().map(this::downsampleComponent).collect(Collectors.toList()));
		}
		
		// hover
		if (comp.hoverEvent() != null && comp.hoverEvent().value() instanceof Component) {
			comp = comp.hoverEvent(this.downsampleComponent((Component) comp.hoverEvent().value()));
		}
		return comp;
	}
	

}
