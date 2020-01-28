package dataFlow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import beans.Disc;
import beans.User;
import beans.VirtualMachine;
import beans.VirtualMachineCategory;
import enums.DiskTypes;
import enums.Roles;

public class ReadData {
	
	public static HashMap<String, User> readUsers(String filePath)
	{
		HashMap<String, User> users = new HashMap<String, User>();
		
		BufferedReader br;

		String line = "";
		try {
			br = new BufferedReader(new FileReader(filePath));
			while((line = br.readLine()) != null)
			{
				User u = new User();
				String[] parts = line.split("\\|");
				u.setEmail(parts[0]);
				u.setPassword(parts[1]);
				u.setName(parts[2]);
				u.setLastName(parts[3]);
				u.setOrganizacija(parts[4]);
				u.setRole(Roles.valueOf(parts[5]));
				users.put(u.getEmail(), u);
					
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return users;
	}
	
	public static HashMap<String, Disc> readDiscs()
	{
		HashMap<String, Disc> discs = new HashMap<String, Disc>(); 
		BufferedReader br;
		String fs = System.getProperty("file.separator");
		String line = "";
		try {
			br = new BufferedReader(new FileReader("."+fs+"data"+fs+"Discs.txt"));
			while((line = br.readLine()) != null)
			{
				String[] parts = line.split("\\|");
				Disc disc = new Disc(parts[0], DiskTypes.valueOf(parts[1]), Integer.parseInt(parts[2]), parts[3]);
				discs.put(disc.getName(), disc);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return discs;
	}
	
	public static HashMap<String, VirtualMachineCategory> readVM_Categories()
	{
		HashMap<String, VirtualMachineCategory> vmcs = new HashMap<String, VirtualMachineCategory>(); 
		BufferedReader br;
		String fs = System.getProperty("file.separator");
		String line = "";
		try {
			br = new BufferedReader(new FileReader("."+fs+"data"+fs+"VM_Categories.txt"));
			while((line = br.readLine()) != null)
			{
				String[] parts = line.split("\\|");
				VirtualMachineCategory vmc = new VirtualMachineCategory(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
				vmcs.put(vmc.getName(), vmc);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return vmcs;
	}
	
	public static HashMap<String, VirtualMachine> readVMs()
	{
		HashMap<String, VirtualMachine> vms = new HashMap<String, VirtualMachine>(); 
		BufferedReader br;
		String fs = System.getProperty("file.separator");
		String line = "";
		try {
			br = new BufferedReader(new FileReader("."+fs+"data"+fs+"VMs.txt"));
			while((line = br.readLine()) != null)
			{
				String[] parts = line.split("\\|");
				
				String[] discsString = parts[3].split("\\,");
				ArrayList<String> discs = new ArrayList<String>();
				for(String s : discsString)
					discs.add(s);
				
				String[] activitiesString  = parts[4].split("\\,");
				HashMap<String, String> activities = new HashMap<String, String>();
				for(String s : activitiesString)
					activities.put(s.split("\\#")[0], s.split("\\#")[1]);
				
				VirtualMachine vm = new VirtualMachine(parts[0], parts[1], parts[2], discs, activities, parts[5].equals("true") ? true : false);
				vms.put(vm.getName(), vm);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return vms;
	}

}
