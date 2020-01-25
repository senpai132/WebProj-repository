package controller;

import beans.User;

public class LogInLogic {
	public static Boolean Authenticate(User u)
	{
		System.out.println(u.getRole());
		return false;
	}

}
