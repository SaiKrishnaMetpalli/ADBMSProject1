package comp6521;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadSortFile {
	int size;
	MergeFiles merge = new MergeFiles();
	ArrayList<String> sortedFiles = new ArrayList<>();

	public ArrayList<File> readFileAndSort(String filePath, ArrayList<File> sortedFiles) {
		File file = null;
		RandomAccessFile reader = null;
		FileChannel fileChannel = null;
		int blocksInFile = 0;
		int maxBlocksInMem = 0;
		int numberOfPass = 0;
		int start = 0;
		ArrayList<String> data = new ArrayList<String>();
		try {
			for (String fileName : TPMMSConstants.INPUT_FILE) {

				file = new File(filePath + "\\" + fileName);
				reader = new RandomAccessFile(file, "r");
				fileChannel = reader.getChannel();

				blocksInFile = getNumberOfLines(file) / 40;
				maxBlocksInMem = (int) (Runtime.getRuntime().freeMemory() / 14000);

				data.clear();

				numberOfPass = (int) Math.ceil(blocksInFile / (float) maxBlocksInMem);
				start = 0;

				for (int j = 0; j < numberOfPass; j++) {

					fileChannel.position(start);
					int capacity = blocksInFile < maxBlocksInMem ? (blocksInFile * 40 * 102) - 2
							: maxBlocksInMem * 40 * 102;
					ByteBuffer buffer = ByteBuffer.allocate(capacity);
					do {
						fileChannel.read(buffer);

					} while (buffer.hasRemaining());

					ByteArrayInputStream stream = new ByteArrayInputStream(buffer.array());
					InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
					BufferedReader bufferedReader = new BufferedReader(streamReader);
					buffer.clear();
					String line = bufferedReader.readLine();

					while (line != null) {
						data.add(line);
						line = bufferedReader.readLine();
					}
					stream.close();
					streamReader.close();
					bufferedReader.close();
					Collections.sort(data);
					if ((data.size() * 102) > size) {
						size = (int) (data.size() * 102);
					}
					File directory = new File(TPMMSConstants.TMP_FILE_PATH);
					if (!directory.exists()) {
						directory.mkdir();
					}
					File fObj = new File(TPMMSConstants.TMP_FILE_PATH + fileName + "_sorted_" + j + ".tmp");
					PrintStream ps = new PrintStream(fObj);
					for (int s = 0; s < data.size(); s++) {
						if (s + 1 < data.size()) {
							if ((data.get(s).substring(0, 8).compareTo(data.get(s + 1).substring(0, 8))) != 0) {
								ps.println(data.get(s));
							}
						} else {
							ps.println(data.get(s));
						}
					}
					ps.close();
					data.clear();
					start += maxBlocksInMem * 40 * 102;
					blocksInFile -= maxBlocksInMem;
					sortedFiles.add(fObj);
				}
				reader.close();

			}
		} catch (Exception ex) {
		}
		return sortedFiles;
	}

	private List<FileChannel> getmergeFileChannels(String filePath, int numberOfPass,
			List<FileChannel> mergeChannelList) throws FileNotFoundException {

		for (int j = 0; j < numberOfPass; j++) {
			File fObj = new File(filePath + "sorted_MainSample_" + j + ".txt");
			RandomAccessFile mergeReader = new RandomAccessFile(fObj, "r");
			FileChannel mergeChannel = mergeReader.getChannel();
			mergeChannelList.add(mergeChannel);

		}
		return mergeChannelList;
	}

	private int getNumberOfLines(File file) throws FileNotFoundException, IOException {
		BufferedReader bfReader = new BufferedReader(new FileReader(file));
		int lines = 0;
		while (bfReader.readLine() != null)
			lines++;
		bfReader.close();
		return lines;
	}

}
