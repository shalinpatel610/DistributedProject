package server.instance2.logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {

	static private FileHandler fileHandler;

	public static void setup(final String logFile) throws IOException {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		logger.setUseParentHandlers(false);
		logger.setLevel(Level.INFO);
		fileHandler = new FileHandler(logFile, true);
		fileHandler.setFormatter(new MyFormatter());
		logger.addHandler(fileHandler);
	}
}
