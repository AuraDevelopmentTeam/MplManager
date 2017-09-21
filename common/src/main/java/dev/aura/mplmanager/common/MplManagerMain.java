package dev.aura.mplmanager.common;

import java.io.File;
import java.util.List;

import dev.aura.mplmanager.common.dependency.DependencyJar;

public interface MplManagerMain {
	public static final String LIBS = "libs";
	public static final String SCRIPTS = "scripts";

	public List<DependencyJar> getStaticDependencies();

	public List<DependencyJar> getDynamicDependencies();

	public File getConfigDir();

	public default File getLibsDir() {
		return new File(getConfigDir(), LIBS);
	}

	public default File getScriptsDir() {
		return new File(getConfigDir(), SCRIPTS);
	}
}
