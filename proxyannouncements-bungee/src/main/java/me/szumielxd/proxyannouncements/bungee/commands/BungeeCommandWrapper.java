package me.szumielxd.proxyannouncements.bungee.commands;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import me.szumielxd.proxyannouncements.bungee.ProxyAnnouncementsBungee;
import me.szumielxd.proxyannouncements.bungee.objects.BungeeSender;
import me.szumielxd.proxyannouncements.common.commands.CommonCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeCommandWrapper extends Command implements TabExecutor {
	
	
	private final @NotNull ProxyAnnouncementsBungee plugin;
	private final @NotNull CommonCommand command;
	

	public BungeeCommandWrapper(@NotNull ProxyAnnouncementsBungee plugin, @NotNull CommonCommand command) {
		super(command.getName(), command.getPermission(), command.getAliases());
		this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
		this.command = Objects.requireNonNull(command, "command cannot be null");
	}


	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return this.command.onTabComplete(BungeeSender.wrap(this.plugin, sender), args);
	}


	@Override
	public void execute(CommandSender sender, String[] args) {
		this.command.execute(BungeeSender.wrap(this.plugin, sender), args);
	}

}
