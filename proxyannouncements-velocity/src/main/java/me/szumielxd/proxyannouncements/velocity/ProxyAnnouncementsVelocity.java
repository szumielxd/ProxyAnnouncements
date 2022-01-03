package me.szumielxd.proxyannouncements.velocity;

import java.io.File;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.ConfigurationSection;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.szumielxd.proxyannouncements.common.ProxyAnnouncements;
import me.szumielxd.proxyannouncements.common.ProxyAnnouncementsProvider;
import me.szumielxd.proxyannouncements.common.commands.CommonCommand;
import me.szumielxd.proxyannouncements.common.commands.MainCommand;
import me.szumielxd.proxyannouncements.common.configuration.Config;
import me.szumielxd.proxyannouncements.common.data.SerializableAnnouncement;
import me.szumielxd.proxyannouncements.common.objects.Announcement;
import me.szumielxd.proxyannouncements.common.objects.CommonProxy;
import me.szumielxd.proxyannouncements.velocity.commands.VelocityCommandWrapper;
import me.szumielxd.proxyannouncements.velocity.objects.VelocityProxy;

@Plugin(
		id = "id----",
		name = "@pluginName@",
		version = "@version@",
		authors = { "@author@" },
		description = "@description@",
		url = "https://github.com/szumielxd/ProxyAnnouncements/"
)
public class ProxyAnnouncementsVelocity implements ProxyAnnouncements {
	
	
	private final ProxyServer server;
	private final Logger logger;
	private final File dataFolder;
	
	
	private @NotNull VelocityProxy proxy;
	
	
	@Inject
	public ProxyAnnouncementsVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
		this.server = server;
		this.logger = logger;
		this.dataFolder = dataDirectory.toFile();
	}
	
	
	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
	    this.onEnable();
	}
	
	
	@Override
	public void onEnable() {
		ProxyAnnouncementsProvider.init(this);
		this.proxy = new VelocityProxy(this);
		Config.load(new File(this.dataFolder, "config.yml"), this);
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
		CommandManager mgr = this.getProxy().getCommandManager();
		CommandMeta meta = mgr.metaBuilder(command.getName()).aliases(command.getAliases()).build();
		mgr.register(meta, new VelocityCommandWrapper(this, command));
	}
	
	
	@Override
	public void onDisable() {
		this.getLogger().info("Disabling all announcements...");
		this.getProxyServer().getScheduler().cancelAll();
		this.getLogger().info("Well done. Time to sleep!");
	}
	
	
	@Override
	public @NotNull Logger getLogger() {
		return this.logger;
	}
	
	
	public @NotNull ProxyServer getProxy() {
		return this.server;
	}


	@Override
	public @NotNull CommonProxy getProxyServer() {
		return this.proxy;
	}


	@Override
	public @NotNull String getName() {
		return this.getProxy().getPluginManager().ensurePluginContainer(this).getDescription().getName().orElse("");
	}


	@Override
	public @NotNull String getVersion() {
		return this.getProxy().getPluginManager().ensurePluginContainer(this).getDescription().getVersion().orElse("");
	}
	

}
