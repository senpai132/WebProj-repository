package logicForVM_Categories;

import java.util.ArrayList;

import beans.VirtualMachineCategory;

public class VM_CategoriesHandler {

	public static boolean addVM_Category(ArrayList<VirtualMachineCategory> VM_Categories, VirtualMachineCategory VM_category)
	{
		for(VirtualMachineCategory vcm : VM_Categories)
			if(vcm.getName().equals(VM_category.getName()))
				return false;
		
		VM_Categories.add(VM_category);
		return true;
	}
	
	public static boolean deleteVM_Category(ArrayList<VirtualMachineCategory> VM_Categories, String name/*, ArrayList<VirtualMachine> VMs*/)
	{
		VirtualMachineCategory VM_Category = null;
		
		//Proveri da li postoji VM koja je te kategorije
		
		for(VirtualMachineCategory vmc : VM_Categories)
			if(vmc.getName().equals(name))
				VM_Category = vmc;
				
		if(VM_Category != null)
		{
			VM_Categories.remove(VM_Category);
			return true;
		}
		
		return false;
	}
	
	public static boolean editVM_Category(ArrayList<VirtualMachineCategory> VM_Categories, VirtualMachineCategory toBeEdited, VirtualMachineCategory editCat)
	{				
		for(VirtualMachineCategory vmc : VM_Categories)
			if(vmc.getName().equals(toBeEdited.getName()))
			{
				vmc.setGbOfRAM(editCat.getGbOfRAM());
				vmc.setNumberOfCores(editCat.getNumberOfCores());
				vmc.setNumberOfGPUCores(editCat.getNumberOfGPUCores());
				vmc.setName(editCat.getName());
				return true;
			}
		
		return false;
				
	}
}
