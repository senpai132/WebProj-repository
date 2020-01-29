package logicForVMs;

import java.util.ArrayList;
import java.util.HashMap;

import beans.Disc;
import beans.VirtualMachine;

public class VirtualMachineHandler {

	public static boolean addVM(HashMap<String, VirtualMachine> vms, VirtualMachine vm, String[] discNames, HashMap<String, Disc> discs) 
	{	
		if(vms.containsKey(vm.getName()))
			return false;
		ArrayList<String> vmDiscs = new ArrayList<String>();
		
		if(discNames.length != 0)
			for(int i = 0; i < discNames.length-1; i++)
			{
				vmDiscs.add(discNames[i]);
				
				if(discs.containsKey(discNames[i]))
					discs.get(discNames[i]).setParentVM(vm.getName());
			}
				
		
		vm.setDiscs(vmDiscs);
		vms.put(vm.getName(), vm);
		for(VirtualMachine m : vms.values())
			System.out.println(m);
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
		
		return true;
	}

}
