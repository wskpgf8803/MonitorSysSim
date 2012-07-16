package tsinghua.hpc.monitoring.newprocess;



import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

import tsinghua.hpc.monitoring.mode.MonitorSysSim;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class RMN extends SimProcess {
	
	public class cluster{
		
		public int id;
		public int host_num;
		public String name;
		
		public cluster(int id, int num){
			this.id = id;
			this.host_num = host_num;
		}
	}
	
	private final int CPU = 0;
	
	private final int MEMORY = 1;
	
	private final int DISK = 2;
	
	private final int NETWORK = 3;
	
	private MonitorSysSim model;

	private int seq;  
	
	private int layer;
	
	private int childNum;
	
	private int host_num;
	
	public TreeSet<cluster> rigister = new TreeSet<cluster>(( new Comparator<cluster>(){

		@Override
		public int compare(cluster arg0, cluster arg1) {
			// TODO Auto-generated method stub
			if(arg0.host_num < arg1.host_num)
				return -1;
			else 
				return 1;
			
		}
		
	}));
	
	public static double totalQuc[];
	
	public static double lossQuc[];
	
	private double totalVolume[];
	
	private double lossVolume[];
	
	public RMN(Model model, String arg1, boolean arg2) {
		super(model, arg1, arg2);
	}
	
	
	public RMN(Model model, String arg1, boolean arg2, int seq, int layer, int host_num) {
		super(model, arg1, arg2);
		// TODO Auto-generated constructor stub
		this.seq = seq;
		this.layer = layer;
		this.host_num = host_num;
		/*
		 * 0  CPU(M Hz)
		 * 1  Memory(M)
		 * 2  Disk(G)
		 * 3  Network(M)
		 */
		totalQuc = new double[4];
		lossQuc = new double[4];
		totalVolume =  new double[4];
		{
			totalVolume[CPU] = 2000;
			totalVolume[MEMORY] = 2000;
			totalVolume[DISK] = 160;
			totalVolume[NETWORK] = 100;
		}
		lossVolume = new double[4];
		{
			int nodeNum = nodeNum(MonitorSysSim.layerSize - layer);
			lossVolume[CPU] = 50 * childNum;
			lossVolume[MEMORY] = 50 * childNum;
			lossVolume[DISK] = 0.1 * childNum;
			lossVolume[NETWORK] = childNum;
		}

		rigister.add(new cluster(seq, host_num));
	}
	
	public int nodeNum(int layer){
		int num = 0;
		for(int i = 0; i <= layer; i++){
			num += Math.pow(childNum, i);
		}
		return num;
	}
	
	public void lifeCycle() {

//		int loop = 1;
//		while(true){
//			double time = MonitorSysSim.getTaskArrivalTime();
//			hold(new TimeSpan(time));
//			for(int i = 0; i < 4 ; i++){
//				totalQuc[i] += MonitorSysSim.getQuc(totalVolume[i], i);
//				switch(i){
//				case DISK:
//					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i] * loop, i);	
//					loop ++;
//					break;
//				case NETWORK:
//					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i] / time , i);
//				default:
//					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i], i);	
//				}
//			}
//		}
		
		Random rand = new Random(47);
		int host_num = 0;
		int cluster_num = (int) Math.sqrt(host_num);	
		
		TreeSet<RMN> rigisterRoot = new TreeSet<RMN>(( new Comparator<RMN>(){

			@Override
			public int compare(RMN arg0, RMN arg1) {
				// TODO Auto-generated method stub
				if(arg0.host_num < arg1.host_num)
					return -1;
				else 
					return 1;
				
			}
			
		}));
		
		int seq = 0;
		while(host_num < 10000){
			int num = rand.nextInt(100) + 1;
			host_num += num;
			cluster_num = (int) Math.sqrt(host_num);
			RMN rmn = new RMN(model, "rmn#" + seq, true, seq, layer+1, host_num);
			
			rigisterRoot.add(rmn);
			
			while(cluster_num < rigisterRoot.size() && rigisterRoot.size() > 1){
				RMN integer1 = rigisterRoot.pollFirst();
				RMN integer2 = rigisterRoot.pollFirst();
				RMN integer3;
				if(integer1.host_num > integer2.host_num){
					integer3 = integer1.merge(integer2);
				}else{
					integer3 = integer2.merge(integer1);
				}
				rigisterRoot.add(integer3);					
			}
			
		}
		
	}
	
	public RMN merge(RMN rmn){
		for(cluster c:rmn.rigister){
			rigister.add(c);
		}
		host_num += host_num;
		return this;
	}

}


