package pso;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class PSO {

	private int bestNum;
	private float w;
	private int MAX_GEN;// 迭代次数
	private int scale;// 种群规模

	private int cityNum; // 城市数量，编码长度
	private int t;// 当前代数

	private int[][] distance; // 距离矩阵
	
	private int[][] oPopulation;// 粒子群
	private ArrayList<ArrayList<SO>> listV;// 每个粒子的初始交换序列

	private int[][] Pd;// 一颗粒子历代中出现最好的解，
	private int[] vPd;// 解的评价值

	private int[] Pgd;// 整个粒子群经历过的的最好的解，每个粒子都能记住自己搜索到的最好解
	private int vPgd;// 最好的解的评价值
	private int bestT;// 最佳出现代数

	private int[] fitness;// 种群适应度，表示种群中各个个体的适应度

	private Random random;

	public PSO() {

	}

	/**
	 * constructor of GA
	 * 
	 * @param n
	 *            城市数量
	 * @param g
	 *            运行代数
	 * @param w
	 *            权重
	 **/
	public PSO(int n, int g, int s, float w) {
		this.cityNum = n;
		this.MAX_GEN = g;
		this.scale = s;
		this.w = w;
	}

	// 给编译器一条指令，告诉它对被批注的代码元素内部的某些警告保持静默
	@SuppressWarnings("resource")
	/**
	 * 初始化PSO算法类
	 * @param filename 数据文件名，该文件存储所有城市节点坐标数据
	 * @throws IOException
	 */
	private void init(String filename) throws IOException {
		// 读取数据
		int[] x;
		int[] y;
		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));
		distance = new int[cityNum][cityNum];
		x = new int[cityNum];
		y = new int[cityNum];
		for (int i = 0; i < cityNum; i++) {
			// 读取一行数据，数据格式1 6734 1453
			strbuff = data.readLine();
			// 字符分割
			String[] strcol = strbuff.split(" ");
			x[i] = Integer.valueOf(strcol[1]);// x坐标
			y[i] = Integer.valueOf(strcol[2]);// y坐标
		}
		// 计算距离矩阵
		// ，针对具体问题，距离计算方法也不一样，此处用的是att48作为案例，它有48个城市，距离计算方法为伪欧氏距离，最优值为10628
		for (int i = 0; i < cityNum - 1; i++) {
			distance[i][i] = 0; // 对角线为0
			for (int j = i + 1; j < cityNum; j++) {
				double rij = Math
						.sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j])
								* (y[i] - y[j])) / 10.0);
				// 四舍五入，取整
				int tij = (int) Math.round(rij);
				if (tij < rij) {
					distance[i][j] = tij + 1;
					distance[j][i] = distance[i][j];
				} else {
					distance[i][j] = tij;
					distance[j][i] = distance[i][j];
				}
			}
		}
		distance[cityNum - 1][cityNum - 1] = 0;

		oPopulation = new int[scale][cityNum];
		fitness = new int[scale];

		Pd = new int[scale][cityNum];
		vPd = new int[scale];

		/*
		 * for(int i=0;i<scale;i++) { vPd[i]=Integer.MAX_VALUE; }
		 */

		Pgd = new int[cityNum];
		vPgd = Integer.MAX_VALUE;

		// nPopulation = new int[scale][cityNum];

		bestT = 0;
		t = 0;

		random = new Random(System.currentTimeMillis());
		/*
		 * for(int i=0;i<cityNum;i++) { for(int j=0;j<cityNum;j++) {
		 * System.out.print(distance[i][j]+","); } System.out.println(); }
		 */

	}

	// 初始化种群，多种随机生成办法
	void initGroup() {
		int i, j, k;
		for (k = 0; k < scale; k++)// 粒子个数
		{
			oPopulation[k][0] = random.nextInt(65535) % cityNum;
			for (i = 1; i < cityNum;)// 粒子个数
			{
				oPopulation[k][i] = random.nextInt(65535) % cityNum;
				for (j = 0; j < i; j++) {
					if (oPopulation[k][i] == oPopulation[k][j]) {
						break;
					}
				}
				if (j == i) {
					i++;
				}
			}
		}

		/*
		 * for(i=0;i<scale;i++) { for(j=0;j<cityNum;j++) {
		 * System.out.print(oldPopulation[i][j]+","); } System.out.println(); }
		 */
	}

	void initListV() {
		int ra;
		int raA;
		int raB;

		listV = new ArrayList<ArrayList<SO>>();

		for (int i = 0; i < scale; i++) {
			ArrayList<SO> list = new ArrayList<SO>();
			ra = random.nextInt(65535) % cityNum;
			for (int j = 0; j < ra; j++) {
				raA = random.nextInt(65535) % cityNum;
				raB = random.nextInt(65535) % cityNum;
				while (raA == raB) {
					raB = random.nextInt(65535) % cityNum;
				}

				// raA与raB不一样
				SO s = new SO(raA, raB);
				list.add(s);
			}

			listV.add(list);
		}
	}

	public int evaluate(int[] chr) {
		// 0123
		int len = 0;
		// 编码，起始城市,城市1,城市2...城市n
		for (int i = 1; i < cityNum; i++) {
			len += distance[chr[i - 1]][chr[i]];
		}
		// 城市n,起始城市
		len += distance[chr[cityNum - 1]][chr[0]];
		return len;
	}

	// 求一个基本交换序列作用于编码arr后的编码
	public void add(int[] arr, ArrayList<SO> list) {
		int temp = -1;
		SO s;
		for (int i = 0; i < list.size(); i++) {
			s = list.get(i);
			temp = arr[s.getX()];
			arr[s.getX()] = arr[s.getY()];
			arr[s.getY()] = temp;
		}
	}

	// 求两个编码的基本交换序列，如A-B=SS
	public ArrayList<SO> minus(int[] a, int[] b) {
		int[] temp = b.clone();
		/*
		 * int[] temp=new int[L]; for(int i=0;i<L;i++) { temp[i]=b[i]; }
		 */
		int index;
		// 交换子
		SO s;
		// 交换序列
		ArrayList<SO> list = new ArrayList<SO>();
		for (int i = 0; i < cityNum; i++) {
			if (a[i] != temp[i]) {
				// 在temp中找出与a[i]相同数值的下标index
				index = findNum(temp, a[i]);
				// 在temp中交换下标i与下标index的值
				changeIndex(temp, i, index);
				// 记住交换子
				s = new SO(i, index);
				// 保存交换子
				list.add(s);
			}
		}
		return list;
	}

	// 在arr数组中查找num，返回num的下标
	public int findNum(int[] arr, int num) {
		int index = -1;
		for (int i = 0; i < cityNum; i++) {
			if (arr[i] == num) {
				index = i;
				break;
			}
		}
		return index;
	}

	// 将数组arr下标index1与下标index2的值交换
	public void changeIndex(int[] arr, int index1, int index2) {
		int temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
	}

	// 二维数组拷贝
	public void copyarray(int[][] from, int[][] to) {
		for (int i = 0; i < scale; i++) {
			for (int j = 0; j < cityNum; j++) {
				to[i][j] = from[i][j];
			}
		}
	}

	// 一维数组拷贝
	public void copyarrayNum(int[] from, int[] to) {
		for (int i = 0; i < cityNum; i++) {
			to[i] = from[i];
		}
	}
	
	public void evolution() {
		int i, j, k;
		int len = 0;
		float ra = 0f;

		ArrayList<SO> Vi;
		
		// 迭代一次
		for (t = 0; t < MAX_GEN; t++) {
			// 对于每颗粒子
			for (i = 0; i < scale; i++) {
				if(i==bestNum) continue;
				ArrayList<SO> Vii = new ArrayList<SO>();
				//System.out.println("------------------------------");
				// 更新速度
				// Vii=wVi+ra(Pid-Xid)+rb(Pgd-Xid)
				Vi = listV.get(i);

				// wVi+表示获取Vi中size*w取整个交换序列
				len = (int) (Vi.size() * w);
				//越界判断
				//if(len>cityNum) len=cityNum;
				//System.out.println("w:"+w+" len:"+len+" Vi.size():"+Vi.size());
				for (j = 0; j < len; j++) {
					Vii.add(Vi.get(j));
				}

				// Pid-Xid
				ArrayList<SO> a = minus(Pd[i], oPopulation[i]);
				ra = random.nextFloat();

				// ra(Pid-Xid)+
				len = (int) (a.size() * ra);
				//越界判断
				//if(len>cityNum) len=cityNum;
				//System.out.println("ra:"+ra+" len:"+len+" a.size():"+a.size());
				for (j = 0; j < len; j++) {
					Vii.add(a.get(j));
				}

				// Pid-Xid
				ArrayList<SO> b = minus(Pgd, oPopulation[i]);
				ra = random.nextFloat();

				// ra(Pid-Xid)+
				len = (int) (b.size() * ra);
				//越界判断
				//if(len>cityNum) len=cityNum;
				//System.out.println("ra:"+ra+" len:"+len+" b.size():"+b.size());
				for (j = 0; j < len; j++) {
					SO tt= b.get(j);
					Vii.add(tt);
				}
				
				//System.out.println("------------------------------Vii.size():"+Vii.size());

				// 保存新Vii
				listV.set(i, Vii);

				// 更新位置
				// Xid’=Xid+Vid
				add(oPopulation[i], Vii);
			}

			// 计算新粒子群适应度，Fitness[max],选出最好的解
			for (k = 0; k < scale; k++) {
				fitness[k] = evaluate(oPopulation[k]);
				if (vPd[k] > fitness[k]) {
					vPd[k] = fitness[k];
					copyarrayNum(oPopulation[k], Pd[k]);
					bestNum=k;
				}
				if (vPgd > vPd[k]) {
					System.out.println("最佳长度"+vPgd+" 代数："+bestT);
					bestT = t;
					vPgd = vPd[k];
					copyarrayNum(Pd[k], Pgd);
				}
			}		
		}
	}

	public void solve() {
		int i;
		int k;

		initGroup();
		initListV();

		// 每颗粒子记住自己最好的解
		copyarray(oPopulation, Pd);

		// 计算初始化种群适应度，Fitness[max],选出最好的解
		for (k = 0; k < scale; k++) {
			fitness[k] = evaluate(oPopulation[k]);
			vPd[k] = fitness[k];
			if (vPgd > vPd[k]) {
				vPgd = vPd[k];
				copyarrayNum(Pd[k], Pgd);
				bestNum=k;
			}
		}

		// 打印
		System.out.println("初始粒子群...");
		for (k = 0; k < scale; k++) {
			for (i = 0; i < cityNum; i++) {
				System.out.print(oPopulation[k][i] + ",");
			}
			System.out.println();
			System.out.println("----" + fitness[k]);

			/*
			ArrayList<SO> li = listV.get(k);
			int l = li.size();
			for (i = 0; i < l; i++) {
				li.get(i).print();
			}

			System.out.println("----");
			*/
		}

		// 进化
		evolution();

		// 打印
		System.out.println("最后粒子群...");
		for (k = 0; k < scale; k++) {
			for (i = 0; i < cityNum; i++) {
				System.out.print(oPopulation[k][i] + ",");
			}
			System.out.println();
			System.out.println("----" + fitness[k]);

			/*
			ArrayList<SO> li = listV.get(k);
			int l = li.size();
			for (i = 0; i < l; i++) {
				li.get(i).print();
			}

			System.out.println("----");
			*/
		}
		
		System.out.println("最佳长度出现代数：");
		System.out.println(bestT);
		System.out.println("最佳长度");
		System.out.println(vPgd);
		System.out.println("最佳路径：");
		for (i = 0; i < cityNum; i++) {
			System.out.print(Pgd[i] + ",");
		}

	}
	

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Start....");

		PSO pso = new PSO(48, 5000, 30, 0.5f);
		pso.init("c://data.txt");
		pso.solve();
	}
}
