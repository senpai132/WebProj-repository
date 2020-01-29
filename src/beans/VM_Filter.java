package beans;

import java.util.ArrayList;

public class VM_Filter {
	private String name;
	private ArrayList<Integer> cores;
	private ArrayList<Integer> ram;
	private ArrayList<Integer> gpu;
	
	public VM_Filter()
	{
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Integer> getCores() {
		return cores;
	}

	public void setCores(ArrayList<Integer> cores) {
		this.cores = cores;
	}

	public ArrayList<Integer> getRam() {
		return ram;
	}

	public void setRam(ArrayList<Integer> ram) {
		this.ram = ram;
	}

	public ArrayList<Integer> getGpu() {
		return gpu;
	}

	public void setGpu(ArrayList<Integer> gpu) {
		this.gpu = gpu;
	}
}
