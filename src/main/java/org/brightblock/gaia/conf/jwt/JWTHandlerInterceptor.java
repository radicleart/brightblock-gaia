package org.brightblock.gaia.conf.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JWTHandlerInterceptor implements HandlerInterceptor {

	private static final Logger logger = LogManager.getLogger(JWTHandlerInterceptor.class);
	private static final String STORE = "/store/";
	private static final String AUTHORIZATION = "Authorization";
	private static final String HUB_INFO = "hubInfo";
	private static final String CONFIG = "config";
	private static final String LIST_FILES = "/list-files/";
	@Autowired private GaiaSettings gaiaSettings;
	private String address;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			logger.info("Handling: " + handler + " path: " + request.getRequestURI());
			if (handler instanceof HandlerMethod) {
				HandlerMethod h = (HandlerMethod) handler;
				String methodName = h.getMethod().getName();
				logger.info("methodName: " + methodName);
				if (!methodName.equals(HUB_INFO) && !methodName.equals(CONFIG)) {
					String authToken = request.getHeader(AUTHORIZATION);
					logger.info("authToken: " + authToken);
					String path = request.getRequestURI();
					if (path.startsWith(LIST_FILES)) {
						address = path.substring(LIST_FILES.length());
					} else if (path.startsWith(STORE)) {
						int secondSlash = path.lastIndexOf("/");
						address = path.substring(STORE.length(), secondSlash);
					} else {
						int secondSlash = path.lastIndexOf("/");
						address = path.substring(1, secondSlash);
					}
					authToken = authToken.split(" ")[1]; // stripe out Bearer string before passing along..
					logger.info("Authenticating request...");
					V1Authentication v1Authentication = V1Authentication.getInstance(authToken);
					String challenge = gaiaSettings.getChallengeText();
					boolean auth = v1Authentication.isAuthenticationValid(address, challenge, false, null);
					if (!auth) {
						throw new Exception("Failed validation of jwt token");
					}
					logger.info("Authenticated request...");
				} else {
					logger.info("Hub Info or config request.");
				}
			} else {
				logger.info("Unknown request.");
			}
		} catch (Exception e) {
			throw e;
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

}
