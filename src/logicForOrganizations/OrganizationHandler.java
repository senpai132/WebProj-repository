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
	
	public static String EditOrganization(HashMap<String, Organization> orgs, Organization org, String oldOrganization) {
		if (!orgs.containsKey(oldOrganization)) {
			return "Key does not exists";
		}
		
		Organization found = orgs.get(oldOrganization);
		found.setName(org.getName());
		found.setDescription(org.getDescription());

		try {
			found.setLogo(changeLogo(found.getLogo(), org.getLogo()));
		} catch (FileNotFoundException e) {
			return "File not found exception";
		} catch (IOException e) {
			return "Error uploading file";
		}

		return null;
	}
	
	private static String extractImageFromBytes(String logo, String imgPath) throws FileNotFoundException, IOException { 
		if(logo == null || logo.isEmpty()) {
			if (!imgPath.isEmpty() && imgPath != null) {
				return imgPath;
			}
			else {
				return "../logos/default.png";
			}
		}
	  
		String newImgPath = "";
		String imgName = generateRandomString() + ".jpg";
		if(imgPath == null || imgPath.contains("default.png")) {
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

	private static String changeLogo(String oldLogoPath, String newLogo) throws FileNotFoundException, IOException {
		return extractImageFromBytes(newLogo, oldLogoPath);
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
