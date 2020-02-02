package logicForOrganizations;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Random;

import beans.Organization;

public class OrganizationHandler {

	public static String AddOrganization(HashMap<String, Organization> orgs, Organization org) {
		if (orgs.containsKey(org.getName())) {
			return "Name already exists";
		}
		
		try {
			setUpLogo(org);
		} catch (FileNotFoundException e) {
			return "File not found exception";
		} catch (IOException e) {
			return "Error uploading file";
		}
		
		orgs.put(org.getName(), org);
		return null;
	}
	
	private static String extractImageFromBytes(String logo, String imgPath) throws FileNotFoundException, IOException { 
		if(logo == null || logo.isEmpty()) {
			return "../logos/default.png";
		}
	  
		String newImgPath = "";
		String imgName = generateRandomString() + ".jpg";
		if(imgPath == null) {
			newImgPath = "./static/logos/" + imgName;
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
		} 
	  
		return "../logos/" + imgName; 
	}
	
	private static void setUpLogo(Organization org) throws FileNotFoundException, IOException {
		org.setLogo(extractImageFromBytes(org.getLogo(), null));
    }

	private static void changeLogo(String oldLogo, Organization newOrg) throws FileNotFoundException, IOException {
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
