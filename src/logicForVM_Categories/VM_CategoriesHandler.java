package logicForVM_Categories;

import java.util.ArrayList;
import java.util.HashMap;

import beans.VirtualMachineCategory;

public class VM_CategoriesHandler {

	public static boolean addVM_Category(HashMap<String, VirtualMachineCategory> VM_Categories, VirtualMachineCategory VM_category)
	{
		if(VM_Categories.containsKey(VM_category.getName()))
			return false;
		
		VM_Categories.put(VM_category.getName(), VM_category);
		return true;
	}
	
	public static boolean deleteVM_Category(HashMap<String, VirtualMachineCategory> VM_Categories, VirtualMachineCategory vmc/*, ArrayList<VirtualMachine> VMs*/)
	{
		//izvrsi proveru da li je postoji VM
		if(!VM_Categories.containsKey(vmc.getName()))
			return false;
		
		VM_Categories.remove(vmc.getName());
		return true;
	}
	
	public static boolean editVM_Category(HashMap<String, VirtualMachineCategory> VM_Categories, VirtualMachineCategory[] VMC_Pair)
	{				
		VM_Categories.remove(VMC_Pair[0].getName());
		VM_Categories.put(VMC_Pair[1].getName(), VMC_Pair[1]);
		
		return true;		
	}
}
