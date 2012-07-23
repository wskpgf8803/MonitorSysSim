package tinghua.hpc;

public class MMNWithNodes extends Consumption{

	private int hosts;
	
	public MMNWithNodes(double cpu, double mem, double disk, double net) {
		super(cpu, mem, disk, net);
		// TODO Auto-generated constructor stub
	}
	
	public MMNWithNodes(int hosts){
		this.hosts = hosts;
		cacWithFormular();
	}
	
	public void cacWithFormular(){
		usage[0] = 2*hosts;
		usage[1] = 50+0.5*hosts;
		usage[2] = hosts;
		usage[3] = hosts;
	}
	
	public static void main(String args[]){
		double cons = 0;
		for(int i = 1; i <= 100; i++){
			MMNWithNodes MMN = new MMNWithNodes(i);
			System.out.println(i+" : "+MMN.getCons() +" : "+MMN.getIdealCons());
		}
	
	}
	
}
