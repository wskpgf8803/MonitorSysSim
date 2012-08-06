package tinghua.hpc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
	}
	
	public void basicFormular(){
		usage[0] = 50+100*Math.log(clusters*100);
		usage[1] = clusters*10 *50 *0.01 ;
		usage[2] = 0;
		usage[3] = 0.1*clusters*10 ;
	}
	
	public double getBCons(){
		basicFormular();
		return getCons();
	}
	
	public void responseFormular(){
		usage[0] = 100+clusters*10*Math.log(clusters*10);
		usage[1] = clusters*10 *50 *0.01;
		usage[2] = 0;
		usage[3] = 0.1*clusters*10;
	}
	
	public double getRCons(){
		responseFormular();
		return getCons();
	}
	
	public double getTCons(){
		return getBCons()+getRCons();
	}


	public static void main(String args[]){
		String filePath = "experimentData//";
		clrAndMkDir(filePath);
		File file = new File(filePath);
		file.mkdir();
		
		PrintWriter X_Federation = null;
		PrintWriter Y_ConsRMN = null;	
		
		try {
			X_Federation = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "X_Federation.txt")));
			Y_ConsRMN = new PrintWriter(new BufferedWriter(new FileWriter(filePath + "Y_ConsRMN.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double cons = 0;
		for(int i = 1; i <= 100; i++){
			RMNWithClusets RMN = new RMNWithClusets(i,i*100);
			cons = RMN.getBCons();
			X_Federation.print(i + " ");
			Y_ConsRMN.print(cons + " ");
			System.out.println(i+" : "+cons +" : "+RMN.getIdealCons());
		}
		X_Federation.close();
		Y_ConsRMN.close();
	
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
