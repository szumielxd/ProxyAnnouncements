package me.szumielxd.proxyannouncements.bungee;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;

import me.szumielxd.proxyannouncements.bungee.commands.BungeeCommandWrapper;
import me.szumielxd.proxyannouncements.bungee.objects.BungeeProxy;
import me.szumielxd.proxyannouncements.common.ProxyAnnouncements;
import me.szumielxd.proxyannouncements.common.ProxyAnnouncementsProvider;
import me.szumielxd.proxyannouncements.common.commands.CommonCommand;
import me.szumielxd.proxyannouncements.common.commands.MainCommand;
import me.szumielxd.proxyannouncements.common.configuration.Config;
import me.szumielxd.proxyannouncements.common.data.SerializableAnnouncement;
import me.szumielxd.proxyannouncements.common.objects.Announcement;
import me.szumielxd.proxyannouncements.common.objects.CommonProxy;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyAnnouncementsBungee extends Plugin implements ProxyAnnouncements {
	
	
	private BungeeAudiences adventure = null;
	private @NotNull BungeeProxy proxy;
	
	
	public BungeeAudiences adventure() {
		if (this.adventure == null) throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
		return this.adventure;
	}
	
	
	@Override
	public void onEnable() {
		ProxyAnnouncementsProvider.init(this);
		this.proxy = new BungeeProxy(this);
		this.adventure = BungeeAudiences.create(this);
		Config.load(new File(this.getDataFolder(), "config.yml"), this);
		this.registerCommand(new MainCommand(this));
		this.getLogger().info("Loading announcements...");
		int i = 0;
		if (!Config.ANNOUNCEMENTS.getValueMap().isEmpty()) for(Entry<String, ?> entry : Config.ANNOUNCEMENTS.getValueMap().entrySet()) {
			SerializableAnnouncement ann = new SerializableAnnouncement((ConfigurationSection) entry.getValue());
			if (ann.getMessages().isEmpty()) {
				this.getLogger().info(String.format("Announcement with id `%s` is empty. Skipping...", entry.getKey()));
				continue;
			}
			this.getProxyServer().getScheduler().runTaskTimer(new Announcement(this, entry.getKey(), ann), ann.getDelay(), ann.getPeriod(), TimeUnit.SECONDS);
			i++;
		}
		this.getLogger().info(String.format("Successfully loaded %d announcements!", i));
	}
	
	
	private void registerCommand(@NotNull CommonCommand command) {
		this.getProxy().getPluginManager().registerCommand(this, new BungeeCommandWrapper(this, command));
	}
	
	
	@Override
	public void onDisable() {
		this.getLogger().info("Disabling all announcements...");
		this.getProxy().getScheduler().cancel(this);
		if (this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}
		try {
			Class<?> BungeeAudiencesImpl = Class.forName("net.kyori.adventure.platform.bungeecord.BungeeAudiencesImpl");
			Field f = BungeeAudiencesImpl.getDeclaredField("INSTANCES");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, BungeeAudiences> INSTANCES = (Map<String, BungeeAudiences>) f.get(null);
			INSTANCES.remove(this.getDescription().getName());
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		this.getProxy().getPluginManager().unregisterCommands(this);
		this.getLogger().info("Well done. Time to sleep!");
	}


	@Override
	public @NotNull CommonProxy getProxyServer() {
		return this.proxy;
	}


	@Override
	public @NotNull String getName() {
		return this.getDescription().getName();
	}


	@Override
	public @NotNull String getVersion() {
		return this.getDescription().getVersion();
	}
	

}
