package comp6521;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Convert byte array to long
public class Utils {
	static ArrayList<Integer> bits = null;
	static BitSet b = null;
	static StringBuilder sb = new StringBuilder("");

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
	
	public static void writeBitmap(TreeMap<String, ArrayList<Integer>> bitMap, String filePath, int count,
			long tuplesInFile) {

		File file = new File(TPMMSConstants.INDEX_FILE_PATH + "_bitmap_" + count);
		BufferedWriter bf = null;
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			bf = new BufferedWriter(new FileWriter(file));
			for (Entry<String, ArrayList<Integer>> entry : bitMap.entrySet()) {

//				bf.write(entry.getKey() + ":" + getBits(entry, tuplesInFile).replace("[","").replace("]", ""));
				bf.write(entry.getKey() + ":" + (entry.getValue().toString()).replace("[", "").replace("]", ""));
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


	private static String getBits(Entry<Integer, ArrayList<Integer>> entry, long tuplesInFile) {
		sb.setLength(0);
		for (int c = 0; c < tuplesInFile; c++) {
			sb.append('0');
//			bits.add(0);
		}
		entry.getValue().stream().forEach(val -> sb.replace(val - 1, (int) val, "1"));
		return sb.toString();
	}

	public static String[] concatenate(String[] a, String[] b) {
		int aLen = a.length;
		int bLen = b.length;

		String[] c = new String[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}
}