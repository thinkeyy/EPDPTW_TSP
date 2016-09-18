import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.plaf.synth.SynthStyle;
import javax.swing.text.MaskFormatter;


/*
 * 问题，全局最优解为含历史最优解。
 */
public class pso {
	
	private int clent_num; //客户个数
	private int grain_num; //粒子个数
	private int car_num; //车辆个数
	private double [][] grain_p; //粒子的位置向量
	private double [][] grain_v; //粒子的速度向量
	private int gbest_g; //当前全局最优解粒子
	private double[] gbest_s; //当前全局最优解粒子位置
	private int[] pbest_s; //局域最优解
	private int[] evaluate_array; //粒子评价值数组

	
	private int[][] clientCostValue;//客户间距离
	private int[][] HomeCostValue;//客户到充电点距离
	private int[] clientBasicValue;//客户起点到终点距离
	
	private int[] clent_array; //客户链表
	private Map<Integer, List<Integer>> clent_p; //客户邻域
	
	private Random random ;
	private int x, y;//充电桩
	
	private int basic_distance;
	
	private double c1 = 2,c2 = 2,c3 = 1,w = 0.5;
	
	//获取客户
	private int[] getClent_array(int c) {
		// TODO Auto-generated method stub
		int[] clent_array = new int[c]; 
		for (int i = 0; i < c; i++){
			clent_array[i] = i;
		}
		return clent_array;
	}
	

	
	//随机生成初始粒子位置向量和粒子的速度向量
	public void init(int car_num, int grain_num, int clent_num){
		this.clent_num = clent_num;
		this.car_num = car_num;
		this.grain_num =grain_num;
		this.grain_p = new double [grain_num][clent_num];
		this.grain_v = new double [grain_num][clent_num];
		for (int i = 0; i < grain_num; i++) {
			
			for (int j = 0; j < clent_num; j++) {
				random = new Random();
				this.grain_p[i][j] = random.nextDouble() * car_num;
				//System.out.print(Math.floor(this.grain_p[i][j]) +"\t");
				random = new Random();
				this.grain_v[i][j] =(car_num - random.nextDouble() * car_num *2) * 0.1;
				
			} 
			//System.out.println(i);
		}
	}

	/*
	 * int[] clientBasicValue 客户上车点和下车点距离
	 * int[][] clientCostValue 客户间距离
	 * double[][] grain_p 粒子位子矩阵
	 */

	private void getEvaluateList(int[][] clientCostValue,int[] clientBasicValue, int[][] HomeCostValue, double[][] grain_p) {
	evaluate_array = new int[grain_num];
	for (int i = 0; i < grain_num; i++){
		double[] grainPosition_i = new double[clent_num];
		
		//向下取整
		for (int j = 0; j < clent_num; j++){
			if(grain_p[i][j] < 0){
				grainPosition_i[j] = 0;
			}else if(grain_p[i][j] > car_num){
				grainPosition_i[j] = car_num -1;
			}else {
				grainPosition_i[j] = Math.floor(grain_p[i][j]);
			}
			
			
		}
		
		//求解粒子i的评价值
		evaluate_array[i] = 0;
		for (int j = 0; j < car_num; j++) {
			int evaluate = 0;
			List<Integer> carClentList = new ArrayList<>();
			for (int j2 = 0; j2 < grainPosition_i.length; j2++) {
				if(grainPosition_i[j2] == j){
					carClentList.add(j2);//将客户分配给车辆
				}
			}
			if (carClentList.size() > 1) {
				for (int j3 = 0 ; j3 < carClentList.size()-1; j3++) {
					if((evaluate + clientBasicValue[carClentList.get(j3+1)] + clientCostValue[carClentList.get(j3)][carClentList.get(j3+1)]+HomeCostValue[1][carClentList.get(j3+1)]) > 200){
						evaluate = evaluate + HomeCostValue[1][carClentList.get(j3)] + HomeCostValue[0][carClentList.get(j3+1)] + clientBasicValue[carClentList.get(j3+1)];
					}else {
						evaluate = evaluate + clientCostValue[carClentList.get(j3)][carClentList.get(j3+1)] + clientBasicValue[carClentList.get(j3+1)];
					}
				}
			}
			if (carClentList.size() != 0) {
				evaluate = evaluate + HomeCostValue[0][carClentList.get(0)] + clientBasicValue[carClentList.get(0)];
				evaluate = evaluate + HomeCostValue[1][carClentList.get(carClentList.size()-1)];
			}
			
			evaluate_array[i] = evaluate_array[i]+evaluate;
		}
		
	}
}
	
