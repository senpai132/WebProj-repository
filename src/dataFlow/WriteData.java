package dataFlow;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import beans.Disc;
import beans.Organization;
import beans.User;
import beans.VirtualMachine;
import beans.VirtualMachineCategory;

public class WriteData {
	
	public static void writeUsers(HashMap<String, User> users)
	{
		String fs = System.getProperty("file.separator");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("."+fs+"data"+fs+"Users.txt"));
			for(User user : users.values())
				pw.println(user);
			
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeOrganizations(HashMap<String, Organization> orgs)
	{
		String fs = System.getProperty("file.separator");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("."+fs+"data"+fs+"Organizations.txt"));
			for(Organization org : orgs.values())
				pw.println(org);
			
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeVMCs(HashMap<String, VirtualMachineCategory> vmcs)
	{
		String fs = System.getProperty("file.separator");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("."+fs+"data"+fs+"VM_Categories.txt"));
			for(VirtualMachineCategory vmc : vmcs.values())
				pw.println(vmc);
			
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeDiscs(HashMap<String, Disc> discs)
	{
		String fs = System.getProperty("file.separator");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("."+fs+"data"+fs+"Discs.txt"));
			for(Disc vmc : discs.values())
				pw.println(vmc);
			
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void writeVMs(HashMap<String, VirtualMachine> vms)
	{
		String fs = System.getProperty("file.separator");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter("."+fs+"data"+fs+"VMs.txt"));
			for(VirtualMachine vmc : vms.values())
				pw.println(vmc);
			
			pw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
