package comp6521;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class MergeFiles {
	ArrayList<Integer> positions = new ArrayList<Integer>();

	public void mergeFiles() throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		byte[] tuple1 = null;
		byte[] tuple2 = null;
		int[] pos1 = null;
		int[] pos2 = null;
		FileChannel channel1 = null;
		FileChannel channel2 = null;
		MappedByteBuffer buffer1 = null;
		MappedByteBuffer buffer2 = null;
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(TPMMSConstants.OUTPUT_FILE_PATH, false), "UTF-8"));
			BufferedReader index1 = Files
					.newBufferedReader(Paths.get(TPMMSConstants.FINAL_INDEX_FILE_PATH + TPMMSConstants.INPUT_FILE[0]));
			BufferedReader index2 = Files
					.newBufferedReader(Paths.get(TPMMSConstants.FINAL_INDEX_FILE_PATH + TPMMSConstants.INPUT_FILE[1]));

			String line1 = index1.readLine();
			String line2 = index2.readLine();
			line1.intern();
			line2.intern();

			channel1 = new FileInputStream(TPMMSConstants.TUPLES_FILE_PATH + TPMMSConstants.INPUT_FILE[0]).getChannel();
			channel2 = new FileInputStream(TPMMSConstants.TUPLES_FILE_PATH + TPMMSConstants.INPUT_FILE[1]).getChannel();

			while (line1 != null || line2 != null) {
				if (line1 != null && line2 != null) {
					if (Long.valueOf(line1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID))
							.compareTo(Long.valueOf(line2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID))) == 0) {

						pos1 = getIndex(line1.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split("(?!^)"));
						pos2 = getIndex(line2.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split("(?!^)"));
						tuple1 = new byte[100];
						tuple2 = null;

						for (int i = 0; i < pos1.length; i++) {
							buffer1 = channel1.map(FileChannel.MapMode.READ_ONLY, pos1[i] * 102, 100);
							buffer1.get(tuple1, 0, 100);
							if (tuple2 == null) {
								tuple2 = tuple1.clone();
							} else {
								if (sdf.parse(new String(Arrays.copyOfRange(tuple1, 8, 18)))
										.compareTo(sdf.parse(new String(Arrays.copyOfRange(tuple2, 8, 18)))) > 0) {
									tuple2 = tuple1.clone();
								}
							}
							TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
						}

						for (int i = 0; i < pos2.length; i++) {
							buffer2 = channel2.map(FileChannel.MapMode.READ_ONLY, pos2[i] * 102, 100);
							buffer2.get(tuple1, 0, 100);
							if (tuple2 == null) {
								tuple2 = tuple1.clone();
							} else {
								if (sdf.parse(new String(Arrays.copyOfRange(tuple1, 8, 18)))
										.compareTo(sdf.parse(new String(Arrays.copyOfRange(tuple2, 8, 18)))) > 0) {
									tuple2 = tuple1.clone();
								}
							}
							TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
						}

						line1 = index1.readLine();
						line2 = index2.readLine();
					} else if (Long.valueOf(line1.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID))
							.compareTo(Long.valueOf(line2.substring(0, TPMMSConstants.LENGTH_OF_EMP_ID))) > 0) {
						pos2 = getIndex(line2.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split("(?!^)"));
						tuple1 = new byte[102];
						tuple2 = null;

						for (int i = 0; i < pos2.length; i++) {
							buffer2 = channel2.map(FileChannel.MapMode.READ_ONLY, pos2[i] * 102, 100);
							buffer2.get(tuple1, 0, 100);
							if (tuple2 == null) {
								tuple2 = tuple1.clone();
							} else {
								if (sdf.parse(new String(Arrays.copyOfRange(tuple1, 8, 18)))
										.compareTo(sdf.parse(new String(Arrays.copyOfRange(tuple2, 8, 18)))) > 0) {
									tuple2 = tuple1.clone();
								}
							}
							TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
						}
						line2 = index2.readLine();
					} else {
						pos1 = getIndex(line1.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split("(?!^)"));
						tuple1 = new byte[102];
						tuple2 = null;

						for (int i = 0; i < pos1.length; i++) {
							buffer1 = channel1.map(FileChannel.MapMode.READ_ONLY, pos1[i] * 102, 100);
							buffer1.get(tuple1, 0, 100);
							if (tuple2 == null) {
								tuple2 = tuple1.clone();
							} else {
								if (sdf.parse(new String(Arrays.copyOfRange(tuple1, 8, 18)))
										.compareTo(sdf.parse(new String(Arrays.copyOfRange(tuple2, 8, 18)))) > 0) {
									tuple2 = tuple1.clone();
								}
							}
							TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
						}

						line1 = index1.readLine();
					}
				} else if (line2 != null) {

					pos2 = getIndex(line2.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split("(?!^)"));
					tuple1 = new byte[102];
					tuple2 = null;

					for (int i = 0; i < pos2.length; i++) {
						buffer2 = channel2.map(FileChannel.MapMode.READ_ONLY, pos2[i] * 102, 100);
						buffer2.get(tuple1, 0, 100);
						if (tuple2 == null) {
							tuple2 = tuple1.clone();
						} else {
							if (sdf.parse(new String(Arrays.copyOfRange(tuple1, 8, 18)))
									.compareTo(sdf.parse(new String(Arrays.copyOfRange(tuple2, 8, 18)))) > 0) {
								tuple2 = tuple1.clone();
							}
						}
						TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
					}
					line2 = index2.readLine();

				} else {
					pos1 = getIndex(line1.substring(TPMMSConstants.LENGTH_OF_EMP_ID + 1).split("(?!^)"));
					tuple1 = new byte[102];
					tuple2 = null;

					for (int i = 0; i < pos1.length; i++) {
						buffer1 = channel1.map(FileChannel.MapMode.READ_ONLY, pos1[i] * 102, 100);
						buffer1.get(tuple1, 0, 100);
						if (tuple2 == null) {
							tuple2 = tuple1.clone();
						} else {
							if (sdf.parse(new String(Arrays.copyOfRange(tuple1, 8, 18)))
									.compareTo(sdf.parse(new String(Arrays.copyOfRange(tuple2, 8, 18)))) > 0) {
								tuple2 = tuple1.clone();
							}
						}
						TPMMS.setDiskIo(TPMMS.getDiskIo() + 1);
					}

					line1 = index1.readLine();
				}
				bufferedWriter.write(new String(Arrays.copyOfRange(tuple2, 0, 100)));
				bufferedWriter.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			bufferedWriter.close();
			channel1.close();
			channel2.close();
		}
	}

	private int[] getIndex(String[] bits) {
		positions.clear();
		return IntStream.range(0, bits.length).filter(i -> 1 == Integer.parseInt(bits[i].trim())).toArray();
	}
}