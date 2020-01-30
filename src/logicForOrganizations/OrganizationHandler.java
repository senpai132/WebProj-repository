package logicForOrganizations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Random;

import beans.Organization;

public class OrganizationHandler {

	public static boolean AddOrganization(HashMap<String, Organization> orgs, Organization org) {
		if (orgs.containsKey(org.getName())) {
			return false;
		}
		
		setUpLogo(org);
		
		orgs.put(org.getName(), org);
		return true;
	}
	
	private static String extractImageFromBytes(String logo, String imgPath) { 
		if(logo == null) {
			return imgPath;
		}
	  
		String newImgPath = ""; 
		if(imgPath == null) {
			newImgPath = "./data/" + generateRandomString() + ".jpg";
		}
			
		else 
			newImgPath = imgPath;
  
		try(OutputStream writer = new FileOutputStream(newImgPath)) {
			StringReader reader = new StringReader(logo); 
			int k = 0; 
			while((k = reader.read()) != -1) { 
				writer.write(k); 
			}
			
			System.out.println("Image extracted successfully"); 
		} catch (IOException ioe) { 
			ioe.printStackTrace();
			System.out.println("Image extracted unsuccessfully"); 
		}
	  
		return newImgPath; 
	}
	
	private static void setUpLogo(Organization org) {
		org.setLogo(extractImageFromBytes(org.getLogo(), null));
    }

	private static void changeLogo(String oldLogo, Organization newOrg) {
		newOrg.setLogo(extractImageFromBytes(newOrg.getLogo(), oldLogo));
    }
    
    private static String generateRandomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 5;
        Random random = new Random();
     
        String generatedString = random.ints(leftLimit, rightLimit + 1)
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
     
        return generatedString;
    }
}
