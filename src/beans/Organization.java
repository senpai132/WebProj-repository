package beans;

import java.util.ArrayList;

public class Organization {
	private String name;
	private String description;
	private String logo;
	private ArrayList<String> users;
	private ArrayList<String> resources;
	
	public Organization()
	{
		
	}

	public Organization(String name, String description, String logo) {
		super();
		this.name = name;
		this.description = description;
		this.logo = logo;
		users = new ArrayList<String>();
		resources = new ArrayList<String>();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	@Override
	public String toString() {
		
		String users = "";
		
		for(String user : this.users) {
			users += user + "#";
		}
		users = users.substring(0, users.length() - 1);
		
		String resources = "";
		
		for(String resource : this.resources) {
			resources += resource + "#";
		}
		resources = resources.substring(0, resources.length() - 1);
		
		return name + "|" + description + "|" + logo + "|"
				+ users + "|" + resources;
	}
	
}
