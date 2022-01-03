package me.szumielxd.proxyannouncements.common;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import me.szumielxd.proxyannouncements.common.objects.CommonProxy;

public interface ProxyAnnouncements {
	
	
	public @NotNull Logger getLogger();
	
	public @NotNull CommonProxy getProxyServer();
	
	public @NotNull String getName();
	
	public @NotNull String getVersion();
	
	
	public void onEnable();
	
	public void onDisable();
	

}
