package logicForVMs;

import java.util.HashMap;

import beans.VirtualMachine;

public class VirtualMachineHandler {

	public static boolean addVM(HashMap<String, VirtualMachine> vms, VirtualMachine vm) 
	{	
		if(vms.containsKey(vm.getName()))
			return false;
		
		vms.put(vm.getName(), vm);
		return true;
	}

	public static boolean removeDisc(HashMap<String, VirtualMachine> vms, VirtualMachine vm) 
	{
		if(!vms.containsKey(vm.getName()))
			return false;
		
		vms.remove(vm.getName());
		return true;
	}

}
