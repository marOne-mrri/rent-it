package connexion;

import gestionUtilisateurs.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class Connexion {
    public static Connection connexion;
    // l'utilisateur qui va utiliser l'application
    public static final Utilisateur utilisateur = new Utilisateur();
    @FXML private Label status;
    @FXML private TextField nomUtilisateur;
    @FXML private PasswordField motDePasse;
    private String url = "jdbc:mysql://localhost:3306/agence?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private String messageErreur;

    public void seConnecter(ActionEvent event) {
        if( isUser() ) {
            if( isAdmin() ) {
                utilisateur.setAdmin(true);
            } else {
                utilisateur.setAdmin(false);
            }
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../menu/main.fxml"));
                Stage myStage = new Stage();
                myStage.setScene(new Scene(root));
                myStage.setTitle("Rent it");
                myStage.getIcons().add(new Image("images/icon.png"));
                myStage.show();
                ((Node) event.getSource()).getScene().getWindow().hide();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            status.setText(messageErreur);
        }
    }

    private boolean isUser() {
        try {
            connexion = DriverManager.getConnection(url, "user", "user");
            PreparedStatement statement = connexion.prepareStatement("SELECT * FROM utilisateur " + "WHERE nom_utilisateur=? AND mot_passe=PASSWORD(?)");
            statement.setString(1, nomUtilisateur.getText());
            statement.setString(2, motDePasse.getText());
            ResultSet resultat=statement.executeQuery();
            if( resultat.next() ) {
                utilisateur.setNomUtilisateur(resultat.getString("nom_utilisateur"));
                utilisateur.setNom(resultat.getString("nom"));
                utilisateur.setPrenom(resultat.getString("prenom"));
                utilisateur.setMail(resultat.getString("mail"));
                utilisateur.setGsm(resultat.getString("gsm"));
                Date debutConge=resultat.getDate("debut_conge");
                Date finConge=resultat.getDate("fin_conge");
                Date now=Date.valueOf(LocalDate.now());

                // tester si l'utilisateur est en conge
                // !!! a noter qu'on PEUT specifier seulement date debut conge sans date fin conge !!!
                if( debutConge!=null && debutConge.compareTo(now)<=0 && (finConge==null || finConge.compareTo(now)>=0) ) {
                    messageErreur="Normalement, vous etes en congé !";
                    return false;
                }
                return true;
            } else {
                messageErreur="nom d'utilisateur ou mot de passe invalide";
            }
        }
        catch (SQLException se) {
            messageErreur = "erreur de connexion à la base de donnée !";
            se.printStackTrace();
        }
        return false;
    }

    private boolean isAdmin() {
        try {
            PreparedStatement statement= connexion.prepareStatement("SELECT * FROM administrateur WHERE utilisateur_nom=?");
            statement.setString(1, nomUtilisateur.getText());
            ResultSet resultat=statement.executeQuery();
            if( resultat.next() ) {
                connexion.close();
                connexion = DriverManager.getConnection(url, "admin_agence", "admin");
                return true;
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return false;
    }
}