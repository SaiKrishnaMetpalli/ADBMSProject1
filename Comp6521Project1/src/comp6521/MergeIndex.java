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
import java.util.Date;
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

	public void processBitmaps(String fileName, long tuples, long startTime) throws IOException {
		for (String keyName : TPMMSConstants.INDEX_KEYS) {
			File outputFile = new File(
					TPMMSConstants.FINAL_INDEX_FILE_PATH + keyName+"_pos_" + fileName);
			if (!outputFile.exists()) {
				outputFile.getParentFile().mkdirs();
			}
			mergeSortedTmpFiles(outputFile, keyName,fileName);
			mergeIndexFiles(outputFile, fileName, tuples, keyName);
			outputFile.delete();
			System.out.print("Index Created for :: "+ keyName +" :: File :: "+ fileName+" Time elasped :: ");
			System.out.println((new Date().getTime() - startTime) + " milliseconds\n");
		}
		System.out.print("Total Disk I/O till now :: ");
		System.out.println(TPMMS.getDiskIo()+ "\n");
	}

	private void mergeSortedTmpFiles(File outputFile, String keyName, String fileName) throws IOException {
		ArrayList<CustomBuffer> fileBufferList = new ArrayList<>();
		File file = new File(TPMMSConstants.INDEX_FILE_PATH + keyName + "\\" + fileName.replace(".txt", "\\"));
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

	public void mergeIndexFiles(File file, String fileName, long tuples, String keyName) throws IOException {
		BufferedReader reader = null;
		BufferedWriter bufferedWriter = null;
		file.createNewFile();
		ArrayList<String> arr = new ArrayList<String>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(Utils.getFileName(fileName, keyName), false), "UTF-8"));
			String line1 = reader.readLine();
			String line2 = reader.readLine();
			line1.intern();
			line2.intern();
			while (line1 != null) {
				arr.addAll(
						Arrays.asList(line1.substring(Utils.getEnd(keyName) - Utils.getStart(keyName) + 1).split(",")));
				if (line1 != null && line2 != null) {
					if (line1.substring(0, Utils.getEnd(keyName) - Utils.getStart(keyName))
							.compareTo(line2.substring(0, Utils.getEnd(keyName) - Utils.getStart(keyName))) != 0) {
						Collections.sort(arr, new CustomComparator());
						bufferedWriter.write(line1.substring(0, Utils.getEnd(keyName) - Utils.getStart(keyName)) + ":"
								+ Utils.getBits(arr, (int) tuples));
						arr.clear();
						bufferedWriter.newLine();
					}
				} else {
					Collections.sort(arr, new CustomComparator());
					bufferedWriter.write(line1.substring(0, Utils.getEnd(keyName) - Utils.getStart(keyName)) + ":"
							+ Utils.getBits(arr, (int) tuples));
					arr.clear();
					bufferedWriter.newLine();
				}
				line1 = line2;
				line2 = reader.readLine();
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
}

class CustomComparator implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		return Integer.compare(Integer.valueOf(o1.trim()), Integer.valueOf(o2.trim()));
	}
}