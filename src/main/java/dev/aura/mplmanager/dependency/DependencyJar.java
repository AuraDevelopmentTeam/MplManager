package dev.aura.mplmanager.dependency;

import java.util.LinkedList;
import java.util.List;

import eu.mikroskeem.picomaven.Dependency;
import lombok.Getter;

public class DependencyJar {
	@Getter
	private final List<Dependency> dependencies = new LinkedList<>();

	public DependencyJar(String groupId, String artifactId, String version) {
		dependencies.add(new Dependency(groupId, artifactId, version));
	}

	public DependencyJar(String groupId, String artifactId, String version, DependencyJar... dependencies) {
		this(groupId, artifactId, version);

		for (DependencyJar dependencyJar : dependencies) {
			this.dependencies.addAll(dependencyJar.getDependencies());
		}
	}

	public DependencyJar(String groupId, String artifactId, String version, Dependency... dependencies) {
		this(groupId, artifactId, version);

		for (Dependency dependency : dependencies) {
			this.dependencies.add(dependency);
		}
	}
}
