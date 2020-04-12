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
		bitmap.createIndex();
		System.gc();
		CreateCompressedBitmap compBitmap=new CreateCompressedBitmap();
		compBitmap.createCompressedEmpIdIndex(TPMMSConstants.EMP_ID);
		System.gc();
		
		System.out.print("Files Sorted :: Time elasped :: ");
		System.out.println((new Date().getTime() - start) + " milliseconds");
		
		MergeFiles merge = new MergeFiles();
		merge.removeDuplicate();

		TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
		System.out.print("Merging Done :: ");
		System.out.print("Total Time elasped ");
		System.out.println(new Date().getTime() - start + " milliseconds");
		System.out.println("Total Disk I/O " + diskIo);
	}

}
