package org.example.controller;

import com.fazecast.jSerialComm.SerialPort;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Label id_lbl_status;
    @FXML
    private Button id_btn_enc;

    @FXML
    private Button id_btn_dec;

    @FXML
    private Button id_btn_exit;

    @FXML
    private TextField id_tfd_dec;

    @FXML
    private TextField id_tfd_enc;

    private SerialPort serialPort;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Serial Portni ochish
        serialPort = SerialPort.getCommPort("COM4"); // Arduino portini kiriting
        serialPort.setBaudRate(9600);

        if (!serialPort.openPort()) {
            System.out.println("Portni ochib bo'lmadi!");
            return;
        }
        System.out.println("Port ochildi!");

        // Shifrlash tugmasi
        id_btn_enc.setOnAction(event -> {
            String inputText = id_tfd_enc.getText();
            if (!inputText.isEmpty()) {
                String result = sendToArduino("ENCRYPT:" + inputText);
                id_tfd_dec.setText(result);
            } else {
                System.out.println("Shifrlash uchun matn kiriting!");
            }
        });

        // Deshifrlash tugmasi
        id_btn_dec.setOnAction(event -> {
            String inputText = id_tfd_dec.getText();
            if (!inputText.isEmpty()) {
                String result = sendToArduino("DECRYPT:" + inputText);
                id_tfd_enc.setText(result);
            } else {
                System.out.println("Deshifrlash uchun matn kiriting!");
            }
        });

        // Chiqish tugmasi
        id_btn_exit.setOnAction(event -> {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
                System.out.println("Port yopildi!");
            }
            System.exit(0);
        });
    }

    private String sendToArduino(String message) {
        try {
            // Arduino ga ma'lumot yuborish
            serialPort.getOutputStream().write((message + "\n").getBytes());
            serialPort.getOutputStream().flush();
            Thread.sleep(200);
            // Arduino javobini o'qish
            byte[] buffer = new byte[1024];
            int len = serialPort.getInputStream().read(buffer);
            System.out.println(len);
            return new String(buffer, 0, len).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Xatolik!";
        }
    }

}
