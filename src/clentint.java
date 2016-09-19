import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class clentint {
	int clent_num;
	
	clentint(int c){
		this.clent_num = c;
	}
	
	//写入数据到文件
	public void WriteStringToFile(String filePath, String str) {
		try {
			FileWriter 	fileWriter = new FileWriter(filePath, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(str);
			bufferedWriter.close();
			fileWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	// 生成随机位置
	public String randomInitClient(int city_lh) {

		String str = "";
		int[] temp = new int[4];// 用来存储用户的起始点和终点。
		
		boolean flag = true;
		int clent_num = this.clent_num;
		while(clent_num >= 0){
			for (int j = 0; j < 4; j++) {
				temp[j] = (int) (Math.random() * city_lh);
				
			}
				str = str + " " + temp[0]+ " " + temp[1]+ " " + temp[2]+ " " + temp[3] + "\r\n";
				clent_num--;
		}
		return str;
	}
		
	// 读取上车、下次数据
	public int[][] getClientStart(String filePath, int n, int m) throws IOException {
		int[][] clientstart = new int[clent_num][2];
		String strbuff;
		@SuppressWarnings("resource")
		BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		for (int i = 0; i < clent_num; i++) {
			// 读取一行数据，数据格式1 6734 1453
			strbuff = data.readLine();
			// 字符分割
			String[] strcol = strbuff.split(" ");
			//System.out.println(strcol[2]);
			clientstart[i][0] = Integer.valueOf(strcol[n]);
			clientstart[i][1] = Integer.valueOf(strcol[m]);
		}
		return clientstart;
	}
	
	// 读取上车、下次数据
		public double[] getTime(String filePath, int n) throws IOException {
			double[] time = new double[clent_num];
			String strbuff;
			@SuppressWarnings("resource")
			BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			for (int i = 0; i < clent_num; i++) {
				// 读取一行数据，数据格式1 6734 1453
				strbuff = data.readLine();
				// 字符分割
				String[] strcol = strbuff.split(" ");
				//System.out.println(strcol[2]);
				time[i] = Double.valueOf(strcol[n]);
				
			}
			return time;
		}
	// 获取客户起点到终点的距离
	public int[] getClientBasicValue(int[][] clientstart, int[][] clientdone) {
		int[] clientBasicValue = new int[clent_num];
		for (int i = 0; i < clent_num; i++) {
			double temp = Math.sqrt(((clientstart[i][0] - clientdone[i][0]) * (clientstart[i][0] - clientdone[i][0])
					+ (clientstart[i][1] - clientdone[i][1]) * (clientstart[i][1] - clientdone[i][1])));
			int inttemp = (int) Math.round(temp);
			if (inttemp < temp) {// 四舍五入
				clientBasicValue[i] = inttemp + 1;
			} else {
				clientBasicValue[i] = inttemp;
			}
		}
		return clientBasicValue;
	}	
	//客户间距离 ,非对称
	public int[][] getClientCostValue(int[][] clientstart, int[][] clientdone) {
		int[][] clientCostValue = new int[clent_num][clent_num];

		for (int i = 0; i < clent_num - 1; i++) {
			for (int j = i; j < clent_num - 1; j++) {
				if (i != j) {
					double temp = Math.pow((Math.pow((clientstart[i][0] - clientdone[j][0]), 2)
							+ Math.pow((clientstart[i][1] - clientdone[j][1]), 2)), 0.5);
					int inttemp = (int) Math.round(temp);
					if (inttemp < temp) {// 四舍五入
						clientCostValue[i][j] = inttemp + 1;
					} else {
						clientCostValue[i][j] = inttemp;
					}
				} else {
					clientCostValue[i][j] = 0;
				}
				// System.out.println("dist_list["+i+"]["+j+"]"+clientBasicValue[i][j]);
			}
		}
		for (int i = (clent_num - 1); i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (i != j) {
					double temp = Math.pow((Math.pow((clientstart[i][0] - clientdone[j][0]), 2)
							+ Math.pow((clientstart[i][1] - clientdone[j][1]), 2)), 0.5);
					int inttemp = (int) Math.round(temp);
					if (inttemp < temp) {// 四舍五入
						clientCostValue[i][j] = inttemp + 1;
					} else {
						clientCostValue[i][j] = inttemp;
					}
				} else {
					clientCostValue[i][j] = 0;

				}
				// System.out.println("dist_list["+i+"]["+j+"]"+clientCostValue[i][j]);
			}
		}

		clientCostValue[clent_num-1][clent_num-1] = 0;

		return clientCostValue;
	}
	
	/*距离充电桩的距离
	 * homeCostValue[0][i] 充电桩到上车点的距离
	 * homeCostValue[1][i] 充电桩到下车点的距离
	 */
	public int[][] getHomeCostValue(int[][] clientstart, int[][] clientdone, int x, int y) {
		int [][] homeCostValue = new int[2][clent_num];
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < clent_num; j++){
				if(i == 0){
					double temp = Math.pow((Math.pow((clientstart[j][0] - x), 2)
							+ Math.pow((clientstart[j][1] - y), 2)), 0.5);
					int inttemp = (int) Math.round(temp);
					if (inttemp < temp) {// 四舍五入
						homeCostValue[i][j] = inttemp + 1;
					} else {
						homeCostValue[i][j] = inttemp;
					}
				}else if (i == 1) {
					double temp = Math.pow((Math.pow((clientdone[j][0] - x), 2)
							+ Math.pow((clientdone[j][1] - y), 2)), 0.5);
					int inttemp = (int) Math.round(temp);
					if (inttemp < temp) {// 四舍五入
						homeCostValue[i][j] = inttemp + 1;
					} else {
						homeCostValue[i][j] = inttemp;
					}
				}
			}
		}
		return homeCostValue;
	}
	
	public String creatTime(int[] clientBasicValue,int[][] homeCostValue){
		String time = "";
		Random random = new Random();
		for(int i = 0; i < clent_num; i++){
			double r1 = 1 - random.nextDouble()*2;
			double a0 = homeCostValue[0][i] + r1 * 10;
			double b0 = homeCostValue[0][i] + clientBasicValue[i] + 300*random.nextDouble() + 30;
			time = time+ " " + a0 + " " + b0 + "\r\n";
		}
		return time;
	}
	
//	public static void main(String[] args) throws IOException {
//		clentint clentinit = new clentint(200);
//		int[][] clientstart = clentinit.getClientStart("src/data.txt", 1, 2);
//		int[][] clientdone = clentinit.getClientStart("src/data.txt", 3, 4);
//		int[][] homeCostValue = clentinit.getHomeCostValue(clientstart, clientdone, 25, 25);
//		int[] clientBasicValue = clentinit.getClientBasicValue(clientstart, clientdone);
//		String time = clentinit.creatTime(clientBasicValue, homeCostValue);
//		clentinit.WriteStringToFile("src/data2.txt", time);
//	}
}
