package com.iexceed.webcontainer.servlet;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.iexceed.webcontainer.utils.WebProperties;

public class AppzillonWebContainerHealth extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5416187438044342329L;
	static Logger LOG = LoggerFactory.getLoggerFactory()
			.getWebContainerLogger(AppzillonWebContainerHealth.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Appzillon web health url");
		checkServerHealth(request, response);
	}

	private void checkServerHealth(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.info("Checking server health");
		HttpURLConnection urlConnHttp = null;
		URL url = new URL(WebProperties.getServerURL() + "/health/");
		urlConnHttp = (HttpURLConnection) url.openConnection();
		urlConnHttp.setRequestMethod(AppzillonConstants.GET);
		int responseCode = urlConnHttp.getResponseCode();
		String responseMessage = urlConnHttp.getResponseMessage();
		LOG.debug("response message from server for health : " + responseMessage);
		LOG.debug("response code from server health : " + responseCode);
		if(responseCode == 404)
			responseCode = 419;
		RequestDispatcher dispatch = request.getRequestDispatcher("/apps/health.jsp");
		if(responseCode != 200) {
			dispatch = request.getRequestDispatcher("/apps/error.jsp");
		}		
		try {
			response.setStatus(responseCode);
			dispatch.forward(request, response);
		} catch (ServletException se) {
			LOG.error("ServletException", se);
		} catch (IOException ioe) {
			LOG.error("IOException", ioe);
		}
		return;

	}

}
