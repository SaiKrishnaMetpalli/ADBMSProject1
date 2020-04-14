package comp6521;

import java.io.IOException;
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
		System.out.println(
				"Process Started with " + Runtime.getRuntime().freeMemory() / (1024 * 1024) + " Mb free Main memory");
		long start = new Date().getTime();

		CreateBitmap bitmap = new CreateBitmap();
		bitmap.createIndex(start);
		System.gc();
		

		CreateCompressedBitmap compBitmap = new CreateCompressedBitmap();
		for (String fileName : TPMMSConstants.INPUT_FILE) {
			compBitmap.createCompressedEmpIdIndex(fileName, Utils.getTuples(fileName),start);
		}
		System.out.print("Compressed Bitmaps are created :: Time Took :: ");
		System.out.println((new Date().getTime() - start) + " milliseconds\n");
		System.out.print("Total Disk I/O till now :: ");
		System.out.println(TPMMS.getDiskIo()/2+ "\n");
		
		MergeFiles merge = new MergeFiles();
		merge.mergeFiles();
		System.out.print("Files Merged :: Time Took :: ");
		System.out.println((new Date().getTime() - start) + " milliseconds\n");
		
		System.out.println("Total Disk I/O " + diskIo);
	}

}
