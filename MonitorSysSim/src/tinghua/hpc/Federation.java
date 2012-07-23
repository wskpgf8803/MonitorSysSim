package tinghua.hpc;

import java.util.ArrayList;

public class Federation {
	private static int counter;
	private int id = counter++;
	private int num_nodes;
	private int num_MMNs;
	public ArrayList<Federation> subFed = new ArrayList<Federation>();
	public ArrayList<Integer> nodes = new ArrayList<Integer>();
	public ArrayList<Integer> MMNs = new ArrayList<Integer>();
	private double rate;
	private double frequency;
	public int getNum_nodes() {
		return num_nodes;
	}
	public void setNum_nodes(int num_nodes) {
		this.num_nodes = num_nodes;
	}
	public int getNum_MMNs() {
		return num_MMNs;
	}
	public void setNum_MMNs(int num_MMNs) {
		this.num_MMNs = num_MMNs;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	

}
