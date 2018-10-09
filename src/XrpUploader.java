import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.SortedSet;
import java.util.TreeSet;

public class XrpUploader {
	private static final String FILE_NAME_XRP_PROCESSED_FILES_TXT = "xrp-processed-files.txt";
	private static final String FILE_NAME_XRP_UNPROCESSED_FILES_TXT = "xrp-unprocessed-files.txt";

	/**
	 * 
	 * @param directory
	 *            to be scanned
	 * @param status
	 *            file
	 * @param name
	 *            -fields
	 * @throws Exception
	 * 
	 */
	public static void main(String[] args) throws Exception {
		if (args == null || args.length != 2) {
			System.out.println("Use: XrpUploader <url> <diretorio>");
			System.exit(400);
		}

		String directory = args[0];
		String url = args[1];
		File dir = new File(directory);

		System.out.println("Lendo diretório:                    " + directory);
		SortedSet<File> set = listFileTree(dir);
		System.out.println("Arquivos no diretório:              " + set.size());

		SortedSet<String> processados = readLog();
		System.out.println("Arquivos processados anteriormente: "
				+ processados.size());

		SortedSet<File> setARemover = new TreeSet<>();
		for (File f : set) {
			if (processados.contains(f.getAbsolutePath()))
				setARemover.add(f);
		}
		set.removeAll(setARemover);
		System.out.println("Arquivos restantes:                 " + set.size());
		System.out.println();

		int i = 1;
		int enviados = 0;
		int erros = 0;
		for (File f : set) {
			System.out.println(String.format("%06d", i) + "/"
					+ String.format("%06d", set.size()) + " - "
					+ f.getAbsolutePath());
			if (upload(url, f)) {
				enviados++;
				addFileToLog(FILE_NAME_XRP_PROCESSED_FILES_TXT, f);
			} else {
				addFileToLog(FILE_NAME_XRP_UNPROCESSED_FILES_TXT, f);
				erros++;
			}
			i++;
		}
		System.out.println();
		System.out.println("Arquivos enviados:                  " + enviados);
		System.out.println("Arquivos não eviados (com erro):    " + erros);
	}

	public static void addFileToLog(String logFileName, File f)
			throws Exception {
		try (PrintWriter output = new PrintWriter(new FileWriter(logFileName,
				true))) {
			output.printf("%s\r\n", f.getAbsolutePath());
		} catch (Exception e) {
			throw e;
		}
	}

	public static SortedSet<String> readLog() throws FileNotFoundException,
			IOException {
		SortedSet<String> fileTree = new TreeSet<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(
				FILE_NAME_XRP_PROCESSED_FILES_TXT))) {
			for (String line; (line = br.readLine()) != null;) {
				fileTree.add(line);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo não encontrado:             "
					+ FILE_NAME_XRP_PROCESSED_FILES_TXT);
		}
		return fileTree;
	}

	public static SortedSet<File> listFileTree(File dir) {
		SortedSet<File> fileTree = new TreeSet<File>();
		if (dir == null || dir.listFiles() == null) {
			return fileTree;
		}
		for (File entry : dir.listFiles()) {
			if (entry.isFile()) {
				if ("application/pdf".equals(URLConnection
						.guessContentTypeFromName(entry.getName())))
					fileTree.add(entry);
			} else
				fileTree.addAll(listFileTree(entry));
		}
		return fileTree;
	}

	public static boolean upload(String url, File binaryFile)
			throws MalformedURLException, IOException {
		String charset = "UTF-8";
		String filename = "value";
		String boundary = Long.toHexString(System.currentTimeMillis());
		String CRLF = "\r\n";

		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);

		try (OutputStream output = connection.getOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(
						output, charset), true);) {
			// Send normal param.
			writer.append("--" + boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"filename\"")
					.append(CRLF);
			writer.append("Content-Type: text/plain; charset=" + charset)
					.append(CRLF);
			writer.append(CRLF).append(filename).append(CRLF).flush();

			// Send binary file.
			writer.append("--" + boundary).append(CRLF);
			writer.append(
					"Content-Disposition: form-data; name=\"binaryFile\"; filename=\""
							+ binaryFile.getName() + "\"").append(CRLF);
			writer.append(
					"Content-Type: "
							+ URLConnection.guessContentTypeFromName(binaryFile
									.getName())).append(CRLF);
			writer.append("Content-Transfer-Encoding: binary").append(CRLF);
			writer.append(CRLF).flush();
			Files.copy(binaryFile.toPath(), output);
			output.flush(); // Important before continuing with writer!
			writer.append(CRLF).flush(); // CRLF is important! It indicates end
											// of boundary.

			// End of multipart/form-data.
			writer.append("--" + boundary + "--").append(CRLF).flush();
		}

		// Request is lazily fired whenever you need to obtain information about
		// response.
		int responseCode = ((HttpURLConnection) connection).getResponseCode();
		if (responseCode != 200) {
			System.err.println("ERRO: "
					+ convertStreamToString(((HttpURLConnection) connection)
							.getErrorStream()));
		}
		return responseCode == 200;
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
