package org.brightblock.gaia.conf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@ComponentScan("org.brightblock")
public class Application {
	private static final String ACCESS_TOKEN = "xzYg4lWtO7sAAAAAAAAAukl0uSyXHbmMasv5JMa2Bg-kNK98OH9cPZHf8ATCe60S";

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate template = new RestTemplate();
		template.getMessageConverters().add(new StringHttpMessageConverter());
		return template;
	}

	@Bean
	public DbxClientV2 client() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/radicle").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
		return client;
	}
	
	@Bean
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		return mapper;
	}
}
