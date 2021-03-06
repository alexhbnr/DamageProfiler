package org.damageprofiler.controller;

import javafx.geometry.Insets;
import org.damageprofiler.gui.*;
import org.damageprofiler.gui.dialogues.AdvancedPlottingOptionsDialogue;
import org.damageprofiler.gui.dialogues.HelpDialogue;
import org.damageprofiler.gui.dialogues.RunInfoDialogue;
import org.damageprofiler.gui.dialogues.RuntimeEstimatorDialogue;
import org.damageprofiler.calculations.RuntimeEstimator;
import org.damageprofiler.calculations.StartCalculations;
import org.damageprofiler.io.Communicator;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

import java.text.DecimalFormat;

public class DamageProfilerMainController {

    private final Button btn_leftpane_run_config;
    private final ProgressBarController progressBarController;
    private final Button btn_estimate_runtime;
    private final Button btn_help;
    private final HelpDialogue help_dialogue;
    private final AdvancedPlottingOptionsDialogue advancedPlottingOptionsDialogue;
    private final RunInfoDialogue runInfoDialogue;
    private final Communicator communicator;
    private final Button btn_inputfile;
    private final Button btn_reference;
    private final Button btn_output;
    private final Button btn_run;
    private final Button btn_speciesList;
    private final TextField textfield_threshold;
    private final TextField textfield_length;
    private final TextField textfield_species;
    private final CheckBox checkbox_use_merged_reads;
    private final CheckBox checkbox_ssLibs_protocol;
    private final TextField textfield_title;
    private final TextField textfield_y_axis_height;
    private final StartCalculations starter;
    private final DamageProfilerMainGUI mainGUI;
    private final TabPane tabpane_species;
    private final RuntimeEstimatorDialogue runtimeInfoDialogue;
    /**
     * Constructor
     * @param damageProfilerMainGUI
     * @param progressBarController
     * @param plottingSettingController
     */
    public DamageProfilerMainController(DamageProfilerMainGUI damageProfilerMainGUI,
                                        ProgressBarController progressBarController,
                                        PlottingSettingController plottingSettingController){

        this.mainGUI = damageProfilerMainGUI;
        this.progressBarController = progressBarController;
        this.communicator = mainGUI.getCommunicator();
        runInfoDialogue = new RunInfoDialogue("Run configuration", communicator);
        starter = new StartCalculations(damageProfilerMainGUI.getVersion());
        this.help_dialogue = new HelpDialogue();

        this.btn_inputfile = mainGUI.getConfig_dialogue().getBtn_inputfile();
        this.btn_reference = mainGUI.getConfig_dialogue().getBtn_reference();
        this.btn_output = mainGUI.getConfig_dialogue().getBtn_output();
        this.btn_run = mainGUI.getConfig_dialogue().getBtn_run();
        this.btn_estimate_runtime = mainGUI.getConfig_dialogue().getBtn_estimate_runtime();
        this.btn_speciesList = mainGUI.getConfig_dialogue().getBtn_speciesList();
        this.btn_leftpane_run_config = mainGUI.getBtn_leftpane_info();
        this.btn_help = mainGUI.getBtn_help();

        this.textfield_threshold = mainGUI.getConfig_dialogue().getTextfield_threshold();
        this.textfield_length = mainGUI.getConfig_dialogue().getTextfield_length();
        this.textfield_species = mainGUI.getConfig_dialogue().getTextfield_species();
        this.textfield_title = mainGUI.getConfig_dialogue().getTextfield_title();
        this.textfield_y_axis_height = mainGUI.getConfig_dialogue().getTextfield_y_axis_height();

        this.checkbox_use_merged_reads = mainGUI.getConfig_dialogue().getCheckbox_use_merged_reads();
        this.checkbox_ssLibs_protocol = mainGUI.getConfig_dialogue().getCheckbox_ssLibs_protocol();

        this.tabpane_species = new TabPane();


        // attributes of advanced plotting settings
        this.advancedPlottingOptionsDialogue = this.mainGUI.getConfig_dialogue().getAdvancedPlottingOptionsDialogue();
        plottingSettingController.addListener(this.advancedPlottingOptionsDialogue);


        runtimeInfoDialogue = new RuntimeEstimatorDialogue("Runtime information",
                "This gives you an estimate of the runtime. If you run a metagenomic analysis on several species, " +
                        "it will take longer.\n\nFor large files with a long runtime, " +
                        "it's recommended to use the command line version of DamageProfiler.");

        runtimeInfoDialogue.addComponents();
        addListener();

    }


