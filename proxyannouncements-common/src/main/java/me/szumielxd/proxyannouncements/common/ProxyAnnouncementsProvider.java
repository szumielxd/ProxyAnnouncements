package me.szumielxd.proxyannouncements.common;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProxyAnnouncementsProvider {
	
	
	private static @Nullable ProxyAnnouncements instance = null;
	
	
	public static void init(@NotNull ProxyAnnouncements instance) {
		ProxyAnnouncementsProvider.instance = Objects.requireNonNull(instance, "instance cannot be null");
	}
	
	
	public static @NotNull ProxyAnnouncements get() {
		if (instance == null) throw new IllegalArgumentException("AdminUtilities is not initialized");
		return instance;
	}
	

}
