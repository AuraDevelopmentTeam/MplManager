package dev.aura.mplmanager.dependency;

import dev.aura.mplmanager.MplManager;
import eu.mikroskeem.picomaven.Dependency;
import eu.mikroskeem.picomaven.DownloaderCallbacks;
import eu.mikroskeem.picomaven.PicoMaven;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Dependencies {
  protected static final URI REPO_MAVEN_CENTRAL =
      URI.create("https://repo.maven.apache.org/maven2");
  protected static final URI REPO_CINDYCATS =
      URI.create("http://basket.cindyscats.com/content/repositories/releases");

  protected static final DependencyJar DEP_COMMONS_IO =
      new DependencyJar("commons-io", "commons-io", "2.4");
  protected static final DependencyJar DEP_NBT =
      new DependencyJar("com.evilco.mc", "nbt", "1.0.2", DEP_COMMONS_IO);
  protected static final DependencyJar DEP_JCOMMANDER =
      new DependencyJar("com.beust", "jcommander", "1.58");
  protected static final DependencyJar DEP_ANTLR4_RUNTIME =
      new DependencyJar("org.antlr", "antlr4-runtime", "4.5.3");
  protected static final DependencyJar DEP_MINA_CORE =
      new DependencyJar("org.apache.mina", "mina-core", "2.0.4");
  protected static final DependencyJar DEP_FTPLET_API =
      new DependencyJar("org.apache.ftpserver", "ftplet-api", "1.0.6");

  public static final DependencyJar DEP_MPL_COMPILER =
      new DependencyJar(
          "de.adrodoc55.mpl", "mpl-compiler", "1.3.2", DEP_ANTLR4_RUNTIME, DEP_JCOMMANDER, DEP_NBT);
  public static final DependencyJar DEP_FTPSERVER_CORE =
      new DependencyJar(
          "org.apache.ftpserver", "ftpserver-core", "1.0.6", DEP_FTPLET_API, DEP_MINA_CORE);

  private static final Method ADD_URL_METHOD;

  static {
    Method addUrlMethod = null;

    try {
      addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      addUrlMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    ADD_URL_METHOD = addUrlMethod;
  }

  public static void loadDependencyJars(List<DependencyJar> dependencyJars) {
    List<Dependency> dependencies = new LinkedList<>();

    dependencyJars.stream().map(DependencyJar::getDependencies).forEach(dependencies::addAll);

    loadDependencies(dependencies);
  }

  public static void loadDependencies(List<Dependency> dependencies) {
    @Cleanup
    PicoMaven picoMaven =
        new PicoMaven.Builder()
            .withDebugLoggerImpl(Dependencies::logDownload)
            .withRepositories(Arrays.asList(REPO_MAVEN_CENTRAL, REPO_CINDYCATS))
            .withDownloadPath(MplManager.getInstance().getLibsDir().toPath())
            .withDependencies(dependencies)
            .withDownloaderCallbacks(new DownLoaderCallback())
            .build();

    try {
      picoMaven
          .downloadAll()
          .stream()
          .forEach(
              path -> {
                try {
                  addPath(path);
                } catch (IOException e) {
                  e.printStackTrace();
                }
              });
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void addPath(Path path) throws IOException {
    addURL(path.toUri().toURL());
  }

  private static void addURL(URL url) throws IOException {
    ClassLoader classLoader = MplManager.getInstance().getClass().getClassLoader();

    if (!(classLoader instanceof URLClassLoader))
      throw new RuntimeException("Unknown classloader: " + classLoader.getClass());

    MplManager.getInstance().getLogger().debug("Adding URL \"" + url + "\" to plugin classloader");

    try {
      ADD_URL_METHOD.invoke(classLoader, url);
    } catch (Exception e) {
      throw new IOException("Error, could not add URL \"" + url + "\" to plugin classloader", e);
    }
  }

  private static void logDownload(String format, Object... contents) {
    MplManager.getInstance().getLogger().debug(String.format(format, contents));
  }

  private static class DownLoaderCallback implements DownloaderCallbacks {
    @Override
    public void onSuccess(Dependency dependency, Path dependencyPath) {
      MplManager.getInstance()
          .getLogger()
          .info(
              "Sucessfully downloaded "
                  + dependency.getGroupId()
                  + ':'
                  + dependency.getArtifactId()
                  + ':'
                  + dependency.getVersion()
                  + " into "
                  + dependencyPath.toString());
    }

    @Override
    public void onFailure(Dependency dependency, IOException exception) {
      MplManager.getInstance()
          .getLogger()
          .error(
              "Failed to download "
                  + dependency.getGroupId()
                  + ':'
                  + dependency.getArtifactId()
                  + ':'
                  + dependency.getVersion()
                  + '.',
              exception);
    }
  }
}
