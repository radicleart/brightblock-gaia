package org.brightblock.gaia.conf.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JWTHandlerInterceptor implements HandlerInterceptor {

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
			if (handler instanceof HandlerMethod) {
				HandlerMethod h = (HandlerMethod) handler;
				String methodName = h.getMethod().getName();
				if (!methodName.equals(HUB_INFO) && !methodName.equals(CONFIG)) {
					String authToken = request.getHeader(AUTHORIZATION);
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
					V1Authentication v1Authentication = V1Authentication.getInstance(authToken);
					String challenge = gaiaSettings.getChallengeText();
					boolean auth = v1Authentication.isAuthenticationValid(address, challenge, false, null);
					if (!auth) {
						throw new Exception("Failed validation of jwt token");
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}

}
