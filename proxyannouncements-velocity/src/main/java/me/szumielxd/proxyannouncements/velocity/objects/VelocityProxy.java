package me.szumielxd.proxyannouncements.velocity.objects;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.szumielxd.proxyannouncements.common.objects.CommonPlayer;
import me.szumielxd.proxyannouncements.common.objects.CommonProxy;
import me.szumielxd.proxyannouncements.common.objects.CommonScheduler;
import me.szumielxd.proxyannouncements.velocity.ProxyAnnouncementsVelocity;

public class VelocityProxy implements CommonProxy {
	
	
	private final @NotNull ProxyAnnouncementsVelocity plugin;
	private final @NotNull VelocityScheduler scheduler;
	
	
	public VelocityProxy(@NotNull ProxyAnnouncementsVelocity plugin) {
		this.plugin = plugin;
		this.scheduler = new VelocityScheduler(plugin);
	}
	

	@Override
	public @Nullable CommonPlayer getPlayer(@NotNull UUID uuid) {
		return this.plugin.getProxy().getPlayer(uuid).map(p -> new VelocityPlayer(this.plugin, p)).orElse(null);
	}

	@Override
	public @Nullable CommonPlayer getPlayer(@NotNull String name) {
		return this.plugin.getProxy().getPlayer(name).map(p -> new VelocityPlayer(this.plugin, p)).orElse(null);
	}

	@Override
	public @NotNull Collection<CommonPlayer> getPlayers() {
		return this.plugin.getProxy().getAllPlayers().parallelStream().map(p -> new VelocityPlayer(this.plugin, p)).collect(Collectors.toList());
	}

	@Override
	public @NotNull Optional<Collection<CommonPlayer>> getPlayers(@NotNull String serverName) {
		return this.plugin.getProxy().getServer(Objects.requireNonNull(serverName, "serverName cannot be null"))
				.map(RegisteredServer::getPlayersConnected)
				.map(list -> list.parallelStream().map(p -> new VelocityPlayer(this.plugin, p)).collect(Collectors.toList()));
	}
	
	@Override
	public @NotNull CommonScheduler getScheduler() {
		return this.scheduler;
	}
	

}
