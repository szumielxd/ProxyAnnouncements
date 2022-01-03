package me.szumielxd.proxyannouncements.common.objects;

import java.util.Collection;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CommonProxy {
	
	
	public @Nullable CommonPlayer getPlayer(@NotNull UUID uuid);
	
	public @Nullable CommonPlayer getPlayer(@NotNull String name);
	
	public @NotNull Collection<CommonPlayer> getPlayers();
	
	public @NotNull Collection<CommonPlayer> getPlayers(@NotNull String serverName);
	
	public @NotNull CommonScheduler getScheduler();
	

}
