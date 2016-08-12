import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;


public class pso {
	
	private int clent_num; //客户个数
	private int grain_num; //粒子个数
	private int car_num; //车辆个数
	private double [][] grain_p; //粒子的位置向量
	private double [][] grain_v; //粒子的速度向量
	private int [] gbest_s; //全局最优解
	
	private int[] clent_array; //客户链表
	private Map<Integer, List<Integer>> clent_p; //客户邻域
	
	
	//获取客户的邻域
	private int[] getClent_array(int c) {
		// TODO Auto-generated method stub
		int[] clent_array = new int[c]; 
		for (int i = 0; i < c; i++){
			clent_array[i] = i;
		}
		return clent_array;
	}
	
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
		for(int t=0 ; t < 9 ; t++){
			Map<Integer, List<Integer>> clent_p = pso.getClent_p(pso.getClent_array(9), t);
		}
	}

}

