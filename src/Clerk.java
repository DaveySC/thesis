import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Clerk {

	private final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");

	public void write(List<String> what, File where){
		try (FileWriter fstream = new FileWriter(where,true);
		     BufferedWriter out = new BufferedWriter(fstream);) {

			String currentDate = format.format(new Date());
			out.write(currentDate);
			out.newLine();

			for (String str : what) {
				out .write(str);
				out.newLine();
			}

			out.flush();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
}
