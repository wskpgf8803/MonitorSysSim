package tinghua.hpc;

import java.util.ArrayList;

public class Node {
	public static int counter;
	private final int id = counter++;
	private int location;
	private int federationId;
	public ArrayList<Request> reqList = new ArrayList<Request>();
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
