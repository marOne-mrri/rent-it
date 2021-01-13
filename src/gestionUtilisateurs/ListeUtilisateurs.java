package gestionUtilisateurs;

import com.jfoenix.controls.JFXToggleButton;
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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListeUtilisateurs implements Initializable{
    private static ListeUtilisateurs controlleurUtilisateur;
    @FXML private JFXToggleButton tous, present, enConge;
    // les deux boutons pour suppendre et annuler suspension
    @FXML Button suspensendre, annulerSuspension;
    @FXML private Label status;
    @FXML private TableView<Utilisateur> tableUtilisateurs;
    @FXML private TableColumn<Utilisateur, String> nomUtilisateur;
    @FXML private TableColumn<Utilisateur, String> nom;
    @FXML private TableColumn<Utilisateur, String> prenom;
    @FXML private TableColumn<Utilisateur, String> mail;
    @FXML private TableColumn<Utilisateur, String> gsm;
    @FXML private TableColumn<Utilisateur, LocalDate> debutConge, finConge;
    private ObservableList<Utilisateur> listeUtilisateurs= FXCollections.observableArrayList();
    enum CritereRecherche{TOUS, PRESENT, EN_CONGE}
    private String recupTous="SELECT * FROM utilisateur " + // all users expect the admin
            "WHERE nom_utilisateur!='"+Connexion.utilisateur.getNomUtilisateur()+"'";
    private String recupPresent=recupTous +
            "AND (debut_conge IS NULL OR debut_conge>CURDATE() OR fin_conge<CURDATE())";
    private String recupEnConge=recupTous +
            "AND (debut_conge<=CURDATE() AND fin_conge>=CURDATE())";
    private Statement recupUtilisateurs;

    static ListeUtilisateurs getControlleurUtilisateur() { return controlleurUtilisateur; }

    public ListeUtilisateurs() {
        controlleurUtilisateur=this;
        try {
            recupUtilisateurs=Connexion.connexion.createStatement();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nomUtilisateur.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));
        nom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        mail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        gsm.setCellValueFactory(new PropertyValueFactory<>("gsm"));
        debutConge.setCellValueFactory(new PropertyValueFactory<>("debutConge"));
        finConge.setCellValueFactory(new PropertyValueFactory<>("finConge"));
        tous.setSelected(true);
        rechercherUtilisateurs();
    }

    private void recupererUtilisateurs(CritereRecherche critere) {
        mettreAjourSuspensions();
        status.setText("");
        listeUtilisateurs.clear();
        try {
            String requete;
            if( critere==CritereRecherche.TOUS ) {
                requete=recupTous;
            }
            else if( critere==CritereRecherche.PRESENT ) {
                requete=recupPresent;
            }
            else { // critere==CritereRecherche.EN_CONGE
                requete=recupEnConge;
            }
            ResultSet utilisateurs=recupUtilisateurs.executeQuery(requete);
            if( utilisateurs.next() ) {
                utilisateurs.beforeFirst();
                extraireDonnees(utilisateurs);// remplir listeUtilisateurs
                tableUtilisateurs.setItems(listeUtilisateurs);
            }
            else {
                status.setText("Aucun utilisateurs trouvé");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void extraireDonnees(ResultSet utilisateurs) {
        try {
            LocalDate debutConge, finConge;
            Date tmp;
            while (utilisateurs.next()) {
                Utilisateur u=new Utilisateur();
                u.setNomUtilisateur(utilisateurs.getString("nom_utilisateur"));
                u.setNom(utilisateurs.getString("nom"));
                u.setPrenom(utilisateurs.getString("prenom"));
                u.setMail(utilisateurs.getString("mail"));
                u.setGsm(utilisateurs.getString("gsm"));
                debutConge=finConge=null;
                tmp=utilisateurs.getDate("debut_conge");
                if( tmp!=null ) debutConge=tmp.toLocalDate();
                u.setDebutConge(debutConge);
                tmp=utilisateurs.getDate("fin_conge");
                if( tmp!=null ) finConge=tmp.toLocalDate();
                u.setFinConge(finConge);
                listeUtilisateurs.add(u);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void rechercherUtilisateurs() {
        if( tous.isSelected() ) {
            recupererUtilisateurs(CritereRecherche.TOUS);
            // si "tous" est selectionne on peut seulement supprimer et modifier
            suspensendre.setDisable(true);
            annulerSuspension.setDisable(true);
        }
        else if( present.isSelected() ) {
            // si "present" est selectionne on peut modifier, supperimer et suspendre
            recupererUtilisateurs(CritereRecherche.PRESENT);
            suspensendre.setDisable(false);
            annulerSuspension.setDisable(true);
        }
        else if( enConge.isSelected() ) {
            // si "en Conge" est selectionne on peut modifier, supprimer et arreter suspension
            recupererUtilisateurs(CritereRecherche.EN_CONGE);
            suspensendre.setDisable(true);
            annulerSuspension.setDisable(false);
        }
    }

    public void supprimerUtilisateur() {
        Utilisateur utilisateurSelectionne=tableUtilisateurs.getSelectionModel().getSelectedItem();
        if( utilisateurSelectionne==null ) { return; }
        tableUtilisateurs.getItems().remove(utilisateurSelectionne);
        utilisateurSelectionne.supprimer();
    }

    public void  modifierUtilisateur() throws Exception{
        Utilisateur utilisateurSelectionne=tableUtilisateurs.getSelectionModel().getSelectedItem();
        if( utilisateurSelectionne==null ) { return; }
        ModificationUtilisateur.setUtilisateurAModifier(utilisateurSelectionne);
        Stage fenetreModification=new Stage();
        fenetreModification.initModality(Modality.APPLICATION_MODAL);
        ModificationUtilisateur.setFenetreModiication(fenetreModification);
        Parent root = FXMLLoader.load(getClass().getResource("modification.fxml"));
        fenetreModification.setTitle("Rent it");
        fenetreModification.setScene(new Scene(root));
        fenetreModification.getIcons().add(new Image("/images/icon.png"));
        fenetreModification.setResizable(false);
        fenetreModification.show();
    }

    public void suspendreUtilisateur() throws Exception {
        Utilisateur utilisateurSelectionne=tableUtilisateurs.getSelectionModel().getSelectedItem();
        if( utilisateurSelectionne==null ) { return; }
        SuspensionUtilisateur.setUtilisateurASuspendre(utilisateurSelectionne);
        Stage fenetreSuspension=new Stage();
        fenetreSuspension.initModality(Modality.APPLICATION_MODAL);
        SuspensionUtilisateur.setFenetreSuspension(fenetreSuspension);
        Parent root = FXMLLoader.load(getClass().getResource("suspension.fxml"));
        fenetreSuspension.setTitle("Rent it");
        fenetreSuspension.getIcons().add(new Image("/images/icon.png"));
        fenetreSuspension.setScene(new Scene(root));
        fenetreSuspension.setResizable(false);
        fenetreSuspension.show();
    }

    public void arreterSuspension() {
        Utilisateur utilisateurSelectionne=tableUtilisateurs.getSelectionModel().getSelectedItem();
        if( utilisateurSelectionne==null ) { return; }
        tableUtilisateurs.getItems().remove(utilisateurSelectionne);
        utilisateurSelectionne.arreterSuspension();
    }

    /* pour automatiquement arreter les suspension */
    private void mettreAjourSuspensions() {
        String requ="UPDATE utilisateur SET debut_conge=NULL, fin_conge=NULL " +
                "WHERE fin_conge<CURDATE()";
        try {
            recupUtilisateurs.executeUpdate(requ);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void AjouterUtilisateur() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("ajout.fxml"));
            Stage myStage = new Stage();
            AjoutUtilisateur.setFenetreAjout(myStage);
            myStage.setScene(new Scene(root));
            myStage.setTitle("Rent it");
            myStage.initModality(Modality.APPLICATION_MODAL);
            myStage.getIcons().add(new Image("/images/icon.png"));
            myStage.setResizable(false);
            myStage.show();
        } catch (IOException e) {
            Logger.getLogger(Connexion.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
