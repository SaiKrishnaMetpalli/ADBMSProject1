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

	public void createIndex(long startTime) {
		File file = null;
		long tuplesInFile = 0;
		int maxTuplesInMem = 0;
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
				Utils.setTuples(fileName, tuples);
				maxTuplesInMem = (int) (Runtime.getRuntime().freeMemory() / (TPMMSConstants.SIZE_OF_TUPLE)) / 2;

				int j = 0;
				int i = 0;

				while (flag) {
					j++;
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
						TPMMS.setDiskIo(TPMMS.getDiskIo()
								+ 1);
						tuplesInFile = (tuplesInFile * TPMMSConstants.SIZE_OF_TUPLE - buffer.capacity())
								/ TPMMSConstants.SIZE_OF_TUPLE;
						start = start + buffer.capacity();
						buffer.clear();
					}
				}
				System.gc();
				MergeIndex merge = new MergeIndex();
				merge.processBitmaps(fileName, tuples, startTime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
