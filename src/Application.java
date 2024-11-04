import java.io.*;
import java.nio.file.Files;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.*;

public class Application {

	private final Clerk clerk;

	private final File ErrorFile;
	private final File outputFile;
	private final Console console;

	//Vertex Count + 1
	private final int ARRAY_SIZE = 11 + 1;

	//GeoDominant/IndependentGeoDominant
	private long[][] geoDominant_independentGeoDominant = new long[ARRAY_SIZE][ARRAY_SIZE];

	//GeoDominant/DependentGeoDominant
	private long[][] geoDominant_dependentGeoDominant = new long[ARRAY_SIZE][ARRAY_SIZE];

	//IndependentGeoDominant/DependentGeoDominant
	private long[][] independentGeoDominant_dependentGeoDominant = new long[ARRAY_SIZE][ARRAY_SIZE];

	public Application(String outputFileName) {
		this.outputFile = new File(outputFileName);
		this.console = System.console();
		this.ErrorFile = new File("error_file.txt");
		this.clerk = new Clerk();
	}

	public int start() throws IOException {
		MyTimerTask timerTask = new MyTimerTask();
		new Timer().scheduleAtFixedRate(timerTask, 0, 600000);
		//600000
		String line = "";
		try(BufferedReader bi = new BufferedReader(new InputStreamReader(System.in))) {
			int dominant = 0, independent = 0, dependent = 0;
			int geoDominant = 0, independentGeoDominant = 0, dependentGeoDominant = 0;
			while ((line = bi.readLine()) != null) {
				if (!Graph6Converter.validate(line)) continue;
				FDS fds = new FDS(Graph6Converter.fromGraph6ToAdjacentMatrix(line));

				geoDominant = fds.getGeoDominantNumber();
				independentGeoDominant = fds.getIndependentGeoDominantNumber();
				dependentGeoDominant = fds.getDependentGeoDominantNumber();

				geoDominant_independentGeoDominant[geoDominant][independentGeoDominant]++;
				geoDominant_dependentGeoDominant[geoDominant][dependentGeoDominant]++;
				independentGeoDominant_dependentGeoDominant[independentGeoDominant][dependentGeoDominant]++;

			}
		} catch (Exception e) {
			e.printStackTrace();
			clerk.write(Arrays.asList(e.toString(), line), ErrorFile);
			return 1;
		}
		timerTask.run();
		return 0;
	}
	class MyTimerTask extends TimerTask {
		public void run() {
			try {
				clerk.write(Arrays.asList(
						Arrays.deepToString(geoDominant_independentGeoDominant),
						Arrays.deepToString(geoDominant_dependentGeoDominant),
                        Arrays.deepToString(independentGeoDominant_dependentGeoDominant)), outputFile);
			} catch (Exception e) {//Catch exception if any
				System.err.println("Error: " + e.getMessage() + e.getStackTrace()[0].toString());
			}
		}
	}
}