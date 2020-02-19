package comp6521;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class TPMMS {
	static int diskIo;

	public static int getDiskIo() {
		return diskIo;
	}

	public static void setDiskIo(int val) {
		diskIo = val;
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Process Started with "+Runtime.getRuntime().freeMemory()/(1024*1024) +" Mb free Main memory");
		long start = new Date().getTime();
		

		ReadSortFile rf = new ReadSortFile();
		ArrayList<File> sortedFiles = new ArrayList<File>();
		sortedFiles = rf.readFileAndSort(TPMMSConstants.INPUT_FILE_PATH, sortedFiles);
		System.out.print("Files Sorted :: Time elasped :: ");
		System.out.println((new Date().getTime() - start) + " milliseconds");
		MergeFiles merge = new MergeFiles();
		File directory = new File(TPMMSConstants.OUTPUT_FILE_PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}
		merge.process(TPMMSConstants.OUTPUT_FILE_PATH + TPMMSConstants.SORTED_FILE_NAME, sortedFiles);
		TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
		System.out.print("Merging Done :: ");

		System.out.print("Total Time elasped ");
		System.out.println(new Date().getTime() - start + " milliseconds");
		System.out.println("Total Disk I/O "+diskIo);
	}

}
