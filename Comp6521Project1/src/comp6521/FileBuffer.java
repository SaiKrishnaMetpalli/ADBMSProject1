package practice;

import java.io.BufferedReader;
import java.io.IOException;

public class FileBuffer {

	public BufferedReader fileBufferReader;
	private String cache;

	public FileBuffer(BufferedReader bufferedReader) throws IOException {
		this.fileBufferReader = bufferedReader;
		read();
	}

	/**
	 * close the buffered reader
	 */
	public void close() throws IOException {
		this.fileBufferReader.close();
	}

	/**
	 * whether reaches the end of file or not
	 */
	public boolean empty() {
		return this.cache == null;
	}

	/**
	 * get the most top line of the file
	 */
	public String getTopLine() {
		return this.cache;
	}

	/**
	 * move the current line pointer to the next line
	 */
	public String increasePointer() throws IOException {
		String result = getTopLine();
		read();
		return result;
	}

	/**
	 * read a line data from the file
	 */
	private void read() throws IOException {
		this.cache = this.fileBufferReader.readLine();
	}

}