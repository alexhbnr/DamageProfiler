import javafx.application.Application;
import org.damageprofiler.calculations.StartCalculations;
import org.damageprofiler.io.Communicator;
import org.damageprofiler.io.UserOptionsParser;

/**
 * Created by neukamm on 06.10.2016.
 */
public class RunDamageProfiler {

    private static final String VERSION = "1.1";

    public static void main(String[] args) throws Exception {



         /*
                  get input parameters

                    $ damageprofiler                    :   starts org.damageprofiler.GUI

                    $ damageprofiler -i <> -o <> ....   :   parse command line arguments
          */

        if(args.length==0){
            new Thread(() -> Application.launch(StarterGUI.class)).start();

        } else {
            System.setProperty("java.awt.headless", "true");
            new StarterCLI(VERSION, args);
        }
    }
}
