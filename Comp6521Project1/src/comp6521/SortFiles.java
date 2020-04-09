package comp6521;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SortFiles {
	int TUPLES_IN_BLOCK = 40;
	int SIZE_OF_TUPLE = 102;
	int size;
	MergeFiles merge = new MergeFiles();
	ArrayList<String> sortedFiles = new ArrayList<>();
	byte[][] bufferArray = null;

	public ArrayList<File> readFileAndSort(String filePath, ArrayList<File> sortedFiles) {
		File file = null;
		double tuplesInFile = 0;
		int maxTuplesInMem = 0;
		ArrayList<String> data = new ArrayList<String>();
		FileChannel channel = null;
		try {
			for (String fileName : TPMMSConstants.INPUT_FILE) {
				System.gc();
				boolean flag = true;
				int start = 0;
				file = new File(filePath + "\\" + fileName);
				tuplesInFile = Math.ceil((file.length() + 2) / (102));
				System.out.println("tuplesInFile " + tuplesInFile);
				maxTuplesInMem = (int) (Runtime.getRuntime().freeMemory() / (SIZE_OF_TUPLE)) / 3;
				System.out.println("maxTuplesInMem " + maxTuplesInMem);
				channel = new FileInputStream(filePath + "\\" + fileName).getChannel();
				int j = 0;
				while (flag) {
					flag = false;
					MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, start,
							((int) (tuplesInFile < maxTuplesInMem ? (tuplesInFile * 102) - 2 : maxTuplesInMem * 102)));
//					if (bufferArray == null)
					bufferArray = new byte[(int) Math.ceil(buffer.capacity() / 102)][102];
//						System.out.println("(buffer.capacity() " + (buffer.capacity() / 102));
//						System.out.println("Math.ceil(buffer.capacity() / 102) "+Math.ceil(buffer.capacity() / 102));
					int i = -1;

					while (buffer.hasRemaining()) {
						if (buffer.remaining() < 102)
							break;
						flag = true;
						i++;
						int rem = buffer.remaining() > 102 ? 102 : buffer.remaining();
//						System.out.println("rem "+rem);
						buffer.get(bufferArray[i], 0, rem);
					}
//				Arrays.sort(bufferArray, new Comparator<byte[]>() {
//					@Override
//					public int compare(byte[] o1, byte[] o2) {
//						return Long.compare(Long.valueOf(new String(o1, 0, 8)), Long.valueOf(new String(o2, 0, 8)));
//					}
//				});

//		 			System.out.println("if ((buffer.capacity() / 102) > 0) " + ((buffer.capacity() / 102) > 0));
					if ((buffer.capacity() / 102) > 0) {
						quickS(0, bufferArray.length - 1);
						writeToFile(bufferArray, TPMMSConstants.TMP_FILE_PATH + fileName + "_sorted_" + (++j) + ".tmp");
					}
//					System.out.println("blocksInFile "+blocksInFile);
					tuplesInFile = tuplesInFile - buffer.capacity() / 102;
					start = (buffer.capacity() / 102) * 102;
//					System.out.println("blocksInFile after "+blocksInFile);
//					System.out.println("buffer "+buffer.capacity() / (TUPLES_IN_BLOCK * SIZE_OF_TUPLE));

				}

			}
			channel.close();
			System.gc();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sortedFiles;
	}

	/**
	 * @param data1
	 * @param fileName
	 */
	private void writeToFile(byte[][] data1, String fileName) {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(new File(fileName), true);
			for (int row = 0; row < bufferArray.length; row++) {

				fileOutputStream.write(bufferArray[row]);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public void quickS(int low, int high) {
		int mid = (low + high) / 2;
		int left = low;
		int right = high;
		byte[] pivot = bufferArray[mid]; // select middle element as pivot

		while (left <= right) {
			while (Long.valueOf(new String(bufferArray[left], 0, 8)) < Long.valueOf(new String(pivot, 0, 8)))
				left++;
			while (Long.valueOf(new String(bufferArray[right], 0, 8)) > Long.valueOf(new String(pivot, 0, 8)))
				right--;
			if (left <= right) {
				byte[] temp = bufferArray[left];
				bufferArray[left] = bufferArray[right];
				bufferArray[right] = temp;
				left++;
				right--;
			}
		}
		if (low < right)
			quickS(low, right);
		if (left < high)
			quickS(left, high);
	}
}
