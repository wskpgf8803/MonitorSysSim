package tinghua.hpc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import jsc.distributions.Normal;
import desmoj.core.dist.ContDistNormal;

public class Interval {
	
	private final double maxValue[] = {2000,2000,300000,1000};
//	private final double CPU = 2000;
//	private final double MEMORY = 2000;
//	private final double DISK = 300000;
//	private final double NETWORK = 1000;
	
	Normal normalCPU = new Normal(0, 200);
	Normal normalMEMORY = new Normal(0, 400);
	Normal normalDISK = new Normal(0, 30000);
	Normal normalNETWORK = new Normal(0, 350);
	
	protected double getNextValue(double value, int type) {

		Normal normal = null;
		switch(type){
		case 0:
			normal = normalCPU;
			break;
		case 1:
			normal = normalMEMORY;
			break;
		case 3:
			normal = normalNETWORK;
			break;
		case 2:
			normal = normalDISK;
//			return value + 100;
		}
		
		double valueTemp;
		Random rank = new Random();
		do{
			double rate = rank.nextDouble();
			if(rate < 0.1){//10%的概率value值会变
				double temp = normal.random();
				while(temp < 0){
					temp = normal.random();
				}
				valueTemp = temp + value;
			}else{//10%的概率value值会变
				valueTemp = value;
			}

		}while(valueTemp <= 0 || valueTemp >= maxValue[type]);
		return valueTemp;
	}
	
	protected double getNextValue(double value, int type, int interval) {

		Normal normal = null;
		switch(type){
		case 0:
			normal = normalCPU;
			break;
		case 1:
			normal = normalMEMORY;
			break;
		case 3:
			normal = normalNETWORK;
			break;
		case 2:
			normal = normalDISK;
			return value + 10*interval;
		}
		
		double valueTemp;
		Random rank = new Random();
		{
			double rate = rank.nextDouble();
			if(rate < 0.05 * interval){//10%的概率value值会变

//				double temp = maxValue[type]/1000*interval;
//				valueTemp = temp + value;
				double temp = normal.random();

				valueTemp = temp + value;
			}else{//10%的概率value值不会变
				valueTemp = value;
			}

		}
		if(valueTemp >= maxValue[type]){
			return maxValue[type];
		}
		if(valueTemp < 0){
			valueTemp = 0;
		}
		return valueTemp;
	}
	
	public double getAccurate(int interval){
		
//		double value0[] = {800,1000,100000,500};
//		double value1[] = {800,1000,100000,500};
		double value0[] = {0,0,0,0};
		double value1[] = {0,0,0,0};
		
//		for(int i = 0; i < interval; i++)
		{
			for(int j = 0; j < 4; j++){
				value1[j] = getNextValue(value0[j], j, interval);
			}
		}
		
		double acc = 0;
		for(int i = 0; i < 4; i++){
			acc += Math.abs(value1[i] - value0[i])/maxValue[i];
		}
		return 1-acc/4;
	}
	
	public double getTraceAcc(int t, int interval){
		if(interval == 0)
			return 0;
		int metric = 4;
		double exp = 1;
		double paraA[] = {2*Math.PI/(360), 2*Math.PI/(360), 2*Math.PI/(3600),2*Math.PI/(3000)};
		double paraB[] = {1/Math.sqrt(2), 1/Math.sqrt(1), 1/Math.sqrt(3), 1/Math.sqrt(2)};

		Random random = new Random();
		double sum = 0;
		for(int i = 0; i < metric; i++){
			int time = random.nextInt(interval);
			double real = Math.pow(0.5 + 0.5*Math.cos(paraA[i]*t)*paraB[i], exp);
			double fake = Math.pow(0.5 + 0.5*Math.cos(paraA[i]*(t-time))*paraB[i], exp);
			double temp = Math.abs(real - fake);
			sum += temp;
		}
		
		return 1- sum/metric;		
	}
	
	public static void main(String args[]){
		
		String filePath = "experimentData//";
		clrAndMkDir(filePath);
		File file = new File(filePath);
		file.mkdir();
		
		PrintWriter X_Interval = null;
		PrintWriter Y_Accuracy = null;	
		
		try {
			X_Interval = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "X_Interval.txt")));
			Y_Accuracy = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_Accuracy.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Interval interval = new Interval();
		
		int loop = 10;
		int timeLen = 3600;
		int intervalLen = 100;
		double accurate[]= new double[intervalLen+1];
//		for(int l = 0; l < 100; l++){
//			for(int i = 1; i <= 100; i++){
//				accurate[i-1] += interval.getAccurate(i);
//			}
//		}
		
		for(int i = 0; i < loop; i++){
			for(int j = 0; j <= intervalLen; j+=5){
				double sum = 0;
//				for(int k = j; k < timeLen + j; k++){
//					sum += interval.getTraceAcc(k, j);
//				}
				int sumLoop = timeLen/360;
				for(int k = 1; k <= sumLoop; k++){
					sum += interval.getTraceAcc(k*(timeLen/sumLoop), j);
				}
				accurate[j] += sum/(sumLoop);
			}
		}
		accurate[0] = 1;
		for(int i = 1; i <= intervalLen; i++){
			accurate[i]/=loop;
		}
		for(int i = 0; i <= intervalLen; i+=5){
			X_Interval.print(i + " ");
			Y_Accuracy.print(accurate[i] + " ");
			System.out.println(i+" : " + accurate[i]);
		}
		
		X_Interval.close();
		Y_Accuracy.close();
		System.out.println("over!");
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
