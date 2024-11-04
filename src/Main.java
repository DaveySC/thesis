import java.io.Console;

public class Main {

    public static void main(String[] args) throws Exception {
        Application application = new Application("output.txt");
        System.exit(application.start());
    }

}