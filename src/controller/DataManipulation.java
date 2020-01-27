package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import beans.User;
import enums.Roles;

public class DataManipulation {
	public static HashMap<String, User> ReadUsers(String filePath)
	{
		HashMap<String, User> users = new HashMap<String, User>();
		
		BufferedReader br;

		String line = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while((line = br.readLine()) != null)
			{
				User u = new User();
				String[] parts = line.split("\\|");
				u.setEmail(parts[0]);
				u.setPassword(parts[1]);
				u.setName(parts[2]);
				u.setLastName(parts[3]);
				u.setOrganizacija(parts[4]);
				u.setRole(Roles.valueOf(parts[5]));
				users.put(u.getEmail(), u);
					
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}

}
