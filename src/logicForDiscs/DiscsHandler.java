package logicForDiscs;

import java.util.HashMap;

import beans.Disc;
import beans.VirtualMachine;

public class DiscsHandler {
	public static boolean addDisc(HashMap<String, Disc> discs, Disc disc, HashMap<String, VirtualMachine> vms)
	{
		if(discs.containsKey(disc.getName()))
			return false;
		
		vms.get(disc.getParentVM()).getDiscs().add(disc.getName());
		
		discs.put(disc.getName(), disc);
		return true;
	}
	
	public static boolean removeDisc(HashMap<String, Disc> discs, Disc disc, HashMap<String, VirtualMachine> vms)
	{
		if(!discs.containsKey(disc.getName()))
			return false;
		
		vms.get(disc.getParentVM()).getDiscs().remove(disc.getName());
		
		discs.remove(disc.getName());
		return true;
	}

	public static boolean editDisc(HashMap<String, Disc> discs, Disc[] discPair, HashMap<String, VirtualMachine> vms) 
	{
		vms.get(discPair[0].getParentVM()).getDiscs().remove(discPair[0].getName());
		vms.get(discPair[1].getParentVM()).getDiscs().add(discPair[1].getName());
		
		for (VirtualMachine value : vms.values()) {
		    System.out.println(value);
		}
		
		discs.remove(discPair[0].getName());
		discs.put(discPair[1].getName(), discPair[1]);
		
		return true;
	}
}
