package tinghua.hpc;

public class Request {
	private static int counter;
	private final int id = counter++;
	private int location;
	private int federationId;
	private int MMNId;
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
	public int getMMNId() {
		return MMNId;
	}
	public void setMMNId(int mMNId) {
		MMNId = mMNId;
	}
	public int getId() {
		return id;
	}

}
