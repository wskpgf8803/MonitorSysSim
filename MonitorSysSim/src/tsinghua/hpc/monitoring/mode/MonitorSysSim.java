package tsinghua.hpc.monitoring.mode;


import java.util.concurrent.TimeUnit;

import tsinghua.hpc.monitoring.process.Gmetad;
import tsinghua.hpc.monitoring.process.Gmond;

import desmoj.core.dist.ContDistUniform;
import desmoj.core.dist.DiscreteDistPoisson;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.InterruptCode;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.ProcessQueue;
import desmoj.core.simulator.TimeInstant;
import desmoj.core.simulator.TimeSpan;

public class MonitorSysSim extends Model{

	private DiscreteDistPoisson taskArrivalStream;
	
	private static ContDistUniform taskVolumeUinStream;

	private static ContDistUniform taskSeriveTimeStream;

	protected desmoj.core.simulator.ProcessQueue<Gmetad> gmetadList;

	protected desmoj.core.simulator.ProcessQueue<Gmond> gmondList;

	public static boolean SHOW_BAR = true;
	
	public static int clusterNum = 10;
	
	public static int clusterSize = 100;
	
	public static int layerSize = 5;
	
	public static double volumeUpBound[] = {2000,2000,160,100};
	
	public static double QUALITY_FUC_PARA[] = {-0.00100,-0.00100,-0.00100,-0.00100};;
	

	public static double taskSeriveTimeStreamLowBound = 10;
	
	public static double taskSeriveTimeStreamUpBound = 20;
	
	
	public static int CIRCLES_NUM_PER_SECEND = 100;
	
	public static int SIM_SECENDS = 1;
	
	public enum VolumeType{
		CPU, Memory, Disk, Network
	}
	
	public static double qualityFuc(double volume, int type){
		if(volume >= volumeUpBound[type])
			return 1;
		return (1-Math.exp(QUALITY_FUC_PARA[type]*volume))/(1-Math.exp(volumeUpBound[type]*QUALITY_FUC_PARA[type]));
	}
	
	/** Quality Function HeYu */
	public static double getQuc(double volume, int type) {
		return qualityFuc(volume, type);	
	}
	
	
	public MonitorSysSim(Model owner, String name, boolean showInReport,
			boolean showIntrace) {
		// call the constructor of the superclass
		super(owner, name, showInReport, showIntrace);
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Hello monitorSysSim!";
	}

	@Override
	public void doInitialSchedules() {
		// TODO Auto-generated method stub
		int nodeNum = 1;
		int seq = 1;
		for(int layerSeq = 1; layerSeq <= layerSize; layerSeq++){
			for(int j = 0; j < nodeNum; j++){
				Gmond gmond = new Gmond(this, "gmond#" + seq, true, seq, layerSeq);
				gmond.activate(new TimeSpan(0.0));
				Gmetad gmetad = new Gmetad(this, "gmetad#" + seq, true, seq, layerSeq);
				gmetad.activate(new TimeSpan(0.0));
				seq ++;
			}
			nodeNum *= 2;	
		}		
		System.out.println(seq -1);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

		taskSeriveTimeStream = new ContDistUniform(this,
				"task Service Time Stream", taskSeriveTimeStreamLowBound, taskSeriveTimeStreamUpBound, true, false);
		
		gmetadList = new ProcessQueue<Gmetad>(this, "Gmetad Queue", true, true);
		gmondList = new ProcessQueue<Gmond>(this, "Gmond Queue", true, true);
		
	}
	
	public static double getTaskArrivalTime() {

		return taskSeriveTimeStream.sample();
	}
	
	public static void main(java.lang.String[] args) {

		MonitorSysSim model = new MonitorSysSim(null, "MonitorSysSim", true, false);
		Experiment exp = new Experiment("simMode1Experiment",
				TimeUnit.SECONDS, TimeUnit.MINUTES, null);

		// connect both
		model.connectToExperiment(exp);

		// set experiment parameters
		exp.setShowProgressBar(SHOW_BAR); // display a progress bar (or not)
		exp.stop(new TimeInstant(CIRCLES_NUM_PER_SECEND * SIM_SECENDS, TimeUnit.MINUTES)); // set end of
															// simulation at
															// 500 minutes
		exp.tracePeriod(new TimeInstant(0), new TimeInstant(500,
				TimeUnit.MINUTES)); // set the period of the trace
		exp.debugPeriod(new TimeInstant(0), new TimeInstant(400,
				TimeUnit.MINUTES)); // and debug output

		exp.start();

		exp.report();

		exp.finish();

		double lossQuc = 0;
		for(int i = 0; i < 4; i ++){
			lossQuc += Gmond.lossQuc[i];
		}
		
		double totalQuc = 0;
		for(int i = 0; i < 4; i ++){
			totalQuc += Gmond.totalQuc[i];
		}
		
		System.out.println(lossQuc);
		
		System.out.println(totalQuc);
		
		System.out.println(lossQuc/totalQuc);
		
		System.out.println("Game Over!!!");
		
		lossQuc = 0;
		for(int i = 0; i < 4; i ++){
			lossQuc += Gmetad.lossQuc[i];
		}
		
		totalQuc = 0;
		for(int i = 0; i < 4; i ++){
			totalQuc += Gmetad.totalQuc[i];
		}
		
		System.out.println(lossQuc);
		
		System.out.println(totalQuc);
		
		System.out.println(lossQuc/totalQuc);
		
		System.out.println("Game Over!!!");
	}

}
