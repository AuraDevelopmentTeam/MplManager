package dev.aura.mplmanager.common;

import java.util.List;

import dev.aura.mplmanager.common.dependency.DependencyJar;

public interface MplManagerMain {
	public List<DependencyJar> getStaticDependencies();

	public List<DependencyJar> getDynamicDependencies();
}
