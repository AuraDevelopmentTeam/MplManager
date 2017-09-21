package dev.aura.mplmanager.spigot;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import dev.aura.mplmanager.common.MplManager;
import dev.aura.mplmanager.common.MplManagerMain;
import dev.aura.mplmanager.common.dependency.Dependencies;
import dev.aura.mplmanager.common.dependency.DependencyJar;

public class MplManagerMainSpigot extends JavaPlugin implements MplManagerMain {
	public MplManagerMainSpigot() {
		MplManager.setInstance(this);
	}

	@Override
	public List<DependencyJar> getStaticDependencies() {
		return Arrays.asList(Dependencies.MPL_COMPILER);
	}

	@Override
	public List<DependencyJar> getDynamicDependencies() {
		return Arrays.asList();
	}

	@Override
	public File getConfigDir() {
		return getDataFolder();
	}

	@Override
	public void onLoad() {
		MplManager.preInit();
	}

	@Override
	public void onEnable() {
		MplManager.init();
	}
}
