package comp6521;

import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		ReadSortFile rf=new ReadSortFile();
		String filePath=Paths.get("").toAbsolutePath().toString() + "\\resources\\";
		rf.readFileAndSort(filePath,"MainSample1");
	}

}
