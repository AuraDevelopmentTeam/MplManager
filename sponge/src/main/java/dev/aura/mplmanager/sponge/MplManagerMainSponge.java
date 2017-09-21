package dev.aura.mplmanager.sponge;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import dev.aura.mplmanager.common.MplManager;
import dev.aura.mplmanager.common.MplManagerMain;
import dev.aura.mplmanager.common.dependency.Dependencies;
import dev.aura.mplmanager.common.dependency.DependencyJar;
import lombok.Getter;
import lombok.NonNull;

@Plugin(id = MplManagerMainSponge.ID, name = MplManagerMainSponge.NAME, version = MplManagerMainSponge.VERSION, description = MplManagerMainSponge.DESCRIPTION, url = MplManagerMainSponge.URL, authors = {
		MplManagerMainSponge.AUTHOR_BRAINSTONE })
public class MplManagerMainSponge implements MplManagerMain {
	public static final String ID = "@id@";
	public static final String NAME = "@name@";
	public static final String VERSION = "@version@";
	public static final String DESCRIPTION = "@description@";
	public static final String URL = "https://github.com/AuraDevelopmentTeam/MplManager";
	public static final String AUTHOR_BRAINSTONE = "The_BrainStone";
	
	@Inject
	@ConfigDir(sharedRoot = false)
	@NonNull
	@Getter
	private File configDir;

	public MplManagerMainSponge() {
		MplManager.setInstance(this);
	}

	@Override
	public List<DependencyJar> getStaticDependencies() {
		return Arrays.asList(Dependencies.MPL_COMPILER);
	}

	@Override
	public List<DependencyJar> getDynamicDependencies() {
		return Arrays.asList();
	}

	@Listener
	public void gameConstruct(GameConstructionEvent event) {
		MplManager.preInit();
	}

	@Listener
	public void init(GameInitializationEvent event) {
		MplManager.init();
	}
}
