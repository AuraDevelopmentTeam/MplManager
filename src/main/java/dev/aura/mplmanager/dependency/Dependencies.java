package dev.aura.mplmanager.dependency;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import dev.aura.mplmanager.MplManager;
import eu.mikroskeem.picomaven.Dependency;
import eu.mikroskeem.picomaven.DownloaderCallbacks;
import eu.mikroskeem.picomaven.PicoMaven;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Dependencies {
	public static final URI MAVEN_CENTRAL = URI.create("https://repo.maven.apache.org/maven2");
	public static final URI CINDYCATS = URI.create("http://basket.cindyscats.com/content/repositories/releases");

	protected static final DependencyJar DEP_COMMONS_IO = new DependencyJar("commons-io", "commons-io", "2.4");
	protected static final DependencyJar DEP_NBT = new DependencyJar("com.evilco.mc", "nbt", "1.0.2", DEP_COMMONS_IO);
	protected static final DependencyJar DEP_JCOMMANDER = new DependencyJar("com.beust", "jcommander", "1.58");
	protected static final DependencyJar DEP_ANTLR4_RUNTIME = new DependencyJar("org.antlr", "antlr4-runtime", "4.5.3");

	public static final DependencyJar DEP_MPL_COMPILER = new DependencyJar("de.adrodoc55.mpl", "mpl-compiler", "1.3.2",
			DEP_ANTLR4_RUNTIME, DEP_JCOMMANDER, DEP_NBT);

	public static void loadDependencyJars(List<DependencyJar> dependencyJars) {
		List<Dependency> dependencies = new LinkedList<>();

		dependencyJars.stream().map(DependencyJar::getDependencies).forEach(dependencies::addAll);

		loadDependencies(dependencies);
	}

	public static void loadDependencies(List<Dependency> dependencies) {
		PicoMaven picoMaven = new PicoMaven.Builder().withDebugLoggerImpl(Dependencies::logDownload)
				.withRepositories(Arrays.asList(MAVEN_CENTRAL, CINDYCATS))
				.withDownloadPath(MplManager.getInstance().getLibsDir().toPath()).withDependencies(dependencies)
				.withDownloaderCallbacks(new DownLoaderCallback()).build();

		try {
			picoMaven.downloadAll().stream().forEach(path -> {
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

	private static void addURL(URL u) throws IOException {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			throw new IOException("Error, could not add URL to system classloader", t);
		}
	}

	private static void logDownload(String format, Object... contents) {
		MplManager.getInstance().getLogger().debug(String.format(format, contents));
	}

	private static class DownLoaderCallback implements DownloaderCallbacks {
		@Override
		public void onSuccess(Dependency dependency, Path dependencyPath) {
			MplManager.getInstance().getLogger()
					.info("Sucessfully downloaded " + dependency.getGroupId() + ':' + dependency.getArtifactId() + ':'
							+ dependency.getVersion() + " into " + dependencyPath.toString());
		}

		@Override
		public void onFailure(Dependency dependency, IOException exception) {
			MplManager.getInstance().getLogger().error("Failed to download " + dependency.getGroupId() + ':'
					+ dependency.getArtifactId() + ':' + dependency.getVersion() + '.', exception);
		}
	}
}
