package beans;

public class Status {
	private boolean statusAkcije;
	
	public Status()
	{
		
	}
	
	public Status(boolean status)
	{
		this.statusAkcije = status;
	}

	public boolean isStatus() {
		return statusAkcije;
	}

	public void setStatus(boolean status) {
		this.statusAkcije = status;
	}
	
	
}
