import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaiementMenu {
   
    private PaiementMenu() {}

    // Fonction pour payer toutes les SommeDueUser.
    public static void showAPayer() throws SQLException {
        // On récupère la somme totale qu'il doit payer.
        ResultSet rs = Main.connection.createStatement().executeQuery(
            "SELECT SUM(prix) as total " + 
            "FROM SommeDueUser " +
            "WHERE idUser=" + Main.idUser
        );
        
        int somme;
        if (rs.next()) {
            somme = rs.getInt("total");
        } else {
            somme = 0;
        }

        // Comme il a tout payé, on met toutes les valeurs de ses SommeDueUser à 0.
        PreparedStatement stmt = Main.connection.prepareStatement(
            "UPDATE SommeDueUser " +
            "SET prix=0 " +
            "WHERE idUser=?"
        );
        stmt.setInt(1, Main.idUser);
        stmt.executeUpdate();
        Main.connection.commit();

        System.out.println("Tout a été payé (" + somme + " euros).");

        PrincipalMenu.displayMenu();
    }

}
