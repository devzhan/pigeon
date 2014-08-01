/**
 * 
 */
package com.dianping.pigeon.log;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * <p>
 * Title: pigeonLog.java
 * </p>
 * <p>
 * Description: 描述
 * </p>
 * 
 * @author saber miao
 * @version 1.0
 * @created 2010-9-2 下午05:58:39
 */
public class CustomLog4jFactory {
    
	private CustomLog4jFactory() {
	}

	private static final String LOGGER_NAME = "com.dianping.pigeon";
	public static final Logger rootLogger = new RootLogger(Level.INFO);
	private static LoggerRepository loggerRepository = null;
	private static Level level = Level.INFO;
	private static volatile boolean initOK = false;

	public static synchronized void initLogger(String className) {
		if (initOK) {
			return;
		}
		Properties logPro = new Properties();
		String logLevel = "info";
		String logSuffix = "default";
		/*
		 * ConfigManager configManager =
		 * ExtensionLoader.getExtension(ConfigManager.class); if(configManager
		 * != null) { logLevel =
		 * configManager.getStringValue("pigeon.log.defaultlevel", logLevel); }
		 */

		try {
			logPro.load(CustomLog4jFactory.class.getClassLoader().getResourceAsStream("config/applicationContext.properties"));
			logLevel = logPro.getProperty("pigeon.logLevel") == null ? null : logPro.getProperty("pigeon.logLevel");
			logSuffix = logPro.getProperty("pigeon.logSuffix");
		} catch (Throwable e) {
			System.out.println("no pigeon log config found in config/applicationContext.properties");
		}
		if (logSuffix == null || logSuffix.length() < 1) {
			try {
				logSuffix = logPro.get("app.prefix").toString();
			} catch (Throwable e) {
				System.out.println("no app.prefix found in config/applicationContext.properties");
			}
		}

		if (logLevel != null && logLevel.equalsIgnoreCase("debug")) {
			level = Level.DEBUG;
		} else if (logLevel != null && logLevel.equalsIgnoreCase("info")) {
			level = Level.INFO;
		} else if (logLevel != null && logLevel.equalsIgnoreCase("warn")) {
			level = Level.WARN;
		} else if (logLevel != null && logLevel.equalsIgnoreCase("error")) {
			level = Level.ERROR;
		}
		LoggerRepository lr = new Hierarchy(rootLogger);
		new DOMConfigurator().doConfigure(CustomLog4jFactory.class.getClassLoader().getResource("pigeon_log4j.xml"), lr);
		rootLogger.setLevel(level);

		String osName = System.getProperty("os.name");
		String bizLogDir = null;
		if (osName != null && osName.toLowerCase().indexOf("windows") > -1) {
			bizLogDir = "c:/";
		}
		for (Enumeration<?> appenders = lr.getLogger(LOGGER_NAME).getAllAppenders(); appenders.hasMoreElements();) {
			Appender appender = (Appender) appenders.nextElement();
			if (FileAppender.class.isInstance(appender)) {
				FileAppender logFileAppender = (FileAppender) appender;
				// logFileAppender.setThreshold(level);
				String logFileName = logFileAppender.getFile();
				File deleteFile = new File(logFileName);
				if (logSuffix != null) {
					logFileName = logFileName.replace(".log", "." + logSuffix + ".log");
				}
				if (bizLogDir != null) {
					File logFile = new File(bizLogDir, logFileName);
					logFileName = logFile.getAbsolutePath();
				}
				if (logSuffix != null || bizLogDir != null) {
					logFileAppender.setFile(logFileName);
					logFileAppender.activateOptions();
					if (deleteFile.exists()) {
						deleteFile.delete();
					}
					System.out.println(logFileAppender.getFile() + "的输出路径改变为:" + logFileName);
				}
			} else if (ConsoleAppender.class.isInstance(appender)) {
				ConsoleAppender consoleAppender = (ConsoleAppender) appender;
			}
		}

		loggerRepository = lr;
		initOK = true;
	}

	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		if (loggerRepository == null) {
			initLogger(name);
		}
		Logger logger = loggerRepository.getLogger(name);
		return logger;
	}

}