package beans;

import enums.Roles;

public class User {
	private String email;
	private String password;
	private String name;
	private String lastName;
	private String organization;
	private Roles role;
	
	public User()
	{
		
	}

	public User(String email, String password, String name, String lastName, String organization, Roles role) {
		super();
		this.email = email;
		this.password = password;
		this.name = name;
		this.lastName = lastName;
		this.organization = organization;
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	/*@Override
	public String toString() {
		return "User [email=" + email + ", password=" + password + ", name=" + name + ", lastName=" + lastName
				+ ", organizacija=" + organizacija + ", role=" + role + "]";
	}*/
	
}
