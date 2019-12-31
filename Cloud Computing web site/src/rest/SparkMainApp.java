package rest;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;

public class SparkMainApp {

	public static void main(String[] args) throws Exception {
		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
	
		get("/testSpark", (req,res) -> {
			res.redirect("/Test.html");
			return null;
		});
	
	}

}
