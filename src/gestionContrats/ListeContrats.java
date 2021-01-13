package gestionContrats;

import connexion.Connexion;
import gestionClients.Client;
import gestionReservation.Reservation;
import javafx.collections.FXCollections;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static gestionContrats.ModificationContrat.setFenetreModification;
import static gestionContrats.ModificationContrat.setContratAModifier;
import static gestionContrats.ajoutContrat.setFenetreAjout;

public class ListeContrats implements Initializable {
    private static ListeContrats controlleurContrat;
    @FXML private Label status;
    @FXML private TextField code, nom, prenom;
    @FXML private TableView<Contrat> tableContrats;
    @FXML private TableColumn<Contrat, Integer> codeContrat;
    @FXML private TableColumn<Contrat, Date> dateContrat, dateDepart, dateRetour;
    @FXML private TableColumn<Contrat, String> codeReservation;
    @FXML private TableColumn<Reservation, String> vehicule;
    @FXML private TableColumn<Client, String> client;
    private ObservableList<Contrat> listeContrats= FXCollections.observableArrayList();
    private PreparedStatement rechercherContrat;
    private PreparedStatement supprimerContrat;

    static ListeContrats getControlleurContrat() { return controlleurContrat; }

    public ListeContrats() {
        controlleurContrat=this;
        String requRecherContrats="SELECT id_contrat, date_contrat, date_depart, date_retour, " +
                "reservation_id, matricule, marque, type, cin, nom, prenom FROM contrat " +
                "INNER JOIN vehicule ON vehicule_matricule=matricule " +
                "INNER JOIN client ON client_cin=cin " +
                "WHERE id_contrat LIKE(?) AND nom LIKE(?) AND prenom LIKE(?) " +
                "ORDER BY date_contrat";
        String requSupprContrat="DELETE FROM contrat WHERE id_contrat=?";
        try {
            rechercherContrat= Connexion.connexion.prepareStatement(requRecherContrats);
            supprimerContrat=Connexion.connexion.prepareStatement(requSupprContrat);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeContrat.setCellValueFactory(new PropertyValueFactory<>("code"));
        dateContrat.setCellValueFactory(new PropertyValueFactory<>("dateContrat"));
        dateDepart.setCellValueFactory(new PropertyValueFactory<>("dateDepart"));
        dateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        codeReservation.setCellValueFactory(new PropertyValueFactory<>("codeReservation"));
        vehicule.setCellValueFactory(new PropertyValueFactory<>("vehicule"));
        client.setCellValueFactory(new PropertyValueFactory<>("client"));
        recupererContrats();
    }
    /* -recuperer toutes les contrats (si apeller sans parametre:  "recupererContrats();" )
       -effectuer recherche par code, nomClient, prenomClient (si appelle avec 3 parametres
       -!! c'est vrai quand a le droit de l'appeler avec n parametres avec n>=0 && n<=3
            mais dans la suite on utilise deux possibilite:
                1-appel sans parametre
                2- appel avec exactement 3 parametres
     */
    private void recupererContrats(String...parametres) {
        status.setText("");
        listeContrats.clear();
        try {
            if( parametres.length==3 ) {
                rechercherContrat.setString(1, parametres[0]);
                rechercherContrat.setString(2, parametres[1]);
                rechercherContrat.setString(3, parametres[2]);
            }
            else if( parametres.length==0 ){
                rechercherContrat.setString(1, "%");
                rechercherContrat.setString(2, "%");
                rechercherContrat.setString(3, "%");
            }
            ResultSet resultat=rechercherContrat.executeQuery();
            if( resultat.next() ) {
                resultat.beforeFirst();
                extraireDonnes(resultat);
                tableContrats.setItems(listeContrats);
            }
            else {
                status.setText("aucun contrat trouvé");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void extraireDonnes(ResultSet resultat) {
        try {
            while ( resultat.next() ) {
                Contrat contrat=new Contrat();
                contrat.setCode(resultat.getInt("id_contrat"));
                contrat.setDateContrat(resultat.getDate("date_contrat"));
                contrat.setDateDepart(resultat.getDate("date_depart"));
                contrat.setDateRetour(resultat.getDate("date_retour"));
                int codeReservation=resultat.getInt("reservation_id");
                if( codeReservation!=0 ) {
                    contrat.setCodeReservation(Integer.toString(codeReservation));
                }
                contrat.setVehicule(resultat.getString("matricule")+" - " +
                        resultat.getString("marque")+" - "+
                        resultat.getString("type"));
                contrat.setClient(resultat.getString("cin")+" - "+
                        resultat.getString("nom")+" - "+
                        resultat.getString("prenom"));
                listeContrats.add(contrat);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void rechercherContrats() {
        String codeContrat=code.getText();
        if( codeContrat.length()==0 )
            codeContrat="%";
        else
            codeContrat="%"+codeContrat+"%";
        String nomClient=nom.getText();
        if( nomClient.length()==0 )
            nomClient="%";
        else
            nomClient="%"+nomClient+"%";
        String prenomClient=prenom.getText();
        if( prenomClient.length()==0 )
            prenomClient="%";
        else
            prenomClient="%"+prenomClient+"%";
        recupererContrats(codeContrat, nomClient, prenomClient);
    }
    public void supprimerContrat() {
        Contrat contratSelectionne=tableContrats.getSelectionModel().getSelectedItem();
        if( contratSelectionne==null ) { return; }
        tableContrats.getItems().remove(contratSelectionne);
        try {
            supprimerContrat.setInt(1, contratSelectionne.getCode());
            supprimerContrat.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
    public void modifierContrat() throws Exception {
        Contrat contratSelectionne=tableContrats.getSelectionModel().getSelectedItem();
        if( contratSelectionne==null ) { return; }
        setContratAModifier(contratSelectionne);
        Stage fenetreModification=new Stage();
        fenetreModification.initModality(Modality.APPLICATION_MODAL);
        setFenetreModification(fenetreModification);
        Parent root = FXMLLoader.load(getClass().getResource("modification.fxml"));
        fenetreModification.setTitle("Rent it");
        fenetreModification.getIcons().add(new Image("/images/icon.png"));
        fenetreModification.setScene(new Scene(root));
        fenetreModification.setResizable(false);
        fenetreModification.show();
    }
    public void afficherTous() {
        // vider les champs de recherche
        code.setText("");   nom.setText("");    prenom.setText("");
        recupererContrats();
    }
    public void ajouterContrat() throws Exception {
        Stage fenetreAjout=new Stage();
        fenetreAjout.initModality(Modality.APPLICATION_MODAL);
        setFenetreAjout(fenetreAjout);
        Parent root = FXMLLoader.load(getClass().getResource("ajout.fxml"));
        fenetreAjout.setTitle("Rent it");
        fenetreAjout.setScene(new Scene(root));
        fenetreAjout.getIcons().add(new Image("/images/icon.png"));
        fenetreAjout.setResizable(false);
        fenetreAjout.show();
    }
}