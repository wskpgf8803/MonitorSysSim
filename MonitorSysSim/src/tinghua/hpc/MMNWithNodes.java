package tinghua.hpc;

import java.util.ArrayList;

public class MMNWithNodes extends Consumption{

	private int hosts;
	private ArrayList<Request> reqList;
	
	public MMNWithNodes(double cpu, double mem, double disk, double net) {
		super(cpu, mem, disk, net);
		// TODO Auto-generated constructor stub
	}
	
	public MMNWithNodes(int hosts, ArrayList<Request> reqList){
		this.hosts = hosts;
		this.reqList = reqList;
	}
	
	public void basicFormular(){
		usage[0] = 2*hosts;
		usage[1] = 50+0.5*hosts;
		usage[2] = hosts;
		usage[3] = hosts;
	}
	
	public double getBCons(){
		basicFormular();
		return getCons();
	}
	
	public void responseFormular(){
		usage[0] = 2*hosts;
		usage[1] = 50+0.5*hosts;
		usage[2] = hosts;
		usage[3] = hosts;
	}
	
	public double getRCons(){
		responseFormular();
		return getCons();
	}
	
	public double getTCons(){
		return getBCons()+getRCons();
	}
	
	public static void main(String args[]){
		double cons = 0;
		for(int i = 1; i <= 100; i++){
			MMNWithNodes MMN = new MMNWithNodes(i,null);
			System.out.println(i+" : "+MMN.getTCons() +" : "+MMN.getIdealCons());
		}
	
	}
	
}
