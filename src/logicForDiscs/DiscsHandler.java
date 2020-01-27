package logicForDiscs;

import java.util.HashMap;

import beans.Disc;

public class DiscsHandler {
	public static boolean addDisc(HashMap<String, Disc> discs, Disc disc)
	{
		if(discs.containsKey(disc.getName()))
			return false;
		
		discs.put(disc.getName(), disc);
		return true;
	}
	
	public static boolean removeDisc(HashMap<String, Disc> discs, Disc disc)
	{
		if(!discs.containsKey(disc.getName()))
			return false;
		
		discs.remove(disc.getName());
		return true;
	}

	public static boolean editDisc(HashMap<String, Disc> discs, Disc[] discPair) 
	{
		discs.remove(discPair[0].getName());
		discs.put(discPair[1].getName(), discPair[1]);
		
		return true;
	}
}
