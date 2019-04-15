
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.ArrayList;
import java.util.List;

public class Csv2Ris {
	static final String FILE_NAME = "file.csv";
	static final Charset ENCODING = StandardCharsets.UTF_8;

	public Csv2Ris() {
	}

//	public static void main(String[] args) throws IOException, InterruptedException {
	public static void export(File file) throws IOException, InterruptedException {
		Csv2Ris text = new Csv2Ris();
		String outputFileName = file.getParentFile().getAbsolutePath()+File.separator+file.getName().replaceFirst("[.][^.]+$", "")+".ris";

		List<String> lines = text.readSmallTextFile(file.getPath());
		List<String> urls = new ArrayList();
		List<String> bibs = new ArrayList();

		if (lines.size() == 0)
			System.exit(-1);
		for (String line : lines) {
			try {
				String[] fields = line.split("\",\"");
				
				String DOI = fields[5].replaceAll("\"", "");
				// String url = "https://citation-needed.springer.com/v2/references/" + DOI
					// 	+ "?format=bibtex&flavour=citation";
				
					String url = "https://citation-needed.springer.com/v2/references/" + DOI
					+ "?format=refman&flavour=citation";

				urls.add(url);

			} catch (Exception e) {
				System.out.println(line);
				e.printStackTrace();
			}
		}

		System.out.println("---------------------------------------");
		System.out.println("Found: " + lines.size() + " citations");
		if (urls.size() == 0)
			System.exit(-1);
		System.out.println("---------------------------------------");
		System.out.print("Starting...");
		Thread.sleep(3000L);
		System.out.println("---------------------------------------");
		int count = 1;
		URL uri;
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		fout = new FileOutputStream(outputFileName);
		try {
			for (String strUrl : urls) {
				System.out.println("Processing: " + count++);
				System.out.println(strUrl);

								in = new BufferedInputStream(new URL(strUrl).openStream());
				

				final byte data[] = new byte[1024];
				int count1;
				while ((count1 = in.read(data, 0, 1024)) != -1) {
					fout.write(data, 0, count1);
				}

			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.close();
			}
		}

		System.out.println("---------------------------------------");
		System.out.println("Workaround: adding spaces between \"=\"");
		Object bibs2 = new ArrayList();
		for (String bib : bibs) {
			String temp = bib.replaceAll("(=)", " = ");
			System.out.println(temp);
			((List) bibs2).add(temp);
		}

//		if (((List) bibs2).size() == 0)
//			//System.exit(-1);
//		System.out.println("---------------------------------------");
//		System.out.println("Writing output file...");
//		System.out.println("---------------------------------------");
//		text.writeSmallTextFile((List) bibs2, outputFileName);
//		System.out.println("---------------------------------------");
//		System.out.println("DONE!");
	}

	static final String OUTPUT_FILE_NAME = "file.bib";

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
