package com.mooc.house.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterBeanConfig {

	@Bean
	public FilterRegistrationBean logFilter(){
		
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new LogFilter());
		
		List<String> urList = new ArrayList<String>();
		urList.add("*");
		filterRegistrationBean.setUrlPatterns(urList);
		return filterRegistrationBean;
		
	}
}
