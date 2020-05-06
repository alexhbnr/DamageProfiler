import javafx.application.Application;

/**
 * Created by neukamm on 06.10.2016.
 */
public class RunDamageProfiler {

    private static final String VERSION = "0.4.9";

    public static void main(String[] args) throws Exception {

         /*

                  get input parameters

                    $ damageprofiler                    :   starts GUI

                    $ damageprofiler -i <> -o <> ....   :   parse command line arguments

          */



        if(args.length==0){
            new Thread(() -> Application.launch(StarterGUI.class)).start();

        } else {
            System.setProperty("java.awt.headless", "true");
            StarterCLI starterCLI = new StarterCLI(VERSION, args);
        }


    }

}
