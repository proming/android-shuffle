package org.dodgybits.shuffle.server.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dodgybits.shuffle.server.model.AppUser;
import org.dodgybits.shuffle.server.servlet.AuthFilter;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Server-side class that provides all login-related
 * operations. Called only from server code.
 */
public class LoginService
{
	public static final String AUTH_USER = "loggedInUser";
	public static final String AUTH_USER_KEY = "loggedInUserKey";

	public static AppUser login(HttpServletRequest req, HttpServletResponse res)
	{
		UserService userService = UserServiceFactory.getUserService();

		// User is logged into GAE
		// Find or add user in our app Datastore
		String userEmail = userService.getCurrentUser().getEmail();
		AppUser loggedInUser = findUser(userEmail);
		if (loggedInUser == null)
		{
			// Auto-add user
			loggedInUser = addUser(userEmail);
		}
		req.setAttribute(AUTH_USER, loggedInUser);
		return loggedInUser;
	}

	public static AppUser getLoggedInUser()
	{
	    HttpServletRequest req = AuthFilter.getThreadLocalRequest();
		return (AppUser)req.getAttribute(AUTH_USER);
	}

	private static AppUser findUser(String userEmail)
	{
		AppUserDao userDao = new AppUserDao();
		// Query for user by email
		return userDao.getByProperty("email", userEmail);
	}

	private static AppUser addUser(String email)
	{
		AppUserDao userDao = new AppUserDao();
		AppUser newUser = new AppUser(email);
		userDao.put(newUser);

		return newUser;
	}

}
