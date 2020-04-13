package comp6521;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class CreateCompressedBitmap {

	public void createCompressedEmpIdIndex(String fileName, long noOfOriginalTuples,long startTime) {
		BufferedWriter bufferedWriter = null;
		long tuplesInFile = 0;
		int maxTuplesInMem = 0;
		TreeMap<Integer, ArrayList<Integer>> bitMap = null;
		try {
			for (String keyName : TPMMSConstants.INDEX_KEYS) {
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
						TPMMSConstants.FINAL_INDEX_FILE_PATH + keyName + "_compressed_" + fileName, false), "UTF-8"));

				File file = new File(Utils.getFileName(fileName, keyName));
				int lineSize = Utils.getLineSize(keyName, noOfOriginalTuples);
				tuplesInFile = (file.length()) / lineSize;
				maxTuplesInMem = (int) (Runtime.getRuntime().freeMemory() / lineSize) / 2;
				FileChannel channel = new FileInputStream(Utils.getFileName(fileName, keyName)).getChannel();
				boolean flag = true;
				int start = 0;
				String currentLine = "";
				currentLine.intern();
				while (flag) {
					bitMap = new TreeMap<Integer, ArrayList<Integer>>();
					flag = false;
					MappedByteBuffer buffer = null;
					if (tuplesInFile > 0) {
						buffer = channel.map(FileChannel.MapMode.READ_ONLY, start,
								((tuplesInFile < maxTuplesInMem ? (tuplesInFile * lineSize)
										: maxTuplesInMem * lineSize)));
						byte[] data = new byte[(int) lineSize];
						String compressed = "";
						compressed.intern();
						String duplicate = "";
						duplicate.intern();
						while (buffer.hasRemaining()) {
							flag = true;
							buffer.get(data, 0, (int) lineSize);
							currentLine = new String(data);
							int count = 0;
							compressed = "";
							String[] bits = currentLine.replace("[", "").replace("]", "")
									.substring(Utils.getEnd(keyName) - Utils.getStart(keyName) + 1).split(",");
							for (String bit : bits) {
								if (Integer.parseInt(bit.trim()) == 0) {
									count++;
								} else {
									compressed = compressed + Integer.toBinaryString(count) + "0";
									count = 0;
								}

							}
							duplicate = compressed;
							int oneCount = duplicate.length() - duplicate.replaceAll("1", "").length();
							String addOnes = "";

							for (int i = 0; i < oneCount - 1; i++) {
								addOnes = addOnes + "1";
							}
							addOnes = addOnes + "0";
							compressed = addOnes + compressed;
							bufferedWriter
									.write(currentLine.substring(0, Utils.getEnd(keyName) - Utils.getStart(keyName) + 1)
											+ compressed.substring(0, compressed.length() - 1));
							bufferedWriter.newLine();
						}

						tuplesInFile = (tuplesInFile * lineSize - buffer.capacity()) / lineSize;
						start = start + buffer.capacity();
						buffer.clear();
					}

				}
				System.out.print("Compressed Index Created for :: "+ keyName +" :: File :: "+ fileName+" Time elasped :: ");
				System.out.println((new Date().getTime() - startTime) + " milliseconds\n");
				bufferedWriter.flush();
				bufferedWriter.close();
				System.gc();
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
