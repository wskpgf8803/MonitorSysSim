package tsinghua.hpc.monitoring.mode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import tsinghua.hpc.monitoring.newprocess.MMN;
import tsinghua.hpc.monitoring.newprocess.Node;
import tsinghua.hpc.monitoring.newprocess.RMN;
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
	
	public static int loop = 1;
	
	public static int seq = 1;

	public static boolean SHOW_BAR = true;
	
	public static int metricNum = 30;
	
	public static int clusterNum = 10;
	
	public static int clusterSize = 100;
	
	public static int gmetadChild = 2;
	
	public static int layerSize = 10;
	
	public static double volumeUpBound[] = {2000,2000,160,100};
	
	public static double QUALITY_FUC_PARA[] = {-0.00100,-0.00100,-0.00100,-0.00100};
	

	public static double taskSeriveTimeStreamLowBound = 10;
	
	public static double taskSeriveTimeStreamUpBound = 20;
	
	public static double LOOP_NUM = 10;
	
	public static int CLUSTER_SIZE_DEC_NUM = 10;
	
	public static int LAYER_SIZE_DEC_NUM = 1;
	
	
	public static int CIRCLES_NUM_PER_SECEND = 3600;
	
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
		for(int layerSeq = 1; layerSeq <= layerSize; layerSeq++){
			for(int j = 0; j < nodeNum; j++){
//				Gmond gmond = new Gmond(this, "gmond#" + seq, true, seq, layerSeq, clusterSize);
//				gmond.activate(new TimeSpan(0.0));
				
				Gmetad gmetad = new Gmetad(this, "gmetad#" + seq, true, seq, layerSeq, gmetadChild);
				gmetad.activate(new TimeSpan(0.0));
				seq ++;
			}
			nodeNum *= gmetadChild;	
		}		
		
		seq = 1;
		nodeNum = 1;
		for(int layerSeq = 1; layerSeq <= layerSize; layerSeq++){
			for(int j = 0; j < nodeNum; j++){
				
//				Node node = new Node(this, "node#" + seq, true, seq, layerSeq, clusterSize);
//				node.activate(new TimeSpan(0.0));
//				
//				MMN mmn = new MMN(this, "mmn#" + seq, true, seq, layerSeq, clusterSize);
//				mmn.activate(new TimeSpan(0.0));
				
				RMN rmn = new RMN(this, "rmn#" + seq, true, seq, layerSeq, gmetadChild);
				rmn.activate(new TimeSpan(0.0));
				seq ++;
			}
			nodeNum *= gmetadChild;	
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

		doExp();
		
		System.out.println("Game over!!!");
		
	}
	
	public static void doExp(){
		
		String filePath = "experimentData//";
		clrAndMkDir(filePath);
		File file = new File(filePath);
		file.mkdir();
		
		PrintWriter X_CSize = null;
		PrintWriter Y_GmondQuc = null;
		PrintWriter Y_MMNQuc = null;
		PrintWriter X_CLayer = null;
		PrintWriter Y_GmetadQuc = null;
		PrintWriter Y_RMNQuc = null;
		
		
		try {
			X_CSize = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "X_CSize.txt")));
			Y_GmondQuc = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_GmondQuc.txt")));
			Y_MMNQuc = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_MMNQuc.txt")));
			X_CLayer = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "X_CLayer.txt")));
			Y_GmetadQuc = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_GmetadQuc.txt")));
			Y_RMNQuc = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_MMNQuc.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int l = 0; l < LOOP_NUM; l ++){
			
			MonitorSysSim model = new MonitorSysSim(null, "MonitorSysSim", true, false);
			Experiment exp = new Experiment("MonitorSysSimExperiment",
					TimeUnit.SECONDS, TimeUnit.SECONDS, null);

			// connect both
			model.connectToExperiment(exp);

			// set experiment parameters
			exp.setShowProgressBar(SHOW_BAR); // display a progress bar (or not)
			exp.stop(new TimeInstant(CIRCLES_NUM_PER_SECEND * SIM_SECENDS, TimeUnit.SECONDS)); // set end of
																// simulation at
																// 500 minutes
			exp.tracePeriod(new TimeInstant(0), new TimeInstant(500,
					TimeUnit.MINUTES)); // set the period of the trace
			exp.debugPeriod(new TimeInstant(0), new TimeInstant(400,
					TimeUnit.MINUTES)); // and debug output

			exp.start();

			exp.report();

			exp.finish();

//			writeGmond(X_CSize, Y_GmondQuc, Y_MMNQuc);
			
			writeGmetad(X_CLayer, Y_GmetadQuc, Y_RMNQuc);

		}
		
		X_CSize.close();
		Y_GmondQuc.close();
		Y_MMNQuc.close();
		X_CLayer.close();
		Y_GmetadQuc.close();
		Y_RMNQuc.close();
		
	}
	
	public static void writeGmond(PrintWriter X_CSize, PrintWriter Y_GmondQuc, PrintWriter Y_MMNQuc){
		
		System.out.println("Gmond:");
		
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
		
		Y_GmondQuc.print(lossQuc/totalQuc + " ");
		
		System.out.println("Node:");
		
		lossQuc = 0;
		for(int i = 0; i < 4; i ++){
			if(clusterSize >= gmetadChild){
				lossQuc += Node.lossQuc[i] * (clusterSize - gmetadChild) + MMN.lossQuc[i] * gmetadChild;
			}else{
				lossQuc += MMN.lossQuc[i] * clusterSize;
			}
			
		}
		
		totalQuc = 0;
		for(int i = 0; i < 4; i ++){
			if(clusterSize >= gmetadChild){
				totalQuc += Node.totalQuc[i] * (clusterSize - gmetadChild)  + MMN.totalQuc[i] * gmetadChild;
			}else{
				totalQuc += MMN.totalQuc[i] * clusterSize;
			}
			
		}
		
		System.out.println(lossQuc);
		
		System.out.println(totalQuc);
		
		System.out.println(lossQuc/totalQuc);
		
		Y_MMNQuc.print(lossQuc/totalQuc + " ");
		
		X_CSize.print(clusterSize + " ");
		
		clusterSize -= CLUSTER_SIZE_DEC_NUM;
		
	}
	
	public static void writeGmetad(PrintWriter X_CLayer, PrintWriter Y_GmetadQuc, PrintWriter Y_RMNQuc){
		
		System.out.println("Gmetad:");
		
		double lossQuc = 0;
		for(int i = 0; i < 4; i ++){
			lossQuc += Gmetad.lossQuc[i];
		}
		
		double totalQuc = 0;
		for(int i = 0; i < 4; i ++){
			totalQuc += Gmetad.totalQuc[i];
		}
		
		System.out.println(lossQuc);
		
		System.out.println(totalQuc);
		
		System.out.println(lossQuc/totalQuc);
		
		Y_GmetadQuc.print(lossQuc/totalQuc + " ");
		
		System.out.println("RMN:");
		
		lossQuc = 0;
		for(int i = 0; i < 4; i ++){
			lossQuc += RMN.lossQuc[i];
		}
		
		totalQuc = 0;
		for(int i = 0; i < 4; i ++){
			totalQuc += RMN.totalQuc[i];
		}
		
		System.out.println(lossQuc);
		
		System.out.println(totalQuc);
		
		System.out.println(lossQuc/totalQuc);
		
		Y_RMNQuc.print(lossQuc/totalQuc + " ");
		
		X_CLayer.print(layerSize + " ");
		
		layerSize -= LAYER_SIZE_DEC_NUM;
		
	}

	public static void clrAndMkDir(String fileName){
		File file = new File(fileName);
		if(file.exists()){
			deleteDirectory(file);
		}		
		
		file = new File(fileName);
		file.mkdir();
	}
	
	public static void deleteDirectory(File file){
        try{
            if(file.exists()&&file.isDirectory()){
                String[] contents = file.list();
                for(int i=0;i<contents.length;i++){
                    File file2X = new File(file.getAbsolutePath() + "/" +contents[i]);
                    if(file2X.exists()){
                        if(file2X.isFile()){
                            file2X.delete();
                        }else if(file2X.isDirectory()){
                            deleteDirectory(file2X);
                        }
                    }else{
                        throw new RuntimeException("File not exist!");
                    }
                }
                file.delete();
            }else{
                throw new RuntimeException("Not a directory!");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
