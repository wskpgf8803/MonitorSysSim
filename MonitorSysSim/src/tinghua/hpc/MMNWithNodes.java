package tinghua.hpc;

import java.util.ArrayList;

public class MMNWithNodes extends Consumption{

	private int hosts;
	private ArrayList<Request> reqList;
	private int location;
	
	public MMNWithNodes(double cpu, double mem, double disk, double net) {
		super(cpu, mem, disk, net);
		// TODO Auto-generated constructor stub
	}
	
	public MMNWithNodes(int hosts, ArrayList<Request> reqList, int location){
		this.hosts = hosts;
		this.reqList = reqList;
		this.location = location;
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
		usage[0] = 0.1*hosts*reqList.size();
		usage[1] = 0.1*hosts;
		usage[2] = 0;
		usage[3] = hosts * reqList.size();
		for(int i = 0; i < reqList.size(); i++){
			usage[3] += distance(location, reqList.get(i).getLocation());
		}
	}
	
	public double getRCons(){
		if(reqList == null){
			return 0;
		}
		responseFormular();
		return getCons();
	}
	
	public double getTCons(){
		return getBCons()+getRCons();
	}
	
	public static int distance(int loc1, int loc2){
		if(loc1 <= loc2){
			return (loc2-loc1)<(10+loc1-loc2)?(1 + loc2-loc1):(11+loc1-loc2);
		}else{
			return (loc1-loc2)<(10+loc2-loc1)?(1 + loc1-loc2):(11+loc2-loc1);
		}
	}
	
	public static void main(String args[]){
		double cons = 0;
		for(int i = 1; i <= 100; i++){
			MMNWithNodes MMN = new MMNWithNodes(i,null,0);
			System.out.println(i+" : "+MMN.getTCons() +" : "+MMN.getIdealCons());
		}
	
	}
	
}
