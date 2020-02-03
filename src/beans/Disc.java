package beans;

import enums.DiskTypes;

public class Disc {
	private String name;
	private DiskTypes type;
	private int capacity;
	private String parentVM;
	
	public Disc()
	{
		
	}

	public Disc(String name, DiskTypes type, int capacity, String parentVM) {
		super();
		this.name = name;
		this.type = type;
		this.capacity = capacity;
		this.parentVM = parentVM;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DiskTypes getType() {
		return type;
	}

	public void setType(DiskTypes type) {
		this.type = type;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getParentVM() {
		return parentVM;
	}

	public void setParentVM(String parentVM) {
		this.parentVM = parentVM;
	}

	@Override
	public String toString() {
		return name + "|" + parentVM + "|" + capacity + "|" + type;
	}
	
	
}
