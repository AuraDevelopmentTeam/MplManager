package dev.aura.mplmanager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import dev.aura.mplmanager.dependency.Dependencies;
import dev.aura.mplmanager.dependency.DependencyJar;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Plugin(id = MplManager.ID, name = MplManager.NAME, version = MplManager.VERSION, description = MplManager.DESCRIPTION, url = MplManager.URL, authors = {
		MplManager.AUTHOR_BRAINSTONE })
public class MplManager {
	public static final String ID = "@id@";
	public static final String NAME = "@name@";
	public static final String VERSION = "@version@";
	public static final String DESCRIPTION = "@description@";
	public static final String URL = "https://github.com/AuraDevelopmentTeam/MplManager";
	public static final String AUTHOR_BRAINSTONE = "The_BrainStone";
	
	public static final String LIBS = "libs";
	public static final String SCRIPTS = "scripts";

	@Setter
	@Getter
	private static MplManager instance;

	@Inject
	@ConfigDir(sharedRoot = false)
	@NonNull
	@Getter
	private File configDir;

	public MplManager() {
		MplManager.setInstance(this);
	}
	
	public File getLibsDir() {
		return new File(getConfigDir(), LIBS);
	}

	public File getScriptsDir() {
		return new File(getConfigDir(), SCRIPTS);
	}

	public List<DependencyJar> getStaticDependencies() {
		return Arrays.asList(Dependencies.MPL_COMPILER);
	}

	public List<DependencyJar> getDynamicDependencies() {
		return Arrays.asList();
	}

	@Listener
	public void gameConstruct(GameConstructionEvent event) {
		initDirs();

		loadStaticDependencies();
	}

	@Listener
	public void init(GameInitializationEvent event) {
		loadConfig();

		loadDynamicDependencies();
	}

	private void loadStaticDependencies() {
		Dependencies.loadDependencyJars(instance.getStaticDependencies());
	}

	private void loadDynamicDependencies() {
		Dependencies.loadDependencyJars(instance.getStaticDependencies());
	}

	private void initDirs() {
		getConfigDir().mkdirs();
		getLibsDir().mkdirs();
		getScriptsDir().mkdirs();
	}

	private void loadConfig() {
		// Nothing yet
	}
}
