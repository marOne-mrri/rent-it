package gestionClients;

import connexion.Connexion;
import donneeInvalide.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static donneeInvalide.Verification.*;

public class ModificationClient implements Initializable {
    private static Stage fenetreModification;
    private static Client clientAModifier;
    @FXML private Label cin;
    @FXML private TextField nouveauNom, nouveauPrenom, nouveauGsm, nouveauMail;
    @FXML private ImageView zonePermis;
    @FXML private Label nErreur, pErreur, gErreur, mErreur, nouveauNomImage, permisErreur;
    private String nouveauCheminPermis;
    private PreparedStatement modifClient;

    static void setFenetreModification(Stage f) { fenetreModification=f; }
    static void setClientAModifier(Client c) { clientAModifier=c; }

    public ModificationClient() {
        String requeModifClient="UPDATE client SET nom=upper(?), prenom=upper(?), " +
                "gsm=?, mail=?, permis=? WHERE cin=?";
        try {
            modifClient=Connexion.connexion.prepareStatement(requeModifClient);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cin.setText(clientAModifier.getCin());
        nouveauNom.setText(clientAModifier.getNom());
        nouveauPrenom.setText(clientAModifier.getPrenom());
        nouveauGsm.setText(clientAModifier.getGsm());
        nouveauMail.setText(clientAModifier.getMail());
        nouveauCheminPermis=clientAModifier.getPermis();// pour le moment nouveau c'est actuel
       if( nouveauCheminPermis!=null ) {
           try {
               Image permis=new Image(new FileInputStream(nouveauCheminPermis));
               zonePermis.setImage(permis);
           }
           catch (java.io.FileNotFoundException f) {
               permisErreur.setText("image "+nouveauCheminPermis+" est introuvable");
           }
        }
    }

    public void enregistrerModification() {
        supprimerErreurs();
        try {
            verifierNom(nouveauNom.getText());
            verifierPrenom(nouveauPrenom.getText());
            verifierGsm(nouveauGsm);
            verifierMail(nouveauMail);
            modifClient.setString(1, nouveauNom.getText());
            modifClient.setString(2, nouveauPrenom.getText());
            modifClient.setString(3, nouveauGsm.getText());
            modifClient.setString(4, nouveauMail.getText());
            modifClient.setString(5, nouveauCheminPermis);
            modifClient.setString(6, clientAModifier.getCin());
            modifClient.executeUpdate();
            ListeClients.getControlleurClient().rechercherClient();
            fenetreModification.close();
        }
        catch (NomInvalide nomInvalide) {
            nErreur.setText(nomInvalide.getMessage()+nomInvalide.getConsigne());
        }
        catch (PrenomInvalide prenomInvalide) {
            pErreur.setText(prenomInvalide.getMessage()+prenomInvalide.getConsigne());
        }
        catch (MailInvalide mailInvalide) {
            mErreur.setText(mailInvalide.getMessage()+mailInvalide.getConsigne());
        }
        catch (GsmInvalide gsmInvalide) {
            gErreur.setText(gsmInvalide.getMessage()+gsmInvalide.getConsigne());
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void selectionnerNouveauPermis() throws java.io.FileNotFoundException {
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Fichier JPG","*.JPG"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Fichier PNG","*.PNG"));
        File fichierSelectionne=fileChooser.showOpenDialog(fenetreModification);
        if( fichierSelectionne!=null ) {
            nouveauCheminPermis=fichierSelectionne.getAbsolutePath();
            nouveauNomImage.setText(fichierSelectionne.getName());
            Image nouveauPermis=new Image(new FileInputStream(nouveauCheminPermis));
            zonePermis.setImage(nouveauPermis);
        }
        else {
            nouveauCheminPermis=null;
            nouveauNomImage.setText("");
            zonePermis.setImage(null);
            permisErreur.setText("");
        }
    }

    private void supprimerErreurs() {
        nErreur.setText("");
        pErreur.setText("");
        gErreur.setText("");
        mErreur.setText("");
    }

    public void annuler(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }










}
