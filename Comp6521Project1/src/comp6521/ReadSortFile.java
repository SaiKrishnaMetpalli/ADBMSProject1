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

public class ReadSortFile {

	public ArrayList<File> readFileAndSort(String filePath,String fileName) {
		try {			
			File file = new File(filePath+fileName);
			RandomAccessFile reader = new RandomAccessFile(file, "r");
			FileChannel fileChannel = reader.getChannel();

			int blocksInFile = getNumberOfLines(file) / 40;
			int maxBlocksInMem = (int) (Runtime.getRuntime().freeMemory() / 14000); 

			ArrayList<String> data = new ArrayList<String>();
			ArrayList<File> sortedFiles=new ArrayList<File>();

			int numberOfPass = (int) Math.ceil( blocksInFile / (float) maxBlocksInMem);
			int start = 0;
			for (int j = 0; j < numberOfPass; j++) {
				
				fileChannel.position(start);
				int capacity = blocksInFile < maxBlocksInMem ? (blocksInFile * 40 * 102)-2 : maxBlocksInMem * 40 * 102;
				ByteBuffer buffer = ByteBuffer.allocate(capacity);
				do {
					fileChannel.read(buffer);
					
				} while (buffer.hasRemaining());
				
				ByteArrayInputStream stream = new ByteArrayInputStream(buffer.array());
				InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				buffer.clear();
				String line=bufferedReader.readLine();
				
				while (line!= null) {
					data.add(line);
					line=bufferedReader.readLine();
				}
				stream.close();
				streamReader.close();
				bufferedReader.close();
				Collections.sort(data);
				File fObj=new File(filePath+"sorted_MainSample_"+j+".txt");
				PrintStream ps=new PrintStream(fObj);
				for(int s=0;s<data.size();s++) {
					if(s+1<data.size()) {
						if((data.get(s).substring(0,8).compareTo(data.get(s+1).substring(0,8)))!=0) {
							ps.println(data.get(s));
						}
					} else {
						ps.println(data.get(s));
					}					
				}			
				ps.close();				
				data.clear();
				sortedFiles.add(fObj);
				start += maxBlocksInMem * 40 * 102;				
				blocksInFile -= maxBlocksInMem;
			}
			reader.close();
			return sortedFiles;
		} catch(Exception ex) {
			return null;
		}
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
