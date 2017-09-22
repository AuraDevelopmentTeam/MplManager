package dev.aura.mplmanager.ftp;

import java.io.File;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;

import dev.aura.mplmanager.MplManager;
import dev.aura.mplmanager.config.Config;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class FtpServerManager {
	private final Config.SectionFTP config;
	private final File homeDir;
	private FtpServer server = null;

	@SneakyThrows({ FtpException.class })
	public void start() {
		stop();

		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory listenerFactory = new ListenerFactory();

		listenerFactory.setServerAddress(config.getHost());
		listenerFactory.setPort(config.getPort());

		serverFactory.addListener("default", listenerFactory.createListener());
		serverFactory.setUserManager(new ConfigUserManager(config, homeDir));

		server = serverFactory.createServer();
		server.start();

		MplManager.getInstance().getLogger()
				.info("Started internal FTP server on " + config.getHost() + ':' + config.getPort());
	}

	public void stop() {
		if ((server == null) || server.isStopped())
			return;

		server.stop();

		MplManager.getInstance().getLogger()
				.info("Stopped internal FTP server on " + config.getHost() + ':' + config.getPort());
	}
}
