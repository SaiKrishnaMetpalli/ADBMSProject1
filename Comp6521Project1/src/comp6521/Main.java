package comp6521;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		ReadSortFile rf=new ReadSortFile();
		String filePath=Paths.get("").toAbsolutePath().toString() + "\\resources\\";
		ArrayList<File> sortedFiles1=rf.readFileAndSort(filePath,"MainSample1");
		ArrayList<File> sortedFiles2=rf.readFileAndSort(filePath, "MainSmaple2");
		MergeFiles mf=new MergeFiles();
		File t1=mf.mergeSortedFiles(sortedFiles1);
		File t2=mf.mergeSortedFiles(sortedFiles2);
	}

}
