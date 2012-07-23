package tinghua.hpc;

import tsinghua.hpc.monitoring.newprocess.RMN;

public class RMNWithClusets extends Consumption {
	

	private int clusters;
	private int totalHosts;
	
	public RMNWithClusets(double cpu, double mem, double disk, double net) {
		super(cpu, mem, disk, net);
		// TODO Auto-generated constructor stub
	}
	
	public RMNWithClusets(int clusters, int totalHosts){
		this.clusters = clusters;
		this.totalHosts = totalHosts;
		cacWithFormular();
	}
	
	public void cacWithFormular(){
		usage[0] = 100+clusters*10*Math.log(clusters*10);
		usage[1] = clusters*10 *50 *0.01;
		usage[2] = 0;
		usage[3] = 0.1*clusters*10;
	}


	public static void main(String args[]){
		double cons = 0;
		for(int i = 1; i <= 100; i++){
			RMNWithClusets RMN = new RMNWithClusets(i,i*10);
			System.out.println(i+" : "+RMN.getCons() +" : "+RMN.getIdealCons());
		}
	
	}
}
