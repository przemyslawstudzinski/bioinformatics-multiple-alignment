package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.biojava.nbio.alignment.Alignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    public enum SequenceType{
        PROTEIN,
        DNA,
        RNA
    }

    @FXML
    private ListView<Alignments.ProfileProfileAlignerType> profilesListView;

    @FXML
    private ListView<Alignments.PairwiseSequenceScorerType> scoringTypesListView;

    @FXML
    private ListView<SequenceType> sequencesListView;

    private final ObservableList<Alignments.ProfileProfileAlignerType> profiles
            = FXCollections.observableArrayList();

    private final ObservableList<Alignments.PairwiseSequenceScorerType> scoringTypes
            = FXCollections.observableArrayList();

    private final ObservableList<SequenceType> sequences
            = FXCollections.observableArrayList();

    private File fileWithSequence;

    @FXML
    private TextArea sequenceTextArea;

    @FXML
    private CheckBox fromFileCheckBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initProfiles();
        initSequences();
        initScoringTypes();
    }

    private void initProfiles() {
        for (Alignments.ProfileProfileAlignerType option : Alignments.ProfileProfileAlignerType.values()) {
            profiles.add(option);
        }
        profilesListView.setItems(profiles);
    }

    private void initSequences() {
        sequences.add(SequenceType.DNA);
        sequences.add(SequenceType.RNA);
        sequences.add(SequenceType.PROTEIN);
        sequencesListView.setItems(sequences);
    }

    private void initScoringTypes() {
        for (Alignments.PairwiseSequenceScorerType type : Alignments.PairwiseSequenceScorerType.values()) {
            scoringTypes.add(type);
        }
        scoringTypesListView.setItems(scoringTypes);
    }

    @FXML
    private void openFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        fileWithSequence = fileChooser.showOpenDialog(sequenceTextArea.getScene().getWindow());
        sequenceTextArea.setText(readFile(fileWithSequence));
    }

    private String readFile(File file){
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String text;
            while ((text = bufferedReader.readLine()) != null) {
                stringBuffer.append(text);
            }

        } catch (Exception ex) {
        }

        return stringBuffer.toString();
    }

    @FXML
    private void addSequence()
    {

    }

    @FXML
    private void calculateMultAlignment()
    {

    }


}