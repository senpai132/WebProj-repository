package beans;

import java.util.ArrayList;

public class Organisation {
	private String name;
	private String logo;
	private ArrayList<String> users;
	private ArrayList<String> resources;
	
	public Organisation()
	{
		
	}

	public Organisation(String name, String logo, ArrayList<String> users, ArrayList<String> resources) {
		super();
		this.name = name;
		this.logo = logo;
		this.users = users;
		this.resources = resources;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public ArrayList<String> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<String> users) {
		this.users = users;
	}

	public ArrayList<String> getResources() {
		return resources;
	}

	public void setResources(ArrayList<String> resources) {
		this.resources = resources;
	}
	
	
}
