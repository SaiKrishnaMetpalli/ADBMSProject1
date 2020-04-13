package comp6521;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Map.Entry;

import java.util.TreeMap;

// Convert byte array to long
public class Utils {
	public static ArrayList<Integer> bits = null;
	public static BitSet b = null;
	public static StringBuilder sb = new StringBuilder("");
	public static int tt1Tuples = 0;
	public static int tt2Tuples = 0;

	public static void writeBitmapAsObject(TreeMap<Integer, ArrayList<Integer>> bitMap, String filePath, int count) {

		try {
			System.gc();
			FileOutputStream f = new FileOutputStream(new File(filePath + "_bitmap_" + count), false);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(bitMap);
			o.close();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void writeBitmap(Map<String, TreeMap<Integer, ArrayList<Integer>>> bitMaps, String filePath,
			int count, long tuplesInFile, String fileName) {
		for (String key : bitMaps.keySet()) {
			File file = new File(
					TPMMSConstants.INDEX_FILE_PATH + key + "\\" + fileName.replace(".txt", "\\") + "_bitmap_" + count);
			BufferedWriter bf = null;
			try {
				if (!file.exists()) {
					file.getParentFile().mkdirs();
				}
				bf = new BufferedWriter(new FileWriter(file));
				for (Entry<Integer, ArrayList<Integer>> entry : bitMaps.get(key).entrySet()) {

					bf.write(String.format("%0" + (Utils.getEnd(key) - Utils.getStart(key)) + "d", entry.getKey()) + ":"
							+ (entry.getValue().toString()).replace("[", "").replace("]", ""));
					bf.newLine();
				}
				System.gc();
				bf.flush();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					bf.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static Integer[] getBits(ArrayList<String> entry, int tuplesInFile) {
		Integer[] a = new Integer[tuplesInFile];
		Arrays.fill(a, 0);
		entry.stream().forEach(val -> {
			a[Integer.parseInt(val.trim()) - 1] = 1;
		});
		return a;
	}

	public static String[] concatenate(String[] a, String[] b) {
		int aLen = a.length;
		int bLen = b.length;

		String[] c = new String[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static int getStart(String key) {

		if (key.equals(TPMMSConstants.EMP_ID)) {
			return 0;
		} else if (key.equals(TPMMSConstants.GENDER)) {
			return 43;
		} else {
			return 44;
		}
	}

	public static int getEnd(String key) {

		if (key.equals(TPMMSConstants.EMP_ID)) {
			return 8;
		} else if (key.equals(TPMMSConstants.GENDER)) {
			return 44;
		} else {
			return 47;
		}
	}

	public static String getFileName(String fileName, String keyName) {
		sb.setLength(0);
		if (keyName.equals(TPMMSConstants.EMP_ID)) {
			return sb.append(TPMMSConstants.FINAL_INDEX_FILE_PATH).append(fileName).toString();
		} else if (keyName.equals(TPMMSConstants.GENDER)) {
			return sb.append(TPMMSConstants.FINAL_INDEX_FILE_PATH).append(keyName).append("_bitmap_").append(fileName)
					.toString();
		} else {
			return sb.append(TPMMSConstants.FINAL_INDEX_FILE_PATH).append(keyName).append("_bitmap_").append(fileName)
					.toString();
		}
	}

	public static int getLineSize(String keyName, long noOfOriginalTuples) {
		if (keyName.equals(TPMMSConstants.EMP_ID)) {
			return (int) (TPMMSConstants.LENGTH_OF_EMP_ID + 3 + noOfOriginalTuples + (2 * (noOfOriginalTuples - 1))
					+ 2);
		} else if (keyName.equals(TPMMSConstants.GENDER)) {
			return (int) (TPMMSConstants.LENGTH_OF_GENDER + 3 + noOfOriginalTuples + (2 * (noOfOriginalTuples - 1))
					+ 2);
		} else {
			return (int) (TPMMSConstants.LENGTH_OF_DEPT + 3 + noOfOriginalTuples + (2 * (noOfOriginalTuples - 1)) + 2);
		}
	}

	public static long getTuples(String fileName) {
		if (fileName.equals(TPMMSConstants.INPUT_FILE[0])) {
			return tt1Tuples;
		} else {
			return tt2Tuples;
		}
	}

	public static void setTuples(String fileName, long tuples) {
		if (fileName.equals(TPMMSConstants.INPUT_FILE[0])) {
			tt1Tuples = (int) tuples;
		} else {
			tt2Tuples = (int) tuples;
		}
	}
}