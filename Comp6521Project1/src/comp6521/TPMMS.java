package comp6521;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TPMMS {
	static int diskIo;

	public static int getDiskIo() {
		return diskIo;
	}

	public static void setDiskIo(int val) {
		diskIo = val;
	}

	public static void main(String[] args) throws IOException {

		System.out.println("Process Started");
		ReadSortFile rf = new ReadSortFile();
		ArrayList<File> sortedFiles = new ArrayList<File>();
		sortedFiles = rf.readFileAndSort(TPMMSConstants.INPUT_FILE_PATH, sortedFiles);
		System.out.println("Files Sorted");
		MergeFiles merge = new MergeFiles();
		File directory = new File(TPMMSConstants.OUTPUT_FILE_PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}
		merge.process(TPMMSConstants.OUTPUT_FILE_PATH + TPMMSConstants.SORTED_FILE_NAME, sortedFiles);
		TPMMS.setDiskIo(TPMMS.getDiskIo()+1);
		System.out.println("Duplicates removed and files merged :: with DISK IO :: " + TPMMS.getDiskIo());
	}

}
