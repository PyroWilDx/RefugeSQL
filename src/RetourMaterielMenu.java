import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RetourMaterielMenu {
    
    private RetourMaterielMenu() {}

    // Demande à l'utilisateur combien de pièce il a cassé.
    public static int askNbPieceCasse(int nbReserve) {
        while (true) {
            try {
            System.out.println("Combien de matériels avez-vous cassé/perdu ?");

            String choiceStr = Utils.scanner.nextLine();
            int choice = Integer.parseInt(choiceStr);
            
            if (choice < 0) {
                throw new NumberFormatException();
            }

            if (choice > nbReserve) {
                System.out.println("Vous ne pouvez pas avoir cassé plus de matériel que vous n'en avez reservé..");
                continue;
            }

            return choice;

            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");
            }
        }
    }

    // Fonction qui affiche les matériels que l'utilisateur doit encore retourner.
    public static void showARetourner() throws SQLException {
        if (Main.idAdherent == null) {
            System.out.print("Il faut être adhérent pour accéder à ce menu.");
            PrincipalMenu.displayMenu();
            return;
        }
        // Requête pour avoir les matériels que l'utilisateur doit retourner (c'est-à-dire ceux où nbPièceR > 0 dans LocationMatériel)
        PreparedStatement stmt = Main.connection.prepareStatement(
            "SELECT * " +
            "FROM LocationMateriel NATURAL JOIN LotMateriel " +
            "WHERE idUser=? AND idAdherent=? AND nbPieceR>0",
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        );
        stmt.setInt(1, Main.idUser);
        stmt.setInt(2, Main.idAdherent);

        ResultSet rs = stmt.executeQuery();

        System.out.println("0 - Retour au menu principal.");

        int start = 1;
        int cpt = start;
        // Affichage des matériels que l'utilisateur doit retourner.
        while (rs.next()) {
            System.out.println(cpt + " - " + "prixAccident=" + rs.getInt("prixAccident") + ", marque=" + rs.getString("marque") +
                ", modèle=" + rs.getString("modele") + ", nbPièceRéservé=" + rs.getInt("nbPieceR") +
                "\n    dateRécup=" + rs.getDate("dateRecup") + ", dateRetour=" + rs.getDate("dateRetour"));
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
                    PrincipalMenu.displayMenu();
                    return;
                }

                rs.first();
                int i = start;
                while (i < choice) {
                    rs.next();
                    i++;
                };

                int nbCasse = askNbPieceCasse(rs.getInt("nbPieceR"));

                // Prix à payer pour les matériels cassés.
                int prixPayer = nbCasse * rs.getInt("prixAccident");

                if (prixPayer > 0) {
                    // L'utilisateur doit payer.

                    // Requête pour voir s'il a déjà une ligne SommeDueUser pour les matériaux cassés.
                    PreparedStatement stmtCheckSommeDue = Main.connection.prepareStatement(
                        "SELECT * " +
                        "FROM SommeDueUser " +
                        "WHERE idUser=? AND typeSO='sommeAccident'"
                    );
                    stmtCheckSommeDue.setInt(1, Main.idUser);
                    ResultSet rsSD = stmtCheckSommeDue.executeQuery();
                    if (!rsSD.next()) {
                        // Si il n'a pas déjà cette ligne, on la créé, et on y met le prix.
                        PreparedStatement stmtSommeDue = Main.connection.prepareStatement(
                            "INSERT INTO SommeDueUser " +
                            "VALUES (?, ?, ?)"
                        );
                        stmtSommeDue.setInt(1, Main.idUser);
                        stmtSommeDue.setString(2, "sommeAccident");
                        stmtSommeDue.setInt(3, prixPayer);

                        stmtSommeDue.executeUpdate();
                        Main.connection.commit();
                    } else {
                        // Si la ligne existe déjà, on y ajoute le prix.
                        PreparedStatement stmtSommeDue = Main.connection.prepareStatement(
                            "UPDATE SommeDueUser " +
                            "SET prix=prix+? " +
                            "WHERE idUser=? AND typeSO='sommeAccident'"
                        );
                        stmtSommeDue.setInt(1, prixPayer);
                        stmtSommeDue.setInt(2, Main.idUser);
                        
                        stmtSommeDue.executeUpdate();
                        Main.connection.commit();
                    }

                    System.out.println("Pour les matériels cassés, vous devez payer " + prixPayer + " euros.");
                }

                if (nbCasse > 0) {
                    // Si l'utilisateur a cassé des matériels, on doit les enlever du LotMatériel correspondant.
                    
                    // On enlève le nombre de pièces que l'utilisateur a cassé dans LotMatériel.
                    PreparedStatement stmtLM = Main.connection.prepareStatement(
                        "UPDATE LotMateriel " +
                        "SET nbPiece=nbPiece-? " +
                        "WHERE marque=? AND modele=? AND anneeAchat=?"
                    );
                    stmtLM.setInt(1, nbCasse);
                    stmtLM.setString(2, rs.getString("marque"));
                    stmtLM.setString(3, rs.getString("modele"));
                    stmtLM.setInt(4, rs.getInt("anneeAchat"));
                    stmtLM.executeUpdate();
                    Main.connection.commit();

                    // On insère dans NbPieceAccidentLocMat le nombre de pièces cassés.
                    PreparedStatement stmtAcc = Main.connection.prepareStatement(
                        "INSERT INTO NbPieceAccidentLocMat " +
                        "VALUES (?, ?)"
                    );
                    stmtAcc.setInt(1, rs.getInt("idLM"));
                    stmtAcc.setInt(2, nbCasse);
                    stmtAcc.executeUpdate();
                    Main.connection.commit();
                }

                // Comme l'utilisateur a rendu les matériels, on met le nombre de pièces réservés nbPièceR de LocationMatériel à 0.
                PreparedStatement stmtLocM = Main.connection.prepareStatement(
                    "UPDATE LocationMateriel " +
                    "SET nbPieceR=0 " +
                    "WHERE idLM=?"
                );
                stmtLocM.setInt(1, rs.getInt("idLM"));
                stmtLocM.executeUpdate();
                Main.connection.commit();

                PrincipalMenu.displayMenu();

                break;
            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");
            }
        }
    }

}
