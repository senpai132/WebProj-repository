package rest;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;

import com.google.gson.Gson;

import beans.User;
import spark.Session;
import controller.LogInLogic;

public class SparkMainApp {
	
	private static Gson g = new Gson();

	public static void main(String[] args) throws Exception {
		port(8081);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		post("/KorisnikFrontPage", (req,res) -> {
			res.type("application/json");
			String payload = req.body();
			User u = g.fromJson(payload, User.class);
			
			//if(LogInLogic.Authenticate(u) == false)
				//return g.toJson(u);
			Session ss = req.session(true);
			User user = ss.attribute("user");
			if (user == null) {
				user = u;
				ss.attribute("user", user);
			}
			return g.toJson(user);
		});
		
		get("/uhvati", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User u = ss.attribute("user");
			return g.toJson(u);
		});
		
		get("/rest/demo/logout", (req, res) -> {
			res.type("application/json");
			Session ss = req.session(true);
			User user = ss.attribute("user");
			
			if (user != null) {
				ss.invalidate();
			}
			return true;
		});
	
	}

}
