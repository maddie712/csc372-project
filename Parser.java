import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
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
				// parse each individual line
				boolean match = false; //TODO
				if (match) {
					//////////
					// call translate and write to output file
					outFile.write("");
				}
				else {
					/////////
					// print error message
					System.out.println();
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
