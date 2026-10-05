package com.iexceed.webcontainer.logger;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.iexceed.webcontainer.startup.WebContextListener;
import org.slf4j.LoggerFactory;

public class Logger {

	private static boolean maskKeyWords = true;
	private org.slf4j.Logger logger = null;
	private static final String PATTERNSTRING = "pin[=;][A-Za-z_0-9]+[0-9][0-9][0-9][0-9]+";
	public static String propertiesPath ="APPZILLONSERVERPROPSCNTX";

	public Logger(String classname) {
		LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(context);
			context.reset();
			if(WebContextListener.propertiesPath !=null && !"".equals(WebContextListener.propertiesPath))
				configurator.doConfigure(Logger.class.getClassLoader()
						.getResource(WebContextListener.propertiesPath+"/"+"logback.xml"));
			else
				configurator.doConfigure(Logger.class.getClassLoader()
						.getResource("logback.xml"));
		} catch (JoranException e) {
			e.printStackTrace();
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(context);
		this.logger = LoggerFactory.getLogger(classname);
	}

	public void trace(String msg) {
		this.logger.trace(msg);
	}

	public void debug(String msg) {
		this.logger.debug(msg);
	}

	public void info(String msg) {
		this.logger.info(msg);
	}

	public void warn(String msg) {
		this.logger.warn(msg);
	}

	public void error(String msg) {
		this.logger.error(msg);
	}
	public void error(String msg, Exception pException) {
		this.logger.error(getStackTrace(pException));
	}

	/*private static String maskPins(final String logMessage) {
		String result;
		if (maskKeyWords) {
			Pattern.compile("'requestKey'[=:][A-Za-z_0-9]+[0-9][0-9][0-9][0-9]+");
			Pattern lPattern = Pattern
					.compile("[\"']*requestKey[\"']*+[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			String lMessage = logMessage;
			Matcher lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"requestKey\"")) {
				lMessage = lMatcher.replaceAll("\"requestKey\":\"******\"");
			} else if (lMessage.contains("'requestKey'")) {
				lMessage = lMatcher.replaceAll("'requestKey':'******'");
			} else if (lMessage.contains("requestKey")) {
				lMessage = lMatcher.replaceAll("requestKey=******");
			}

			Pattern.compile(PATTERNSTRING);
			lPattern = Pattern
					.compile("[\"']*pin[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}=]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"pin\"")) {
				lMessage = lMatcher.replaceAll("\"pin\":\"******\"");
			} else if (lMessage.contains("'pin'")) {
				lMessage = lMatcher.replaceAll("'pin':'******'");
			} else if (lMessage.contains("pin")) {
				lMessage = lMatcher.replaceAll("pin=******");
			}

			Pattern.compile(PATTERNSTRING);
			lPattern = Pattern
					.compile("[\"']*oldPassword[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"oldPassword\"")) {
				lMessage = lMatcher.replaceAll("\"oldPassword\":\"******\"");
			} else if (lMessage.contains("'oldPassword'")) {
				lMessage = lMatcher.replaceAll("'oldPassword':'******'");
			} else if (lMessage.contains("oldPassword")) {
				lMessage = lMatcher.replaceAll("oldPassword=******");
			}

			Pattern.compile(PATTERNSTRING);
			lPattern = Pattern
					.compile("[\"']*newPassword[\"']*[\\s=|:\\s]+[\"']*+[a-zA-Z0-9@!#$%^&*(){}]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"newPassword\"")) {
				lMessage = lMatcher.replaceAll("\"newPassword\":\"******\"");
			} else if (lMessage.contains("'newPassword'")) {
				lMessage = lMatcher.replaceAll("'newPassword':'******'");
			} else if (lMessage.contains("newPassword")) {
				lMessage = lMatcher.replaceAll("newPassword=******");
			}

			lPattern = Pattern
					.compile("[\"']*hashKey1[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"hashKey1\"")) {
				lMessage = lMatcher.replaceAll("\"hashKey1\":\"******\"");
			} else if (lMessage.contains("'hashKey1'")) {
				lMessage = lMatcher.replaceAll("'hashKey1':'******'");
			} else if (lMessage.contains("hashKey1")) {
				lMessage = lMatcher.replaceAll("hashKey1=******");
			}

			lPattern = Pattern
					.compile("[\"']*hashKey2[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"hashKey2\"")) {
				lMessage = lMatcher.replaceAll("\"hashKey2\":\"******\"");
			} else if (lMessage.contains("'hashKey2'")) {
				lMessage = lMatcher.replaceAll("'hashKey2':'******'");
			} else if (lMessage.contains("hashKey2")) {
				lMessage = lMatcher.replaceAll("hashKey2=******");
			}

			lPattern = Pattern
					.compile("[\"']*deviceId[\"']*[\\s=|:\\s]+[\"']*+[A-Za-z_0-9.]+[\"']*");
			lMatcher = lPattern.matcher(lMessage);
			if (lMessage.contains("\"deviceId\"")) {
				lMessage = lMatcher.replaceAll("\"deviceId\":\"******\"");
			} else if (lMessage.contains("'deviceId'")) {
				lMessage = lMatcher.replaceAll("'deviceId':'******'");
			} else if (lMessage.contains("deviceId")) {
				lMessage = lMatcher.replaceAll("deviceId=******");
			}
			result = lMessage;
		} else {
			result = logMessage;
		}
		return result;
	}*/

	/*	
	 * getStackTrace() method takes Exception as its parameter
	 * and returns the stack trace of the exception as a string
	 * which helps in logging and better debugging
	 */
	public static String getStackTrace(Exception pEx) {
		StringWriter lSw = null;
		PrintWriter lPw = null;
		try {
			lSw = new StringWriter();
			lPw = new PrintWriter(lSw);
			pEx.printStackTrace(lPw);
		} catch (Exception ex) {
			//LOG.error("Exception",ex);
		}
		return lSw.toString();
	}	
	
	public static boolean isMaskKeyWords() {
		return maskKeyWords;
	}

	public static void setMaskKeyWords(final boolean lMaskKeyWords) {
		Logger.maskKeyWords = lMaskKeyWords;
	}
}
