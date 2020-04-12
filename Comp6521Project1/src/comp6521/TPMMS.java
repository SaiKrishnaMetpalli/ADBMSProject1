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
		bitmap.createIndex(TPMMSConstants.TUPLES_FILE_PATH);
		bitmap.createGenderIndex(TPMMSConstants.TUPLES_FILE_PATH);
		bitmap.createDeptIndex(TPMMSConstants.TUPLES_FILE_PATH);
		System.gc();
		CreateCompressedBitmap compBitmap=new CreateCompressedBitmap();
		compBitmap.createCompressedEmpIdIndex();
		System.gc();
		MergeFiles merge = new MergeFiles();
		merge.removeDuplicate();
		System.out.print("Files Sorted :: Time elasped :: ");
		System.out.println((new Date().getTime() - start) + " milliseconds");

		TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
		System.out.print("Merging Done :: ");

		System.out.print("Total Time elasped ");
		System.out.println(new Date().getTime() - start + " milliseconds");
		System.out.println("Total Disk I/O " + diskIo);
	}

}
