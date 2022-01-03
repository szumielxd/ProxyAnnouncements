package me.szumielxd.proxyannouncements.common.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import me.szumielxd.proxyannouncements.common.ProxyAnnouncements;
import me.szumielxd.proxyannouncements.common.configuration.Config;
import me.szumielxd.proxyannouncements.common.objects.CommonSender;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MainCommand extends CommonCommand {
	
	private final @NotNull ProxyAnnouncements plugin;

	public MainCommand(@NotNull ProxyAnnouncements plugin) {
		super(Config.COMMAND_NAME.getString(), "adminutilities.command.main", Config.COMMAND_ALIASES.getStringList().toArray(new String[0]));
		this.plugin = plugin;
	}

	@Override
	public void execute(@NotNull CommonSender sender, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
				if (sender.hasPermission("bungeeannouncements.command.reload")) {
					boolean failed = false;
					Function<String,String> replacer = (str) -> {
						return str.replace("{plugin}", this.plugin.getName()).replace("{version}", this.plugin.getVersion());
					};
					sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(replacer.apply(Config.PREFIX.getString()+Config.COMMAND_SUB_RELOAD_EXECUTE.getString())));
					try {
						this.plugin.onDisable();
					} catch (Exception e) {
						e.printStackTrace();
						failed = true;
					}
					try {
						this.plugin.onEnable();
					} catch (Exception e) {
						e.printStackTrace();
						failed = true;
					}
					if (failed) sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(replacer.apply(Config.PREFIX.getString()+Config.COMMAND_SUB_RELOAD_ERROR.getString())));
					else sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(replacer.apply(Config.PREFIX.getString()+Config.COMMAND_SUB_RELOAD_SUCCESS.getString())));
				} else {
					sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(Config.PREFIX.getString()+Config.MESSAGES_PERM_ERROR.getString()));
				}
				return;
			}
		}
		sender.sendMessage(LegacyComponentSerializer.legacySection().deserialize(Config.PREFIX.getString()+"/"+Config.COMMAND_NAME.getString()+" reload|rl"));
	}

	@Override
	public @NotNull List<String> onTabComplete(@NotNull CommonSender sender, @NotNull String[] args) {
		ArrayList<String> list = new ArrayList<>();
		if (sender.hasPermission("adminutilities.command.main")) {
			if (args.length == 1) {
				if ("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("adminutilities.command.main.reload")) list.add("reload");
				if ("rl".startsWith(args[0].toLowerCase()) && sender.hasPermission("adminutilities.command.main.reload")) list.add("rl");
				return list;
			}
		}
		return list;
	}

}
