package comp6521;

import java.io.File;
import java.io.FileInputStream;
import java.io.LineNumberReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CreateBitmap {
	MergeFiles merge = new MergeFiles();

	public void createIndex() {
		File file = null;
		long tuplesInFile = 0;
		int maxTuplesInMem = 0;
//		FileChannel channel = null;
		TreeMap<Integer, ArrayList<Integer>> bitMap = null;
		Map<String, TreeMap<Integer, ArrayList<Integer>>> bitMaps = null;

		for (String fileName : TPMMSConstants.INPUT_FILE) {

			try (FileChannel channel = new FileInputStream(TPMMSConstants.TUPLES_FILE_PATH + "\\" + fileName)
					.getChannel()) {
				System.gc();
				boolean flag = true;
				int start = 0;
				file = new File(TPMMSConstants.TUPLES_FILE_PATH + "\\" + fileName);
				tuplesInFile = (file.length() + 2) / TPMMSConstants.SIZE_OF_TUPLE;
				long tuples = tuplesInFile;
				maxTuplesInMem = (int) (Runtime.getRuntime().freeMemory() / (TPMMSConstants.SIZE_OF_TUPLE)) / 4;

				int j = 0;
				int i = 0;

				while (flag) {
					j++;
					bitMap = new TreeMap<Integer, ArrayList<Integer>>();
					bitMaps = new HashMap<String, TreeMap<Integer, ArrayList<Integer>>>();
					flag = false;
					MappedByteBuffer buffer = null;
					if (tuplesInFile > 0) {
						buffer = channel.map(FileChannel.MapMode.READ_ONLY, start,
								((tuplesInFile < maxTuplesInMem ? (tuplesInFile * TPMMSConstants.SIZE_OF_TUPLE) - 2
										: maxTuplesInMem * TPMMSConstants.SIZE_OF_TUPLE)));
						byte[] data = new byte[TPMMSConstants.SIZE_OF_TUPLE];

						while (buffer.hasRemaining()) {
							if (buffer.remaining() < TPMMSConstants.SIZE_OF_TUPLE && buffer.remaining() != 100) {
								break;
							}
							flag = true;
							i++;
							buffer.get(data, 0,
									buffer.remaining() >= TPMMSConstants.SIZE_OF_TUPLE ? TPMMSConstants.SIZE_OF_TUPLE
											: buffer.remaining());

							for (String key : TPMMSConstants.INDEX_KEYS) {
								if (bitMaps.containsKey(key)) {
								} else {
									bitMaps.put(key, new TreeMap<Integer, ArrayList<Integer>>());
								}
								if (!bitMaps.get(key).containsKey(Integer.valueOf(new String(
										Arrays.copyOfRange(data, Utils.getStart(key), Utils.getEnd(key)))))) {
									bitMaps.get(key)
											.put(Integer.valueOf(new String(
													Arrays.copyOfRange(data, Utils.getStart(key), Utils.getEnd(key)))),
													new ArrayList<Integer>());
								}
								(bitMaps.get(key)
										.get(Integer.valueOf(new String(
												Arrays.copyOfRange(data, Utils.getStart(key), Utils.getEnd(key))))))
														.add(i);

							}
						}

						if ((buffer.capacity() / 102.0) > 0) {
							Utils.writeBitmap(bitMaps, TPMMSConstants.TUPLES_FILE_PATH, j, tuples, fileName);
							System.gc();
						}
						tuplesInFile = (tuplesInFile * TPMMSConstants.SIZE_OF_TUPLE - buffer.capacity())
								/ TPMMSConstants.SIZE_OF_TUPLE;
						start = start + buffer.capacity();
						buffer.clear();
					}
				}
				System.gc();
				MergeIndex merge = new MergeIndex();
				merge.processBitmaps(fileName, tuples);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

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
