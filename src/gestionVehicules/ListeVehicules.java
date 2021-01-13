package gestionVehicules;

import com.jfoenix.controls.JFXToggleButton;
import gestionParkings.Parking;
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
import java.sql.Date;
import java.util.ResourceBundle;
import static gestionVehicules.ModificationVehicule.setVehiculeAModifier;
import static gestionVehicules.ModificationVehicule.setFenetreModification;

public class ListeVehicules implements Initializable {
    private static ListeVehicules controlleurVehicule;
    @FXML private TextField matricule, marque, type;
    @FXML private TableView<Vehicule> tablesVehicules;
    @FXML private TableColumn<Vehicule, String> matriculeVehicule, marqueVehicule, typeVehicule;
    @FXML private TableColumn<Vehicule, Carburant> carburantVehicule;
    @FXML private TableColumn<Vehicule, Long> kilometrageVehicule;
    @FXML private TableColumn<Vehicule, Date> dateMiseEnCirculation;
    @FXML private TableColumn<Vehicule, Double> prixLocation;
    @FXML private TableColumn<Vehicule, Integer> parking;
    @FXML private Label status;
    @FXML private JFXToggleButton tous, disponibles, reserves, enLocation;

    static ListeVehicules getControlleurVehicule() { return controlleurVehicule; }

    public ListeVehicules() {
        controlleurVehicule=this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tous.setSelected(true);
        matriculeVehicule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        marqueVehicule.setCellValueFactory(new PropertyValueFactory<>("marque"));
        typeVehicule.setCellValueFactory(new PropertyValueFactory<>("type"));
        carburantVehicule.setCellValueFactory(new PropertyValueFactory<>("carburant"));
        kilometrageVehicule.setCellValueFactory(new PropertyValueFactory<>("kilometrage"));
        dateMiseEnCirculation.setCellValueFactory(new PropertyValueFactory<>("dateMiseEnCirculation"));
        prixLocation.setCellValueFactory(new PropertyValueFactory<>("prixLocation"));
        parking.setCellValueFactory(new PropertyValueFactory<>("parking"));
        recupererVehicules(CritereRecherche.TOUS);
    }

    private void recupererVehicules(CritereRecherche critere, String...parametres) {
        ObservableList<Vehicule> listeVehicules;
        listeVehicules=Vehicule.recupere(critere, parametres);
        assert listeVehicules != null;
        if( listeVehicules.isEmpty() ) {
            status.setText("Aucun véhicule trouvé");
        }
        else {
            status.setText("");
        }
        tablesVehicules.setItems(listeVehicules);
    }

    public void rechercherVehicules() {
        String matricule=this.matricule.getText();
        if( matricule.length()==0 )
            matricule="%";
        else
            matricule="%"+matricule+"%";
        String marque=this.marque.getText();
        if( marque.length()==0 )
            marque="%";
        else
            marque="%"+marque+"%";
        String type=this.type.getText();
        if( type.length()==0 )
            type="%";
        else
            type="%"+type+"%";
        CritereRecherche critereRecherche=CritereRecherche.TOUS; // par defaut
        if( disponibles.isSelected() ) {
            critereRecherche=CritereRecherche.DISPONIBLE;
        }
        else if( reserves.isSelected() ) {
            critereRecherche=CritereRecherche.RESERVE;
        }
        else if( enLocation.isSelected() ) {
            critereRecherche=CritereRecherche.EN_LOCATION;
        }
        recupererVehicules(critereRecherche, matricule, marque, type);
    }

    // appele lorsque on clique sur le bouton "afficher toutes"
    public void afficherTous() {
        matricule.setText("");
        marque.setText("");
        type.setText("");
        if( tous.isSelected() ) {
            recupererVehicules(CritereRecherche.TOUS);
        }
        if( disponibles.isSelected() ) {
           recupererVehicules(CritereRecherche.DISPONIBLE);
        }
        else if( reserves.isSelected() ) {
            recupererVehicules(CritereRecherche.RESERVE);
        }
        else if( enLocation.isSelected() ) {
            recupererVehicules(CritereRecherche.EN_LOCATION);
        }
    }

    public void  modifierVehicule() throws Exception{
        Vehicule vehiculeSelectionne=tablesVehicules.getSelectionModel().getSelectedItem();
        if( vehiculeSelectionne==null ) { return; }
        setVehiculeAModifier(vehiculeSelectionne);
        Stage fenetreModification=new Stage();
        fenetreModification.initModality(Modality.APPLICATION_MODAL);
        setFenetreModification(fenetreModification);
        Parent root = FXMLLoader.load(getClass().getResource("modification.fxml"));
        fenetreModification.setTitle("Rent it");
        fenetreModification.setScene(new Scene(root));
        fenetreModification.getIcons().add(new Image("/images/icon.png"));
        fenetreModification.setResizable(false);
        fenetreModification.show();
    }

    public void ajouterVehicule() throws Exception {
        Stage fenetreAjout=new Stage();
        fenetreAjout.initModality(Modality.APPLICATION_MODAL);
        AjoutVehicule.setFenetreAjout(fenetreAjout);
        Parent root = FXMLLoader.load(getClass().getResource("ajout.fxml"));
        fenetreAjout.setTitle("Rent it");
        fenetreAjout.setResizable(false);
        fenetreAjout.getIcons().add(new Image("/images/icon.png"));
        fenetreAjout.setScene(new Scene(root));
        fenetreAjout.show();
    }

    public void supprimerVehicule() {
        Vehicule vehiculeSelectionne = tablesVehicules.getSelectionModel().getSelectedItem();
        if (vehiculeSelectionne == null) { return; }
        tablesVehicules.getItems().remove(vehiculeSelectionne);
        vehiculeSelectionne.supprimer();
        // si vehicule ete dans un parking on va diminuer la taille de ce parking !!!!!
        if( !vehiculeSelectionne.getParking().equals("Sans Parking") ) {
            Parking.decrimenterTaille( Integer.parseInt(vehiculeSelectionne.getParking()) );
        }
    }
}