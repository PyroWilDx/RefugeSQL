import java.sql.ResultSet;
import java.sql.SQLException;

public class DroitOubliMenu {
    
    private DroitOubliMenu() {}

    // Affichage du menu de droit à l'oubli.
    public static void displayMenu() throws SQLException {
        while (true) {
            System.out.println("0 - Retour au menu principal.");
            System.out.println("1 - Supprimer toutes vos données.");

            String choiceStr = Utils.scanner.nextLine();
            try {
                int choice = Integer.parseInt(choiceStr);
                switch (choice) {
                    case 0:
                        PrincipalMenu.displayMenu();
                        break;
                    case 1:
                        DroitOubliMenu.deleteAllUserData();
                        break;

                    default:
                        throw new NumberFormatException();
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro entre 0 et ??.");
            }
        }
    }

    // Fonction pour supprimer toutes les données d'un utilisateur (si il n'a rien à payer).
    public static void deleteAllUserData() throws SQLException {
        // Requête pour avoir les potentielles sommes que l'utilisateur doit payer.
        // Si il doit encore payer des sommes, il ne peut pas supprimer son compte. Sinon, il peut le supprimer.
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
        
        if (somme <= 0) {
            // On met les idUser à -1 comme expliqué dans le rapport.
            // Pour les RéservationRefuge.
            Main.connection.createStatement().executeUpdate(
                "UPDATE ReservationRefuge " +
                "SET idUser=-1 " +
                "WHERE idUser=" + Main.idUser
            );
            Main.connection.commit();

            // Pour les RéservationFormation.
            Main.connection.createStatement().executeUpdate(
                "UPDATE ReservationFormation " +
                "SET idUser=-1, idAdherent=-1 " +
                "WHERE idUser=" + Main.idUser + " AND idAdherent=" + Main.idAdherent
            );
            Main.connection.commit();

            // Pour les LocationMatériel.
            Main.connection.createStatement().executeUpdate(
                "UPDATE LocationMateriel " +
                "SET idUser=-1, idAdherent=-1 " +
                "WHERE idUser=" + Main.idUser + " AND idAdherent=" + Main.idAdherent
            );
            Main.connection.commit();

            // Comme l'utilisateur n'a plus rien à payer, on peut supprimer ses SommeDueUser.
            Main.connection.createStatement().executeUpdate(
                "DELETE FROM SommeDueUser " +
                "WHERE idUser=" + Main.idUser
            );
            Main.connection.commit();

            // Suppression de la ligne Adhérent correspondante.
            if (Main.idAdherent != null) {
                Main.connection.createStatement().executeUpdate(
                    "DELETE FROM Adherent " +
                    "WHERE idUser=" + Main.idUser + " AND idAdherent=" + Main.idAdherent
                );
                Main.connection.commit();
            }

            // Suppression de la ligne Membre correspondante.
            Main.connection.createStatement().executeUpdate(
                "DELETE FROM Membre " +
                "WHERE idUser=" + Main.idUser
            );
            Main.connection.commit();

            System.out.println("Compte bien supprimé.");

            if (somme < 0) {
                // On doit lui rembourser des sommes.
                System.out.println("Il vous restait " + -somme + 
                    " euros sur votre compte. Nous allons vous envoyer un mail de confirmaton pour le remboursement.");
            }

        } else {
            // L'utilisateur doit encore payer des sommes.
            System.out.println("Vous ne pouvez pas supprimer votre compte, vous devez payer encore " + somme + " euros.");
            PrincipalMenu.displayMenu();
        }

    }

}
