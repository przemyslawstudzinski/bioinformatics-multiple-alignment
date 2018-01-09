package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.compound.DNACompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.compound.RNACompoundSet;
import org.biojava.nbio.core.sequence.io.DNASequenceCreator;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import org.biojava.nbio.core.sequence.io.RNASequenceCreator;
import org.biojava.nbio.core.sequence.template.Sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static sample.Controller.SequenceType.*;

public class Controller implements Initializable {

    public enum SequenceType {
        PROTEIN,
        DNA,
        RNA
    }

    public static final String addSequenceInfo = "Sequence/s was/were added.";

    @FXML
    private ListView<Alignments.ProfileProfileAlignerType> profilesListView;

    @FXML
    private ListView<Alignments.PairwiseSequenceScorerType> scoringTypesListView;

    @FXML
    private ListView<SequenceType> sequencesListView;

    @FXML
    private TextArea sequenceTextArea;

    @FXML
    private Label addInfoText;

    @FXML
    private TextArea resultTextArea;

    @FXML
    private CheckBox fromFileCheckBox;

    private final ObservableList<Alignments.ProfileProfileAlignerType> profiles
            = FXCollections.observableArrayList();

    private final ObservableList<Alignments.PairwiseSequenceScorerType> scoringTypes
            = FXCollections.observableArrayList();

    private final ObservableList<SequenceType> sequenceTypes
            = FXCollections.observableArrayList();

    private File fileWithSequence;

    private List<Sequence> sequences = new ArrayList<>();

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
        sequenceTypes.add(DNA);
        sequenceTypes.add(RNA);
        sequenceTypes.add(PROTEIN);
        sequencesListView.setItems(sequenceTypes);
    }

    private void initScoringTypes() {
        for (Alignments.PairwiseSequenceScorerType type : Alignments.PairwiseSequenceScorerType.values()) {
            scoringTypes.add(type);
        }
        scoringTypesListView.setItems(scoringTypes);
    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        fileWithSequence = fileChooser.showOpenDialog(sequenceTextArea.getScene().getWindow());
        sequenceTextArea.setText(readFile(fileWithSequence));
    }

    private String readFile(File file) {
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
    private void addSequence() throws CompoundNotFoundException, IOException {
        SequenceType selectedSequenceType = sequencesListView.getSelectionModel().getSelectedItem();
        String inputText = sequenceTextArea.getText();

        Sequence sequence = null;
        List<Sequence> sequenceList;

        if (fromFileCheckBox.isSelected()) {
            sequenceList = getSequenceFromFASTAFile(selectedSequenceType);
            if (sequenceList != null) {
                sequences.addAll(sequenceList);
            }
        } else {
            sequence = getSequenceFromInputText(selectedSequenceType, inputText, sequence);
            if (sequence != null) {
                sequences.add(sequence);
            }
        }
        addInfoText.setText(addSequenceInfo);
    }

    private List<Sequence> getSequenceFromFASTAFile(SequenceType selectedSequenceType)
            throws CompoundNotFoundException, IOException {
        List<Sequence> sequenceList = null;
        switch (selectedSequenceType) {
            case DNA:
                FastaReader<DNASequence, NucleotideCompound> fastaReaderDNA = new FastaReader<DNASequence, NucleotideCompound>(
                        fileWithSequence,
                        new GenericFastaHeaderParser<>(),
                        new DNASequenceCreator(DNACompoundSet.getDNACompoundSet()));

                sequenceList = fastaReaderDNA.process().values().stream().collect(Collectors.toList());
                break;
            case RNA:
                FastaReader<RNASequence, NucleotideCompound> fastaReaderRNA = new FastaReader<RNASequence, NucleotideCompound>(
                        fileWithSequence,
                        new GenericFastaHeaderParser<>(),
                        new RNASequenceCreator(RNACompoundSet.getRNACompoundSet()));

                sequenceList = fastaReaderRNA.process().values().stream().collect(Collectors.toList());
                break;
            case PROTEIN:
                FastaReader<ProteinSequence, AminoAcidCompound> fastaReaderProtein = new FastaReader<ProteinSequence, AminoAcidCompound>(
                        fileWithSequence,
                        new GenericFastaHeaderParser<>(),
                        new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));

                sequenceList = fastaReaderProtein.process().values().stream().collect(Collectors.toList());
                break;
        }
        return sequenceList;
    }

    private Sequence getSequenceFromInputText(SequenceType selectedSequenceType, String inputText, Sequence sequence)
            throws CompoundNotFoundException {
        switch (selectedSequenceType) {
            case DNA:
                sequence = new DNASequence(inputText);
                break;
            case RNA:
                sequence = new RNASequence(inputText);
                break;
            case PROTEIN:
                sequence = new ProteinSequence(inputText);
                break;
        }
        return sequence;
    }

    @FXML
    private void calculateMultiAlignment() {
        Alignments.ProfileProfileAlignerType profileProfileAlignerType =
                profilesListView.getSelectionModel().getSelectedItem();

        Alignments.PairwiseSequenceScorerType pairwiseSequenceScorerType =
                scoringTypesListView.getSelectionModel().getSelectedItem();

        String result = Alignments.getMultipleSequenceAlignment(
                this.sequences, profileProfileAlignerType, pairwiseSequenceScorerType).toString();

        resultTextArea.setText(result);
    }
}