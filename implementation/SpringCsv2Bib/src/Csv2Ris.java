
import java.awt.Font;
import java.awt.TextArea;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JTextArea;

public class Csv2Ris {
	static final String FILE_NAME = "file.csv";
	static final Charset ENCODING = StandardCharsets.UTF_8;
	private static final String prefix = "http://link.springer.com/";

	public Csv2Ris() {
	}

	public static void export(File file, JTextArea output) throws IOException {
		Csv2Ris text = new Csv2Ris();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		String outputFileName = file.getParentFile().getAbsolutePath() + File.separator
				+ file.getName().replaceFirst("[.][^.]+$", "") + "_" + timeStamp + ".ris";

		String logFileName = file.getParentFile().getAbsolutePath() + File.separator
				+ file.getName().replaceFirst("[.][^.]+$", "") + "_" + timeStamp +".txt";
		List<String> lines = text.readSmallTextFile(file.getPath());
		List<String> urls = new ArrayList();
		List<String> bibs = new ArrayList();

		if (lines.size() == 0)
			System.exit(-1);
		for (String line : lines) {
			try {

				if (!line.contains(prefix)) {
					continue;
				}

				String DOI = line.substring(line.indexOf(prefix) + prefix.length());
				DOI = DOI.substring(DOI.indexOf("/") + 1, DOI.indexOf("\""));

				String url = "https://citation-needed.springer.com/v2/references/" + DOI
						+ "?format=refman&flavour=citation";

				urls.add(url);

			} catch (Exception e) {
				System.out.println(line);
				e.printStackTrace();
			}
		}
		int count = 1;
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		FileWriter logFile = new FileWriter(logFileName);
		
		try {
			setLog("---------------------------------------", false, output, logFile);
			setLog("CSV lines count: " + lines.size() + " citations founded: " + urls.size() + "\n", false, output, logFile);
			if (urls.size() == 0)
				System.exit(-1);
			setLog("---------------------------------------", false, output, logFile);
			setLog("Starting...\\n", false, output, logFile);
			Thread.sleep(3000L);
			setLog("---------------------------------------", false, output, logFile);
			fout = new FileOutputStream(outputFileName);
			 
			for (String strUrl : urls) {
				try {

					setLog("Processing: " + (count++) + "/" + urls.size() + "\n", false, output,logFile);
					setLog("Processing: " + strUrl, false, output, logFile);

					in = new BufferedInputStream(new URL(strUrl).openStream());

					final byte data[] = new byte[1024];
					int count1;
					while ((count1 = in.read(data, 0, 1024)) != -1) {
						fout.write(data, 0, count1);
					}

				} catch (IOException e1) {
					setLog("Erro in line " + count + " Erro:" + e1.getMessage(), true, output, logFile);
				} finally {
					
				}
			}

			Object bibs2 = new ArrayList();
			for (String bib : bibs) {
				String temp = bib.replaceAll("(=)", " = ");
				System.out.println(temp);
				((List) bibs2).add(temp);
			}
		} catch (IOException e1) {
			output.append("Erro in line " + count + " Erro:" + e1.getMessage());
		} catch (InterruptedException e1) {
			output.append("Erro in line " + count + " Erro:" + e1.getMessage());
		}finally {
			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.close();
			}
		}

		setLog("---------------------------------------\nDONE", true, output, logFile);

	}

	private static void setLog(String log, boolean isErro, JTextArea output, FileWriter  logFile) throws IOException {

		System.out.println(log);
		logFile.write("\n"+log);

	}

	List<String> readSmallTextFile(String aFileName) {
		try {
			Path path = Paths.get(aFileName, new String[0]);
			return Files.readAllLines(path, ENCODING);
		} catch (IOException e) {
			System.out.println("use: java -jar SPRINGER_CSV2BIB.jar <file_in.csv> <file_out.bib>");
			e.printStackTrace();
		}
		return new ArrayList();
	}

	void writeSmallTextFile(List<String> aLines, String aFileName) {
		try {
			Path path = Paths.get(aFileName, new String[0]);
			Files.write(path, aLines, ENCODING, new java.nio.file.OpenOption[0]);
		} catch (IOException e) {
			System.out.println("use: java -jar SPRINGER_CSV2BIB.jar <file_in.csv> <file_out.bib>");
		}
	}
}
