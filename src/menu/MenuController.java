package menu;

import connexion.Connexion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private Button userButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!Connexion.utilisateur.isAdmin()) {
            this.userButton.setDisable(true);
        }
    }
}
