package comp6521;

import java.io.BufferedReader;
import java.io.IOException;

public class CustomBuffer {

	public BufferedReader fileBufferReader;
	private String value;

	public CustomBuffer(BufferedReader bufferedReader) throws IOException {
		this.fileBufferReader = bufferedReader;
		read();
	}

	public void close() throws IOException {
		this.fileBufferReader.close();
	}

	public boolean empty() {
		return this.value == null;
	}

	public String getTopLine() {
		return this.value;
	}

	public String increasePointer() throws IOException {
		String result = getTopLine();
		read();
		return result;
	}

	private void read() throws IOException {
		this.value = this.fileBufferReader.readLine();
	}

}