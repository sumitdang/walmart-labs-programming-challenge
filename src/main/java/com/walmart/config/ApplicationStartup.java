package com.walmart.config;

import java.util.TimeZone;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
	}
}