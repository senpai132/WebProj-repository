package logicForVM_Categories;

import java.util.ArrayList;
import java.util.HashMap;

import beans.VirtualMachine;
import beans.VirtualMachineCategory;

public class VM_CategoriesHandler {

	public static boolean addVM_Category(HashMap<String, VirtualMachineCategory> VM_Categories, VirtualMachineCategory VM_category)
	{
		if(VM_Categories.containsKey(VM_category.getName()))
			return false;
		
		VM_Categories.put(VM_category.getName(), VM_category);
		return true;
	}
	
	public static boolean deleteVM_Category(HashMap<String, VirtualMachineCategory> VM_Categories, VirtualMachineCategory vmc, HashMap<String, VirtualMachine> vms)
	{
		for(VirtualMachine vm : vms.values())
			if(vm.getCategory().equals(vmc.getName()))
				return false;
		
		
		if(!VM_Categories.containsKey(vmc.getName()))
			return false;
		
		VM_Categories.remove(vmc.getName());
		return true;
	}
	
	public static boolean editVM_Category(HashMap<String, VirtualMachineCategory> VM_Categories, VirtualMachineCategory[] VMC_Pair, HashMap<String, VirtualMachine> vms)
	{		
		if(VM_Categories.containsKey(VMC_Pair[1].getName()) && VMC_Pair[0].getName().equals(VMC_Pair[1].getName()) == false)
			return false;
		
		for(VirtualMachine vm : vms.values())
			if(vm.getCategory().equals(VMC_Pair[0].getName()))
				vm.setCategory(VMC_Pair[1].getName());
		
		VM_Categories.remove(VMC_Pair[0].getName());
		VM_Categories.put(VMC_Pair[1].getName(), VMC_Pair[1]);
		
		return true;		
	}
	
	public static ArrayList<Integer> getMaxCores(HashMap<String, VirtualMachineCategory> vmcs)
	{
		ArrayList<Integer> maxCores = new ArrayList<Integer>();
		Integer maxC = 0;
		Integer maxRAM = 0;
		Integer maxGPU = 0;
		
		for(VirtualMachineCategory vmc : vmcs.values())
		{
			if(vmc.getNumberOfCores() > maxC)
				maxC = vmc.getNumberOfCores();
			if(vmc.getGbOfRAM() > maxRAM)
				maxRAM = vmc.getGbOfRAM();
			if(vmc.getNumberOfGPUCores() > maxGPU)
				maxGPU = vmc.getNumberOfGPUCores();
		}
		maxCores.add(maxC);
		maxCores.add(maxRAM);
		maxCores.add(maxGPU);
		return maxCores;
	}
}
