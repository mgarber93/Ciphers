package com.mattgarb;


import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller of the main view.
 * TODO look into TextFlow instead of TextArea so I can apply styling to matched/unmatched words
 */
public class Controller {

    public TextArea PlainText;
    public TextArea CipherText;
    public ComboBox<String> encryptionMode;
    public TextField Psk;
    public Button Crack;
    public HBox MenuBar;
    public ComboBox<Integer> RotNumber;
    public Button Stats;
    public HBox Bottom;
    private Boolean stats;

    @FXML
    public void initialize() {

        stats = false;
        Bottom.setMaxHeight(400);

        PlainText.setOnKeyReleased(e -> encrypt());
        PlainText.setOnDragOver(e -> {

        });
        CipherText.setOnKeyReleased(e -> decrypt());

        Psk.setOnAction(e -> {
            Main.passSet.add(Psk.getText().trim().toLowerCase().replaceAll("[^a-z]",""));
            encrypt();
        });

        //Initialize 26 rotation ciphers. If Route cipher is selected this is also the number of columns.
        List<Integer> rots = new ArrayList<>();
        for(int i = 0; i < 26; i++) {
            rots.add(i);
        }

        RotNumber.setOnAction(e -> {
            if("Rotation".equals(encryptionMode.getSelectionModel().getSelectedItem())) encrypt();
        });
        RotNumber.getItems().setAll(rots);
        RotNumber.getSelectionModel().select(13);

        Crack.setOnAction(e -> {
            String[] response = Main.parallelBruteForce(CipherText.getText()).split(":");

            if(response[0].equals("Rotation")) {
                RotNumber.getSelectionModel().select(Integer.parseInt(response[1].trim()));
                System.out.println(26 - RotNumber.getSelectionModel().getSelectedIndex());
            } else {
                Psk.setText(response[1]);
            }

            encryptionMode.getSelectionModel().select(response[0].trim());
            decrypt();
        });

        Stats.setOnAction(e -> {
            if(!stats) {
                stats = true;
                updateCharts();
            } else {
                Bottom.getChildren().clear();
                stats = false;
            }
        });
    }

    private void decrypt() {
        if("Rotation".equals(encryptionMode.getSelectionModel().getSelectedItem())){
            PlainText.setText(Main.rotate(CipherText.getText(),
                    26 - RotNumber.getSelectionModel().getSelectedIndex()));
        } else if("Vigenere".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            PlainText.setText(Vigenere.decrypt(CipherText.getText(),Main.onlyLowerLetters(Psk.getText())));
        } else if("AutoKey".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            PlainText.setText(AutoKey.decrypt(CipherText.getText(),Main.onlyLowerLetters(Psk.getText())));
        } else if("Base64".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            PlainText.setText(Main.base64Decode(CipherText.getText()));
        } else if("Column".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            PlainText.setText(ColumnTransposition.decrypt(CipherText.getText(),Psk.getText()));
        } else if("Route".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            PlainText.setText(Route.decrypt(CipherText.getText(),RotNumber.getSelectionModel().getSelectedIndex()));
        } else {
            PlainText.setText(CipherText.getText());
        }
        updateCharts();
    }

    private void encrypt() {
        if("Rotation".equals(encryptionMode.getSelectionModel().getSelectedItem())){
            CipherText.setText(Main.rotate(PlainText.getText(),RotNumber.getSelectionModel().getSelectedIndex()));
        } else if("Vigenere".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            CipherText.setText(Vigenere.encrypt(PlainText.getText(),Main.onlyLowerLetters(Psk.getText())));
        } else if("AutoKey".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            CipherText.setText(AutoKey.encrypt(PlainText.getText(),Main.onlyLowerLetters(Psk.getText())));
        } else if("Base64".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            CipherText.setText(Main.base64(PlainText.getText()));
        } else if("Column".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            CipherText.setText(ColumnTransposition.encrypt(PlainText.getText(),Psk.getText()));
        } else if("Route".equals(encryptionMode.getSelectionModel().getSelectedItem())) {
            CipherText.setText(Route.encrypt(PlainText.getText(),RotNumber.getSelectionModel().getSelectedIndex()));
        } else {
            CipherText.setText(PlainText.getText());
        }
        updateCharts();
    }

    private void updateCharts() {
        if(!stats) return;
        Bottom.getChildren().clear();
        final PieChart plainChart = new Frequency(PlainText.getText()).toPieChart("Plain Text Letter Frequency");
        Region region = new Region();
        final PieChart cipherChart = new Frequency(CipherText.getText()).toPieChart("Cipher Text Letter Frequency");
        Bottom.getChildren().addAll(plainChart, region, cipherChart);
        HBox.setHgrow(region, Priority.ALWAYS);
    }

}
