package sample;

import javafx.scene.control.Alert;

public final class AlertClass {
    public static void showAlert(String title, String header, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }
}