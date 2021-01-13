package gestionUtilisateurs;

import connexion.Connexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import donneeInvalide.*;
import static donneeInvalide.Verification.*;


public class ModificationUtilisateur implements Initializable {
    private static Stage fenetreModiication;
    private static Utilisateur utilisateurAModifier;
    @FXML private TextField nouveauNomUtilisateur, nouveauNom, nouveauPrenom, nouveauMail, nouveauGsm;
    @FXML private DatePicker nouveauDebutConge, nouveauFinConge;
    @FXML private Label nUErreur, nErreur, pErreur, mErreur, gErreur, dCErreur, fCErreur, sqlErreur;
    private PreparedStatement modifierUtilisateur;

    static void setFenetreModiication(Stage f) { fenetreModiication=f; }
    static void setUtilisateurAModifier(Utilisateur u) { utilisateurAModifier=u; }
    public ModificationUtilisateur() {
        String requModifUtili="UPDATE utilisateur SET nom_utilisateur=?, nom=upper(?), prenom=upper(?), mail=?, " +
                "gsm=?, debut_conge=?, fin_conge=? WHERE nom_utilisateur=?";
        try {
            modifierUtilisateur=Connexion.connexion.prepareStatement(requModifUtili);
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nouveauNomUtilisateur.setText(utilisateurAModifier.getNomUtilisateur());
        nouveauNom.setText(utilisateurAModifier.getNom());
        nouveauPrenom.setText(utilisateurAModifier.getPrenom());
        String donnee;
        donnee=utilisateurAModifier.getMail();
        if( donnee!=null ) { nouveauMail.setText(donnee); }
        else { nouveauMail.setText(""); }
        donnee=utilisateurAModifier.getGsm();
        if( donnee!=null ) { nouveauGsm.setText(donnee); }
        else { nouveauGsm.setText(""); }
        nouveauDebutConge.setValue(utilisateurAModifier.getDebutConge());
        nouveauFinConge.setValue(utilisateurAModifier.getFinConge());
    }

    public void enregistrerModification() {
        supprimerErreurs();
        try {
            verifierNomUtilisateur(nouveauNomUtilisateur.getText());
            verifierNom(nouveauNom.getText());
            verifierPrenom(nouveauPrenom.getText());
            verifierMail(nouveauMail);
            verifierGsm(nouveauGsm);
            verifierDates(nouveauDebutConge.getValue(), nouveauFinConge.getValue(),
                    dCErreur, fCErreur);
            modifierUtilisateur.setString(1, nouveauNomUtilisateur.getText());
            modifierUtilisateur.setString(2, nouveauNom.getText());
            modifierUtilisateur.setString(3, nouveauPrenom.getText());
            modifierUtilisateur.setString(4, nouveauMail.getText());
            modifierUtilisateur.setString(5, nouveauGsm.getText());
            if( nouveauDebutConge.getValue()!=null ) {
                Date debutConge=Date.valueOf(nouveauDebutConge.getValue());
                modifierUtilisateur.setDate(6, debutConge);
            }
            else {
                modifierUtilisateur.setDate(6, null);
            }
            if( nouveauFinConge.getValue()!=null ) {
                Date finConge=Date.valueOf(nouveauFinConge.getValue());
                modifierUtilisateur.setDate(7, finConge);
            }
            else {
                modifierUtilisateur.setDate(7, null);
            }
            modifierUtilisateur.setString(8, utilisateurAModifier.getNomUtilisateur());
            modifierUtilisateur.executeUpdate();
            ListeUtilisateurs.getControlleurUtilisateur().rechercherUtilisateurs();
            fenetreModiication.close();
        }
        catch (NomUtilisateurInvalide nomUtilisateurInvalide) {
            nUErreur.setText(nomUtilisateurInvalide.getMessage()+nomUtilisateurInvalide.getConsigne());
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
        catch (DateInvalide dateInvalide) {
            dateInvalide.getCible().setText(dateInvalide.getMessage()+dateInvalide.getConsigne());
        }
        catch (SQLIntegrityConstraintViolationException sicv ){
            sqlErreur.setText("nom d'utilisateur ou (nom et prenom) ou e-mail ou gsm existe déja");
            sicv.getMessage();
        }
        catch (SQLException se) {
            sqlErreur.setText("Erreur lors de l'application des modifications, essayer plus tards");
            se.printStackTrace();
        }
    }

    private void supprimerErreurs() {
        nUErreur.setText("");
        nErreur.setText("");
        pErreur.setText("");
        mErreur.setText("");
        gErreur.setText("");
        dCErreur.setText("");
        fCErreur.setText("");
        sqlErreur.setText("");
    }

    @FXML
    void annuler(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }

}





