package comp6521;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TPMMS {

	public static void main(String[] args) throws IOException {

		ReadSortFile rf = new ReadSortFile();
		ArrayList<File> sortedFiles = new ArrayList<File>();
		sortedFiles = rf.readFileAndSort(TPMMSConstants.INPUT_FILE_PATH, sortedFiles);
		MergeFiles merge = new MergeFiles();
		File directory = new File(TPMMSConstants.OUTPUT_FILE_PATH);
		if (!directory.exists()) {
			directory.mkdir();
		}
		merge.process(TPMMSConstants.OUTPUT_FILE_PATH + TPMMSConstants.SORTED_FILE_NAME, sortedFiles);
	}

}