	/*获取时间t时的粒子速度、粒子位置
	 * history_p 粒子最优解
	 * gbest_s 全局最优解
	 * gbest_s 邻域最优解
	 */
	public void getStatusT(double[][] grain_v, double[][] grain_p, int[] pbest_s, double[] gbest_s ,double[][] history_p) {
		double[][] grain_vTemp = grain_v.clone();
		double[][] grain_pTemp = grain_p.clone();
		
		for (int i = 0; i < grain_vTemp.length; i++) {
			for (int j = 0; j < grain_vTemp[i].length; j++) {
				random = new Random();
				double r1 = random.nextDouble();
				double r2 = random.nextDouble();
				double r3 = random.nextDouble();
				double mother = c1 * r1 + c2 * r2 + c3 * r3;
				grain_v[i][j] = w * grain_vTemp[i][j] + (c1 * r1 *(1-w) /mother) *(history_p[i][j]-grain_pTemp[i][j])
						+  (c2 * r2 *(1-w) /mother)* (gbest_s[j]-grain_pTemp[i][j]) +  (c3 * r3 *(1-w) /mother)* (grain_pTemp[pbest_s[i]][j]-grain_pTemp[i][j]);
//				if(grain_v[i][j] <(0-car_num)){
//					grain_v[i][j] = -car_num;
//				}else if (grain_v[i][j] >car_num) {
//					grain_v[i][j] = car_num;
//				}
				
				grain_p[i][j] = grain_pTemp[i][j] + grain_v[i][j];
				
				//System.out.print(grain_v[i][j]+"\t");
			}
			//System.out.println();
		}
		
	}
	
	/*获取局部最优解
	 * evaluate_array : 粒子的评价值
	 * clent_p： 粒子的邻域
	 */
	public void getPbest_s(int[] evaluate_array ,Map<Integer, List<Integer>> clent_p,double [][] grain_p) {
		pbest_s = new int[grain_num];
		for (int i = 0; i < grain_num; i++) {
			if(clent_p.get(i).size() == clent_num){
				pbest_s[i] = gbest_g;
			}else {
				int best_p = clent_p.get(i).get(0);
				int temp = evaluate_array[clent_p.get(i).get(0)];
				for(int j = 1; j < clent_p.get(i).size(); j++) {
					if(evaluate_array[clent_p.get(i).get(j)] <= temp) {
						best_p = clent_p.get(i).get(j);
						temp = evaluate_array[clent_p.get(i).get(j)];
					}
				}
				pbest_s[i] = best_p;
			}
		}
	}
	
	/*获取当前最优粒子
	 *evaluate_array : 粒子的评价值
	 *int[][] grain_p : 粒子位子矩阵
	 */
	public void getGbest_s(int[] evaluate_array, double [][] grain_p) {
		int best_g = 0;
		int temp = evaluate_array[0];
		for (int i = 1; i < evaluate_array.length; i++) {
			if(evaluate_array[i] <= temp) {
				best_g = i;
				temp = evaluate_array[i];
			}
		}
		gbest_g = best_g;
		gbest_s = grain_p[best_g].clone();
	}
	
	//获取客户的邻域
	private Map<Integer, List<Integer>> getClent_p(int[] clent_array, int t) {
		
		Map<Integer, List<Integer>> clent_p = new HashMap<Integer, List<Integer>>();
		int clent_num = clent_array.length;
		for (int i=0; i<grain_num; i++){
			//System.out.print("t="+t+"时粒子"+i+"的邻域：\t");
			List<Integer> temp = new ArrayList<>();
			temp.add(clent_array[i]);
			//System.out.print(clent_array[i]+"\t");
			if(2*(t+1) < grain_num){
				for (int x = 0; x < t ;x++) {
					if(i-x-1 < 0) {
						temp.add(clent_array[i-x-1+grain_num]);
						//System.out.print(clent_array[i-x-1+grain_num]+"\t");
					}else {
						temp.add(clent_array[i-x-1]);
						//System.out.print(clent_array[i-x-1]+"\t");
					}
					
					if(i+x+1 >= grain_num) {
						temp.add(clent_array[i+x+1-grain_num]);
						//System.out.print(clent_array[i+x+1-grain_num]+"\t");
					}else {
						temp.add(clent_array[i+x+1]);
						//System.out.print(clent_array[i+x+1]+"\t");
					}
				}
			}else {
				for (int ii = 0; ii<grain_num; ii++){
					if(ii != i){
						temp.add(clent_array[ii]);
						//System.out.print(clent_array[ii]+"\t");
					}
				}
			}
			clent_p.put(i, temp);
			//System.out.println();
		}
		return clent_p;
	}
	
