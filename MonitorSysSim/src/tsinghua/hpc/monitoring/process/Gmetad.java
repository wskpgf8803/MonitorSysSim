package tsinghua.hpc.monitoring.process;

import tsinghua.hpc.monitoring.mode.MonitorSysSim;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.SimProcess;
import desmoj.core.simulator.TimeSpan;

public class Gmetad extends SimProcess {
	
	private final int CPU = 0;
	
	private final int MEMORY = 1;
	
	private final int DISK = 2;
	
	private final int NETWORK = 3;
	
	private MonitorSysSim model;

	private int seq;  
	
	private int layer;
	
	private int childNum;
	
	public static double totalQuc[];
	
	public static double lossQuc[];
	
	private double totalVolume[];
	
	private double lossVolume[];
	
	public Gmetad(Model model, String arg1, boolean arg2, int seq, int layer, int childNum) {
		super(model, arg1, arg2);
		// TODO Auto-generated constructor stub
		this.seq = seq;
		this.layer = layer;
		this.childNum = childNum;
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
			lossVolume[CPU] = 50;
			lossVolume[MEMORY] = 50 * nodeNum;
			lossVolume[DISK] = 0.1 * nodeNum;
			lossVolume[NETWORK] = childNum;
		}
	}
	
	public int nodeNum(int layer){
		int num = 0;
		for(int i = 0; i <= layer; i++){
			num += Math.pow(childNum, i);
		}
		return num;
	}
	
	public void lifeCycle() {

		int loop = 1;
		while(true){
			double time = MonitorSysSim.getTaskArrivalTime();
			hold(new TimeSpan(time));
			for(int i = 0; i < 4 ; i++){
				totalQuc[i] += MonitorSysSim.getQuc(totalVolume[i], i);
				switch(i){
				case DISK:
					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i] * loop, i);	
					loop ++;
					break;
				case NETWORK:
					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i] / time , i);
				default:
					lossQuc[i] += MonitorSysSim.getQuc(lossVolume[i], i);	
				}
			}
		}
		
	}

}


