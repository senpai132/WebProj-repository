package rest;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.delete;
import static spark.Spark.staticFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import beans.Disc;
import beans.User;
import beans.VirtualMachineCategory;
import controller.LogInLogic;
import logicForDiscs.DiscsHandler;
import logicForVM_Categories.VM_CategoriesHandler;
import spark.Session;

public class SparkMainApp {
	
	private static Gson g = new Gson();

	public static void main(String[] args) throws Exception {
		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		post("/login", (req,res) -> {
			String payload = req.body();
			User u = g.fromJson(payload, User.class);
			
			if(!LogInLogic.Authenticate(u))
				return "Invalid email or password";
			
			Session ss = req.session(true);
			User user = ss.attribute("user");
			if (user == null) {
				user = u;
				ss.attribute("user", user);
			}
			
			return true;
		});
		
		get("/preuzmiKorisnika", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			return g.toJson(u);
		});
		
		get("/logout", (req, res) -> {
			//res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			
			if (user != null) {
				ss.invalidate();
			}
			res.redirect("/index.html");
			return true;
		});
		
		post("/addVM_Category", (req, res) ->{
			res.type("application/json");
			VirtualMachineCategory vmc = g.fromJson(req.body(), VirtualMachineCategory.class);
			Session ss = req.session(true);
			ArrayList<VirtualMachineCategory> VM_Categories = ss.attribute("VMCategories"); 
			
			if(VM_Categories == null)
				ss.attribute("VMCategories", new ArrayList<VirtualMachineCategory>());
			VM_Categories = ss.attribute("VMCategories"); 

			return VM_CategoriesHandler.addVM_Category(VM_Categories, vmc);
		});
		
		get("/getVM_Categories", (req, res) -> {
			Session ss = req.session(true);
			ArrayList<VirtualMachineCategory> VM_Categories = ss.attribute("VMCategories"); 

			return g.toJson(VM_Categories);
		});
		
		get("/DeleteVM_Category/:vmc_name", (req, res) -> {
			String name = req.params("vmc_name");
			Session ss = req.session(true);
			ArrayList<VirtualMachineCategory> VM_Categories = ss.attribute("VMCategories"); 
			
			return VM_CategoriesHandler.deleteVM_Category(VM_Categories, name);
		});
		
		post("/EditVM_Category", (req, res) -> {
			String payload = req.body();
			VirtualMachineCategory vmc = g.fromJson(payload, VirtualMachineCategory.class);
			
			Session ss = req.session(true);
			ss.attribute("VM_CategoryToEdit", vmc);
			
			return true;
		});
		
		post("/SaveEditVM_Category", (req, res) -> {
			String payload = req.body();
			VirtualMachineCategory editedCat = g.fromJson(payload, VirtualMachineCategory.class);
			
			Session ss = req.session(true);
			VirtualMachineCategory catToBeEdited = ss.attribute("VM_CategoryToEdit");
			ArrayList<VirtualMachineCategory> VM_Categories = ss.attribute("VMCategories");

			return VM_CategoriesHandler.editVM_Category(VM_Categories, catToBeEdited, editedCat);
		});
		
		post("/addDisc", (req, res) ->{
			res.type("application/json");
			Disc disc = g.fromJson(req.body(), Disc.class);
			Session ss = req.session(true);
			HashMap<String, Disc> discs = ss.attribute("Discs"); 
			
			if(discs == null)
				ss.attribute("Discs", new HashMap<String, Disc>());
			discs = ss.attribute("Discs"); 

			return DiscsHandler.addDisc(discs, disc);
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
			
			return DiscsHandler.removeDisc(discs, disc);
		});
		
		post("/editDisc", (req, res) ->{
			res.type("application/json");
			Disc[] discPair = g.fromJson(req.body(), Disc[].class);
			
			Session ss = req.session(true);
			HashMap<String, Disc> discs = ss.attribute("Discs"); 
			
			return DiscsHandler.editDisc(discs, discPair);
		});
	
	}

}