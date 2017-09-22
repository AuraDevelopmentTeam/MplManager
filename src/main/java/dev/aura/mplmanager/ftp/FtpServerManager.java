package dev.aura.mplmanager.ftp;

import java.io.File;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.listener.nio.FtpLoggingFilter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.mina.core.filterchain.IoFilterEvent;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;

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
		disableLogging();

		stop();

		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory listenerFactory = new ListenerFactory();

		listenerFactory.setServerAddress(config.getHost());
		listenerFactory.setPort(config.getPort());

		serverFactory.addListener("default", listenerFactory.createListener());
		serverFactory.setUserManager(new ConfigUserManager(config, homeDir));

		server = serverFactory.createServer();
		server.start();

		disableLogging();

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

	private void disableLogging() {
		if (config.isLogging())
			return;

		try {
			((Logger) LogManager.getLogger(FtpLoggingFilter.class.getName())).setLevel(Level.WARN);
			((Logger) LogManager.getLogger(IoFilterEvent.class.getName())).setLevel(Level.WARN);
			((Logger) LogManager.getLogger(ProtocolCodecFilter.class.getName())).setLevel(Level.WARN);
			((Logger) LogManager.getLogger(OrderedThreadPoolExecutor.class.getName())).setLevel(Level.WARN);
		} catch (Exception e) {
			MplManager.getInstance().getLogger().error("Could not disable FTP logging", e);
		}
	}
}
