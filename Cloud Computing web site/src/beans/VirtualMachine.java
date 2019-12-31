package beans;

import java.util.ArrayList;

public class VirtualMachine {
	private String name;
	private String category;
	private ArrayList<String> disks;
	private ArrayList<String> activity;
	
	public VirtualMachine()
	{
		
	}

	public VirtualMachine(String name, String category, ArrayList<String> disks, ArrayList<String> activity) {
		super();
		this.name = name;
		this.category = category;
		this.disks = disks;
		this.activity = activity;
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

	public ArrayList<String> getDisks() {
		return disks;
	}

	public void setDisks(ArrayList<String> disks) {
		this.disks = disks;
	}

	public ArrayList<String> getActivity() {
		return activity;
	}

	public void setActivity(ArrayList<String> activity) {
		this.activity = activity;
	}
	
	
}
