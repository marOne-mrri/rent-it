package gestionParkings;

import gestionVehicules.Vehicule;
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
import java.net.URL;
import java.util.ResourceBundle;

public class ListeParkings implements Initializable {
    private static ListeParkings controlleurParking;
    @FXML private Label status;
    @FXML private TextField champId, champRue;
    @FXML private TableView<Parking> tableParkings;
    @FXML private TableColumn<Parking, Integer> idParking;
    @FXML private TableColumn<Parking, String> rue;
    @FXML private TableColumn<Parking, Short> capacite, taille;
    @FXML private TableView<Vehicule> tableVehicules;
    @FXML private TableColumn<Vehicule, String> matricule, marque, type;

    static ListeParkings getControlleurParking() {
        return controlleurParking;
    }

    public ListeParkings() {
        controlleurParking = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idParking.setCellValueFactory(new PropertyValueFactory<>("id"));
        rue.setCellValueFactory(new PropertyValueFactory<>("rue"));
        capacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        taille.setCellValueFactory(new PropertyValueFactory<>("taille"));
        matricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        marque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        recupererParking();
        tableParkings.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> affichVehicules(newValue)));
    }

   private void recupererParking(String...parametres) {
       ObservableList<Parking> listeParkings=Parking.recupererParking(parametres);
       if( listeParkings.isEmpty() ) {
           status.setText("Aucun parking trouvé");
       }
       else {
           status.setText("");
       }
       tableParkings.setItems(listeParkings);
   }

    public void rechercherParking() {
        String idParking=champId.getText();
        if( idParking.equals("") )
            idParking="%";
        else
            idParking="%"+idParking+"%";
        String rueParking=champRue.getText();
        if( rueParking.equals("") )
            rueParking="%";
        else
            rueParking="%"+rueParking+"%";
        recupererParking(idParking, rueParking);
    }

    // appelee lorsque on clique sur la bouton "Afficher Tous"
    public void afficherTous() {
        champId.setText("");
        champRue.setText("");
        recupererParking();
    }

    public void deplacerVehicule() throws Exception {
        Vehicule vehiculeSelectionne=tableVehicules.getSelectionModel().getSelectedItem();
        if( vehiculeSelectionne==null ) { return; }
        DeplacementVehicule.setVehiculeADeplacer(vehiculeSelectionne);
        Stage fenetreDeplacement=new Stage();
        fenetreDeplacement.initModality(Modality.APPLICATION_MODAL);
        DeplacementVehicule.setFenetreDeplacement(fenetreDeplacement);
        Parent root = FXMLLoader.load(getClass().getResource("deplacement.fxml"));
        fenetreDeplacement.setTitle("Rent it");
        fenetreDeplacement.getIcons().add(new Image("/images/icon.png"));
        fenetreDeplacement.setResizable(false);
        fenetreDeplacement.setScene(new Scene(root));
        fenetreDeplacement.show();
    }

    public void ajouterParking() throws Exception {
        Stage fenetreAjout=new Stage();
        fenetreAjout.initModality(Modality.APPLICATION_MODAL);
        AjoutParking.setFentreAjout(fenetreAjout);
        Parent root = FXMLLoader.load(getClass().getResource("ajout.fxml"));
        fenetreAjout.setTitle("Rent it");
        fenetreAjout.getIcons().add(new Image("/images/icon.png"));
        fenetreAjout.setResizable(false);
        fenetreAjout.setScene(new Scene(root));
        fenetreAjout.show();
    }

    public void modifierParking() throws Exception {
        Parking parkingSelectionne = tableParkings.getSelectionModel().getSelectedItem();
        if( parkingSelectionne==null ) {
            return;
        }
        ModificationParking.setParkingAModifier(parkingSelectionne);
        Stage fenetreModification = new Stage();
        fenetreModification.initModality(Modality.APPLICATION_MODAL);
        ModificationParking.setFenetreModification(fenetreModification);
        Parent root = FXMLLoader.load(getClass().getResource("modification.fxml"));
        fenetreModification.setTitle("Rent it");
        fenetreModification.setResizable(false);
        fenetreModification.setScene(new Scene(root));
        fenetreModification.getIcons().add(new Image("/images/icon.png"));
        fenetreModification.show();
    }

    public void supprimerParking() {
        Parking parkingSelectionne = tableParkings.getSelectionModel().getSelectedItem();
        if( parkingSelectionne == null ) {
            return;
        }
        tableParkings.getItems().remove(parkingSelectionne);
        parkingSelectionne.supprimer();
    }

    public void affichVehiculesSansParking() throws Exception {
        Stage fenetre=new Stage();
        fenetre.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("deposition.fxml"));
        fenetre.setTitle("Rent it");
        fenetre.setResizable(false);
        fenetre.setScene(new Scene(root));
        fenetre.getIcons().add(new Image("/images/icon.png"));
        fenetre.show();
    }

    public void affichVehiculesReserves() throws Exception {
        Stage fenetre = new Stage();
        fenetre.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("sortir.fxml"));
        fenetre.setTitle("Rent it");
        fenetre.setResizable(false);
        fenetre.setScene(new Scene(root));
        fenetre.getIcons().add(new Image("/images/icon.png"));
        fenetre.show();
    }

    public void affichVehiculesEnLocation() throws Exception {
        Stage fenetre=new Stage();
        fenetre.initModality(Modality.APPLICATION_MODAL);
        Parent root = FXMLLoader.load(getClass().getResource("restitution.fxml"));
        fenetre.setTitle("Rent it");
        fenetre.setScene(new Scene(root));
        fenetre.setResizable(false);
        fenetre.getIcons().add(new Image("/images/icon.png"));
        fenetre.show();
    }

    private void affichVehicules(Parking parkingSelectionne) {
        if( parkingSelectionne==null ) {
            tableVehicules.getItems().clear();
            return;
        }
        tableVehicules.setItems( parkingSelectionne.getVehicules() );
    }

    void selectionner(int indexParking) {
        tableParkings.getSelectionModel().select(indexParking);
    }

    int getParkingSlectionner() {
        return tableParkings.getSelectionModel().getSelectedIndex();
    }
}
