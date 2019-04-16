package org.brightblock.gaia.conf;

import org.brightblock.gaia.conf.jwt.JWTHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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

}
