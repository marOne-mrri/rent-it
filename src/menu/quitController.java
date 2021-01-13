package menu;

import connexion.Connexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class quitController {
    @FXML
    void logoutClicked(ActionEvent event) throws SQLException {
        Connexion.connexion.close();
        ((Node)event.getSource()).getScene().getWindow().hide();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../connexion/login.fxml"));
            Stage myStage = new Stage();
            myStage.setScene(new Scene(root));
            myStage.setTitle("rent it");
            myStage.getIcons().add(new Image("/images/icon.png"));
            myStage.setResizable(false);
            myStage.show();
        } catch (IOException e){
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    void quitClicked(ActionEvent event) throws SQLException {
        Connexion.connexion.close();
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
}
