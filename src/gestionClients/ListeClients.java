package gestionClients;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ListeClients implements Initializable {
    private static ListeClients controlleurClients;
    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, String> cin;
    @FXML private TableColumn<Client, String> nom;
    @FXML private TableColumn<Client, String> prenom;
    @FXML private TableColumn<Client, String> gsm;
    @FXML private TableColumn<Client, String> mail;
    @FXML private TextField zoneNom, zonePrenom;
    @FXML private Label status;

    static ListeClients getControlleurClient() { return controlleurClients; }

    public ListeClients() {
        controlleurClients = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        gsm.setCellValueFactory(new PropertyValueFactory<>("gsm"));
        mail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        recupererClients();
    }

    private void recupererClients(String...parametres) {
        ObservableList<Client> listeClients=Client.recuperer(parametres);
        if( listeClients.isEmpty() ) {
            status.setText("Aucun client trouvé");
        }
        else {
            status.setText("");
        }
        tableClients.setItems(listeClients);
    }

    public void rechercherClient() {
        String nomCherche=zoneNom.getText();
        if( nomCherche.equals("") )
            nomCherche="%"; // tous
        else
            nomCherche="%"+nomCherche+"%";
        String prenomCherche=zonePrenom.getText();
        if( prenomCherche.equals("") )
            prenomCherche="%"; // tous
        else
            prenomCherche="%"+prenomCherche+"%";
        recupererClients(nomCherche, prenomCherche);
    }

    public void ajouterClient() throws IOException {
        Stage fenetreAjout = new Stage();
        AjoutClient.setFenetreAjout(fenetreAjout);
        fenetreAjout.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("ajout.fxml"));
        fenetreAjout.setTitle("Rent it");
        fenetreAjout.getIcons().add(new Image("/images/icon.png"));
        fenetreAjout.setResizable(false);
        fenetreAjout.setScene(new Scene(root));
        fenetreAjout.show();
    }

    public void modifierClient() throws Exception {
        Client clientSelectionne=tableClients.getSelectionModel().getSelectedItem();
        if( clientSelectionne==null ) { return; }
        ModificationClient.setClientAModifier(clientSelectionne);
        Stage fenetreModification=new Stage();
        fenetreModification.initModality(Modality.APPLICATION_MODAL);
        ModificationClient.setFenetreModification(fenetreModification);
        Parent root = FXMLLoader.load(getClass().getResource("modification.fxml"));
        fenetreModification.setTitle("Rent it");
        fenetreModification.setScene(new Scene(root));
        fenetreModification.getIcons().add(new Image("/images/icon.png"));
        fenetreModification.setResizable(false);
        fenetreModification.show();
    }

    public void supprimerClient() {
        Client clientASupprimer=tableClients.getSelectionModel().getSelectedItem();
        if( clientASupprimer==null ) { return; }
        tableClients.getItems().remove(clientASupprimer);
        clientASupprimer.supprimer();
    }

    /* appelee lorsque on clique sur bouton "afficher tous" */
    public void afficherTous() {
        zoneNom.setText("");
        zonePrenom.setText("");
        recupererClients();
    }
}
