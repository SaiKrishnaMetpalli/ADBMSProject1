package comp6521;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CreateCompressedBitmap {

	public void createCompressedEmpIdIndex() {
		BufferedWriter bufferedWriter = null;
		try {
			for(String fileName:TPMMSConstants.INPUT_FILE) {
				bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(TPMMSConstants.FINAL_INDEX_FILE_PATH + fileName.substring(0,3) + 
								"_EmpIDCompressed.txt", false), "UTF-8"));
				BufferedReader brRead = Files
						.newBufferedReader(Paths.get(TPMMSConstants.FINAL_INDEX_FILE_PATH + fileName));
				String compressed="";
				String duplicate="";
				int count=0;
				String line=brRead.readLine();
				while(line!=null) {
					compressed="";
					String[] bits=line.replace("[", "").replace("]", "").substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split(",");
					for(String bit:bits) {
						if(bit.trim().equals("0")) {
							count++;
						} else {
							compressed=compressed+Integer.toBinaryString(count)+"0";
							count=0;
						}
					}
					duplicate=compressed;
					int oneCount=duplicate.length()-duplicate.replaceAll("1", "").length();
					String addOnes="";
					for(int i=0;i<oneCount-1;i++) {
						addOnes=addOnes+"1";
					}
					addOnes=addOnes+"0";
					compressed=addOnes+compressed;
					bufferedWriter.write(line.substring(0,9)+compressed.substring(0,compressed.length()-1));
					bufferedWriter.newLine();
					line=brRead.readLine();
				}
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