    private void addListener() {

        btn_inputfile.setOnAction(e -> {

            BamFileChooser fqfc = new BamFileChooser(communicator);
            if (communicator.getInput() != null){
                Tooltip tooltip_input = new Tooltip(communicator.getInput());
                btn_inputfile.setTooltip(tooltip_input);

                String filepath = communicator.getInput().substring(0, communicator.getInput().lastIndexOf('.'));
                String filename = filepath.split("/")[filepath.split("/").length-1];
                mainGUI.getConfig_dialogue().setTextfield_title(filename);

                if (checkIfInputWasSelected()) {
                    btn_run.setDisable(false);
                    btn_estimate_runtime.setDisable(false);

                } else {
                    btn_run.setDisable(true);
                    btn_estimate_runtime.setDisable(true);
                }
            }


        });

        btn_help.setOnAction(e -> mainGUI.getRoot().setCenter(new ScrollPane(this.help_dialogue.getGridPane())));

        btn_reference.setOnAction(e -> {

            ReferenceFileChooser rfc = new ReferenceFileChooser(communicator);
            Tooltip tooltip_ref = new Tooltip(communicator.getReference());
            btn_reference.setTooltip(tooltip_ref);

            if (checkIfInputWasSelected()) {
                btn_run.setDisable(false);
                btn_estimate_runtime.setDisable(false);
            } else {
                btn_run.setDisable(true);
                btn_estimate_runtime.setDisable(true);
            }

        });



        btn_output.setOnAction(e -> {

            OutputDirChooser rfc = new OutputDirChooser(communicator);
            Tooltip tooltip_output = new Tooltip(communicator.getOutfolder());
            btn_output.setTooltip(tooltip_output);

            if (checkIfInputWasSelected()) {
                btn_run.setDisable(false);
                btn_estimate_runtime.setDisable(false);

            } else {
                btn_run.setDisable(true);
                btn_estimate_runtime.setDisable(true);
            }

        });



        btn_estimate_runtime.setOnAction(e -> {

            RuntimeEstimator runtimeEstimator = new RuntimeEstimator(communicator.getInput());
            long estimatedRuntimeInSeconds = runtimeEstimator.getEstimatedRuntimeInSeconds();
            String text_estimatedRuntime;
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            runtimeInfoDialogue.setFileSize(df.format(runtimeEstimator.getMegabytes()));


            if(estimatedRuntimeInSeconds > 60) {
                long minutes = estimatedRuntimeInSeconds / 60;
                long seconds = estimatedRuntimeInSeconds % 60;
                text_estimatedRuntime = "Estimated Runtime: " + minutes + " minutes, and " + seconds + " seconds.";
            } else {
                if(estimatedRuntimeInSeconds == 0 ){
                    text_estimatedRuntime = "Estimated Runtime:\tInsignificant";
                } else {
                    text_estimatedRuntime = "Estimated Runtime:\t" + estimatedRuntimeInSeconds + " seconds.";
                }

            }

            runtimeInfoDialogue.setNumberOfRecords(runtimeEstimator.getEstimatedNumberOfRecords());
            runtimeInfoDialogue.setResultText(text_estimatedRuntime);
            runtimeInfoDialogue.update();
            runtimeInfoDialogue.show();
        });

        runtimeInfoDialogue.getBtn_proceed().setOnAction(e_start -> {
            runtimeInfoDialogue.close();
            runDamageProfiler();
        });

        runtimeInfoDialogue.getBtn_cancel().setOnAction(e_cancel -> runtimeInfoDialogue.close()
        );

        btn_run.setOnAction(e -> runDamageProfiler());

        btn_speciesList.setOnAction(e -> {

            SpeciesListFileChooser slfc = new SpeciesListFileChooser(communicator);
            btn_speciesList.setDisable(!checkIfInputWasSelected());

        });

        btn_leftpane_run_config.setOnAction(e -> {
            if(starter.isCalculationsDone()){
                ScrollPane scrollPane_adv_config = new ScrollPane();
                scrollPane_adv_config.setPadding(new Insets(10,10,10,10));
                scrollPane_adv_config.setContent(runInfoDialogue.getGridPane());
                mainGUI.getRoot().setCenter(scrollPane_adv_config);
            } else {
                ScrollPane scrollPane_adv_config = new ScrollPane();
                scrollPane_adv_config.setPadding(new Insets(10,10,10,10));
                scrollPane_adv_config.setContent(mainGUI.getConfig_dialogue().getConfig_gridpane());
                mainGUI.getRoot().setCenter(scrollPane_adv_config);
            }
        });

        runInfoDialogue.getBtn_new_config().setOnAction(e -> {
            mainGUI.getRoot().setCenter(mainGUI.getConfig_dialogue().getConfig_gridpane());
            clear();
        });


    }


