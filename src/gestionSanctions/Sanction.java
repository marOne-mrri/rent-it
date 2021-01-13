package gestionSanctions;

import connexion.Connexion;
import gestionVehicules.Vehicule;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Sanction {
    private int idContrat;
    private String client;
    private Date dateRetourTheorique, DateRetourReele;
    private int retard;//we don't think the delay will last forever
    private double montant;

    public int getIdContrat() { return idContrat; }
    public String getClient() { return client; }
    public Date getDateRetourTheorique() { return dateRetourTheorique; }
    public Date getDateRetourReele() { return DateRetourReele; }
    public int getRetard() { return retard; }
    public double getMontant() { return montant; }

    public void setIdContrat(int idContrat) { this.idContrat = idContrat; }
    public void setClient(String client) { this.client = client; }
    public void setDateRetourTheorique(Date d) { dateRetourTheorique=d; }
    public void setDateRetourReele(Date d) { DateRetourReele = d; }
    public void setRetard(int retard) { this.retard = retard; }
    public void setMontant(double montant) { this.montant = montant; }

    public void regler() {
        String requ="UPDATE facture SET sanction=0 WHERE contrat_id=?";
        try {
            PreparedStatement reglSanction=Connexion.connexion.prepareStatement(requ);
            reglSanction.setInt(1, idContrat);
            reglSanction.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void verifierDateRetour(Vehicule vehicule) {
        String requ="UPDATE facture INNER JOIN contrat ON id_contrat=contrat_id " +
                "SET sanction=DATEDIFF(?, date_retour)*2000, date_retour_reele=? " +
                "WHERE contrat_id=(SELECT MAX(id_contrat) FROM contrat WHERE vehicule_matricule=?)";
        try {
            PreparedStatement verification= Connexion.connexion.prepareStatement(requ);
            verification.setDate(1, Date.valueOf(LocalDate.now()));
            verification.setDate(2, Date.valueOf(LocalDate.now()));
            verification.setString(3, vehicule.getMatricule());
            verification.executeUpdate();
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
