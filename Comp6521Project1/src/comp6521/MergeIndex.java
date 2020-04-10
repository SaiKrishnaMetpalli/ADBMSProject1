package comp6521;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MergeIndex {
	private Comparator<String> idComparator = new Comparator<String>() {
		@Override
		public int compare(String r1, String r2) {
			return r1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)
					.compareTo(r2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID));
		}
	};

	public String processBitmaps(String fileName, long tuples) throws IOException {
		File outputFile = new File(TPMMSConstants.FINAL_INDEX_FILE_PATH + fileName + "_positionIndex.txt");
		if (!outputFile.exists()) {
			outputFile.getParentFile().mkdirs();
		}
		mergeSortedTmpFiles(outputFile);
		removeDuplicate(outputFile, fileName, tuples);
		outputFile.delete();
		return null;
	}

	private void mergeSortedTmpFiles(File outputFile) throws IOException {
		ArrayList<CustomBuffer> fileBufferList = new ArrayList<>();
		File file = new File(TPMMSConstants.INDEX_FILE_PATH);
		File[] files = file.listFiles();
		for (File f : files) {

			BufferedReader reader = Files.newBufferedReader(Paths.get(f.getAbsolutePath()));
			CustomBuffer fileBuffer = new CustomBuffer(reader);
			fileBufferList.add(fileBuffer);
		}
		outputFile.delete();
		BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outputFile, true), "UTF-8"));
		merge(bufferedWriter, fileBufferList);
		for (File f : files) {
			f.delete();
		}

	}

	private void merge(BufferedWriter fileBufferWriter, List<CustomBuffer> buffers) throws IOException {
		PriorityQueue<CustomBuffer> pq = new PriorityQueue<>(11, new Comparator<CustomBuffer>() {
			@Override
			public int compare(CustomBuffer fb1, CustomBuffer fb2) {
				return idComparator.compare(fb1.getTopLine(), fb2.getTopLine());
			}
		});

		for (CustomBuffer fileBuffer : buffers) {
			if (!fileBuffer.empty()) {
				TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
				pq.add(fileBuffer);
			}
		}
		try {
			while (pq.size() > 0) {

				CustomBuffer fileBuffer = pq.poll();
				String line = fileBuffer.increasePointer();
				fileBufferWriter.write(line);
				fileBufferWriter.newLine();
				if (fileBuffer.empty()) {
					fileBuffer.fileBufferReader.close();
				} else {
					pq.add(fileBuffer);
				}
			}
		} finally {
			fileBufferWriter.flush();
			fileBufferWriter.close();
			for (CustomBuffer fileBuffer : pq) {
				fileBuffer.close();
			}
		}
	}

	public void removeDuplicate(File file, String fileName, long tuples) throws IOException {
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;
		file.createNewFile();
		ArrayList<String> arr = new ArrayList<String>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(TPMMSConstants.FINAL_INDEX_FILE_PATH +fileName, false), "UTF-8"));
			String line1 = reader.readLine();
			String line2 = reader.readLine();
			line1.intern();
			line2.intern();
			while (line2 != null) {
				if (line1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)
						.compareTo(line2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)) == 0) {
					arr.addAll(Arrays.asList(line1.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split(",")));
					line1 = line2;
					line2 = reader.readLine();
				} else {
					arr.addAll(Arrays.asList(line1.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split(",")));
					Collections.sort(arr, new CustomComparator());
					Integer[] boxedArray = getBits(arr, (int) tuples);
					List<Integer> list = new ArrayList<>();
					Collections.addAll(list, boxedArray);
					bufferedWriter.write(line1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID) + ":" + Arrays.asList(boxedArray));
					arr.clear();
					bufferedWriter.newLine();
					line1 = line2;
					line2 = reader.readLine();

				}

			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			reader.close();
			bufferedWriter.close();
		}

	}

	private Integer[] getBits(ArrayList<String> entry, int tuplesInFile) {
		int v = tuplesInFile;
		Integer[] a = new Integer[20000];
		Arrays.fill(a, 0);
		entry.stream().forEach(val -> {
			a[Integer.parseInt(val.trim()) - 1] = 1;
		});
		return a;
	}
}

class CustomComparator implements Comparator<String> {
	@Override
	public int compare(String o1, String o2) {

		return Integer.compare(Integer.valueOf(o1.trim()), Integer.valueOf(o2.trim()));
	}
}