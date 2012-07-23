package tinghua.hpc;

public class Consumption {
	protected double price[] = {0.02/2000,0.02/2000,0.02/300000,0.02/1000};
	protected double usage[] = new double[4];
	
	public Consumption(){
		
	}
	public Consumption(double cpu, double mem, double disk, double net){
		usage[0] = cpu;
		usage[1] = mem;
		usage[2] = disk;
		usage[3] = net;
	}
	
	public void setUsage(int type, double value){
		if(type >=0 && type <4){
			usage[type] = value;
		}else{
			System.out.println("set type error");
		}

	}
	
	public double getUsage(int type){
		if(type >=0 && type <4){
			return usage[type];
		}else{
			System.out.println("get type error");
			return 0;
		}

	}
	
	public double getCons(){
		double cons = 0;
		for(int i = 0; i < 4; i++){
			cons += usage[i] * price[i];
		}
		return cons;
	}
	
	public double getIdealCons(){
		double cons = 0;
		double idealU[] = {2000,2000,300000,1000};
		for(int i = 0; i < 4; i++){
			cons += idealU[i] * price[i];
		}
		return cons;
	}

}
