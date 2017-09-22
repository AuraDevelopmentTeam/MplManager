package dev.aura.mplmanager;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import dev.aura.mplmanager.config.Config;
import dev.aura.mplmanager.dependency.Dependencies;
import dev.aura.mplmanager.dependency.DependencyJar;
import dev.aura.mplmanager.ftp.FtpServerManager;
import lombok.Getter;
import lombok.NonNull;

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

	@Getter
	private static MplManager instance;

	@Inject
	@ConfigDir(sharedRoot = false)
	@NonNull
	@Getter
	private File configDir;
	@Inject
	@DefaultConfig(sharedRoot = false)
	@NonNull
	@Getter
	protected File configFile;
	@Inject
	@NonNull
	@Getter
	protected Logger logger;
	@NonNull
	@Getter
	protected Config config;
	protected FtpServerManager ftpServerManager;

	protected static <T> void callSafely(T object, Consumer<T> method) {
		if (object != null) {
			method.accept(object);
		}
	}

	public MplManager() {
		assert instance == null;

		instance = this;
	}

	public File getLibsDir() {
		return new File(getConfigDir(), LIBS);
	}

	public File getScriptsDir() {
		return new File(getConfigDir(), SCRIPTS);
	}

	public List<DependencyJar> getStaticDependencies() {
		return Arrays.asList(Dependencies.DEP_MPL_COMPILER);
	}

	public List<DependencyJar> getDynamicDependencies() {
		if (config.getFtpSection().isEnabled())
			return Arrays.asList(Dependencies.DEP_FTPSERVER_CORE);
		else
			return Arrays.asList();
	}

	@Listener
	public void onConstruct(GameConstructionEvent event) {
		onConstruct();
	}

	public void onConstruct() {
		initDirs();

		loadStaticDependencies();
	}

	@Listener
	public void onInit(GameInitializationEvent event) {
		onInit();
	}

	public void onInit() {
		logger.info("Initializing " + NAME + " Version " + VERSION);

		if (VERSION.contains("SNAPSHOT")) {
			logger.warn("WARNING! This is a snapshot version!");
			logger.warn("Use at your own risk!");
		}
		if (VERSION.contains("DEV")) {
			logger.info("This is a unreleased development version!");
			logger.info("Things might not work properly!");
		}

		config = new Config(this, configFile.toPath());
		config.load();

		loadDynamicDependencies();

		if (config.getFtpSection().isEnabled()) {
			ftpServerManager = new FtpServerManager(config.getFtpSection(), getScriptsDir());
			ftpServerManager.start();
		}

		logger.info("Loaded successfully!");
	}

	@Listener
	public void onReload(GameReloadEvent event) {
		// Unregistering everything
		onStopping();

		// Starting over
		onConstruct();
		onInit();

		logger.info("Reloaded successfully!");
	}

	@Listener
	public void onStopping(GameStoppingEvent event) {
		onStopping();
	}

	public void onStopping() {
		logger.info("Shutting down " + NAME + " Version " + VERSION);

		if (config.getFtpSection().isEnabled()) {
			callSafely(ftpServerManager, FtpServerManager::stop);
			ftpServerManager = null;
		}

		config = null;

		logger.info("Unloaded successfully!");
	}

	private void loadStaticDependencies() {
		Dependencies.loadDependencyJars(instance.getStaticDependencies());
	}

	private void loadDynamicDependencies() {
		Dependencies.loadDependencyJars(instance.getDynamicDependencies());
	}

	private void initDirs() {
		getConfigDir().mkdirs();
		getLibsDir().mkdirs();
		getScriptsDir().mkdirs();
	}
}
