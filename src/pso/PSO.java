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
	private int MAX_GEN;// ��������
	private int scale;// ��Ⱥ��ģ

	private int cityNum; // �������������볤��
	private int t;// ��ǰ����

	private int[][] distance; // �������
	
	private int[][] oPopulation;// ����Ⱥ
	private ArrayList<ArrayList<SO>> listV;// ÿ�����ӵĳ�ʼ��������

	private int[][] Pd;// һ�����������г�����õĽ⣬
	private int[] vPd;// �������ֵ

	private int[] Pgd;// ��������Ⱥ�������ĵ���õĽ⣬ÿ�����Ӷ��ܼ�ס�Լ�����������ý�
	private int vPgd;// ��õĽ������ֵ
	private int bestT;// ��ѳ��ִ���

	private int[] fitness;// ��Ⱥ��Ӧ�ȣ���ʾ��Ⱥ�и����������Ӧ��

	private Random random;

	public PSO() {

	}

	/**
	 * constructor of GA
	 * 
	 * @param n
	 *            ��������
	 * @param g
	 *            ���д���
	 * @param w
	 *            Ȩ��
	 **/
	public PSO(int n, int g, int s, float w) {
		this.cityNum = n;
		this.MAX_GEN = g;
		this.scale = s;
		this.w = w;
	}

	// ��������һ��ָ��������Ա���ע�Ĵ���Ԫ���ڲ���ĳЩ���汣�־�Ĭ
	@SuppressWarnings("resource")
	/**
	 * ��ʼ��PSO�㷨��
	 * @param filename �����ļ��������ļ��洢���г��нڵ���������
	 * @throws IOException
	 */
	private void init(String filename) throws IOException {
		// ��ȡ����
		int[] x;
		int[] y;
		String strbuff;
		BufferedReader data = new BufferedReader(new InputStreamReader(
				new FileInputStream(filename)));
		distance = new int[cityNum][cityNum];
		x = new int[cityNum];
		y = new int[cityNum];
		for (int i = 0; i < cityNum; i++) {
			// ��ȡһ�����ݣ����ݸ�ʽ1 6734 1453
			strbuff = data.readLine();
			// �ַ��ָ�
			String[] strcol = strbuff.split(" ");
			x[i] = Integer.valueOf(strcol[1]);// x����
			y[i] = Integer.valueOf(strcol[2]);// y����
		}
		// ����������
		// ����Ծ������⣬������㷽��Ҳ��һ�����˴��õ���att48��Ϊ����������48�����У�������㷽��Ϊαŷ�Ͼ��룬����ֵΪ10628
		for (int i = 0; i < cityNum - 1; i++) {
			distance[i][i] = 0; // �Խ���Ϊ0
			for (int j = i + 1; j < cityNum; j++) {
				double rij = Math
						.sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j])
								* (y[i] - y[j])) / 10.0);
				// �������룬ȡ��
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

	// ��ʼ����Ⱥ������������ɰ취
	void initGroup() {
		int i, j, k;
		for (k = 0; k < scale; k++)// ���Ӹ���
		{
			oPopulation[k][0] = random.nextInt(65535) % cityNum;
			for (i = 1; i < cityNum;)// ���Ӹ���
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

				// raA��raB��һ��
				SO s = new SO(raA, raB);
				list.add(s);
			}

			listV.add(list);
		}
	}

	public int evaluate(int[] chr) {
		// 0123
		int len = 0;
		// ���룬��ʼ����,����1,����2...����n
		for (int i = 1; i < cityNum; i++) {
			len += distance[chr[i - 1]][chr[i]];
		}
		// ����n,��ʼ����
		len += distance[chr[cityNum - 1]][chr[0]];
		return len;
	}

	// ��һ�������������������ڱ���arr��ı���
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

	// ����������Ļ����������У���A-B=SS
	public ArrayList<SO> minus(int[] a, int[] b) {
		int[] temp = b.clone();
		/*
		 * int[] temp=new int[L]; for(int i=0;i<L;i++) { temp[i]=b[i]; }
		 */
		int index;
		// ������
		SO s;
		// ��������
		ArrayList<SO> list = new ArrayList<SO>();
		for (int i = 0; i < cityNum; i++) {
			if (a[i] != temp[i]) {
				// ��temp���ҳ���a[i]��ͬ��ֵ���±�index
				index = findNum(temp, a[i]);
				// ��temp�н����±�i���±�index��ֵ
				changeIndex(temp, i, index);
				// ��ס������
				s = new SO(i, index);
				// ���潻����
				list.add(s);
			}
		}
		return list;
	}

	// ��arr�����в���num������num���±�
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

	// ������arr�±�index1���±�index2��ֵ����
	public void changeIndex(int[] arr, int index1, int index2) {
		int temp = arr[index1];
		arr[index1] = arr[index2];
		arr[index2] = temp;
	}

	// ��ά���鿽��
	public void copyarray(int[][] from, int[][] to) {
		for (int i = 0; i < scale; i++) {
			for (int j = 0; j < cityNum; j++) {
				to[i][j] = from[i][j];
			}
		}
	}

	// һά���鿽��
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
		
		// ����һ��
		for (t = 0; t < MAX_GEN; t++) {
			// ����ÿ������
			for (i = 0; i < scale; i++) {
				if(i==bestNum) continue;
				ArrayList<SO> Vii = new ArrayList<SO>();
				//System.out.println("------------------------------");
				// �����ٶ�
				// Vii=wVi+ra(Pid-Xid)+rb(Pgd-Xid)
				Vi = listV.get(i);

				// wVi+��ʾ��ȡVi��size*wȡ������������
				len = (int) (Vi.size() * w);
				//Խ���ж�
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
				//Խ���ж�
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
				//Խ���ж�
				//if(len>cityNum) len=cityNum;
				//System.out.println("ra:"+ra+" len:"+len+" b.size():"+b.size());
				for (j = 0; j < len; j++) {
					SO tt= b.get(j);
					Vii.add(tt);
				}
				
				//System.out.println("------------------------------Vii.size():"+Vii.size());

				// ������Vii
				listV.set(i, Vii);

				// ����λ��
				// Xid��=Xid+Vid
				add(oPopulation[i], Vii);
			}

			// ����������Ⱥ��Ӧ�ȣ�Fitness[max],ѡ����õĽ�
			for (k = 0; k < scale; k++) {
				fitness[k] = evaluate(oPopulation[k]);
				if (vPd[k] > fitness[k]) {
					vPd[k] = fitness[k];
					copyarrayNum(oPopulation[k], Pd[k]);
					bestNum=k;
				}
				if (vPgd > vPd[k]) {
					System.out.println("��ѳ���"+vPgd+" ������"+bestT);
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

		// ÿ�����Ӽ�ס�Լ���õĽ�
		copyarray(oPopulation, Pd);

		// �����ʼ����Ⱥ��Ӧ�ȣ�Fitness[max],ѡ����õĽ�
		for (k = 0; k < scale; k++) {
			fitness[k] = evaluate(oPopulation[k]);
			vPd[k] = fitness[k];
			if (vPgd > vPd[k]) {
				vPgd = vPd[k];
				copyarrayNum(Pd[k], Pgd);
				bestNum=k;
			}
		}

		// ��ӡ
		System.out.println("��ʼ����Ⱥ...");
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

		// ����
		evolution();

		// ��ӡ
		System.out.println("�������Ⱥ...");
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
		
		System.out.println("��ѳ��ȳ��ִ�����");
		System.out.println(bestT);
		System.out.println("��ѳ���");
		System.out.println(vPgd);
		System.out.println("���·����");
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
