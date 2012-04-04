package tsinghua.hpc.monitoring.mode;

import java.util.concurrent.TimeUnit;

import tsinghua.hpc.monitoring.process.Gmetad;
import tsinghua.hpc.monitoring.process.Gmond;

import desmoj.core.dist.ContDistUniform;
import desmoj.core.dist.DiscreteDistPoisson;
import desmoj.core.simulator.Experiment;
import desmoj.core.simulator.InterruptCode;
import desmoj.core.simulator.Model;
import desmoj.core.simulator.TimeInstant;

public class MonitorSysSim extends Model{

	private DiscreteDistPoisson taskArrivalStream;
	
	private static ContDistUniform taskVolumeUinStream;

	private ContDistUniform taskSeriveTimeStream;

	protected desmoj.core.simulator.ProcessQueue<Gmetad> gmetadList;

	protected desmoj.core.simulator.ProcessQueue<Gmond> gmondList;

	public static boolean SHOW_BAR = true;
	
	public static double QUALITY_FUC_PARA = -0.00020;
	
	
	public static int CIRCLES_NUM_PER_SECEND = 1000;
	
	public static int SIM_SECENDS = 360;
	
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
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(java.lang.String[] args) {

		MonitorSysSim model = new MonitorSysSim(null, "simMode1", true, false);
		Experiment exp = new Experiment("simMode1Experiment",
				TimeUnit.SECONDS, TimeUnit.MINUTES, null);

		// connect both
		model.connectToExperiment(exp);

		// set experiment parameters
		exp.setShowProgressBar(SHOW_BAR); // display a progress bar (or not)
		exp.stop(new TimeInstant(CIRCLES_NUM_PER_SECEND * SIM_SECENDS, TimeUnit.MINUTES)); // set end of
															// simulation at
															// 500 minutes
		exp.tracePeriod(new TimeInstant(0), new TimeInstant(50000,
				TimeUnit.MINUTES)); // set the period of the trace
		exp.debugPeriod(new TimeInstant(0), new TimeInstant(400000,
				TimeUnit.MINUTES)); // and debug output

		exp.start();

		exp.report();

		exp.finish();

		System.out.println("Game Over!!!");
	}

}
