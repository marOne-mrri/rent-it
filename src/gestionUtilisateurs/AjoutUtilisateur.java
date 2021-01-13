package gestionUtilisateurs;

import connexion.Connexion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import donneeInvalide.*;
import javafx.stage.Stage;

import static donneeInvalide.Verification.*;

public class AjoutUtilisateur {
    private static Stage fenetreAjout;
    @FXML private TextField nomUtilisateur, motPasse, nom, prenom, mail, gsm;
    @FXML private Label nUErreur,mpErreur, nErreur, pErreur, mErreur, gErreur, sqlErreur;
    private PreparedStatement ajoutUtilisateur;

    static void setFenetreAjout(Stage fA) { fenetreAjout=fA; }

    public AjoutUtilisateur() {
        String requAjoutUtilis="INSERT INTO utilisateur(nom_utilisateur, nom, prenom, mail, gsm, " +
                "mot_passe) VALUES (?, upper(?), upper(?), ?, ?, PASSWORD(?))";
        try {
            ajoutUtilisateur= Connexion.connexion.prepareStatement(requAjoutUtilis);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void ajouterUtilisateur() {
        supprimerErreurs();
        try {
            verifierNomUtilisateur(nomUtilisateur.getText());
            verifierMotPasse(motPasse.getText());
            verifierNom(nom.getText());
            verifierPrenom(prenom.getText());
            verifierMail(mail);
            verifierGsm(gsm);
            ajoutUtilisateur.setString(1, nomUtilisateur.getText());
            ajoutUtilisateur.setString(2, nom.getText());
            ajoutUtilisateur.setString(3, prenom.getText());
            ajoutUtilisateur.setString(4, mail.getText());
            ajoutUtilisateur.setString(5, gsm.getText());
            ajoutUtilisateur.setString(6, motPasse.getText());
            ajoutUtilisateur.executeUpdate();
            ListeUtilisateurs.getControlleurUtilisateur().rechercherUtilisateurs();
            fenetreAjout.close();
        }
        catch (NomUtilisateurInvalide nomUtilisateurInvalide) {
            nUErreur.setText(nomUtilisateurInvalide.getMessage()+nomUtilisateurInvalide.getConsigne());
        }
        catch (MotPasseVide motPasseVide ) {
            mpErreur.setText(motPasseVide.getMessage()+motPasseVide.getConsigne());
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
        } catch (SQLException se) {
            se.printStackTrace();
            sqlErreur.setText("nom d'utilisateur ou (nom et prénom) ou mail ou gsm existe déja");
        }
    }

    private void supprimerErreurs() {
        nUErreur.setText("");
        mpErreur.setText("");
        nErreur.setText("");
        pErreur.setText("");
        mErreur.setText("");
        gErreur.setText("");
    }

    public void annuler() {
        fenetreAjout.close();
    }
}