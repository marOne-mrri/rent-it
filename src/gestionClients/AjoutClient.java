package gestionClients;

import connexion.Connexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import donneeInvalide.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static donneeInvalide.Verification.*;

public class AjoutClient {
    private static Stage fenetreAjout;
    @FXML private TextField cin, nom, prenom, gsm, mail;
    private String cheminPermis;
    @FXML private Label cErreur, nErreur, pErreur, gErreur, mErreur, nomFichier, status;
    private PreparedStatement ajouterClient;

    static void setFenetreAjout(Stage fA) { fenetreAjout=fA; }
    public AjoutClient() {
        String requAjoutClient="INSERT INTO client(cin, nom, prenom, gsm, mail, permis)" +
                "VALUES(UPPER(?), UPPER(?), UPPER(?), ?, ?, ?)";
        try {
            ajouterClient=Connexion.connexion.prepareStatement(requAjoutClient);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void ajouterClient() {
        supprimerErreurs();
        try {
            verifierCin(cin.getText());
            verifierNom(nom.getText());
            verifierPrenom(prenom.getText());
            verifierGsm(gsm);
            verifierMail(mail);
            ajouterClient.setString(1, cin.getText());
            ajouterClient.setString(2, nom.getText());
            ajouterClient.setString(3, prenom.getText());
            ajouterClient.setString(4, gsm.getText());
            ajouterClient.setString(5, mail.getText());
            ajouterClient.setString(6, cheminPermis);
            ajouterClient.executeUpdate();
            ListeClients.getControlleurClient().rechercherClient();
            fenetreAjout.close();
        }
        catch ( CinInvalide cinInvalide) {
            cErreur.setText(cinInvalide.getMessage()+cinInvalide.getConsigne());
        }
        catch (NomInvalide nomInvalide) {
            nErreur.setText(nomInvalide.getMessage()+nomInvalide.getConsigne());
        }
        catch (PrenomInvalide prenomInvalide) {
            pErreur.setText(prenomInvalide.getMessage()+prenomInvalide.getConsigne());
        }
        catch (GsmInvalide gsmInvalide) {
            gErreur.setText(gsmInvalide.getMessage()+gsmInvalide.getConsigne());
        }
        catch (MailInvalide mailInvalide) {
            mErreur.setText(mailInvalide.getMessage()+mailInvalide.getConsigne());
        }
        catch (SQLException se) {
            status.setText("cin ou (nom et pénom) ou gsm ou e-mail existe déja");
        }
        catch (Exception e) {
            status.setText("erreur inconnu lors de l'ajout de ce client, essayer plus tards");
        }
    }

    public void selectionnerPermis() {
        FileChooser fileChooser=new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier JPG","*.JPG"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PNG","*.PNG"));

        File fichierSelectionne=fileChooser.showOpenDialog(fenetreAjout);
        if( fichierSelectionne!=null ) {
            cheminPermis=fichierSelectionne.getAbsolutePath();
            nomFichier.setText(fichierSelectionne.getName());
        }
        else {
            cheminPermis=null;
            nomFichier.setText("");
        }
    }

    public void annuler(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();

    }

    private void supprimerErreurs() {
        cErreur.setText("");
        nErreur.setText("");
        pErreur.setText("");
        gErreur.setText("");
        mErreur.setText("");
        status.setText("");
    }
}