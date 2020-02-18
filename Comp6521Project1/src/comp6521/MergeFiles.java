package comp6521;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

public class MergeFiles {

	/**
	 * an comparator used to compare the EmpId
	 */
	private Comparator<String> idComparator = new Comparator<String>() {
		@Override
		public int compare(String r1, String r2) {
			return r1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)
					.compareTo(r2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID));
		}
	};

	public String process(String fileName, List<File> tmpFileList) throws IOException {
		System.out.println("1");
		File outputFile = new File(fileName);
		mergeSortedTmpFiles(tmpFileList, outputFile);
		return null;
	}

	private void mergeSortedTmpFiles(List<File> fileList, File outputFile) throws IOException {
		System.out.println("2 " + outputFile.getName());
		ArrayList<FileBuffer> fileBufferList = new ArrayList<>();
		for (File file : fileList) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			FileBuffer fileBuffer = new FileBuffer(reader);
			fileBufferList.add(fileBuffer);
		}
		outputFile.delete();
		BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outputFile, true), "UTF-8"));
		merge(bufferedWriter, fileBufferList);
		System.out.println("3 " + outputFile.getName());
//		for (File file : fileList) {
//			System.out.println("4 "+outputFile.getName());
//			file.delete();
//		}

	}

	private void merge(BufferedWriter fileBufferWriter, List<FileBuffer> buffers) throws IOException {
		System.out.println("4 ");
		PriorityQueue<FileBuffer> pq = new PriorityQueue<>(11, new Comparator<FileBuffer>() {
			@Override
			public int compare(FileBuffer fb1, FileBuffer fb2) {
				return idComparator.compare(fb1.getTopLine(), fb2.getTopLine());
			}
		});

		for (FileBuffer fileBuffer : buffers) {
			System.out.println("5 ");
			if (!fileBuffer.empty()) {
				System.out.println("6 ");
				pq.add(fileBuffer);
			}
		}
		try {
			while (pq.size() > 0) {

				FileBuffer fileBuffer = pq.poll();
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
			for (FileBuffer fileBuffer : pq) {
				fileBuffer.close();
			}
		}
	}

	public void finalMerge(BufferedWriter fileBufferWriter, String fileName1, String fileName2) throws IOException {
		File file1 = new File(fileName1);
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
		FileBuffer buffer1 = new FileBuffer(reader1);

		File file2 = new File(fileName2);
		BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
		FileBuffer buffer2 = new FileBuffer(reader2);
		try {
			String line1 = buffer1.increasePointer();
			String line2 = buffer2.increasePointer();
			while (!buffer1.empty() || !buffer2.empty()) {
//System.out.println("line1 "+line1);
//System.out.println("line2 "+line2);
				if (line1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)
						.compareTo(line2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID)) == 0) {

					if (compareDates(line1.substring(8, 18), line2.substring(8, 18)) > 0) {
						fileBufferWriter.write(line1);
						fileBufferWriter.newLine();
						if (buffer1.empty()) {
							buffer1.fileBufferReader.close();
						} else {
							line1 = buffer1.increasePointer();
						}
					} else if (compareDates(line1.substring(8, 18), line2.substring(8, 18)) < 0) {
						fileBufferWriter.write(line2);
						fileBufferWriter.newLine();
						if (buffer2.empty()) {
							buffer2.fileBufferReader.close();
						} else {
							line2 = buffer2.increasePointer();
						}

					} else {

						fileBufferWriter.write(line2);
						fileBufferWriter.newLine();
						if (buffer1.empty()) {
							buffer1.fileBufferReader.close();
						} else {
							line1 = buffer1.increasePointer();
						}
						if (buffer2.empty()) {
							buffer2.fileBufferReader.close();
						} else {
							line2 = buffer2.increasePointer();
						}

					}
				}

			}
		} finally {
			buffer1.close();
			buffer2.close();
		}

	}

	public static long compareDates(String timestamp1, String timestamp2) {
		long epoch1 = 0;
		long epoch2 = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dt1 = sdf.parse(timestamp1);
			epoch1 = dt1.getTime();

			Date dt2 = sdf.parse(timestamp2);
			epoch2 = dt2.getTime();

		} catch (ParseException e) {
		}
		return (epoch1 - epoch2);

	}
}
