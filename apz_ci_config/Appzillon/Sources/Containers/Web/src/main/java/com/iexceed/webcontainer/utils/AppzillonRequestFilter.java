package com.iexceed.webcontainer.utils;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import org.owasp.encoder.Encode;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;
import static com.iexceed.webcontainer.utils.AppzillonConstants.CONTENT_SECURITY_POLICY;

public class AppzillonRequestFilter implements Filter {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AppzillonRequestFilter.class.getName());
	static String frameVal;
	static String setCache;
	static String clientDefinedHost;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		// if(frameVal!=null && "Y".equals(frameVal)){
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		LOG.debug("All Headers " + req.getHeaderNames());

		LOG.debug("REQSTR::::" + req);
		LOG.debug("" + req.getHeader("Host"));

		LOG.debug("CSRF Token X-XSRF-TOKEN  " + req.getHeader("X-XSRF-TOKEN"));
		LOG.debug("CSRF Token X-XSRF-TOKEN-1  " + req.getHeader("X-XSRF-TOKEN1"));
		LOG.debug("CSRF Token XSRF-TOKEN  " + req.getAttribute("X-XSRF-TOKEN"));
		LOG.debug("CSRF Token csrfToken  " + req.getHeader("csrfToken"));
		//Host header injection check with defined domain value
		String contentType = request.getContentType();
		res.setContentType(contentType != null ? Encode.forJava(contentType) : null);
		if (req.getMethod().equalsIgnoreCase("POST") && !req.getRequestURI().contains("JavaScriptServlet")) {
			res.setHeader(CONTENT_TYPE, CONTENT_TYPE_APP_JSON);
			if (!(req.getContentType().contains("multipart/form-data;")
					|| req.getContentType().equalsIgnoreCase("application/json"))) {
				RequestDispatcher dispatch = request.getRequestDispatcher("/apps/error.jsp");
				try {
					dispatch.forward(request, response);
				} catch (ServletException se) {
				} catch (IOException ioe) {
				}
				return;
			}
		}
		// Setting Response Character Encoding
		res.setCharacterEncoding(UTF_8);
		// Citi change for xframe
		if (frameVal != null && "Y".equals(frameVal)) {
			res.addHeader("X-Frame-Options", "SAMEORIGIN");
		} else {
			res.addHeader("X-Frame-Options", "DENY");
		}
		res.setHeader("X-XSS-Protection", "1; mode=block");
		res.setHeader("X-Content-Type-Options", "nosniff");
		res.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		// Setting Cache Control
		if (setCache == null || AppzillonConstants.NO.equals(setCache)) {
			// CACHE is not required by application
			res.addHeader(CACHE_CONTROL, CACHE_CONTROL_VAL);
		} else {
			// Setting Cache Control
			res.addHeader(CACHE_CONTROL, CACHE_CONTROL_VAL_2);
		}
		String contentSecPolicy = PropertyUtils.getPropertyValue(CONTENT_SECURITY_POLICY);
		if(contentSecPolicy != null) {
			res.addHeader(CONTENT_SECURITY_POLICY, Encode.forJava(contentSecPolicy));
		}
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		frameVal = PropertyUtils.getPropertyValue("setXFrame");
		setCache = PropertyUtils.getPropertyValue("ENABLECACHE");
		clientDefinedHost = PropertyUtils.getPropertyValue("Host");
	}
}
