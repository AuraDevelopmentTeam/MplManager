package dev.aura.mplmanager.sponge;

import java.util.List;

import org.spongepowered.api.plugin.Plugin;

import dev.aura.mplmanager.common.MplManagerMain;
import dev.aura.mplmanager.common.dependency.DependencyJar;

@Plugin(id = MplManagerMainSponge.ID, name = MplManagerMainSponge.NAME, version = MplManagerMainSponge.VERSION, description = MplManagerMainSponge.DESCRIPTION, url = MplManagerMainSponge.URL, authors = {
		MplManagerMainSponge.AUTHOR_BRAINSTONE })
public class MplManagerMainSponge implements MplManagerMain {
	public static final String ID = "@id@";
	public static final String NAME = "@name@";
	public static final String VERSION = "@version@";
	public static final String DESCRIPTION = "@description@";
	public static final String URL = "https://github.com/AuraDevelopmentTeam/MplManager";
	public static final String AUTHOR_BRAINSTONE = "The_BrainStone";

	@Override
	public List<DependencyJar> getStaticDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<DependencyJar> getDynamicDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
}
