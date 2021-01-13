package gestionFactures;

import connexion.Connexion;
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
import static  gestionFactures.ModificationFacture.setFactureAModifier;
import static  gestionFactures.ModificationFacture.setFenetreModification;

public class ListeFactures implements Initializable {
    private static ListeFactures controlleurFacture;
    @FXML private Label status;
    @FXML private TextField code, nom, prenom;
    @FXML private TableView<Facture> tableFactures;
    @FXML private TableColumn<Facture, Integer> codeFacture;
    @FXML private TableColumn<Facture, Date> dateFacture;
    @FXML private TableColumn<Facture, String> client;
    @FXML private TableColumn<Facture, Double> montant;

    private ObservableList<Facture> listeFactures= FXCollections.observableArrayList();
    private PreparedStatement rechercherFacture;

    static ListeFactures getControlleurFacture() { return controlleurFacture; }

    public ListeFactures() {
        controlleurFacture=this;
        String requRecherchFacture="SELECT contrat_id AS id_facture, date_contrat AS date_facture, " +
                "montant, cin, nom, prenom FROM facture " +
                "INNER JOIN contrat on contrat_id=id_contrat " +
                "INNER JOIN client on client_cin=cin " +
                "WHERE contrat_id LIKE(?) AND nom LIKE(?) AND prenom LIKE(?) " +
                "ORDER BY date_facture";
        try {
            rechercherFacture=Connexion.connexion.prepareStatement(requRecherchFacture);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeFacture.setCellValueFactory(new PropertyValueFactory<>("code"));
        dateFacture.setCellValueFactory(new PropertyValueFactory<>("dateFacture"));
        client.setCellValueFactory(new PropertyValueFactory<>("client"));
        montant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        recupererFactures();
    }

    private void recupererFactures(String...parametres) {
        status.setText("");
        listeFactures.clear();
        try {
            if( parametres.length==3 ) {
                rechercherFacture.setString(1, parametres[0]);
                rechercherFacture.setString(2, parametres[1]);
                rechercherFacture.setString(3, parametres[2]);
            }
            else if( parametres.length==0 ){
                rechercherFacture.setString(1, "%");
                rechercherFacture.setString(2, "%");
                rechercherFacture.setString(3, "%");
            }
            ResultSet resultat=rechercherFacture.executeQuery();
            if( resultat.next() ) {
                resultat.beforeFirst();
                extraireDonnes(resultat);
                tableFactures.setItems(listeFactures);
            }
            else {
                status.setText("aucune facture trouvé !");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void extraireDonnes(ResultSet resultat) {
        try {
            while ( resultat.next() ) {
                Facture facture=new Facture();
                facture.setCode(resultat.getInt("id_facture"));
                facture.setDateFacture(resultat.getDate("date_facture"));
                facture.setClient(resultat.getString("cin") + " - "+ resultat.getString("nom")+" - "+ resultat.getString("prenom"));
                facture.setMontant(resultat.getDouble("montant"));
                listeFactures.add(facture);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void rechercherFactures() {
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
        recupererFactures(codeContrat, nomClient, prenomClient);
    }

    public void modifierFacture() throws Exception {
        Facture factureSelectionne=tableFactures.getSelectionModel().getSelectedItem();
        if( factureSelectionne == null ) { return; }
        setFactureAModifier(factureSelectionne);
        Stage fenetreModification=new Stage();
        fenetreModification.initModality(Modality.APPLICATION_MODAL);
        setFenetreModification(fenetreModification);
        Parent root = FXMLLoader.load(getClass().getResource("modification.fxml"));
        fenetreModification.setTitle("Rent it");
        fenetreModification.getIcons().add(new Image("/images/icon.png"));
        fenetreModification.setResizable(false);
        fenetreModification.setScene(new Scene(root));
        fenetreModification.show();
    }

    public void supprimerFacture() {
        Facture factureSelectionne=tableFactures.getSelectionModel().getSelectedItem();
        if( factureSelectionne==null ) { return; }
        tableFactures.getItems().remove(factureSelectionne);
        factureSelectionne.supprimer();
    }

    public void afficherTous() {
        // vider les champs de recherche
        code.setText("");   nom.setText("");    prenom.setText("");
        recupererFactures();
    }
}
