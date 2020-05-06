package IO;

import org.apache.commons.cli.*;


/**
 * Created by neukamm on 01.08.16.
 */
public class UserOptionsParser {

    private static final String CLASS_NAME = "User option parser";
    private final String version;
    private String[] args;
    private Communicator communicator;


    public UserOptionsParser(String[] args, Communicator c, String version){
        this.args = args;
        this.communicator = c;
        this.version = version;
        parse();
    }

    private void parse() {

        // create command line parameters
        Options helpOptions = new Options();
        helpOptions.addOption("h", "help", false, "show this help page");

        Options options = new Options();

        options.addOption("h", false, "Shows this help page.");

        options.addOption(new Option("version", false,
                "Shows the version of DamageProfiler."));

        options.addOption(Option.builder("i")
                .argName("INPUT")
                .desc("The input sam/bam/cram file.")
                .hasArg()
                .build());

        options.addOption(Option.builder("r")
                .argName("REFERENCE")
                .desc("The reference file (fasta format).")
                .hasArg()
                .build());

        options.addOption(Option.builder("o")
                .argName("OUTPUT")
                .desc("The output folder.")
                .hasArg()
                .build());

        options.addOption(Option.builder("t")
                .argName("THRESHOLD")
                .desc("DamagePlot: Number of bases which are considered for plotting nucleotide misincorporations. Default: 25")
                .hasArg()
                .build());

        options.addOption(Option.builder("s")
                .argName("SPECIES")
                .desc("Reference sequence name (RNAME flag of SAM record). For more details see Documentation.")
                .hasArg()
                .build());

        options.addOption(Option.builder("sf")
                .argName("SPECIES LIST")
                .desc("List with species for which damage profile has to be calculated. For more details see Documentation.")
                .hasArg()
                .build());

        options.addOption(Option.builder("l")
                .argName("LENGTH")
                .desc("Number of bases which are considered for frequency computations. Default: 100.")
                .hasArg()
                .build());


        /*
                plotting options
         */

        options.addOption(Option.builder("title")
                .argName("TITLE")
                .desc("Title used for all plots. Default: input filename.")
                .hasArg()
                .build());

        // damage plot

        options.addOption(Option.builder("yaxis_dp_max")
                .argName("MAX_VALUE")
                .desc("DamagePlot: Maximal y-axis value.")
                .hasArg()
                .build());

        // Identity plot

        options.addOption(Option.builder("xaxis_id_min")
                .argName("MIN_VALUE")
                .desc("Identity Distribution: Minimal value x-axis.")
                .hasArg()
                .build());

        options.addOption(Option.builder("xaxis_id_max")
                .argName("MAX_VALUE")
                .desc("Identity Distribution: Maximal value x-axis.")
                .hasArg()
                .build());

        // Length plot

        options.addOption(Option.builder("xaxis_length_min")
                .argName("MIN_VALUE")
                .desc("Length Distribution: Minimal value x-axis.")
                .hasArg()
                .build());

        options.addOption(Option.builder("xaxis_length_max")
                .argName("MAX_VALUE")
                .desc("Length Distribution: Maximal value x-axis.")
                .hasArg()
                .build());


        // others

        options.addOption(Option.builder("only_merged")
                .desc("Use only mapped and merged (in case of paired-end sequencing) reads to calculate damage plot " +
                        "instead of using all mapped reads. The SAM/BAM entry must start with 'M_', otherwise " +
                        "it will be skipped. Default: false ")
                .build());

        options.addOption(Option.builder("ssLib")
                .desc("Single-stranded library protocol was used. Default: false")
                .build());

        String header = "\nDetailed description:\n\n";

        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.setWidth(130);
        CommandLineParser parser = new BasicParser();


// ---------------------------------------------------------------------------------------


        try {

            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption('h')) {
                formatter.printHelp("DamageProfiler", header, options, null, true);
                System.exit(0);
            }

            if(cmd.hasOption("version")) {
                System.out.print("DamageProfiler v" + this.version + "\n");
                System.exit(0);
            }

            // input files

            if (cmd.hasOption('i')) {
                communicator.setInput(cmd.getOptionValue('i'));
            }
            if (cmd.hasOption('r')) {
                communicator.setReference(cmd.getOptionValue('r'));
            }
            if (cmd.hasOption('o')) {
                communicator.setOutfolder(cmd.getOptionValue('o'));
            }

            // damage calculation

            if (cmd.hasOption('s')) {
                communicator.setSpecies_ref_identifier(cmd.getOptionValue('s'));
            }
            if (cmd.hasOption("sf")) {
                communicator.setSpecieslist_filepath(cmd.getOptionValue("sf"));
            }
            if (cmd.hasOption('l')) {
                communicator.setLength(Integer.parseInt(cmd.getOptionValue('l')));
            }
            if(cmd.hasOption("all_mapped_and_merged_reads")) {
                communicator.setUse_merged_and_mapped_reads(true);
            }

            if(cmd.hasOption("use_all_reads")) {
                communicator.setUse_all_reads(false);
            }

            if(cmd.hasOption("sslib")) {
                communicator.setSsLibsProtocolUsed(true);
            }

            // Plotting

            if(cmd.hasOption("title")) {
                communicator.setTitle_plots(cmd.getOptionValue("title"));
            }

            if(cmd.hasOption("yaxis_damageplot")) {
                communicator.setyAxis_damageplot(Double.parseDouble(cmd.getOptionValue("yaxis_damageplot")));
            }

            if(cmd.hasOption("xaxis_histo_id_min")) {
                communicator.setXaxis_histo_id_min(Double.parseDouble(cmd.getOptionValue("xaxis_histo_id_min")));
            }


            if(cmd.hasOption("xaxis_histo_id_max")) {
                communicator.setXaxis_histo_id_max(Double.parseDouble(cmd.getOptionValue("xaxis_histo_id_max")));
            }

            if(cmd.hasOption("xaxis_histo_length_min")) {
                communicator.setXaxis_histo_length_min(Double.parseDouble(cmd.getOptionValue("xaxis_histo_length_min")));
            }


            if(cmd.hasOption("xaxis_histo_length_max")) {
                communicator.setXaxis_histo_length_max(Double.parseDouble(cmd.getOptionValue("xaxis_histo_length_max")));
            }


            if (cmd.hasOption('t')) {
                communicator.setThreshold(Integer.parseInt(cmd.getOptionValue('t')));
            }


        } catch (ParseException e) {
            formatter.printHelp(CLASS_NAME, options);
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }
}
