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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class MergeFiles {

	private Comparator<String> idComparator = new Comparator<String>() {
		@Override
		public int compare(String r1, String r2) {
			return r1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)
					.compareTo(r2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID));
		}
	};

	public String process(String fileName, List<File> tmpFileList) throws IOException {
		File outputFile = new File(fileName);
		mergeSortedTmpFiles(tmpFileList, outputFile);
		removeDuplicate(outputFile);
		outputFile.delete();
		return null;
	}

	private void mergeSortedTmpFiles(List<File> fileList, File outputFile) throws IOException {
		ArrayList<CustomQueue> fileBufferList = new ArrayList<>();
		for (File file : fileList) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			CustomQueue fileBuffer = new CustomQueue(reader);
			fileBufferList.add(fileBuffer);
		}
		outputFile.delete();
		BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outputFile, true), "UTF-8"));
		merge(bufferedWriter, fileBufferList);
		for (File file : fileList) {
			file.delete();
		}

	}

	private void merge(BufferedWriter fileBufferWriter, List<CustomQueue> buffers) throws IOException {
		PriorityQueue<CustomQueue> pq = new PriorityQueue<>(11, new Comparator<CustomQueue>() {
			@Override
			public int compare(CustomQueue fb1, CustomQueue fb2) {
				return idComparator.compare(fb1.getTopLine(), fb2.getTopLine());
			}
		});

		for (CustomQueue fileBuffer : buffers) {
			if (!fileBuffer.empty()) {
				pq.add(fileBuffer);
			}
		}
		try {
			while (pq.size() > 0) {

				CustomQueue fileBuffer = pq.poll();
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
			fileBufferWriter.close();
			for (CustomQueue fileBuffer : pq) {
				fileBuffer.close();
			}
		}
	}

	public void removeDuplicate(File file) throws IOException {
		BufferedReader reader = null;

		BufferedWriter bufferedWriter = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(TPMMSConstants.OUTPUT_FILE_PATH + TPMMSConstants.FINAL_FILE_NAME, false),
					"UTF-8"));
			String line1 = reader.readLine();
			String line2 = reader.readLine();
			while (line2 != null) {
				if (line1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)
						.compareTo(line2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)) == 0) {

					line1 = line2;
					line2 = reader.readLine();
					if (line2 == null) {
						bufferedWriter.write(line1);
					}
				} else {
					bufferedWriter.write(line1);
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

}