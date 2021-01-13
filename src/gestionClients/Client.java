package gestionClients;

import connexion.Connexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Client {
    private String cin, nom, prenom, gsm, mail, permis;

    public Client() {}
    public Client(String c, String n, String pr, String g, String m, String p) {
        cin=c;
        nom=n;
        prenom=pr;
        gsm=g;
        mail=m;
        permis=p;
    }

    public String getCin() { return cin; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getGsm() { return gsm; }
    public String getMail() { return mail; }
    public String getPermis() { return permis; }

    public void setCin(String cin) { this.cin = cin; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setGsm(String gsm) { this.gsm = gsm; }
    public void setMail(String mail) { this.mail = mail; }
    public void setPermis(String permis) { this.permis = permis; }

    public void supprimer() {
        String requSupprClient="DELETE FROM client WHERE cin=?";
        try {
            PreparedStatement supprClient=Connexion.connexion.prepareStatement(requSupprClient);
            supprClient.setString(1, cin);
            supprClient.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static ObservableList<Client> recuperer(String...parametres) {
        ObservableList<Client> listeClients= FXCollections.observableArrayList();
        String requRecupClient="SELECT * FROM client WHERE nom LIKE (?) AND prenom LIKE (?) " +
                "ORDER BY nom, prenom";
        try {
            PreparedStatement recupClients= Connexion.connexion.prepareStatement(requRecupClient);
            if( parametres.length==2 ) {
                recupClients.setString(1, parametres[0]);
                recupClients.setString(2, parametres[1]);
            }
            else if( parametres.length==0 ) { // recuperer tous les clients
                recupClients.setString(1, "%");
                recupClients.setString(2, "%");
            }
            ResultSet clients=recupClients.executeQuery();
            while ( clients.next() ) {
                Client client=new Client();
                client.setCin(clients.getString("cin"));
                client.setNom(clients.getString("nom"));
                client.setPrenom(clients.getString("prenom"));
                client.setGsm(clients.getString("gsm"));
                client.setMail(clients.getString("mail"));
                client.setPermis(clients.getString("permis"));
                listeClients.add(client);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return listeClients;
    }


}
