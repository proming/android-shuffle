/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dodgybits.shuffle.server.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dodgybits.shuffle.server.service.LoginService;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * A servlet filter that handles basic GAE user authentication.
 * Based on http://code.google.com/p/google-web-toolkit/source/browse/trunk/samples/expenses/src/main/java/com/google/gwt/sample/gaerequest/server/GaeAuthFilter.java
 */
public class AuthFilter implements Filter
{

    private static final ThreadLocal<HttpServletRequest> perThreadRequest =
            new ThreadLocal<HttpServletRequest>();

    /**
     * Returns the thread-local {@link HttpServletRequest}.
     * 
     * @return an {@link HttpServletRequest} instance
     */
    public static HttpServletRequest getThreadLocalRequest() {
      return perThreadRequest.get();
    }
    
    @Override
    public void init(FilterConfig config)
    {
    }

    @Override
	public void destroy()
	{
	}

    @Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException
	{
		UserService userService = UserServiceFactory.getUserService();
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

	    perThreadRequest.set(request);

	    try
	    {
    		if (!userService.isUserLoggedIn())
    		{
    			// User is not logged in to App Engine so redirect to login page
    			response.setHeader("login",
    					userService.createLoginURL(request.getRequestURI()));
    			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    			return;
    		}
    		
    		LoginService.login(request, response);
    		
    		filterChain.doFilter(request, response);
	    }
	    finally {
	        perThreadRequest.set(null);
	    }
	}

}
