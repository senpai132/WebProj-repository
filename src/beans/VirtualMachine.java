package beans;

import java.util.ArrayList;
import java.util.HashMap;

public class VirtualMachine {
	private String name;
	private String organisation;
	private String category;
	private ArrayList<String> discs;
	private HashMap<String, String> activity;
	private boolean on;
	
	public VirtualMachine()
	{
		discs = new ArrayList<String>();
		activity = new HashMap<String, String>();
		on = false;
	}

	public VirtualMachine(String name, String organisation, String category, ArrayList<String> disks, HashMap<String, String> activity, boolean on) {
		super();
		this.name = name;
		this.category = category;
		this.discs = disks;
		this.activity = activity;
		this.organisation = organisation;
		this.on = on;
	}
	
	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public ArrayList<String> getDiscs() {
		return discs;
	}

	public void setDiscs(ArrayList<String> disks) {
		this.discs = disks;
	}

	public HashMap<String, String> getActivity() {
		return activity;
	}

	public void setActivity(HashMap<String, String> activity) {
		this.activity = activity;
	}

	@Override
	public String toString() {
		String discsStr = "";
		for(int i = 0; i < discs.size(); i++)
		{
			if(i == discs.size()-1)
				discsStr += discs.get(i);
			else
				discsStr += discs.get(i)+",";
		}
		
		String activityStr = "";
		for (HashMap.Entry<String, String> entry : activity.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    activityStr += key + "#" + value + ",";
		}
		if (activityStr.length() > 0) {
			activityStr = activityStr.substring(0, activityStr.length() - 1);
	    }
		
		return name + "|" + organisation + "|" + category + "|"
				+ discsStr + "|" + activityStr + "|" + on;
	}
	
	
}
