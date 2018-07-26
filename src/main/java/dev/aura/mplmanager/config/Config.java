package dev.aura.mplmanager.config;

import dev.aura.mplmanager.MplManager;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

public class Config {
  // private static final String ACTIVE_WORLDS = "activeWorlds";

  @NonNull private final MplManager instance;
  @NonNull private final Logger logger;
  @NonNull private final Path configFile;

  private ConfigurationLoader<CommentedConfigurationNode> loader;
  private ConfigurationNode rootNode;

  @Getter private SectionFTP ftpSection;

  public Config(MplManager instance, Path configFile) {
    this.instance = instance;
    logger = instance.getLogger();
    this.configFile = configFile;
  }

  public void load() {
    if (!configFile.toFile().exists()) {
      try {
        Sponge.getAssetManager()
            .getAsset(instance, configFile.getFileName().toString())
            .get()
            .copyToFile(configFile);
      } catch (IOException
          | NoSuchElementException
          | NullPointerException
          | IllegalStateException e) {
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

    logger.debug("Config loaded!");

    save();
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
  public static class SectionFTP {
    private static final String SECTION_KEY = "ftp";

    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_HOST = "host";
    private static final String KEY_LOGGING = "logging";
    private static final String KEY_PORT = "port";
    private static final String KEY_USERS = "users";

    private boolean enabled;
    private String host;
    private boolean logging;
    private int port;
    private Map<String, String> users;

    public SectionFTP(ConfigurationNode node) {
      enabled = node.getNode(KEY_ENABLED).getBoolean(false);
      host = node.getNode(KEY_HOST).getString("0.0.0.0");
      logging = node.getNode(KEY_LOGGING).getBoolean(false);
      port = node.getNode(KEY_PORT).getInt(2121);
      users =
          node.getNode(KEY_USERS)
              .getChildrenMap()
              .entrySet()
              .stream()
              .collect(
                  Collectors.toMap(
                      set -> set.getKey().toString(), set -> set.getValue().getString()));
    }

    public void saveToNode(ConfigurationNode node) {
      node.getNode(KEY_ENABLED).setValue(enabled);
      node.getNode(KEY_HOST).setValue(host);
      node.getNode(KEY_LOGGING).setValue(logging);
      node.getNode(KEY_PORT).setValue(port);
      node.getNode(KEY_USERS).setValue(users);
    }

    public boolean validLogin(String user, String password) {
      return password.equals(users.get(user));
    }
  }
}
