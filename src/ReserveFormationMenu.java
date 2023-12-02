import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReserveFormationMenu {
    
    private ReserveFormationMenu() {}

    // Fonction pour réserver une formation.
    public static int reserverFormation(int anneeForm, int rangForm) throws SQLException {
        // On regarde s'il reste des places dans la formation.
        int havePlace = (getPlaceRestantFormation(anneeForm, rangForm) > 0) ? 1 : 0;

        // Requête pour déterminer l'id de la réservation.
        PreparedStatement stmt0 = Main.connection.prepareStatement(
            "SELECT MAX(idRF) AS maxIdRF " +
            "FROM ReservationFormation");
        ResultSet rs0 = stmt0.executeQuery();
        rs0.next();
        int maxIdRF = rs0.getInt("maxIdRF");

        // Requête pour ajouter la réservation dans RéservationFormation.
        PreparedStatement stmt = Main.connection.prepareStatement(
            "INSERT INTO ReservationFormation " +
            "VALUES (?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, maxIdRF + 1); // idRF
        stmt.setInt(3, Main.idAdherent); // idAdhérent
        stmt.setInt(4, Main.idUser); // idUser
        stmt.setInt(5, anneeForm); // annéeForm
        stmt.setInt(6, rangForm); // rangForm
        if (havePlace == 1) {
            stmt.setString(2, "validé"); // inscrit
        } else {
            stmt.setString(2, "attente"); // inscrit
        }

        if (havePlace == 0) {
            // S'il n'y a pas de place, on l'ajoute dans la liste d'attente associée à la formation.
            
            // Requête pour déterminer le rang dans la liste d'attente.
            PreparedStatement stmt2 = Main.connection.prepareStatement(
                "SELECT MAX(rangLA) as maxRang " +
                "FROM RangAttenteReservationForm NATURAL JOIN ReservationFormation " +
                "WHERE anneeForm=? AND rangForm=?");
            stmt2.setInt(1, anneeForm); // annéeForm
            stmt2.setInt(2, rangForm); // rangForm
            ResultSet rs2 = stmt2.executeQuery();
            int maxRang = 0;
            if (rs2.next()) {
                maxRang = rs2.getInt("maxRang");
            }

            if (maxRang == 20) {
                // Ici, on choisi que la maximum du nombre de personne dans une liste d'attente est 20.
                return 2;
            }

            stmt.executeUpdate();
            Main.connection.commit();

            // Ajout dans la liste d'attente.
            PreparedStatement stmt1 = Main.connection.prepareStatement(
                "INSERT INTO RangAttenteReservationForm " +
                "VALUES (?, ?)");
            stmt1.setInt(1, maxIdRF + 1); // idRF
            stmt1.setInt(2, maxRang + 1); // rangLA
            
            stmt1.executeUpdate();
            Main.connection.commit();
        } else {
            stmt.executeUpdate();
            Main.connection.commit();
        }

        return havePlace;
    }

    // Fonction pour avoir le nombre de place total d'une Formation.
    public static int getNbPlaceFormation(int anneeForm, int rangForm) throws SQLException {
        PreparedStatement stmt = Main.connection.prepareStatement("SELECT nbPlace " +
            "FROM Formation " +
            "WHERE anneeForm=? AND rangForm=?");
        stmt.setInt(1, anneeForm);
        stmt.setInt(2, rangForm);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("nbPlace");
        }
        return 0;
    }

    // Fonction pour avoir le nombre de place restant d'une Formation.
    public static int getPlaceRestantFormation(int anneeForm, int rangForm) throws SQLException {
        int nbForm = getNbPlaceFormation(anneeForm, rangForm);
        PreparedStatement stmt = Main.connection.prepareStatement("SELECT COUNT(*) as nbFormReserve " +
            "FROM ReservationFormation " +
            "WHERE anneeForm=? AND rangForm=?");
        stmt.setInt(1, anneeForm);
        stmt.setInt(2, rangForm);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return nbForm - rs.getInt("nbFormReserve");
        }
        return 0;
    }

    // Fonction pour avoir toutes les activités d'une Formation.
    public static ArrayList<String> getActivitesFromFormation(int anneeForm, int rangForm) throws SQLException {
        PreparedStatement stmt = Main.connection.prepareStatement(
            "SELECT nomActivite " +
            "FROM FormationPossedeActivites " +
            "WHERE anneeForm=? AND rangForm=?"
        );
        stmt.setInt(1, anneeForm);
        stmt.setInt(2, rangForm);

        ResultSet rs = stmt.executeQuery();

        ArrayList<String> activites = new ArrayList<>();
        while (rs.next()) {
            activites.add(rs.getString("nomActivite"));
        }
        return activites;
    }

}
