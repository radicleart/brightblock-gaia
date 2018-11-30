package org.brightblock.gaia.conf;

import org.brightblock.gaia.conf.jwt.JWTHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Autowired
	JWTHandlerInterceptor jwtInjectedInterceptor;

    @Override
    public void configurePathMatch(PathMatchConfigurer matcher) {
        matcher.setUseRegisteredSuffixPatternMatch(false);
//        matcher.setUseSuffixPatternMatch(false);
//        matcher.setUseTrailingSlashMatch(false);
    }

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInjectedInterceptor).addPathPatterns("/**");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowedOrigins("http://localhost:8888", "https://www.openartmart.org", "http://localhost:8080", "https://staging.transit8.com", "https://www.transit8.com", "https://www.brightblock.org",
				"https://staging.brightblock.org");
		registry.addMapping("/**").allowedMethods("*").allowedHeaders("*").allowedOrigins("*");
	}
}
