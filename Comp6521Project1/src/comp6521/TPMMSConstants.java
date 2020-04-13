package comp6521;

public interface TPMMSConstants {

	public static final String TMP_FILE_PATH = "./resources/input/tmp/";
	public static final String TUPLES_FILE_PATH = "./resources/input/";
	public static final String INPUT_PATH = "./resources/input/";
	public static final String OUTPUT_FILE_PATH = "./resources/output/result.txt";
	public static final String INDEX_FILE_PATH = "./resources/input/tmp_index/";
	public static final String FINAL_INDEX_FILE_PATH = "./resources/index/";
	public static final String SORTED_FILE_NAME = "sorted.txt";
	public static final String FINAL_FILE_NAME = "result.txt";
	public static final String EMP_ID = "empId";
	public static final String GENDER = "gender";
	public static final String DEPT = "dept";
	public static final String[] INPUT_FILE = { "tt1.txt", "tt2.txt" };
	public static final String[] INDEX_KEYS = { EMP_ID, GENDER, DEPT };
	public static final int LENGTH_OF_EMP_ID = 8;
	public static final int LENGTH_OF_GENDER = 1;
	public static final int LENGTH_OF_DEPT = 3;
	public static final int diskIo = 0;
	public static final int TUPLES_IN_BLOCK = 40;
	public static final int SIZE_OF_TUPLE = 102;

}