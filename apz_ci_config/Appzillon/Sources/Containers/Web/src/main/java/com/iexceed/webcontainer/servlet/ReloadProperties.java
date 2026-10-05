package com.iexceed.webcontainer.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.PropertyUtils;
import com.iexceed.webcontainer.utils.WebProperties;

public class ReloadProperties extends HttpServlet {
	static Logger LOG = LoggerFactory.getLoggerFactory()
			.getWebContainerLogger(AppzillonWebContainerHealth.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = -2183404617075214120L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Loading web properties");
		PropertyUtils.initializeProperties(WebProperties.getServletContext());
	}

}
