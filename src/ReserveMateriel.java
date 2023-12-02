import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReserveMateriel {
    
    private ReserveMateriel() {}

    // Fonction pour demander à l'utilisateur le nombre de pièce qu'il veut réserver.
    public static int askNbPiece(int nbRestants) {
        if (nbRestants <= 0) {
            System.out.println("Vous ne pouvez pas réserver ce matériel, il n'y a plus de pièces disponibles.");
            return 0;
        }
        while (true) {
            System.out.println("Il reste " + nbRestants + " pièce(s), combien en voulez-vous ?");
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);

                if (choice < 0) {
                    throw new NumberFormatException();
                }

                if (choice > nbRestants) {
                    throw new ArrayIndexOutOfBoundsException();
                }

                return choice;

            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");

            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Il n'en reste pas assez.");
            }
        }
    }

    // Fonction pour demander la date de début de réservation.
    public static Date askDateDebut() {
        while (true) {
            System.out.println("Début réservation (Année) ?");
            System.out.println("Début réservation (Mois) ?");
            System.out.println("Début réservation (Jour) ?");
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choiceYear = Integer.parseInt(choiceStr);

                choiceStr = Utils.scanner.nextLine();
                int choiceMonth = Integer.parseInt(choiceStr);

                choiceStr = Utils.scanner.nextLine();
                int choiceDay = Integer.parseInt(choiceStr);

                Date date = new Date(choiceYear - 1900, choiceMonth - 1, choiceDay);

                return date;

            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");

            }
        }
    }

    // Fonction pour demander la date de fin de réservation.
    public static Date askDateFin(Date dateDeb) {
        while (true) {
            System.out.println("Fin réservation (Année) ?");
            System.out.println("Fin réservation (Mois) ?");
            System.out.println("Fin réservation (Jour) ?");
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choiceYear = Integer.parseInt(choiceStr);

                choiceStr = Utils.scanner.nextLine();
                int choiceMonth = Integer.parseInt(choiceStr);

                choiceStr = Utils.scanner.nextLine();
                int choiceDay = Integer.parseInt(choiceStr);

                Date dateEnd = new Date(choiceYear - 1900, choiceMonth - 1, choiceDay);

                if (dateEnd.compareTo(dateDeb) > 0) {
                    if ((dateEnd.getTime() - dateDeb.getTime()) > Utils.getTwoWeekMs()) {
                        System.out.println("Vous ne pouvez pas réserver le matériel pour plus de 2 semaines.");
                        continue;
                    } else {
                        return dateEnd;
                    }
                } else {
                    System.out.println("La fin doit être plus grande que le début.");
                    continue;
                }

            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");
            }
        }
    }

    // Fonction pour afficher le menu de réservation d'un LotMatériel.
    public static void reserverMateriel(String categoriePere, String activite) throws SQLException {
        System.out.println("De quand à quand voulez-vous réserver ?");
        Date dateDeb = askDateDebut();
        Date dateEnd = askDateFin(dateDeb);

        System.out.println("0 - Retour au parcours des services.");

        PreparedStatement stmtMat;
        ResultSet rsMat;
        if (activite == null) {
            // Si l'utilisateur a choisi un tri par catégorie.
            // Requête pour avoir les matériels correspondant à la catégorie choisie.
            stmtMat = Main.connection.prepareStatement(
                "SELECT * " +
                "FROM LotMateriel NATURAL JOIN LotMaterielInfo " +
                "WHERE categorie=?",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            stmtMat.setString(1, categoriePere);
        } else {
            // Si l'utilisateur a choisi un tri par activité.
            // Requête pour avoir les matériels correspondant à l'activité choisie.
            stmtMat = Main.connection.prepareStatement(
                "SELECT * " +
                "FROM LotMaterielPourActivites NATURAL JOIN LotMateriel NATURAL JOIN LotMaterielInfo " +
                "WHERE nomActivite=?",
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            stmtMat.setString(1, activite);
        }
        rsMat = stmtMat.executeQuery();
        ArrayList<String> marques = new ArrayList<>();
        ArrayList<String> modeles = new ArrayList<>();
        ArrayList<Integer> anneeAchats = new ArrayList<>();
        ArrayList<Integer> nbRestants = new ArrayList<>();
        int start = 1;
        int cpt = start;
        // Affichage des matériels correspondants aux critères.
        while (rsMat.next()) {
            String marque = rsMat.getString("marque");
            marques.add(marque);
            String modele = rsMat.getString("modele");
            modeles.add(modele);
            int anneeAchat = rsMat.getInt("anneeAchat");
            anneeAchats.add(anneeAchat);
            int nbMatRestant = ReserveMateriel.getNbMaterielRestant(marque, 
                modele, anneeAchat, dateDeb, dateEnd);
            nbRestants.add(nbMatRestant);
            System.out.println(cpt + " - " + marque + ", " + modele + ", nbPiece=" +
                rsMat.getInt("nbPiece") + ", nbRestants=" + nbMatRestant);
            cpt++;
        }

        while (true) {
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);

                if (choice < 0 || choice >= cpt) {
                    throw new NumberFormatException();
                }

                if (choice == 0) {
                    ServiceMenu.displayMenu();
                    return;
                }

                int j = choice - start;

                rsMat.first();
                int i = start;
                while (i < choice) {
                    rsMat.next();
                    i++;
                };

                // On affiche la fiche complète du matériel selectionné.
                ArrayList<String> activites = getActivitesFromMateriel(rsMat.getString("marque"), rsMat.getString("modele"), rsMat.getInt("anneeAchat"));
                System.out.println("Voici les détails du matériel :");
                System.out.println("marque=" + rsMat.getString("marque") + ", modele=" + rsMat.getString("modele") + ", anneeAchat=" +
                    + rsMat.getInt("anneeAchat") + ", nbPiecesTotal=" + rsMat.getString("nbPiece") + ", nbRestants=" + nbRestants.get(j) + ", prixAccident="
                    + rsMat.getInt("prixAccident") + ", categorie=" + rsMat.getString("categorie") + ", activités=" + activites
                    + "\ndescription=" + rsMat.getString("infoMat"));

                if (Main.idAdherent == null) {
                    // Si ce n'est pas un Adhérent on ne lui propose pas de réserver.
                    System.out.println("Il faut être adhérent pour réserver un matériel.");
                    continue;
                }

                // Requête pour déterminer l'id de la réservation.
                PreparedStatement stmtMaxId = Main.connection.prepareStatement(
                    "SELECT MAX(idLM) as maxIdLM " +
                    "FROM LocationMateriel ");
                ResultSet rsMaxId = stmtMaxId.executeQuery();
                rsMaxId.next();
                int maxId = rsMaxId.getInt("maxIdLM");

                int choixNbMat = askNbPiece(nbRestants.get(j));
                if (choixNbMat == 0) {
                    // Si il choisi de réserver 0 matériel, on ne fait pas la réservation.
                    ServiceMenu.displayMenu();
                    return;
                }

                // Insertion de la réservation dans la table.
                PreparedStatement stmtReserv = Main.connection.prepareStatement(
                    "INSERT INTO LocationMateriel " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                stmtReserv.setInt(1, maxId + 1); // idLM
                stmtReserv.setInt(2, choixNbMat); // nbPièce
                stmtReserv.setDate(3, dateDeb); // dateRécup
                stmtReserv.setDate(4, dateEnd); // dateRetour
                stmtReserv.setInt(5, Main.idAdherent); // idAdherent
                stmtReserv.setInt(6, Main.idUser); // idUser
                stmtReserv.setString(7, marques.get(j)); // marque
                stmtReserv.setString(8, modeles.get(j)); // modèle
                stmtReserv.setInt(9, anneeAchats.get(j)); // annéeAchat

                stmtReserv.executeUpdate();
                Main.connection.commit();
                
                System.out.println("Réservation bien faite.");

                PrincipalMenu.displayMenu();

                break;
            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");
            }
        }

    }

    // Fonction pour avoir le nombre de matériel total pour un LotMatériel.
    public static int getNbMat(String marque, String modele, int anneeAchat) throws SQLException {
        PreparedStatement stmt = Main.connection.prepareStatement("SELECT nbPiece " +
            "FROM LotMateriel " +
            "WHERE marque=? AND modele=? AND anneeAchat=?");
        stmt.setString(1, marque);
        stmt.setString(2, modele);
        stmt.setInt(3, anneeAchat);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("nbPiece");
        }
        return 0;
    }

    // Fonction pour avoir le nombre de matériel restant pour un LotMatériel aux dates voulues par l'utilisateur.
    public static int getNbMaterielRestant(String marque, String modele, int anneeAchat,
            Date dateDeb, Date dateEnd) throws SQLException {
        int nbMat = getNbMat(marque, modele, anneeAchat);
        PreparedStatement stmt = Main.connection.prepareStatement("SELECT SUM(nbPieceR) AS nbMatReserve " +
            "FROM LocationMateriel " +
            "WHERE marque=? AND modele=? AND anneeAchat=? AND ((dateRecup<=? AND dateRetour>=?) OR (dateRecup>=? AND dateRecup<=?))");
        stmt.setString(1, marque);
        stmt.setString(2, modele);
        stmt.setInt(3, anneeAchat);
        stmt.setDate(4, dateDeb);
        stmt.setDate(5, dateEnd);
        stmt.setDate(6, dateDeb);
        stmt.setDate(7, dateEnd);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return nbMat - rs.getInt("nbMatReserve");
        }
        return 0;
    }

    // Fonction pour avoir les activités associées à un LotMateriel.
    public static ArrayList<String> getActivitesFromMateriel(String marque, String modele, int anneeAchat) throws SQLException {
        PreparedStatement stmt = Main.connection.prepareStatement(
            "SELECT nomActivite " +
            "FROM LotMaterielPourActivites " +
            "WHERE marque=? AND modele=? AND anneeAchat=?"
        );
        stmt.setString(1, marque);
        stmt.setString(2, modele);
        stmt.setInt(3, anneeAchat);

        ResultSet rs = stmt.executeQuery();

        ArrayList<String> activites = new ArrayList<>();
        while (rs.next()) {
            activites.add(rs.getString("nomActivite"));
        }
        return activites;
    }

}
