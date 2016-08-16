import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.x500.X500Principal;


public class pso {
	
	private int clent_num; //客户个数
	private int grain_num; //粒子个数
	private int car_num; //车辆个数
	private double [][] grain_p; //粒子的位置向量
	private double [][] grain_v; //粒子的速度向量
	private int [] gbest_s; //全局最优解
	private int [][] pbest_s; //局域最优解
	private int[] evaluate_list; //粒子评价值数组
	
	private int[] clent_array; //客户链表
	private Map<Integer, List<Integer>> clent_p; //客户邻域
	
	private Random random ;
	
	//获取客户的邻域
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
		this.grain_p = new double [grain_num][clent_num];
		this.grain_v = new double [grain_num][clent_num];
		for (int i = 0; i < grain_num; i++) {
			random = new Random(i);
			for (int j = 0; j < clent_num; j++) {
				this.grain_p[i][j] = random.nextDouble() * car_num;
				//System.out.print(this.grain_p[i][j]+"\t");
				this.grain_v[i][j] =car_num - random.nextDouble() * car_num *2;
				
			}
			//System.out.println();
		}
	}
	
	
	/*
	 * int[] clientBasicValue 客户上车点和下车点距离
	 * int[][] clientCostValue 客户间距离
	 * double[][] grain_p 粒子位子矩阵
	 */
	private void getEvaluateList(int[] clientBasicValue, int[][] clientCostValue, int[][] HomeCostValue, double[][] grain_p) {
		evaluate_list = new int[grain_num];
		for (int i = 0; i < grain_num; i++){
			double[] grainPosition_i = new double[clent_num];
			
			//向下取整
			for (int j = 0; j < clent_num; j++){
				grainPosition_i[j] = Math.floor(grain_p[i][j]);
			}
			
			//求解粒子i的评价值
			evaluate_list[i] = 0;
			for (int j = 0; j < car_num; j++) {
				List<Integer> carClentList = new ArrayList<>();
				for (int j2 = 0; j2 < grainPosition_i.length; j2++) {
					if(grainPosition_i[j2] == j){
						carClentList.add(j2);
					}
				}
				if (carClentList.size() > 1) {
					for (int j3 = 0 ; j3 < carClentList.size()-1; j3++) {
						evaluate_list[i] = evaluate_list[i] + clientCostValue[carClentList.get(j3)][carClentList.get(j3+1)];
					}
				}
				if (carClentList.size() != 0) {
						evaluate_list[i] = evaluate_list[i] + HomeCostValue[0][carClentList.get(0)];
						evaluate_list[i] = evaluate_list[i] + HomeCostValue[1][carClentList.get(carClentList.size()-1)];
				}
			}
			
		}
	}
	
	//获取时间t时的粒子速度、粒子位置
	
	
	//获取客户的邻域
	private Map<Integer, List<Integer>> getClent_p(int[] clent_array, int t) {
		// TODO Auto-generated method stub
		Map<Integer, List<Integer>> clent_p = new HashMap<Integer, List<Integer>>();
		int clent_num = clent_array.length;
		for (int i=0; i<clent_num; i++){
			System.out.print("t="+t+"时粒子"+i+"的邻域：\t");
			List<Integer> temp = new ArrayList<>();
			temp.add(clent_array[i]);
			System.out.print(clent_array[i]+"\t");
			if(2*(t+1) < clent_num){
				for (int x = 0; x <= t ;x++) {
					if(i-x-1 < 0) {
						temp.add(clent_array[i-x-1+clent_num]);
						System.out.print(clent_array[i-x-1+clent_num]+"\t");
					}else {
						temp.add(clent_array[i-x-1]);
						System.out.print(clent_array[i-x-1]+"\t");
					}
					
					if(i+x+1 >= clent_num) {
						temp.add(clent_array[i+x+1-clent_num]);
						System.out.print(clent_array[i+x+1-clent_num]+"\t");
					}else {
						temp.add(clent_array[i+x+1]);
						System.out.print(clent_array[i+x+1]+"\t");
					}
				}
			}else {
				for (int ii = 0; ii<clent_num; ii++){
					if(ii != i){
						temp.add(clent_array[ii]);
						System.out.print(clent_array[ii]+"\t");
					}
				}
			}
			clent_p.put(i, temp);
			System.out.println();
		}
		return clent_p;
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
		pso.init(3, 4, 6);
	}

}

