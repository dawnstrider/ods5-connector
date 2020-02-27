package net.sf.dawnstrider.odsconnector;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.springframework.stereotype.Component;

@Component
public class ConsoleWrapper {

	private PrintWriter pw;
	private BufferedReader reader;
	private Console c;

	public ConsoleWrapper() {
		c = System.console();
		if (c != null) {
			pw = c.writer();
			reader = new BufferedReader(c.reader());
		} else {
			pw = new PrintWriter(System.out);
			reader = new BufferedReader(new InputStreamReader(System.in));
		}
	}

	public void printLine(String msg) {
		pw.println(msg);
		pw.flush();
	}

	public String readLine() throws IOException {
		return readLine(null);
	}

	public String readLine(String reason, String def) throws IOException {
		if(reason != null) {
			if(def != null) {
				printLine(reason+"["+def+"]");
			}
			else {
				printLine(reason);
			}
		}
		String ret = reader.readLine();
		if(ret.length() == 0) {
			ret = def;
		}
		return ret;
	}

	public String readLine(String reason) throws IOException {
		return readLine(reason, null);
	}

	public String readPassword() throws IOException {
		return readPassword(null);
	}

	public String readPassword(String reason) throws IOException {
		if(reason != null) {
			printLine(reason);
		}
		String pw;
		if (c != null)
			pw = new String(c.readPassword());
		else
			pw = reader.readLine();
		return pw;
	}

}
