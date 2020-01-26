package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import beans.User;
import enums.Roles;

public class LogInLogic {
	public static Boolean Authenticate(User u)
	{
		BufferedReader br;
		String fs = System.getProperty("file.separator");
		String line = "";
		try {
			br = new BufferedReader(new FileReader("."+fs+"data"+fs+"Users.txt"));
			while((line = br.readLine()) != null)
			{
				String[] parts = line.split("\\|");
				if(u.getEmail().equals(parts[0]) && u.getPassword().equals(parts[1]))
				{
					u.setName(parts[2]);
					u.setLastName(parts[3]);
					u.setOrganizacija(parts[4]);
					u.setRole(Roles.valueOf(parts[5]));
					return true;
				}
					
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
