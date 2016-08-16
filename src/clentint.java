import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class clentint {
	int clent_num;
	
	clentint(int c){
		this.clent_num = c;
	}
	
	//д�����ݵ��ļ�
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
	
	// �������λ��
	public String randomInitClient(int city_lh) {

		String str = "";
		int[] temp = new int[4];// �����洢�û�����ʼ����յ㡣
		
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
		
	// ��ȡ�ϳ����´�����
	public int[][] getClientStart(String filePath, int n, int m) throws IOException {
		int[][] clientstart = new int[clent_num][2];
		String strbuff;
		@SuppressWarnings("resource")
		BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
		for (int i = 0; i < clent_num; i++) {
			// ��ȡһ�����ݣ����ݸ�ʽ1 6734 1453
			strbuff = data.readLine();
			// �ַ��ָ�
			String[] strcol = strbuff.split(" ");
			clientstart[i][0] = Integer.valueOf(strcol[n]);
			clientstart[i][1] = Integer.valueOf(strcol[m]);
		}
		return clientstart;
	}
	
	// ��ȡ�ͻ���㵽�յ�ľ���
	public int[] getClientBasicValue(int[][] clientstart, int[][] clientdone) {
		int[] clientBasicValue = new int[clent_num];
		for (int i = 0; i < clent_num; i++) {
			double temp = Math.sqrt(((clientstart[i][0] - clientdone[i][0]) * (clientstart[i][0] - clientdone[i][0])
					+ (clientstart[i][1] - clientdone[i][1]) * (clientstart[i][1] - clientdone[i][1])));
			int inttemp = (int) Math.round(temp);
			if (inttemp < temp) {// ��������
				clientBasicValue[i] = inttemp + 1;
			} else {
				clientBasicValue[i] = inttemp;
			}
		}
		return clientBasicValue;
	}	
	//�ͻ������ ,�ǶԳ�
	public int[][] getClientCostValue(int[][] clientstart, int[][] clientdone) {
		int[][] clientCostValue = new int[clent_num][clent_num];

		for (int i = 0; i < clent_num - 1; i++) {
			for (int j = i; j < clent_num - 1; j++) {
				if (i != j) {
					double temp = Math.pow((Math.pow((clientstart[i][0] - clientdone[j][0]), 2)
							+ Math.pow((clientstart[i][1] - clientdone[j][1]), 2)), 0.5);
					int inttemp = (int) Math.round(temp);
					if (inttemp < temp) {// ��������
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
					if (inttemp < temp) {// ��������
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
		clientCostValue[clientstart[0].length - 1][clientstart[0].length - 1] = 0;

		return clientCostValue;
	}
	
	/*������׮�ľ���
	 * homeCostValue[0][i] ���׮���ϳ���ľ���
	 * homeCostValue[1][i] ���׮���³���ľ���
	 */
	public int[][] getHomeCostValue(int[][] clientstart, int[][] clientdone, int x, int y) {
		int [][] homeCostValue = new int[2][clent_num];
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < clent_num; j++){
				if(i == 0){
					double temp = Math.pow((Math.pow((clientstart[j][0] - x), 2)
							+ Math.pow((clientstart[j][1] - y), 2)), 0.5);
					int inttemp = (int) Math.round(temp);
					if (inttemp < temp) {// ��������
						homeCostValue[i][j] = inttemp + 1;
					} else {
						homeCostValue[i][j] = inttemp;
					}
				}else if (i == 1) {
					double temp = Math.pow((Math.pow((clientdone[j][0] - x), 2)
							+ Math.pow((clientdone[j][1] - y), 2)), 0.5);
					int inttemp = (int) Math.round(temp);
					if (inttemp < temp) {// ��������
						homeCostValue[i][j] = inttemp + 1;
					} else {
						homeCostValue[i][j] = inttemp;
					}
				}
			}
		}
		return homeCostValue;
	}
}
