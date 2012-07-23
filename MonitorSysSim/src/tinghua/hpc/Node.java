package tinghua.hpc;

public class Node {
	private static int counter;
	private final int id = counter++;
	private int location;
	private int federationId;
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getFederationId() {
		return federationId;
	}
	public void setFederationId(int federationId) {
		this.federationId = federationId;
	}
	public int getId() {
		return id;
	}
	
}
