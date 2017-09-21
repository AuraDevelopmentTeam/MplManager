package dev.aura.mplmanager.common.dependency;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import eu.mikroskeem.picomaven.Dependency;
import eu.mikroskeem.picomaven.PicoMaven;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Dependencies {
	public static final URI MAVEN_CENTRAL = URI.create("https://repo.maven.apache.org/maven2");
	public static final URI CINDYCATS = URI.create("http://basket.cindyscats.com/content/repositories/releases");

	public static final DependencyJar MPL_COMPILER = new DependencyJar("de.adrodoc55.mpl", "mpl-compiler", "1.3.2");

	public static void loadDependencyJars(List<DependencyJar> dependencies) {
		dependencies.stream().map(DependencyJar::getDependencies).forEach(Dependencies::loadDependencies);
	}

	public static void loadDependencies(List<Dependency> dependencies) {
		PicoMaven picoMaven = new PicoMaven.Builder().withRepositories(Arrays.asList(MAVEN_CENTRAL, CINDYCATS))
				.withDownloadPath(null).withDependencies(dependencies).build();

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
}
