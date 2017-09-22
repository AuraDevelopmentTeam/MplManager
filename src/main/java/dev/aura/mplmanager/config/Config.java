package dev.aura.mplmanager.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

import dev.aura.mplmanager.MplManager;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Config {
	// private static final String ACTIVE_WORLDS = "activeWorlds";

	@NonNull
	private final MplManager instance;
	@NonNull
	private final Logger logger;
	@NonNull
	private final Path configFile;

	private ConfigurationLoader<CommentedConfigurationNode> loader;
	private ConfigurationNode rootNode;

	@Getter
	private SectionFTP ftpSection;

	public Config(MplManager instance, Path configFile) {
		this.instance = instance;
		logger = instance.getLogger();
		this.configFile = configFile;
	}

	public void load() {
		if (!configFile.toFile().exists()) {
			try {
				Sponge.getAssetManager().getAsset(instance, configFile.getFileName().toString()).get()
						.copyToFile(configFile);
			} catch (IOException | NoSuchElementException | IllegalStateException e) {
				logger.error("Could not load default config!", e);

				return;
			}
		}

		loader = HoconConfigurationLoader.builder().setPath(configFile).build();

		try {
			rootNode = loader.load();
		} catch (IOException e) {
			logger.error("Config could not be loaded!", e);

			return;
		}

		ftpSection = new SectionFTP(rootNode.getNode(SectionFTP.SECTION_KEY));
		
		save();

		logger.debug("Config loaded!");
	}

	public void save() {
		try {
			ftpSection.saveToNode(rootNode.getNode(SectionFTP.SECTION_KEY));

			loader.save(rootNode);

			logger.debug("Config saved!");
		} catch (IOException | NullPointerException e) {
			logger.error("Config could not be saved!", e);
		}
	}

	@Data
	public class SectionFTP {
		public static final String SECTION_KEY = "ftp";

		public static final String KEY_ENABLED = "enabled";
		public static final String KEY_HOST = "host";
		public static final String KEY_PORT = "port";
		public static final String KEY_USERS = "users";

		private boolean enabled;
		private String host;
		private int port;
		private Map<String, String> users;

		public SectionFTP(ConfigurationNode node) {
			enabled = node.getNode(KEY_ENABLED).getBoolean(false);
			host = node.getNode(KEY_HOST).getString("0.0.0.0");
			port = node.getNode(KEY_PORT).getInt(2121);
			users = node.getNode(KEY_USERS).getChildrenMap().entrySet().stream()
					.collect(Collectors.toMap(set -> set.getKey().toString(), set -> set.getValue().getString()));
		}

		public void saveToNode(ConfigurationNode node) {
			node.getNode(KEY_ENABLED).setValue(enabled);
			node.getNode(KEY_HOST).setValue(host);
			node.getNode(KEY_PORT).setValue(port);
			node.getNode(KEY_USERS).setValue(users);
		}

		public boolean validLogin(String user, String password) {
			return password.equals(users.get(user));
		}
	}
}