	public void clentInit() throws IOException {
		clentint clentinit = new clentint(clent_num);
		int[][] clientstart = clentinit.getClientStart("src/data.txt", 1, 2);
		int[][] clientdone = clentinit.getClientStart("src/data.txt", 3, 4);
		System.out.println("clent_num="+clent_num);
		clientCostValue = clentinit.getClientCostValue(clientstart, clientdone);
		HomeCostValue = clentinit.getHomeCostValue(clientstart, clientdone, x, y);
		clientBasicValue = clentinit.getClientBasicValue(clientstart, clientdone);
		basic_distance = 0;
		for (int i = 0; i < clientBasicValue.length; i++) {
			basic_distance = basic_distance + clientBasicValue[i];
		}
		System.out.println("有效距离： "+basic_distance);
	}
	public void solve() throws IOException {
		clentInit();
		int Gbest = 99999;
		double[] Gbest_a = new double[clent_num]; 
		double[][] history_p = null;
		
		int[] history_e = null;//粒子历史最优解
		
		for(int t = 0; t < 1000; t++) {
			getEvaluateList(clientCostValue,clientBasicValue, HomeCostValue, grain_p);
			

			if(history_e == null || history_e.length == 0){
				history_e = evaluate_array.clone();
				history_p = grain_p.clone();
			}else {
				for (int i = 0; i < history_e.length; i++) {
					if(evaluate_array[i] <= history_e[i]) {
						history_e[i] = evaluate_array[i];//粒子历史最优解评价值
						history_p[i] = grain_p[i].clone();//粒子历史最优解的位置向量
					}
				}
			}
			
			getGbest_s(evaluate_array, grain_p);//获取当前全局最优解
			
			if(evaluate_array[gbest_g] <= Gbest) { //获取历史全局最优解
				Gbest = evaluate_array[gbest_g];
				Gbest_a = gbest_s.clone();
			}
			clent_array = getClent_array(clent_num);
			clent_p = getClent_p(clent_array, t);
			
//			System.out.print("t="+t+"时最优解评价值为"+Gbest+"粒子位子");
//			for(int i = 0; i < Gbest_a.length; i++){
//				System.out.print(Gbest_a[i]+"\t");
//			}
//			System.out.println();
			
		
	
			getPbest_s(evaluate_array, clent_p, grain_p);//获取领域最优解
			//System.out.print("t="+t+"时最优解评价值为"+Gbest+"粒子位子");
//			System.out.print((Gbest-basic_distance)+",");
//			System.out.println(gbest_g+"\t" + evaluate_array[gbest_g]);
//			for(int i = 0; i < evaluate_array.length; i++){
//				System.out.print(evaluate_array[i]+"\t");
//			}
			System.out.println("qqqqqq");
			
			for(int i = 0; i < Gbest_a.length; i++){
				if(Gbest_a[i]<0){
					System.out.print(0+"\t");
				}else if(Gbest_a[i]>car_num){
					System.out.print(car_num-1+"\t");
				}else {
					System.out.print(Math.floor(Gbest_a[i])+"\t");
				}
				
			}
			System.out.println();
			getStatusT(grain_v, grain_p, pbest_s, Gbest_a, history_p);
		}
	}
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Start....");
		pso pso = new pso();
//		for(int t=0 ; t < 9 ; t++){
//			Map<Integer, List<Integer>> clent_p = pso.getClent_p(pso.getClent_array(9), t);
//		}
		pso.init(25, 100, 200);
		pso.solve();
	}

}

