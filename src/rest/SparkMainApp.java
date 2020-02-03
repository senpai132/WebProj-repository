package rest;

import static spark.Spark.after;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import beans.Disc;
import beans.Organization;
import beans.Status;
import beans.User;
import beans.VM_Filter;
import beans.VirtualMachine;
import beans.VirtualMachineCategory;
import dataFlow.ReadData;
import enums.Roles;
import logicForDiscs.DiscsHandler;
import logicForOrganizations.OrganizationHandler;
import logicForUsers.UserHandler;
import logicForVM_Categories.VM_CategoriesHandler;
import logicForVMs.VirtualMachineHandler;
import spark.Session;

public class SparkMainApp {
	
	private static Gson g = new Gson();

	public static HashMap<String, User> users;
	public static HashMap<String, VirtualMachine> vms;
	public static HashMap<String, Disc> discs;
	public static HashMap<String, VirtualMachineCategory> vmcs;
	public static HashMap<String, Organization> orgs;
	
	public static void main(String[] args) throws Exception {
		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());

		orgs = ReadData.readOrganizations();
		users = ReadData.readUsers();
		vms = ReadData.readVMs();
		discs = ReadData.readDiscs();
		vmcs = ReadData.readVM_Categories();

		post("/rest/login", (req,res) -> {
			res.type("application/json");
			
			String payload = req.body();
			User u = g.fromJson(payload, User.class);
			String result = "false";
			String message = "\"\"";
			
			Session ss = req.session(true);

			if (!checkEmail(u.getEmail())) {
				message = "\"Invalid email\"";
			}
			else if (u.getPassword() == null || u.getPassword().isEmpty()) {
				message = "\"Password cannot be empty\"";
			}
			else {
				User loaded = users.get(u.getEmail());
				
				if (loaded != null && loaded.getPassword().equals(u.getPassword())) {
					User user = ss.attribute("user");
					if (user == null) {
						ss.attribute("user", loaded);
						result = "true";
					}
					else {
						message = "\"User already logged\"";
					}
				}
				else {
					message = "\"Invalid email or password\"";
				}
			}

			return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		get("/rest/logout", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			String result = "true";
			
			if (user != null) {
				ss.invalidate();
			}
			else {
				result = "false";
			}

			return "{\"result\": " + result + "}";
		});
		
		after("/rest/logout", (req, res) -> {
			Session ss = req.session(true);
			User user = ss.attribute("user");
			
			if (user == null) {
				res.redirect("/html/login.html", 301);
			}
		});

		get("/rest/goToUsers", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				halt(403, "Unauthorized operation!");
			}
			
			return "{\"result\":false}";
		});
		
		after("/rest/goToUsers", (req, res) -> {
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u != null && u.getRole() != Roles.CLIENT) {
				res.redirect("/html/users.html", 301);
			}
		});
		
		get("/rest/getUserType", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String logged = "false";
			String role = "\"\"";

			if (u != null) {
				logged = "true";
				switch(u.getRole()) {
					case SUPERADMIN:
						role = "\"superadmin\"";
						break;
						
					case ADMIN:
						role = "\"admin\"";
						break;
						
					case CLIENT:
						role = "\"client\"";
						break;
				}
			}
			
			return "{\"logged\": " + logged + ", \"role\": " + role + "}";
		});
		
		get("/rest/getUsers", (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String result = "true";
			
			ArrayList<User> curr = new ArrayList<User>();
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
			}
			else if (u.getRole() == Roles.SUPERADMIN) {
				for(User user : users.values()) {
					curr.add(user);
				}
			}
			else {
				for (String email : orgs.get(u.getOrganization()).getUsers()) {
					curr.add(users.get(email));
				}
			}

			return "{\"users\": " + g.toJson(curr) + ", \"result\": " + result + "}";
		});
		
		get("/rest/goToNewUser", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				halt(403, "Unauthorized operation!");
			}
			
			return "{\"message\":false}";
		});
		
		after("/rest/goToNewUser", (req, res) -> {
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u != null && u.getRole() != Roles.CLIENT) {
				res.redirect("/html/new_user.html", 301);
			}
		});

		post("/rest/addUser", (req,res) -> {
			String payload = req.body();
			User u = g.fromJson(payload, User.class);
			String result = "false";
			String message = "\"\"";
			
			Session ss = req.session(true);
			User logged = ss.attribute("user");
			
			if (!checkEmail(u.getEmail()) || u.getPassword() == null || u.getPassword().isEmpty() || u.getName() == null || u.getName().isEmpty() || u.getLastName() == null || u.getLastName().isEmpty()) {
				result = "false";
				message = "\"Bad input!\"";
			}
			else if (logged.getRole() == Roles.ADMIN && u.getRole() == null) {
				result = "false";
				message = "\"Organisation is reqired\"";
			}
			else {
				if (logged.getRole() == Roles.ADMIN) {
					u.setOrganization(logged.getOrganization());
				}
				
				if (users.containsKey(u.getEmail())) {
					result = "false";
					message = "\"Email already in use\"";
				}
				else {
					users.put(u.getEmail(), u);
					orgs.get(u.getOrganization()).getUsers().add(u.getEmail());
					
					result = "true";
				}
			}

			return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		get("/rest/goToEditUser", (req,res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String result = "true";
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				halt(403, "Unauthorized operation!");
				result = "false";
			}
			
			return "{\"result\":" + result + "}";
		});
		
		after("/rest/goToEditUser", (req,res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u != null && u.getRole() != Roles.CLIENT) {
				res.redirect("/html/edit_user.html", 301);
			}
		});
		
		post("/rest/setEditUser", (req, res) -> {
			res.type("application/json");
			String[] data = g.fromJson(req.body(), String[].class);
            String result = "true";
            String message = "\"\"";

            Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
				message = "\"You cannot be client\"";
			}
			else if (u.getRole() == Roles.ADMIN && !orgs.get(u.getOrganization()).getUsers().contains(data[0])) {
				result = "false";
				message = "\"You don't have rights to edit user from other organization\"";
			}
			else {
				ss.attribute("editUserEmail", data[0]);
			}

            return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		get("/rest/getEditUser", (req, res) -> {
			res.type("application/json");
			String result = "true";
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String userEmail = ss.attribute("editUserEmail");
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
			}
			else if (u.getRole() == Roles.ADMIN && !orgs.get(u.getOrganization()).getUsers().contains(userEmail)) {
				result = "false";
			}

			return "{\"user\": " + g.toJson(users.get(userEmail)) + ", \"result\": " + result + "}";
		});
		
		post("/rest/editUser", (req,res) -> {
			res.type("application/json");
            User newUser = g.fromJson(req.body(), User.class);
            String result = "true";
            String message = "\"\"";
            
            Session ss = req.session(true);
			User u = ss.attribute("user");
			String oldUserEmail = ss.attribute("editUserEmail");
			User userFound = users.get(oldUserEmail);
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
				message = "\"You must be superadmin\"";
			}
			else if (u.getRole() == Roles.ADMIN) {
				if (!orgs.get(userFound.getOrganization()).getUsers().contains((u.getEmail()))) {
					result = "false";
					message = "\"You are not in organization\"";
				}
			}
			else {
				if (newUser.getPassword() == null || newUser.getPassword().isEmpty()) {
					result = "false";
					message = "\"Password is reqired\"";
				}
				else if (newUser.getName() == null || newUser.getName().isEmpty()) {
					result = "false";
					message = "\"Name is reqired\"";
				}
				else if (newUser.getLastName() == null || newUser.getLastName().isEmpty()) {
					result = "false";
					message = "\"Last name is reqired\"";
				}
				else {
					if (userFound.getRole() == Roles.SUPERADMIN) {
						newUser.setRole(Roles.SUPERADMIN);
					}
					
					String editMsg = UserHandler.EditUser(orgs, users, newUser, oldUserEmail);
		            if (editMsg != null) {
		            	message = "\"" + editMsg + "\"";
		            	result = "false";
		            }
				}
			}

            return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		post("/rest/deleteUser", (req,res) -> {
			res.type("application/json");
			String[] data = g.fromJson(req.body(), String[].class);
            String result = "true";
            String message = "\"\"";
            
            Session ss = req.session(true);
			User u = ss.attribute("user");
			User userFound = users.get(data[0]);
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
				message = "\"You must be superadmin\"";
			}
			else if (u.getEmail().equals(data[0])) {
				result = "false";
				message = "\"You cannot delete yourself\"";
			}
			else if (u.getRole() == Roles.ADMIN) {
				if (!orgs.get(userFound.getOrganization()).getUsers().contains((u.getEmail()))) {
					result = "false";
					message = "\"You are not in organization\"";
				}
			}
			else {
				String deleteMsg = UserHandler.DeleteUser(orgs, users, userFound.getEmail());
	            if (deleteMsg != null) {
	            	message = "\"" + deleteMsg + "\"";
	            	result = "false";
	            }
			}

            return "{\"result\": " + result + ", \"message\": " + message + "}";
		});

		get("/rest/goToOrganizations", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String result = "false";
			
			if (u == null || u.getRole() != Roles.SUPERADMIN) {
				halt(403, "Unauthorized operation!");
			}
			else {
				result = "true";
			}
			
			return "{\"result\":" + result + "}";
		});
		
		after("/rest/goToOrganizations", (req, res) -> {
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u != null && u.getRole() == Roles.SUPERADMIN) {
				res.redirect("/html/organizations.html", 301);
			}
		});
		
		get("/rest/getOrganizations", (req, res) -> {
			res.type("application/json");
			String result = "true";
			
			ArrayList<Organization> curr = new ArrayList<Organization>();
			for (Organization org : orgs.values()) {
				curr.add(org);
			}
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
			}

			return "{\"orgs\": " + g.toJson(curr) + ", \"result\": " + result + "}";
		});
		
		get("/getOrganisations", (req, res)-> {
			
			return g.toJson(orgs.values());
		});

		get("/rest/goToNewOrganization", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String result = "true";
			
			if (u == null || u.getRole() != Roles.SUPERADMIN) {
				halt(403, "Unauthorized operation!");
				result = "false";
			}
			
			return "{\"result\":" + result + "}";
		});
		
		after("/rest/goToNewOrganization", (req, res) -> {
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u != null && u.getRole() == Roles.SUPERADMIN) {
				res.redirect("/html/new_organization.html", 301);
			}
		});
		
		post("/rest/addOrganization", (req,res) -> {
			res.type("application/json");
            Organization org = g.fromJson(req.body(), Organization.class);
            String result = "true";
            String message = "\"\"";
            
            Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() != Roles.SUPERADMIN) {
				result = "false";
				message = "\"You must be superadmin\"";
			}
			else {
				if (org.getName() == null || org.getName().isEmpty()) {
					result = "false";
					message = "\"Name is reqired\"";
				}
				else if (org.getDescription() == null || org.getDescription().isEmpty()) {
					result = "false";
					message = "\"Description is reqired\"";
				}
				else {
					String addMsg = OrganizationHandler.AddOrganization(orgs, org);
		            if (addMsg != null) {
		            	message = "\"" + addMsg + "\"";
		            	result = "false";
		            }
				}
			}

            return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		get("/rest/goToEditOrganization", (req,res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String result = "true";
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				halt(403, "Unauthorized operation!");
				result = "false";
			}
			
			return "{\"result\":" + result + "}";
		});
		
		after("/rest/goToEditOrganization", (req,res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u != null && u.getRole() != Roles.CLIENT) {
				res.redirect("/html/edit_organization.html", 301);
			}
		});
		
		post("/rest/setEditOrganization", (req, res) -> {
			res.type("application/json");
			String[] data = g.fromJson(req.body(), String[].class);
            String result = "true";
            String message = "\"\"";

            Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
				message = "\"You cannot be client\"";
			}
			else if (u.getRole() == Roles.ADMIN && !orgs.get(data[0]).getUsers().contains(u.getEmail())) {
				result = "false";
				message = "\"You don't have rights to edit other organization\"";
			}
			else {
				ss.attribute("editOrgName", data[0]);
			}

            return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		get("/rest/getEditOrganization", (req, res) -> {
			res.type("application/json");
			String result = "true";
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String orgName = ss.attribute("editOrgName");
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
			}
			else if (u.getRole() == Roles.ADMIN && !orgs.get(orgName).getUsers().contains(u.getEmail())) {
				result = "false";
			}

			return "{\"org\": " + g.toJson(orgs.get(orgName)) + ", \"result\": " + result + "}";
		});
		
		post("/rest/editOrganization", (req,res) -> {
			res.type("application/json");
            Organization org = g.fromJson(req.body(), Organization.class);
            String result = "true";
            String message = "\"\"";
            
            Session ss = req.session(true);
			User u = ss.attribute("user");
			String oldOrg = ss.attribute("editOrgName");
			Organization orgFound = orgs.get(oldOrg);
			
			if (u == null || u.getRole() == Roles.CLIENT) {
				result = "false";
				message = "\"You must be superadmin\"";
			}
			else if (u.getRole() == Roles.ADMIN) {
				if (!orgFound.getUsers().contains((u.getEmail()))) {
					result = "false";
					message = "\"You are not in organization\"";
				}
			}
			else {
				if (org.getName() == null || org.getName().isEmpty()) {
					result = "false";
					message = "\"Name is reqired\"";
				}
				else if (org.getDescription() == null || org.getDescription().isEmpty()) {
					result = "false";
					message = "\"Description is reqired\"";
				}
				else {
					String addMsg = OrganizationHandler.EditOrganization(orgs, org, oldOrg);
		            if (addMsg != null) {
		            	message = "\"" + addMsg + "\"";
		            	result = "false";
		            }
				}
			}

            return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		post("/addVM_Category", (req, res) ->{
			res.type("application/json");
			
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null || u.getRole() != Roles.SUPERADMIN)
			{
				halt(403, "Unauthorized operation!");
				return false;
			}
				
			
			VirtualMachineCategory vmc;
			
			try {
				vmc = g.fromJson(req.body(), VirtualMachineCategory.class);
			}
			catch (NumberFormatException e) {
				System.out.println("Usao");
				e.printStackTrace();
				return false;		
			} 
			

			return VM_CategoriesHandler.addVM_Category(vmcs, vmc);
		});
		
		get("/getVM_Categories", (req, res) -> {
			res.type("application/json"); 
			
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null)
			{
				halt(403, "Unauthorized operation!");
			}
			
			return g.toJson(vmcs.values());
		});
		
		delete("/DeleteVM_Category", (req, res) -> {
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null || u.getRole() != Roles.SUPERADMIN)
				halt(403, "Unauthorized operation!");
			
			VirtualMachineCategory vmc = g.fromJson(req.body(), VirtualMachineCategory.class);
			
			return VM_CategoriesHandler.deleteVM_Category(vmcs, vmc, vms);
		});
		
		post("/EditVM_Category", (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null || u.getRole() != Roles.SUPERADMIN)
				halt(403, "Unauthorized operation!");
			
			VirtualMachineCategory[] VMC_Pair = g.fromJson(req.body(), VirtualMachineCategory[].class);
			
			return VM_CategoriesHandler.editVM_Category(vmcs, VMC_Pair, vms);
		});
		
		post("/addDisc", (req, res) ->{
			res.type("application/json");
			
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null || u.getRole() == Roles.CLIENT)
				halt(403, "Unauthorized operation!");
			
			Disc disc = g.fromJson(req.body(), Disc.class);

			return DiscsHandler.addDisc(discs, disc, vms);
		});
		
		get("/getDiscs", (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null)
				halt(403, "Unauthorized operation!");
			
			if(u.getRole() == Roles.SUPERADMIN)
				return g.toJson(discs.values());
			else
			{
				ArrayList<Disc> localDiscs = new ArrayList<Disc>();
				
				for(VirtualMachine v : vms.values())
					if(v.getOrganisation().equals(u.getOrganization()))
						for(String s : v.getDiscs())
							localDiscs.add(discs.get(s));
				for(Disc d : discs.values())
					if(d.getParentVM().equals(""))
						localDiscs.add(d);
				localDiscs.removeIf(Objects::isNull);
				
				return g.toJson(localDiscs);
			}
			
		});
		
		delete("/deleteDisc", (req, res) -> {
			String payload = req.body();
			
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null || u.getRole() != Roles.SUPERADMIN)
				halt(403, "Unauthorized operation!");
			
			Disc disc = g.fromJson(payload, Disc.class);
			
			return DiscsHandler.removeDisc(discs, disc, vms);
		});
		
		post("/editDisc", (req, res) ->{
			res.type("application/json");
			
			Session ss = req.session(true);
			User u  =  ss.attribute("user");
			
			if(u == null || u.getRole() == Roles.CLIENT)
				halt(403, "Unauthorized operation!");
			
			Disc[] discPair = g.fromJson(req.body(), Disc[].class);
			
			return DiscsHandler.editDisc(discs, discPair, vms);
		});
		
		post("/addVM", (req, res) ->{
			res.type("application/json");
			VirtualMachine vm = g.fromJson(req.body(), VirtualMachine.class);
			Session ss = req.session(true);
			
			User u  =  ss.attribute("user");
			
			if(u == null || u.getRole() == Roles.CLIENT)
				halt(403, "Unauthorized operation!");
			
			String[] selectedDiscs = ss.attribute("selectedDiscs");
			Status status = new Status(VirtualMachineHandler.addVM(vms, vm, selectedDiscs, discs, u));
			return g.toJson(status);
		});
		
		get("/getVMs", (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			
			User u  =  ss.attribute("user");
			if(u == null)
				halt(403, "Unauthorized operation!");
			
			if(u.getRole() == Roles.SUPERADMIN)
				return g.toJson(vms.values());
			else
			{
				ArrayList<VirtualMachine> localMachines = new ArrayList<VirtualMachine>();
				for(VirtualMachine v : vms.values())
					if(v.getOrganisation().equals(u.getOrganization()))
						localMachines.add(v);
				return g.toJson(localMachines);
			}
		});
		
		delete("/deleteVM", (req, res) -> {
			String payload = req.body();
			Session ss = req.session(true);
			
			User u  =  ss.attribute("user");
			if(u == null || u.getRole() == Roles.CLIENT)
				halt(403, "Unauthorized operation!");
			
			VirtualMachine vm = g.fromJson(payload, VirtualMachine.class); 
			
			return VirtualMachineHandler.removeDisc(vms, vm, discs);
		});
		
		get("/getFreeDiscs", (req, res)->{
			res.type("application/json");
			
			ArrayList<Disc> freeDiscs = DiscsHandler.getFreeDiscs(discs);
			
			return g.toJson(freeDiscs);
		});
		
		post("/saveSelectedDiscs", (req, res) ->{
			String[] selectedDiscs = g.fromJson(req.body(), String[].class);
			System.out.println(req.body());
			if(selectedDiscs.length <= 1)
				selectedDiscs = new String[0];
			
			Session ss = req.session(true);
			ss.attribute("selectedDiscs", selectedDiscs);
			
			return true;
		});
		
		post("/getActiveDiscs", (req, res) ->{
			res.type("application/json");
			VirtualMachine vm = g.fromJson(req.body(), VirtualMachine.class);
			
			return g.toJson(VirtualMachineHandler.getActiveDiscs(discs, vm, vms));
		});
		
		post("/editVM", (req, res) ->{
			res.type("application/json");
			
			Session ss = req.session(true);
			
			User u  =  ss.attribute("user");
			if(u == null || u.getRole() == Roles.CLIENT)
				halt(403, "Unauthorized operation!");
			
			VirtualMachine[] vmPair = g.fromJson(req.body(), VirtualMachine[].class);
			
			String[] selectedDiscs = ss.attribute("selectedDiscs");
			
			return g.toJson(new Status(VirtualMachineHandler.editVM(vms, vmPair, discs, selectedDiscs)));
		});
		
		get("/initVM_Sliders", (req, res)->{
			res.type("application/json");
			return VM_CategoriesHandler.getMaxCores(vmcs);
		});
	
		post("/filterVMs", (req, res)->{
			res.type("application/json");
			VM_Filter vmf = g.fromJson(req.body(), VM_Filter.class);
			
			return (g.toJson(VirtualMachineHandler.getFilteredVMs(vms, vmf, vmcs)));
		});
		
		get("/goToVirtualMachines",  (req, res) -> {
			res.type("application/json");
			return "{\"result\":true}";			
		});
		
		after("/goToVirtualMachines", (req, res) -> {
			res.redirect("/", 301);
		});
		
		get("/goToNewVM",  (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || (u.getRole() != Roles.SUPERADMIN && u.getRole() != Roles.ADMIN)) {
				halt(403, "Unauthorized operation!");
			}
			
			return "{\"result\":true}";			
		});
		
		after("/goToNewVM", (req, res) -> {
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u != null && (u.getRole() == Roles.SUPERADMIN || u.getRole() == Roles.ADMIN)) {
				res.redirect("/html/addVM.html", 301);
			}
			
		});
		
		get("/goToEditDetailsVM",  (req, res) -> {
			res.type("application/json");
			
			return "{\"result\":true}";			
		});
		
		after("/goToEditDetailsVM", (req, res) -> {			
			res.redirect("/html/edit_detailVM.html", 301);			
		});
		
		post("/setNameDetailsVM", (req, res) -> {
			String[] data = g.fromJson(req.body(), String[].class);
			Session ss = req.session(true);
			
			ss.attribute("editVM_Name", data[0]);
			
			return true;
		});
		
		get("/getDetailsVM", (req, res) -> {
			
			Session ss = req.session(true);
			VirtualMachine vm = vms.get(ss.attribute("editVM_Name"));
			
			return g.toJson(vm);
		});
		
		get("/goToVM_Categories",  (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u == null || u.getRole() != Roles.SUPERADMIN)
				halt(403, "Unauthorized operation!");
			
			return g.toJson(new Status(true));			
		});
		
		after("/goToVM_Categories", (req, res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u != null || u.getRole() == Roles.SUPERADMIN)
				res.redirect("/html/viewVM_Categories.html", 301);
		});
		
		get("/goToAddVM_Categories",  (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u == null || u.getRole() != Roles.SUPERADMIN)
				halt(403, "Unauthorized operation!");
			
			return g.toJson(new Status(true));			
		});
		
		after("/goToAddVM_Categories", (req, res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u != null || u.getRole() == Roles.SUPERADMIN)
				res.redirect("/html/addVM_Category.html", 301);
		});
		
		get("/goToEditDetailsVMC",  (req, res) -> {
			res.type("application/json");
			
			return g.toJson(new Status(true));			
		});
		
		after("/goToEditDetailsVMC", (req, res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u != null || u.getRole() == Roles.SUPERADMIN)
				res.redirect("/html/edit_detailVMC.html", 301);
			
		});
		
		post("/setNameDetailsVMC", (req, res) -> {
			String[] data = g.fromJson(req.body(), String[].class);
			Session ss = req.session(true);
			
			ss.attribute("editVMC_Name", data[0]);
			
			return true;
		});
		
		get("/getDetailsVMC", (req, res) -> {
			
			Session ss = req.session(true);
			VirtualMachineCategory vmc = vmcs.get(ss.attribute("editVMC_Name"));
			
			return g.toJson(vmc);
		});
		
		get("/goToDiscs",  (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u == null)
				halt(403, "Unauthorized operation!");
			
			return g.toJson(new Status(true));			
		});
		
		after("/goToDiscs", (req, res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u != null)
				res.redirect("/html/viewDiscs.html", 301);
		});
		
		get("/goToAddDisc",  (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u == null || u.getRole() == Roles.CLIENT)
				halt(403, "Unauthorized operation!");
			
			return g.toJson(new Status(true));			
		});
		
		after("/goToAddDisc", (req, res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u != null && (u.getRole() == Roles.SUPERADMIN || u.getRole() == Roles.ADMIN))
				res.redirect("/html/addDisc.html", 301);
		});
		
		get("/goToEditDetailsDisc",  (req, res) -> {
			res.type("application/json");
			
			return g.toJson(new Status(true));			
		});
		
		after("/goToEditDetailsDisc", (req, res) -> {
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if(u != null)
				res.redirect("/html/edit_detailDisc.html", 301);
			
		});
		
		post("/setNameDetailsDisc", (req, res) -> {
			String[] data = g.fromJson(req.body(), String[].class);
			Session ss = req.session(true);
			
			ss.attribute("editDisc", data[0]);
			
			return true;
		});
		
		get("/getDetailsDisc", (req, res) -> {
			
			Session ss = req.session(true);
			Disc disc = discs.get(ss.attribute("editDisc"));
			
			return g.toJson(disc);
		});
		
		post("/toggleVMStatus", (req, res)->{
			res.type("application/json");
			String[] vm = g.fromJson(req.body(), String[].class);
			
			return g.toJson(new Status(VirtualMachineHandler.toggleStatus(vms, vm)));
		});
		
	}
	
	public static boolean checkEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
                "[a-zA-Z0-9_+&*-]+)*@" + 
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                "A-Z]{2,7}$"; 
                  
		Pattern pat = Pattern.compile(emailRegex); 
		if (email == null) 
			return false; 
		
		return pat.matcher(email).matches(); 
	}
}
