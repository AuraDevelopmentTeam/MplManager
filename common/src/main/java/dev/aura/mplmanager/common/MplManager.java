package dev.aura.mplmanager.common;

import dev.aura.mplmanager.common.dependency.Dependencies;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MplManager {
	@Setter
	@Getter
	private static MplManagerMain instance;

	public static void preInit() {
		initDirs();

		loadStaticDependencies();
	}

	public static void init() {
		loadConfig();

		loadDynamicDependencies();
	}

	private static void loadStaticDependencies() {
		Dependencies.loadDependencyJars(instance.getStaticDependencies());
	}

	private static void loadDynamicDependencies() {
		Dependencies.loadDependencyJars(instance.getStaticDependencies());
	}

	private void initDirs() {
		instance.getConfigDir().mkdirs();
		instance.getLibsDir().mkdirs();
		instance.getScriptsDir().mkdirs();
	}

	private static void loadConfig() {
		// Nothing yet
	}
}
