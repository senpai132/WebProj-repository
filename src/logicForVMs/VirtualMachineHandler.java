package logicForVMs;

import java.util.HashMap;

import beans.Disc;
import beans.VirtualMachine;

public class VirtualMachineHandler {

	public static boolean addVM(HashMap<String, VirtualMachine> vms, VirtualMachine vm, String[] discNames, HashMap<String, Disc> discs) 
	{	
		if(vms.containsKey(vm.getName()))
			return false;
		
		//if(vm == null)
		//	System.out.println("VM null");
		if(discNames.length != 0)
			for(int i = 0; i < discNames.length; i++)
				if(discs.containsKey(discNames[i]))
					discs.get(discNames[i]).setParentVM(vm.getName());
		
		//for(String k : discs.keySet())
		//	System.out.println(k.equals(discNames[0]));
		//System.out.println("---");
		

				//discs.get(discNames[i]).setParentVM(vm.getName());
		
		vms.put(vm.getName(), vm);
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
		return true;
	}

}
