package gestionParkings;

import connexion.Connexion;
import gestionVehicules.Vehicule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Parking {
    private int id;
    private String rue;
    private short capacite; // nombre totale qu'un parking peut supporter
    private short taille;// nombre actuel des vehicules dans un parking
    private ObservableList<Vehicule> vehicules= FXCollections.observableArrayList();

    public int getId() { return id; }
    public String getRue() { return rue; }
    public short getCapacite() { return capacite; }
    public short getTaille() { return taille; }
    public ObservableList<Vehicule> getVehicules() { return vehicules; }

    public void setId(int id) { this.id = id; }
    public void setRue(String rue) { this.rue = rue; }
    public void setCapacite(short capacite) { this.capacite = capacite; }
    public void setTaille(short taille) { this.taille = taille; }
    public void setVehicules(ObservableList<Vehicule> vehicules) {
        this.vehicules = vehicules;
    }

    /* recuperer les vehicules d'un parking */
    public ObservableList<Vehicule> recupererVehicules(PreparedStatement recupVehicules) {
        if( !vehicules.isEmpty() )  return vehicules;
        try {
            recupVehicules.setInt(1, id);
            ResultSet vehicules=recupVehicules.executeQuery();
            while( vehicules.next() ) {
                Vehicule vehicule=new Vehicule();
                vehicule.setMatricule(vehicules.getString("matricule"));
                vehicule.setMarque(vehicules.getString("marque"));
                vehicule.setType(vehicules.getString("type"));
                vehicule.setParking(vehicules.getString("parking_id"));
                this.vehicules.add(vehicule);
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return vehicules;
    }

    public void supprimer() {
        Vehicule.marquerSansParking(this.id);
        String requSupprParking = "DELETE FROM parking WHERE id_parking=?";
        try {
            PreparedStatement supprParking = Connexion.connexion.prepareStatement(requSupprParking);
            supprParking.setInt(1, id);
            supprParking.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void incrementerTaille(int idParking) {
        String requIncrTaille = "UPDATE parking SET taille=taille+1 WHERE id_parking=?";
        try {
            PreparedStatement incrementTaille=Connexion.connexion.prepareStatement(requIncrTaille);
            incrementTaille.setInt(1, idParking);
            incrementTaille.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void decrimenterTaille(int idParking) {
        String requDecrTaille="UPDATE parking SET taille=taille-1 WHERE id_parking=?";
        try {
            PreparedStatement decrimentTaille=Connexion.connexion.prepareStatement(requDecrTaille);
            decrimentTaille.setInt(1, idParking);
            decrimentTaille.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    /* pour recuperer la liste des parkings non pleins */
    public static ObservableList<String> recupererNonPleins() {
        ObservableList<String> listeParkings= FXCollections.observableArrayList();
        try {
            Statement statement= Connexion.connexion.createStatement();
            ResultSet parkings=statement.executeQuery("SELECT id_parking from parking " +
                    "WHERE taille < capacite");
            while( parkings.next() ) {
                listeParkings.add( parkings.getString("id_parking") );
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return listeParkings;
    }

    /* recuperer tous les parkings avec possibilite d'effectuer des recherches */
    public static ObservableList<Parking> recupererParking(String...parametres) {
        ObservableList<Parking> listeParkings= FXCollections.observableArrayList();
        try {
            String requRecupParkings="SELECT * FROM parking WHERE id_parking LIKE(?) AND rue LIKE(?)";
            PreparedStatement recupParkings=Connexion.connexion.prepareStatement(requRecupParkings);
            if( parametres.length==2 ) {
                recupParkings.setString(1, parametres[0]);
                recupParkings.setString(2, parametres[1]);
            }
            else { //parametres.length==0
                recupParkings.setString(1, "%");
                recupParkings.setString(2, "%");
            }
            ResultSet parkings=recupParkings.executeQuery();
            String requRecupVehicules="SELECT matricule,marque,type,parking_id FROM vehicule " +
                    "WHERE parking_id=?";
            PreparedStatement recupVehicules=Connexion.connexion.prepareStatement(requRecupVehicules);
            while( parkings.next() ) {
                Parking parking=new Parking();
                parking.setId(parkings.getInt("id_parking"));
                parking.setRue(parkings.getString("rue"));
                parking.setCapacite(parkings.getShort("capacite"));
                parking.setTaille(parkings.getShort("taille"));
                listeParkings.add(parking);
                parking.recupererVehicules(recupVehicules); //recuperer les vehicules d'un parking de la BD
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
        return listeParkings;
    }
}
