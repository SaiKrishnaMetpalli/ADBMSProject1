package comp6521;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class CreateBitmap {
	int TUPLES_IN_BLOCK = 40;
	int SIZE_OF_TUPLE = 102;
	int size;
	MergeFiles merge = new MergeFiles();
	ArrayList<String> sortedFiles = new ArrayList<>();

	public void createIndex(String filePath) {
		File file = null;
		long tuplesInFile = 0;
		int maxTuplesInMem = 0;
		FileChannel channel = null;
		TreeMap<Integer, ArrayList<Integer>> bitMap = null;
		try {
			for (String fileName : TPMMSConstants.INPUT_FILE) {

				System.gc();
				boolean flag = true;
				int start = 0;
				file = new File(filePath + "\\" + fileName);
				tuplesInFile = (file.length() + 2) / 102;
				long t = tuplesInFile;
				maxTuplesInMem = (int) (Runtime.getRuntime().freeMemory() / (SIZE_OF_TUPLE)) / 4;
				channel = new FileInputStream(filePath + "\\" + fileName).getChannel();
				int j = 0;
				int i = 0;

				while (flag) {
					j++;
					bitMap = new TreeMap<Integer, ArrayList<Integer>>();

					flag = false;
					MappedByteBuffer buffer = null;
					if (tuplesInFile > 0) {
						buffer = channel.map(FileChannel.MapMode.READ_ONLY, start,
								((tuplesInFile < maxTuplesInMem ? (tuplesInFile * 102) - 2 : maxTuplesInMem * 102)));
						byte[] data = new byte[102];
						while (buffer.hasRemaining()) {
							if (buffer.remaining() < 102 && buffer.remaining() != 100) {
								break;
							}
							flag = true;
							i++;
							buffer.get(data, 0, buffer.remaining() >= 102 ? 102 : buffer.remaining());
							if (!bitMap.containsKey(Integer.valueOf(new String(Arrays.copyOfRange(data, 0, 8))))) {
								bitMap.put(Integer.valueOf(new String(Arrays.copyOfRange(data, 0, 8))),
										new ArrayList<Integer>());
							}
							(bitMap.get(Integer.valueOf(new String(Arrays.copyOfRange(data, 0, 8))))).add(i);
						}

						if ((buffer.capacity() / 102.0) > 0) {
//						quickS(0, bufferArray.length - 1);
//						writeToFile(bitMap, "bitmap");
							Utils.writeBitmap(bitMap, filePath, j, t);
							System.gc();
						}
						tuplesInFile = (tuplesInFile * 102 - buffer.capacity()) / 102;
						start = start + buffer.capacity();
						buffer.clear();
					}
				}
				System.gc();
				MergeIndex merge = new MergeIndex();
				merge.processBitmaps(fileName,t);
			}
			channel.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param data1
	 * @param fileName
	 */
	private void writeToFile(byte[][] data1, String fileName) {
		/*
		 * FileOutputStream fileOutputStream = null; try { fileOutputStream = new
		 * FileOutputStream(new File(fileName), true); for (int row = 0; row <
		 * bufferArray.length; row++) {
		 * 
		 * fileOutputStream.write(bufferArray[row]); } fileOutputStream.flush();
		 * fileOutputStream.close(); } catch (Exception e) { e.printStackTrace(); }
		 * finally { try { if (fileOutputStream != null) { fileOutputStream.close(); } }
		 * catch (Exception e2) { e2.printStackTrace(); } }
		 */}

	private void writeLogsToFile(byte[] data, int j) {

		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(new File("Logs_" + j), true);

			fileOutputStream.write(data);
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

	@SuppressWarnings("unchecked")
	private HashMap<String, BitSet> readBitmap(String filePath) {
		HashMap<String, BitSet> bitMap = null;
		try {
			File f = new File(filePath + "\\" + "bitmap");
			if (f.exists()) {
				FileInputStream fi = new FileInputStream(f);
				ObjectInputStream oi = new ObjectInputStream(fi);

				bitMap = (HashMap<String, BitSet>) oi.readObject();

				System.out.println("Read from file bitmap " + bitMap);

				oi.close();
				fi.close();
			} else {
				return new HashMap<String, BitSet>();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return bitMap;

	}

	public void quickS(int low, int high) {
		/*
		 * int mid = (low + high) / 2; int left = low; int right = high; byte[] pivot =
		 * bufferArray[mid]; // select middle element as pivot
		 * 
		 * while (left <= right) { while (Long.valueOf(new String(bufferArray[left], 0,
		 * 8)) < Long.valueOf(new String(pivot, 0, 8))) left++; while (Long.valueOf(new
		 * String(bufferArray[right], 0, 8)) > Long.valueOf(new String(pivot, 0, 8)))
		 * right--; if (left <= right) { byte[] temp = bufferArray[left];
		 * bufferArray[left] = bufferArray[right]; bufferArray[right] = temp; left++;
		 * right--; } } if (low < right) quickS(low, right); if (left < high)
		 * quickS(left, high);
		 */}

	public void combineIndex(String indexFilePath) {
		System.gc();
		System.out.println(
				"Process Started with " + Runtime.getRuntime().freeMemory() / (1024 * 1024) + " Mb free Main memory");
		try {
			ArrayList<LineNumberReader> indexBuffers = new ArrayList<LineNumberReader>();
			ArrayList<Integer> counters = new ArrayList<Integer>();
			ArrayList<String> currenLines = new ArrayList<String>();
			File file = new File(TPMMSConstants.INDEX_FILE_PATH);
			File[] files = file.listFiles();
			for (File f : files) {
				indexBuffers.add(new LineNumberReader(Files.newBufferedReader(Paths.get(f.getAbsolutePath()))));
				counters.add(1);
			}
			boolean flag = true;
			while (flag)
				for (int i = 0; i < indexBuffers.size(); i++) {
					LineNumberReader lineReader = indexBuffers.get(i);
					lineReader.mark(counters.get(i));

					currenLines.add(lineReader.readLine());
				}
			System.gc();
			for (int i = 0; i < indexBuffers.size(); i++) {
				LineNumberReader lineReader = indexBuffers.get(i);
				currenLines.add(lineReader.readLine());
			}
			final List<Integer> indexList = new ArrayList<>();
//			for (int i = 0; i < currenLines.size(); i++) {
//				if (obj.equals(currenLines.get(i))) {
//					indexList.add(i);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
