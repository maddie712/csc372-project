import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Translator {
	ArrayList<String> intVars = new ArrayList<>();
	ArrayList<String> stringVars = new ArrayList<>();
	ArrayList<String> boolVars = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the name of the file you want to translate: ");
		String filename = in.nextLine();
		in.close();

		File inFile = new File(filename);
		FileWriter outFile = initializeFile(filename);
		Scanner reader;
		try {
			reader = new Scanner(inFile);
			while (reader.hasNextLine()) {
				String line = reader.nextLine().trim();
				Line lineParser = new Line();
				boolean match = lineParser.parseCmd(line);
				if (match) {
					outFile.write(lineParser.translated + "\n");
				}
				else {
					System.out.println(lineParser.result);
					System.exit(0);
				}
			}
			outFile.write("}");
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find the specified file, please try again.");
		}
	}

	public static FileWriter initializeFile(String oldFilename) {
		String newFilename = oldFilename.split(".")[0] + ".java";
		FileWriter out;
		try {
			out = new FileWriter(newFilename);
			out.write("public class " + newFilename + "{");
			return out;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
