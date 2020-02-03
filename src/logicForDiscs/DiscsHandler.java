package logicForDiscs;

import java.util.ArrayList;
import java.util.HashMap;

import beans.Disc;
import beans.VirtualMachine;
import dataFlow.WriteData;

public class DiscsHandler {
	public static boolean addDisc(HashMap<String, Disc> discs, Disc disc, HashMap<String, VirtualMachine> vms)
	{
		if(discs.containsKey(disc.getName()))
			return false;
		
		if(vms.containsKey(disc.getParentVM()))
			vms.get(disc.getParentVM()).getDiscs().add(disc.getName());
		
		discs.put(disc.getName(), disc);
		WriteData.writeDiscs(discs);
		for(Disc m : discs.values())
			System.out.println(m);
		WriteData.writeVMs(vms);
		return true;
	}
	
	public static boolean removeDisc(HashMap<String, Disc> discs, Disc disc, HashMap<String, VirtualMachine> vms)
	{
		if(!discs.containsKey(disc.getName()))
			return false;
		
		vms.get(disc.getParentVM()).getDiscs().remove(disc.getName());
		
		discs.remove(disc.getName());
		for(Disc m : discs.values())
			System.out.println(m);
		WriteData.writeDiscs(discs);
		WriteData.writeVMs(vms);
		return true;
	}

	public static boolean editDisc(HashMap<String, Disc> discs, Disc[] discPair, HashMap<String, VirtualMachine> vms) 
	{
		if(discs.containsKey(discPair[1].getName()) && discPair[0].getName().equals(discPair[1].getName()) == false)
			return false;
		
		if(vms.containsKey(discPair[0].getParentVM()))
			vms.get(discPair[0].getParentVM()).getDiscs().remove(discPair[0].getName());
		if(vms.containsKey(discPair[1].getParentVM()))
			vms.get(discPair[1].getParentVM()).getDiscs().add(discPair[1].getName());
		
		for (VirtualMachine value : vms.values()) {
		    System.out.println(value);
		}
		
		discs.remove(discPair[0].getName());
		discs.put(discPair[1].getName(), discPair[1]);
		for(Disc m : discs.values())
			System.out.println(m);
		WriteData.writeDiscs(discs);
		WriteData.writeVMs(vms);
		return true;
	}

	public static ArrayList<Disc> getFreeDiscs(HashMap<String, Disc> discs) {
		ArrayList<Disc> freeDiscs = new ArrayList<Disc>();
		
		for(Disc d : discs.values())
			if(d.getParentVM().equals(""))
				freeDiscs.add(d);
		
		return freeDiscs;
	}
}
