package logicForVMs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import beans.Disc;
import beans.User;
import beans.VM_Filter;
import beans.VirtualMachine;
import beans.VirtualMachineCategory;
import dataFlow.WriteData;
import enums.Roles;

public class VirtualMachineHandler {

	public static boolean addVM(HashMap<String, VirtualMachine> vms, VirtualMachine vm, String[] discNames, HashMap<String, Disc> discs, User u) 
	{	
		if(vms.containsKey(vm.getName()))
			return false;
		ArrayList<String> vmDiscs = new ArrayList<String>();
		
		//if(discNames != null)
		//{
			System.out.println("Usao");
			if(discNames.length != 0 )
				for(int i = 0; i < discNames.length-1; i++)
				{
					vmDiscs.add(discNames[i]);
					
					if(discs.containsKey(discNames[i]))
						discs.get(discNames[i]).setParentVM(vm.getName());
				}
		//}
		
				
		
		vm.setDiscs(vmDiscs);
		if(u.getRole() == Roles.ADMIN)
			vm.setOrganisation(u.getOrganization());
		vms.put(vm.getName(), vm);
		for(VirtualMachine m : vms.values())
			System.out.println(m);
		WriteData.writeVMs(vms);
		WriteData.writeDiscs(discs);
		return true;
	}

	public static boolean removeDisc(HashMap<String, VirtualMachine> vms, VirtualMachine vm, HashMap<String, Disc> discs) 
	{
		if(!vms.containsKey(vm.getName()))
			return false;
		
		for(Disc d : discs.values())
			if(d.getParentVM().equals(vm.getName()))
				d.setParentVM("");
		
		vms.remove(vm.getName());
		for(VirtualMachine m : vms.values())
			System.out.println(m);
		WriteData.writeVMs(vms);
		WriteData.writeDiscs(discs);
		return true;
	}

	public static ArrayList<Disc> getActiveDiscs(HashMap<String, Disc> discs, VirtualMachine vm, HashMap<String, VirtualMachine> vms) {
		ArrayList<Disc> activeDiscs = new ArrayList<Disc>();
		
		for(String d : vms.get(vm.getName()).getDiscs())
			activeDiscs.add(discs.get(d));
		
		return activeDiscs;
	}

	public static boolean editVM(HashMap<String, VirtualMachine> vms, VirtualMachine[] vmPair, HashMap<String, Disc> discs, String[] selectedDiscs) {
		
		if(vms.containsKey(vmPair[1].getName()) && vmPair[0].getName().equals(vmPair[1].getName()) == false)
			return false;
		
		for(Disc d : discs.values())
			if(d.getParentVM().equals(vmPair[0].getName()))
				d.setParentVM("");
		
		vms.remove(vmPair[0].getName());
		
		ArrayList<String> newDiscs = new ArrayList<String>();
		for(int i = 0; i < selectedDiscs.length-1; i++)
		{
			newDiscs.add(selectedDiscs[i]);
			discs.get(selectedDiscs[i]).setParentVM(vmPair[1].getName());
		}
		
		vmPair[1].setDiscs(newDiscs);
		
		vms.put(vmPair[1].getName(), vmPair[1]);
		WriteData.writeVMs(vms);
		WriteData.writeDiscs(discs);
		return true;
	}
	
	public static ArrayList<VirtualMachine> getFilteredVMs(HashMap<String, VirtualMachine> vms, VM_Filter filter, HashMap<String, VirtualMachineCategory> vmcs) {
		ArrayList<VirtualMachine> vmsFiltered = new ArrayList<VirtualMachine>();
		
		if(vms.containsKey(filter.getName()))
		{
			System.out.println("Nasao po imenu");
			vmsFiltered.add(vms.get(filter.getName()));
			return vmsFiltered;
		}
		
		for(VirtualMachine vm : vms.values())
		{
			int numberOfCores = vmcs.get(vm.getCategory()).getNumberOfCores();
			int maxCores = filter.getCores().get(1);
			int minCores = filter.getCores().get(0);
			
			int RAM = vmcs.get(vm.getCategory()).getGbOfRAM();
			int maxRam = filter.getRam().get(1);
			int minRam = filter.getRam().get(0);
			
			int GPU = vmcs.get(vm.getCategory()).getNumberOfGPUCores();
			int maxGPU = filter.getGpu().get(1);
			int minGPU = filter.getGpu().get(0);
			
			if(numberOfCores >= minCores && RAM >= minRam && GPU >= minGPU && numberOfCores <= maxCores && RAM <= maxRam && GPU <= maxGPU)
				vmsFiltered.add(vm);
		}
		
		return vmsFiltered;
	}

	public static boolean toggleStatus(HashMap<String, VirtualMachine> vms, String[] vm) {
		
		if(vms.get(vm[0]).isOn() == true)
		{
			vms.get(vm[0]).setOn(false);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();
			for (HashMap.Entry<String, String> entry : vms.get(vm[0]).getActivity().entrySet()) {
			    if(entry.getValue().equals(""));
			    	entry.setValue(dtf.format(now));
			}
			
		}
		else
		{
			vms.get(vm[0]).setOn(true);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now();
			vms.get(vm[0]).getActivity().put(dtf.format(now), "");
		}
		
		for(VirtualMachine v: vms.values())
			System.out.println(v);
		
		return true;
	}

}
