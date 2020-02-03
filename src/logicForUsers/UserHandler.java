package logicForUsers;

import java.util.HashMap;

import beans.Organization;
import beans.User;
import enums.Roles;

public class UserHandler {

	public static String AddUser(HashMap<String, User> users, User user) {
		if (users.containsKey(user.getEmail())) {
			return "Email already exists";
		}
		
		users.put(user.getName(), user);
		return null;
	}
	
	public static String EditUser(HashMap<String, Organization> orgs, HashMap<String, User> users, User user, String oldEmail) {
		if (!users.containsKey(oldEmail)) {
			return "Email does not exists";
		}
		
		if (!user.getEmail().equals(oldEmail) && users.containsKey(user.getEmail())) {
			return "Email already exists";
		}
		
		User found = users.get(oldEmail);
		found.setEmail(user.getEmail());
		found.setName(user.getName());
		found.setLastName(user.getLastName());
		found.setPassword(user.getPassword());
		found.setRole(user.getRole());
		
		if (user.getRole() != Roles.SUPERADMIN) {
			if (found.getOrganization() != user.getOrganization()) {
				for (String usr : orgs.get(found.getOrganization()).getUsers()) {
					if (usr == oldEmail) {
						orgs.get(found.getOrganization()).getUsers().remove(usr);
						break;
					}
				}
				orgs.get(user.getOrganization()).getUsers().add(user.getEmail());
				
				found.setOrganization(user.getOrganization());
			}
		}

		return null;
	}
	
	public static String DeleteUser(HashMap<String, Organization> orgs, HashMap<String, User> users, String email) {
		if (!users.containsKey(email)) {
			return "Email does not exists";
		}

		User found = users.get(email);

		for (String usr : orgs.get(found.getOrganization()).getUsers()) {
			if (usr == email) {
				orgs.get(found.getOrganization()).getUsers().remove(usr);
				break;
			}
		}
		
		users.remove(email);

		return null;
	}
}