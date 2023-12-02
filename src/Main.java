import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static final String URL = "";
    public static final String USER = "";
    public static final String PSWD = "";
    public static Connection connection = null;

    public static int idUser = 0;
    public static Integer idAdherent = null;

    // Main.
    public static void main(String[] args) {
        try {
            // Initialisations.
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            Main.connection = DriverManager.getConnection(URL, USER, PSWD);

            // Pas d'AutoCommit.
            Main.connection.setAutoCommit(false);

            // Serializable.
            Main.connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            boolean logged = false;

            while (!logged) {
                System.out.print("Entrez Votre E-Mail : ");
                String userMail = Utils.scanner.nextLine();

                System.out.print("\nEntrez Votre Password : ");
                String userPswd = Utils.scanner.nextLine();

                // Requête pour voir si un Membre avec les informations entrées existe.
                PreparedStatement stmt = Main.connection.prepareStatement(
                    "SELECT * FROM Membre WHERE emailUser=? AND MDP=?"
                );
                stmt.setString(1, userMail);
                stmt.setString(2, userPswd);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    // Les informations entrées n'existent pas dans la base de données.
                    System.out.println("Les informations entrées ne sont pas bonnes, veuillez réessayer.");
                    continue;
                }

                idUser = rs.getInt("idUser");
                System.out.println("\nLogged in with id=" + idUser);

                // Requête pour voir si l'utilisateur est un Adhérent.
                PreparedStatement stmt1 = Main.connection.prepareStatement(
                    "SELECT * FROM Adherent WHERE idUser=?"
                );
                stmt1.setInt(1, idUser);
                ResultSet rs1 = stmt1.executeQuery();

                if (rs1.next()) {
                    // C'est un adhérent.
                    idAdherent = rs1.getInt("idAdherent");
                    System.out.println("User is Adherent with id=" + idAdherent);
                }

                logged = true;
            }

            // Affichage du menu principal.
            PrincipalMenu.displayMenu();

            // Fermetures.
            Utils.scanner.close();
            Main.connection.close();
        } catch (SQLException e) {
            try {
                // Ceci se produit quand plusieurs utilisateurs réservent la même chose, pour ne pas avoir d'incohérence dans la base de données.
                Main.connection.rollback();
                System.out.println("Désolé, une erreur est survenue, réessayez votre réservation.");
                PrincipalMenu.displayMenu();
            } catch (SQLException e1) {
                // Si jamais le rollback ne se passe pas bien.
                e1.printStackTrace();
                System.out.println("Erreur lors du rollback, fin de l'application.");
            }
        }
    }
    
}
