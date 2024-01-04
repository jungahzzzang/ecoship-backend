package com.ecoship.test.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.http.HttpServletRequest;

public class FilterMatcher implements RequestMatcher {
	
	private final OrRequestMatcher orRequestMatcher;
	private final RequestMatcher requestMatcher;
	
	public FilterMatcher(List<String> path, String processPath) {
		
		this.orRequestMatcher = new OrRequestMatcher(path
				.stream()
				.map(this :: httpPath)
				.collect(Collectors.toList()));
		this.requestMatcher = new AntPathRequestMatcher(processPath);
	}
	
	private AntPathRequestMatcher httpPath(String path) {
		String[] splitStr = path.split(",");
		
		return new AntPathRequestMatcher(splitStr[1], splitStr[0]);
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		
		return !orRequestMatcher.matches(request) && requestMatcher.matches(request);
	}

}