    private void clear() {
        starter.setCalculationsDone(false);
        btn_inputfile.setTooltip(null);
        btn_output.setTooltip(null);
        btn_reference.setTooltip(null);
    }

    private void runDamageProfiler() {

        // set all user options
        communicator.setLength(Integer.parseInt(textfield_length.getText()));
        communicator.setThreshold(Integer.parseInt(textfield_threshold.getText()));
        communicator.setSsLibsProtocolUsed(checkbox_ssLibs_protocol.isSelected());
        communicator.setUse_merged_and_mapped_reads(checkbox_use_merged_reads.isSelected());
        communicator.setyAxis_damageplot(Double.parseDouble(textfield_y_axis_height.getText()));
        communicator.setTitle_plots(textfield_title.getText());

        // set colors
        communicator.setColor_DP_C_to_T(this.advancedPlottingOptionsDialogue.getTabAdvancedSettingsDamagePlot().getColorPicker_C_to_T().getValue());
        communicator.setColor_DP_G_to_A(this.advancedPlottingOptionsDialogue.getTabAdvancedSettingsDamagePlot().getColorPicker_G_to_A().getValue());
        communicator.setColor_DP_insertions(this.advancedPlottingOptionsDialogue.getTabAdvancedSettingsDamagePlot().getColorPicker_insertions().getValue());
        communicator.setColor_DP_deletions(this.advancedPlottingOptionsDialogue.getTabAdvancedSettingsDamagePlot().getColorPicker_deletions().getValue());
        communicator.setColor_DP_other(this.advancedPlottingOptionsDialogue.getTabAdvancedSettingsDamagePlot().getColorPicker_others().getValue());


        if(textfield_species.getText().equals(""))
            communicator.setSpecies_ref_identifier(null);
        else
            communicator.setSpecies_ref_identifier(textfield_species.getText());

        if(!textfield_title.getText().equals("")){
            communicator.setTitle_plots(textfield_title.getText());
        }


        try {
            // add progress indicator
            Task startCalculuations = new Task() {
                @Override
                protected Object call() throws Exception {
                    starter.start(communicator);
                    runInfoDialogue.updateParameters();
                    return true;
                }
            };

            progressBarController.activate(startCalculuations);

            startCalculuations.setOnSucceeded((EventHandler<Event>) event -> {

                if(starter.getSpecieslist() == null){
                    // do normal plot
                    TabPane tabPane_results = new TabPane();

                    Tab tab_dp_all = new Tab("DamageProfile");
                    tab_dp_all.setClosable(false);
                    tab_dp_all.setContent(generateDamageProfile(null));

                    Tab tab_edit = generateEditDistance(null);

                    Tab tab_length = new Tab("Length distribution");
                    tab_length.setClosable(false);
                    tab_length.setContent(generateLengthDist(null));

                    tabPane_results.getTabs().addAll(tab_dp_all, tab_edit, tab_length);

                    mainGUI.getRoot().setCenter(tabPane_results);
                } else {

                    for (String species : starter.getSpecieslist()) {

                        Tab tab_species = new Tab(species);
                        tab_species.setClosable(false);
                        TabPane tabpane_per_species_result = new TabPane();
                        Tab tab_dp_all = new Tab("DamageProfile");
                        tab_dp_all.setClosable(false);
                        Tab tab_length = new Tab("Length distribution");
                        tab_length.setClosable(false);

                        // add results
                        tab_dp_all.setContent(generateDamageProfile(species));
                        Tab tab_edit = generateEditDistance(species);
                        tab_length.setContent(generateLengthDist(species));

                        tabpane_per_species_result.getTabs().addAll(tab_dp_all, tab_edit, tab_length);
                        tab_species.setContent(tabpane_per_species_result);
                        tabpane_species.getTabs().add(tab_species);
                        mainGUI.getRoot().setCenter(tabpane_species);
                    }
                }
                progressBarController.stop();
            });

            new Thread(startCalculuations).start();

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    /**
     * The following methods generate the result plots after successful damage profile calculations.
     *
     */

    private TabPane generateLengthDist(String species) {

        TabPane tabPane_lengthDist = new TabPane();
        Tab allData = new Tab("All data");
        Tab splitData = new Tab("Forward vs. Reverse");

        ChartViewer viewerLengthAll;
        ChartViewer viewerLengthSep;

        if(species==null){
            viewerLengthAll = new ChartViewer(starter.getOutputGenerator().getLength_chart_all());
            viewerLengthSep = new ChartViewer(starter.getOutputGenerator().getLength_chart_sep());
        } else {
            viewerLengthAll = new ChartViewer(starter.getSpecies_output_summary().get(species).get(3));
            viewerLengthSep = new ChartViewer(starter.getSpecies_output_summary().get(species).get(4));
        }

        // disable zoom on x-axis
        viewerLengthAll.getCanvas().setDomainZoomable(false);
        viewerLengthAll.getCanvas().setDomainZoomable(false);

        allData.setContent(viewerLengthAll);
        splitData.setContent(viewerLengthSep);
        allData.setClosable(false);
        splitData.setClosable(false);

        tabPane_lengthDist.getTabs().addAll(allData, splitData);

        return tabPane_lengthDist;

    }

    private Tab generateEditDistance(String species) {
        JFreeChart chart_edit;
        if(species==null){
            chart_edit = starter.getOutputGenerator().getEditDist_chart();
        } else {
            chart_edit = starter.getSpecies_output_summary().get(species).get(2);
        }

        ChartViewer viewerEditDistance = new ChartViewer(chart_edit);
        Tab tab_edit_dist = new Tab("Edit distance");
        tab_edit_dist.setClosable(false);
        tab_edit_dist.setContent(viewerEditDistance);
        return tab_edit_dist;

    }

    private TabPane generateDamageProfile(String species) {

        TabPane tabPane_damagePlot = new TabPane();

        Tab fivePrime = new Tab("5'end");
        Tab threePrime = new Tab("3'end");

        ChartViewer viewer5prime;
        ChartViewer viewer3prime;
        if(species==null)  {
            viewer5prime = new ChartViewer(starter.getOutputGenerator().getChart_DP_5prime());
            viewer3prime = new ChartViewer(starter.getOutputGenerator().getChart_DP_3prime());
        } else {
            viewer5prime = new ChartViewer(starter.getSpecies_output_summary().get(species).get(0));
            viewer3prime = new ChartViewer(starter.getSpecies_output_summary().get(species).get(1));
        }

        // disable zoom on x-axis
        viewer5prime.getCanvas().setDomainZoomable(false);
        viewer3prime.getCanvas().setDomainZoomable(false);

        fivePrime.setContent(viewer5prime);
        threePrime.setContent(viewer3prime);
        fivePrime.setClosable(false);
        threePrime.setClosable(false);

        tabPane_damagePlot.getTabs().addAll(fivePrime, threePrime);

        return tabPane_damagePlot;
    }


    /**
     * This method checks if all mandatory fields are set. Otherwise, it's not possible to run the tool.
     * @return boolean if input all is specified
     */
    private boolean checkIfInputWasSelected() {
        boolean tmp = false;
        if (communicator.getInput() != null && communicator.getOutfolder() != null) {
            if (communicator.getInput().length() != 0) {
                tmp = true;
            }
        }
        return tmp;
    }

}
