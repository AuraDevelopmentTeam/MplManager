package dev.aura.mplmanager.spigot;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import dev.aura.mplmanager.common.MplManagerMain;
import dev.aura.mplmanager.common.dependency.DependencyJar;

public class MplManagerMainSpigot extends JavaPlugin implements MplManagerMain {
	@Override
	public List<DependencyJar> getStaticDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DependencyJar> getDynamicDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
}
