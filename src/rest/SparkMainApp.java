package rest;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.after;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;

import beans.Disc;
import beans.User;
import beans.VirtualMachine;
import beans.VirtualMachineCategory;
<<<<<<< HEAD
import controller.LogInLogic;
import dataFlow.ReadData;
=======
import controller.DataManipulation;
import enums.Roles;
>>>>>>> 60b46c9136d851ddb36674d64cdf1c8614def328
import logicForDiscs.DiscsHandler;
import logicForVM_Categories.VM_CategoriesHandler;
import logicForVMs.VirtualMachineHandler;
import spark.Session;

public class SparkMainApp {

	private static Gson g = new Gson();

	public static HashMap<String, User> users;

	public static void main(String[] args) throws Exception {
		port(8083);

		staticFiles.externalLocation(new File("./static").getCanonicalPath());

		String fs = System.getProperty("file.separator");
		//users = DataManipulation.ReadUsers("."+fs+"data"+fs+"users.txt");
		users = new HashMap<String, User>();

		if (users.size() == 0)
			users.put("admin@admin.com", new User("admin@admin.com", "admin", "admin", "admin", null, Roles.SUPERADMIN));


<<<<<<< HEAD
		get("/initApp", (req, res) ->{
			res.type("application/json");

			Session ss = req.session(true);

			HashMap<String, VirtualMachine> vms = ss.attribute("VMs");
			if(vms == null)
			{
				vms = ReadData.readVMs();
				ss.attribute("VMs", vms);
			}

			HashMap<String, Disc> discs = ss.attribute("Discs");
			if(discs == null)
			{
				discs = ReadData.readDiscs();
				ss.attribute("Discs", discs);
			}

			HashMap<String, VirtualMachineCategory> vmcs = ss.attribute("VMCategories");
			if(vmcs == null)
			{
				vmcs = ReadData.readVM_Categories();
				ss.attribute("VMCategories", vmcs);
			}

			User u = ss.attribute("user");

			return g.toJson(u);
		});
=======

		post("/rest/login", (req,res) -> {
>>>>>>> 60b46c9136d851ddb36674d64cdf1c8614def328
			String payload = req.body();
			User u = g.fromJson(payload, User.class);
			String message = "false";

			User loaded = users.get(u.getEmail());

			if (loaded != null && loaded.getPassword().equals(u.getPassword())) {
				Session ss = req.session(true);
				User user = ss.attribute("user");
				if (user == null) {
					user = u;
					ss.attribute("user", user);
					message = "true";
				}
			}

			return "{\"message\": " + message + "}";
		});

		get("/rest/logout", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");

			if (user != null) {
				ss.invalidate();
			}

			return "{\"message\": true}";
		});

		after("/rest/logout", (req, res) -> {
			Session ss = req.session(true);
			User user = ss.attribute("user");

			if (user == null) {
				res.redirect("/html/login.html", 301);
			}
		});

		get("/preuzmiKorisnika", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			return g.toJson(u);
		});

		get("/rest/isLoggedIn", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			String loggedIn = "true";
			if (u == null)
				loggedIn = "false";

			return "{\"loggedIn\":" + loggedIn + "}";
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

			Session ss = req.session(true);
			HashMap<String, VirtualMachineCategory> VM_Categories = ss.attribute("VMCategories");

			if(VM_Categories == null)
				ss.attribute("VMCategories", new HashMap<String, VirtualMachineCategory>());
			VM_Categories = ss.attribute("VMCategories");

			return VM_CategoriesHandler.addVM_Category(VM_Categories, vmc);
		});

		get("/getVM_Categories", (req, res) -> {
			Session ss = req.session(true);
			HashMap<String, VirtualMachineCategory> VM_Categories = ss.attribute("VMCategories");

			return g.toJson(VM_Categories.values());
		});

		delete("/DeleteVM_Category", (req, res) -> {
			Session ss = req.session(true);
			VirtualMachineCategory vmc = g.fromJson(req.body(), VirtualMachineCategory.class);
			HashMap<String, VirtualMachineCategory> VM_Categories = ss.attribute("VMCategories");

			return VM_CategoriesHandler.deleteVM_Category(VM_Categories, vmc);
		});

		post("/EditVM_Category", (req, res) -> {
			res.type("application/json");
			VirtualMachineCategory[] VMC_Pair = g.fromJson(req.body(), VirtualMachineCategory[].class);

			Session ss = req.session(true);
			HashMap<String, VirtualMachineCategory> vmcs = ss.attribute("VMCategories");

			return VM_CategoriesHandler.editVM_Category(vmcs, VMC_Pair);
		});

		post("/addDisc", (req, res) ->{
			res.type("application/json");
			Disc disc = g.fromJson(req.body(), Disc.class);
			Session ss = req.session(true);
			HashMap<String, Disc> discs = ss.attribute("Discs");

			if(discs == null)
				ss.attribute("Discs", new HashMap<String, Disc>());
			discs = ss.attribute("Discs");

			HashMap<String, VirtualMachine> vms = ss.attribute("VMs");

			return DiscsHandler.addDisc(discs, disc, vms);
		});

		get("/getDiscs", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			HashMap<String, Disc> discs = ss.attribute("Discs");

			return g.toJson(discs.values());
		});

		delete("/deleteDisc", (req, res) -> {
			String payload = req.body();
			Disc disc = g.fromJson(payload, Disc.class);
			Session ss = req.session(true);
			HashMap<String, Disc> discs = ss.attribute("Discs");

			HashMap<String, VirtualMachine> vms = ss.attribute("VMs");

			return DiscsHandler.removeDisc(discs, disc, vms);
		});

		post("/editDisc", (req, res) ->{
			res.type("application/json");
			Disc[] discPair = g.fromJson(req.body(), Disc[].class);

			Session ss = req.session(true);
			HashMap<String, Disc> discs = ss.attribute("Discs");

			HashMap<String, VirtualMachine> vms = ss.attribute("VMs");

			return DiscsHandler.editDisc(discs, discPair, vms);
		});

		post("/addVM", (req, res) ->{
			res.type("application/json");
			VirtualMachine vm = g.fromJson(req.body(), VirtualMachine.class);
			Session ss = req.session(true);
			HashMap<String, VirtualMachine> vms = ss.attribute("VMs");

			if(vms == null)
				ss.attribute("VMs", new HashMap<String, Disc>());
			vms = ss.attribute("VMs");

			return VirtualMachineHandler.addVM(vms, vm);
		});

		get("/getVMs", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			HashMap<String, VirtualMachine> vms = ss.attribute("VMs");

			return g.toJson(vms.values());
		});

		delete("/deleteVM", (req, res) -> {
			String payload = req.body();
			VirtualMachine vm = g.fromJson(payload, VirtualMachine.class);
			Session ss = req.session(true);
			HashMap<String, VirtualMachine> vms = ss.attribute("VMs");

			return VirtualMachineHandler.removeDisc(vms, vm);
		});

	}

}
