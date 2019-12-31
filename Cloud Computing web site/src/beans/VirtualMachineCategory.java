package beans;

public class VirtualMachineCategory {
	private String name;
	private int numberOfCores;
	private int gbOfRAM;
	private int numberOfGPUCores;
	
	public VirtualMachineCategory()
	{
		
	}

	public VirtualMachineCategory(String name, int numberOfCores, int gbOfRAM, int numberOfGPUCores) {
		super();
		this.name = name;
		this.numberOfCores = numberOfCores;
		this.gbOfRAM = gbOfRAM;
		this.numberOfGPUCores = numberOfGPUCores;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfCores() {
		return numberOfCores;
	}

	public void setNumberOfCores(int numberOfCores) {
		this.numberOfCores = numberOfCores;
	}

	public int getGbOfRAM() {
		return gbOfRAM;
	}

	public void setGbOfRAM(int gbOfRAM) {
		this.gbOfRAM = gbOfRAM;
	}

	public int getNumberOfGPUCores() {
		return numberOfGPUCores;
	}

	public void setNumberOfGPUCores(int numberOfGPUCores) {
		this.numberOfGPUCores = numberOfGPUCores;
	}
	
	
}
