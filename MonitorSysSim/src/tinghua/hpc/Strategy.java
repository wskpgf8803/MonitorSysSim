package tinghua.hpc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.xerces.impl.xpath.regex.REUtil;

public class Strategy {
	
	private ArrayList<Federation> fedList;
	private Map<Integer, Node> nodeMap;
	private int frequency;
	private int runningTime = 24;
	private final double minCons = 0.001;
	private final double maxCons = 0.005;
	private int minHosts = 15;
	
	public void initFedList(){
		fedList = new ArrayList<Federation>();
		nodeMap = new HashMap<Integer, Node>();
		int num_nodes_array[] = {4,50,70,3,12,28,56,44,19,89};
		int num_MMN_array[] = {3,3,3,3,3,3,3,3,3,3};
		double rate_array[] = {0.05,0.15,0.15,0.02,0.07,0.07,0.13,0.1,0.06,0.2};
		for(int i = 0; i < 10; i++){
			Federation fed= new Federation();
			fed.setNum_nodes(num_nodes_array[i]);
			fed.setNum_MMNs(num_MMN_array[i]);
			fed.setRate(rate_array[i]);
			fed.subFed.add(fed);
			for(int j = 0; j < num_nodes_array[i]; j++){
				Node node = new Node();
				node.setFederationId(fed.getId());
				node.setLocation(fed.getId());
				fed.nodes.add(node.getId());
				nodeMap.put(node.getId(), node);
			}

			selectMMN(fed, num_MMN_array[i]);
			fedList.add(fed);
		}
	}
	
	/**
	 * randomly select number num_MMNs MMNs in Fderation
	 * @param fed
	 * @param num_MMNs
	 */
	public void selectMMN(Federation fed, int num_MMNs){
		fed.MMNs.clear();
		if(fed.nodes.size() <= num_MMNs){
			for(int j = 0; j < fed.nodes.size(); j++){
				fed.MMNs.add(fed.nodes.get(j));
			}
		}else{
			Set<Integer> nodeSet = new TreeSet<Integer>();
			Random rand = new Random();
			for(int j = nodeSet.size(); j < num_MMNs; j=nodeSet.size()){
				nodeSet.add(fed.nodes.get(rand.nextInt(fed.nodes.size())));
			}
			for (Integer integer : nodeSet) {
				fed.MMNs.add(integer);
			}
		}
	}
	
	/**
	 * implement the Huffman-Like Strategy
	 */
	public void huffman(){
		
		TreeSet<Federation> fedTree = new TreeSet<Federation>( new Comparator<Federation>(){

			@Override
			public int compare(Federation arg0, Federation arg1) {
				// TODO Auto-generated method stub
				if(arg0.getNum_nodes() < arg1.getNum_nodes())
					return -1;
				else if(arg0.getNum_nodes() > arg1.getNum_nodes())
					return 1;
				if(arg0.getId() < arg1.getId())
					return -1;
				else
					return 1;
			}	
		});
		
		while(!isHuffBalance()){		
			fedTree.clear();
			for (Federation federation : fedList) {
				fedTree.add(federation);
			}
			Federation a = fedTree.pollFirst();
			Federation b = fedTree.pollFirst();
			fedTree.add(combineFeder(a, b));	
			fedList = new ArrayList<Federation>(fedTree);
		}	
	}
	
