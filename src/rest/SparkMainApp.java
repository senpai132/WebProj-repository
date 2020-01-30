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
		
		//String fs = System.getProperty("file.separator");
		//users = DataManipulation.readUsers("."+fs+"data"+fs+"users.txt");
		orgs = new HashMap<String, Organization>();
		users = new HashMap<String, User>();
		vms = ReadData.readVMs();
		discs = ReadData.readDiscs();
		vmcs = ReadData.readVM_Categories();
		
		if (users.size() == 0) {
			users.put("s@s.com", new User("s@s.com", "s", "s", "s", null, Roles.SUPERADMIN));
			users.put("c@c.com", new User("c@c.com", "c", "c", "c", "ime1", Roles.CLIENT));
			users.put("a@a.com", new User("a@a.com", "a", "a", "a", "ime1", Roles.ADMIN));
		}
		
		orgs.put("ime1", new Organization("ime1", "opis1", "logo1"));
		orgs.put("ime2", new Organization("ime2", "opis2", "logo2"));
		orgs.put("ime3", new Organization("ime3", "opis3", "logo3"));
			
		post("/rest/login", (req,res) -> {
			String payload = req.body();
			User u = g.fromJson(payload, User.class);
			String result = "false";
			String message = "\"Valid\"";
			
			if (!checkEmail(u.getEmail())) {
				message = "\"Invalid email\"";
			}
			else if (u.getPassword() == null || u.getPassword().isEmpty()) {
				message = "\"Password cannot be empty\"";
			}
			else {
				User loaded = users.get(u.getEmail());
				
				if (loaded != null && loaded.getPassword().equals(u.getPassword())) {
					Session ss = req.session(true);
					User user = ss.attribute("user");
					if (user == null) {
						ss.attribute("user", loaded);
						result = "true";
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
			
			if (user != null) {
				ss.invalidate();
			}

			return "{\"result\": true}";
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
			String client = "false";
			String admin = "false";
			String superAdmin = "false";
				
			if (u != null) {
				logged = "true";
				switch(u.getRole()) {
					case SUPERADMIN:
						superAdmin = "true";
						break;
						
					case ADMIN:
						admin = "true";
						break;
						
					case CLIENT:
						client = "true";
						break;
				}
			}
			
			return "{\"logged\": " + logged + ", \"client\": " + client + ", \"admin\": " + admin + ", \"superadmin\": " + superAdmin + "}";
		});
		
		get("/rest/getUsers", (req, res) -> {
			res.type("application/json");
			
			Session ss = req.session(true);
			User u = ss.attribute("user");

			ArrayList<User> curr = new ArrayList<User>();
			if (u.getRole() == Roles.SUPERADMIN) {
				
				for(User user : users.values()) {
					curr.add(user);
				}
			}
			else {
				for (String email : orgs.get(u.getOrganization()).getUsers()) {
					curr.add(users.get(email));
				}
			}

			return "{\"users\": " + g.toJson(curr) + "}";
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
		
		get("/rest/getOrganizations", (req, res) -> {
			res.type("application/json");

			ArrayList<String> curr = new ArrayList<String>();
			for (String org : orgs.keySet()) {
				curr.add(org);
			}

			return "{\"orgs\": " + g.toJson(curr) + "}";
		});
		
		post("/rest/addUser", (req,res) -> {
			String payload = req.body();
			User u = g.fromJson(payload, User.class);
			String result = "false";
			String message = "";
			
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
		
		get("/rest/goToOrganizations", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() != Roles.SUPERADMIN) {
				halt(403, "Unauthorized operation!");
			}
			
			return "{\"result\":false}";
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
			
			ArrayList<Organization> curr = new ArrayList<Organization>();
			
			for (Organization org : orgs.values()) {
				curr.add(org);
			}
			
			return g.toJson(curr);
		});
		
		get("/rest/goToNewOrganization", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			
			if (u == null || u.getRole() != Roles.SUPERADMIN) {
				halt(403, "Unauthorized operation!");
			}
			
			return "{\"result\":false}";
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
            
            if (org.getName() == null || org.getName().isEmpty()) {
				result = "false";
				message = "\"Name is reqired\"";
			}
            if (org.getDescription() == null || org.getDescription().isEmpty()) {
				result = "false";
				message = "\"Description is reqired\"";
			}
            
            if (!OrganizationHandler.AddOrganization(orgs, org)) {
            	message = "\"Name already exists\"";
            	result = "false";
            }

            return "{\"result\": " + result + ", \"message\": " + message + "}";
		});
		
		get("/preuzmiKorisnika", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			return g.toJson(u);
		});

		post("/addVM_Category", (req, res) ->{
			res.type("application/json");
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

			return g.toJson(vmcs.values());
		});
		
		delete("/DeleteVM_Category", (req, res) -> {
			VirtualMachineCategory vmc = g.fromJson(req.body(), VirtualMachineCategory.class);
			
			return VM_CategoriesHandler.deleteVM_Category(vmcs, vmc, vms);
		});
		
		post("/EditVM_Category", (req, res) -> {
			res.type("application/json");
			VirtualMachineCategory[] VMC_Pair = g.fromJson(req.body(), VirtualMachineCategory[].class);
			
			return VM_CategoriesHandler.editVM_Category(vmcs, VMC_Pair, vms);
		});
		
		post("/addDisc", (req, res) ->{
			res.type("application/json");
			Disc disc = g.fromJson(req.body(), Disc.class);

			return DiscsHandler.addDisc(discs, disc, vms);
		});
		
		get("/getDiscs", (req, res) -> {
			res.type("application/json");
			
			return g.toJson(discs.values());
		});
		
		delete("/deleteDisc", (req, res) -> {
			String payload = req.body();
			Disc disc = g.fromJson(payload, Disc.class);
			
			return DiscsHandler.removeDisc(discs, disc, vms);
		});
		
		post("/editDisc", (req, res) ->{
			res.type("application/json");
			Disc[] discPair = g.fromJson(req.body(), Disc[].class);
			
			return DiscsHandler.editDisc(discs, discPair, vms);
		});
		
		post("/addVM", (req, res) ->{
			res.type("application/json");
			VirtualMachine vm = g.fromJson(req.body(), VirtualMachine.class);
			Session ss = req.session(true);
			
			String[] selectedDiscs = ss.attribute("selectedDiscs");
			Status status = new Status(VirtualMachineHandler.addVM(vms, vm, selectedDiscs, discs));
			return g.toJson(status);
		});
		
		get("/getVMs", (req, res) -> {
			res.type("application/json");
			
			return g.toJson(vms.values());
		});
		
		delete("/deleteVM", (req, res) -> {
			String payload = req.body();
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
			
			if(selectedDiscs.length < 1)
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
			VirtualMachine[] vmPair = g.fromJson(req.body(), VirtualMachine[].class);
			Session ss = req.session(true);
			String[] selectedDiscs = ss.attribute("selectedDiscs");
			
			return VirtualMachineHandler.editVM(vms, vmPair, discs, selectedDiscs);
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
			res.redirect("/html/dummyWorkWithVM.html", 301);
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