	/**
	 * check whether all the federation satisfy the balance of Huffman-Like Strategy
	 * @return
	 */
	public boolean isHuffBalance(){
		if(fedList.size() <= 1)
			return true;
		for (Federation federation : fedList) {
			if(federation.getNum_nodes() <= minHosts){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * combine two federation, the hosts of a is larger than of b
	 * @param a
	 * @param b
	 * @return
	 */
	public Federation combineFeder(Federation a, Federation b){
		a.setId(a.getId());
		a.setNum_nodes(a.getNum_nodes() + b.getNum_nodes());
		a.setNum_MMNs(a.getNum_MMNs() + b.getNum_MMNs());
		a.setRate(a.getRate() + b.getRate());
		for(Federation federation : b.subFed){
			a.subFed.add(federation);
		}
		for(Integer intt : b.nodes){
			a.nodes.add(intt);
		}
		selectMMN(a, a.getNum_MMNs());	
		return a;
	}
	
	public void rankMMN(){
		Random rand = new Random();
		for(Federation fed : fedList){
			int num_req = (int) (fed.getRate()*frequency);
			for(int i = 0; i < num_req; i++){
				Request req = new Request();
				req.setFederationId(fed.getId());
				req.setLocation(rand.nextInt(fedList.size()));
				int mMNId = getRankTopId(fed,req);
				req.setMMNId(mMNId);
				Node node = nodeMap.get(mMNId);
				node.reqList.add(req);			
				nodeMap.put(mMNId, node);
				if(new MMNWithNodes(fed.getNum_nodes(), node.reqList).getTCons() > maxCons){
					incMMN(fed);
				}
			}
		}
	}
	
	public int getRankTopId(Federation fed, Request req){
		double minValue = 1;
		int id = 0;
		for(int i = 0; i < fed.MMNs.size(); i++){
			Node node = nodeMap.get(fed.MMNs.get(i));
			double value = 0.5/(new MMNWithNodes(fed.getNum_nodes(), node.reqList).getTCons()) 
				+ 0.5/(1+Math.abs(node.getLocation() - req.getLocation()));
			if(minValue <= value){
				value = minValue;
				id = node.getId();
			}
		}
		return id;
	}
	
	public void incMMN(Federation fed){
		if(fed.getNum_MMNs() == fed.getNum_nodes())
			return;
		Random rand = new Random();
		int id;
		while(true){
			id = rand.nextInt(fed.nodes.size());
			for(int i = 0; i < fed.MMNs.size(); i++){
				if(fed.MMNs.get(i) == id)
					continue;
			}
			fed.MMNs.add(id);
			fed.setNum_MMNs(fed.getNum_MMNs() + 1);
		}
	}
	
	public double getCons(){
		double cons = 0;
		RMNWithClusets rmn = new RMNWithClusets(fedList.size(), nodeMap.size());
		cons += rmn.getTCons();
		for(Federation fed:fedList){
			for(int i = 0; i < fed.MMNs.size(); i++){
				MMNWithNodes mmn = new MMNWithNodes(fed.nodes.size(), nodeMap.get(fed.MMNs.get(i)).reqList);
				cons += mmn.getTCons();
			}
		}
		return cons;
	}
	
	public double getSatisiedRate(){
		int total = 0;
		int satisfied = 0;
		for(Federation fed:fedList){
			total += fed.getNum_MMNs();
			for(int i = 0; i < fed.MMNs.size(); i++){
				MMNWithNodes mmn = new MMNWithNodes(fed.nodes.size(), nodeMap.get(fed.MMNs.get(i)).reqList);
				if(mmn.getTCons() <= maxCons)
					satisfied ++;
			}
		}	
		return (double)satisfied/total;
	}
	
	public void exp1(){
		initFedList();
		huffman();
		rankMMN();
		System.out.println(getCons());
		System.out.println(getSatisiedRate());		
	}
	
	public void exp2(){
		initFedList();
		rankMMN();
		System.out.println(getCons());
		System.out.println(getSatisiedRate());		
	}
	
	public void exp3(){
		initFedList();
		huffman();
		System.out.println(getCons());
		System.out.println(getSatisiedRate());		
	}
	
	public void exp4(){
		initFedList();
		System.out.println(getCons());
		System.out.println(getSatisiedRate());		
	}
	
	public static void main(String args[]){
		Strategy stra = new Strategy();
		stra.exp1();
		stra.exp2();
		stra.exp3();
		stra.exp4();

	}

}
