import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReserveRefuge {

    private ReserveRefuge() {}

    // On demande pour combien de jour l'utilisateur veut réserver le refuge.
    public static int askNbJour() {
        while (true) {
            System.out.println("Pour combien de jour ?");
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);

                if (choice <= 0) {
                    throw new NumberFormatException();
                }

                return choice;

            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");
            }
        }
    }

    // On demande l'heure à laquelle il arrive.
    public static int askHeureArrive() {
        while (true) {
            System.out.println("A quelle heure ?");
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);

                if (choice <= 0 || choice >= 24) {
                    throw new NumberFormatException();
                }

                return choice;

            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide.");
            }
        }
    }

    // Fonction pour afficher le menu de réservation d'un Refuge.
    public static void reserverRefuge(String emailRef) throws SQLException {
        // Requête pour avoir le refuge que l'utilisateur veut réserver.
        PreparedStatement stmtRef = Main.connection.prepareStatement(
            "SELECT * " +
            "FROM Refuge " +
            "WHERE emailRef=?"
        );
        stmtRef.setString(1, emailRef);
        ResultSet rsRef = stmtRef.executeQuery();
        rsRef.next();

        Date dateDebRef = rsRef.getDate("dateStart");
        Date dateEndRef = rsRef.getDate("dateEnd");

        int prixDej = getPrixRepas(emailRef, "Dejeuner");
        int prixDiner = getPrixRepas(emailRef, "Diner");
        int prixSouper = getPrixRepas(emailRef, "Souper");
        int prixCC = getPrixRepas(emailRef, "CasseCroute");

        // Affichage de la fiche complète du Refuge.
        System.out.println("Voici les détails du refuge :");
        System.out.println(rsRef.getString("nomRef") + ", " + rsRef.getString("sectGeo") + ", placesRepas=" + 
            rsRef.getString("nbPlaceRepas") + ", placesDormir=" + rsRef.getString("nbPlaceDormir") +
            ", dateDébut=" + dateDebRef + ", dateFin=" + dateEndRef + ", prixDej=" + prixDej + ", prixDiner=" + prixDiner +
            ", prixSouper=" + prixSouper + ", prixCasseCroute=" + prixCC + ", téléphone=" + getNumTel(emailRef) +
            ", typesPaiement=" + getTypePaiement(emailRef));
        System.out.println("Présentation : " + rsRef.getString("txtPres"));

        // Affichage de quand à quand on peut réserver ce Refuge.
        System.out.println("Ce refuge est " +
                "réservable du " + dateDebRef + " au " + dateEndRef + ".");

        // On redemande si l'utilisateur veut bien réserver ce Refuge après avoir vu les détails du Refuge.
        boolean reserve = ServiceMenu.askYesNo("Voulez-vous réserver ce refuge ?");
        if (!reserve) {
            ServiceMenu.displayMenu();
            return;
        }


        // Demande à l'utilisateur de quand à quand il veut réserver.
        Date dateDeb, dateEnd;
        int nbJour = -1;
        do {
            dateDeb = ReserveMateriel.askDateDebut();
            nbJour = askNbJour();
            dateEnd = new Date(dateDeb.getYear(), dateDeb.getMonth(), dateDeb.getDay() + nbJour);

            if (nbJour != -1 && !(dateDeb.compareTo(dateDebRef) >= 0 && dateEnd.compareTo(dateEndRef) <= 0)) {
                System.out.println("Attention votre date de réservation n'est pas valide, ce refuge est " +
                    "réservable que du " + dateDebRef + " au " + dateEndRef + ".");
            }
        } while (!(dateDeb.compareTo(dateDebRef) >= 0 && dateEnd.compareTo(dateEndRef) <= 0));

        // Demande l'heure d'arriver.
        int heureArrive = askHeureArrive();

        // id de la réservation.
        int idRR = getIdReservation();

        // On regarde s'il reste des places pour dormir aux dates que l'utilisateur réserve.
        int placeRestantDodo = getNbRestantDormir(emailRef, dateDeb, nbJour);
        if (placeRestantDodo <= 0) {
            System.out.println("Il ne reste plus de place pour dormir, vous ne pouvez pas réserver.");
            PrincipalMenu.displayMenu();
            return;
        } else {
            System.out.println("Il reste " + placeRestantDodo + " places pour dormir.");
        }

        // Nombre de place qu'il reste pour manger.
        int placeRestantManger = getNbRestantManger(emailRef, dateDeb, nbJour);

        int prixDodo = getPrixDodo(emailRef);

        int cptRepas = 0;
        int prixTotal = nbJour * prixDodo;
        ArrayList<String> rDejs = new ArrayList<>();
        ArrayList<String> rDiners = new ArrayList<>();
        ArrayList<String> rSoupers = new ArrayList<>();
        ArrayList<String> rCCs = new ArrayList<>();

        // Pour chaque jour, on lui demande s'il veut manger, le déjeuner, dîner, souper, casse-croûte?
        for (int i = 0; i < nbJour; i++) {
            System.out.println("Pour le jour " + (i + 1) + ", il reste " +
                placeRestantManger + " places pour manger");
            boolean rDej = false;
            boolean rDiner = false;
            boolean rSoup = false;
            boolean rCC = false;
            if (placeRestantManger > 0) {
                rDej = ServiceMenu.askYesNo("Voulez-vous réserver le déjeuner ?");
                if (rDej) {
                    placeRestantManger--;
                    cptRepas++;
                    prixTotal += prixDej;
                }
                if (placeRestantManger > 0) {
                    rDiner = ServiceMenu.askYesNo("Voulez-vous réserver le dîner ?");
                    if (rDiner) {
                        placeRestantManger--;
                        cptRepas++;
                        prixTotal += prixDiner;
                    }
                    if (placeRestantManger > 0) {
                        rSoup = ServiceMenu.askYesNo("Voulez-vous réserver le souper ?");
                        if (rSoup) {
                            placeRestantManger--;
                            cptRepas++;
                            prixTotal += prixSouper;
                        }
                        if (placeRestantManger > 0) {
                            rCC = ServiceMenu.askYesNo("Voulez-vous réserver le casse-croûte ?");
                            if (rCC) {
                                placeRestantManger--;
                                cptRepas++;
                                prixTotal += prixCC;
                            }
                        }
                    }
                }
            }
            rDejs.add(rDej ? "oui" : "non");
            rDiners.add(rDiner ? "oui" : "non");
            rSoupers.add(rSoup ? "oui" : "non");
            rCCs.add(rCC ? "oui" : "non");
        }

        // Insertion de la réservation.
        PreparedStatement stmt = Main.connection.prepareStatement(
            "INSERT INTO ReservationRefuge " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        stmt.setInt(1, idRR);
        stmt.setDate(2, dateDeb);
        stmt.setInt(3, heureArrive);
        stmt.setInt(4, nbJour);
        stmt.setInt(5, cptRepas);
        stmt.setInt(6, prixTotal);
        stmt.setInt(7, Main.idUser);
        stmt.setString(8, emailRef);

        stmt.executeUpdate();
        Main.connection.commit();

        // Insertion des repas réservé pour chaque jour.
        for (int i = 0; i < rDejs.size(); i++) {
            PreparedStatement stmtRepas = Main.connection.prepareStatement(
                "INSERT INTO RepasReserve " +
                "VALUES (?, ?, ?, ?, ?, ?)");
            stmtRepas.setInt(1, idRR);
            stmtRepas.setDate(2, new Date(dateDeb.getYear(), dateDeb.getMonth(), dateDeb.getDay() + i + 1));
            stmtRepas.setString(3, rDejs.get(i));
            stmtRepas.setString(4, rDiners.get(i));
            stmtRepas.setString(5, rSoupers.get(i));
            stmtRepas.setString(6, rCCs.get(i));

            stmtRepas.executeUpdate();
            Main.connection.commit();
        }

        // Requête pour voir s'il a déjà une ligne SommeDueUser pour les réservations de refuge.
        PreparedStatement stmtCheckSommeDue = Main.connection.prepareStatement(
            "SELECT * " +
            "FROM SommeDueUser " +
            "WHERE idUser=? AND typeSO='coûtsREF'"
        );
        stmtCheckSommeDue.setInt(1, Main.idUser);
        ResultSet rs = stmtCheckSommeDue.executeQuery();
        if (!rs.next()) {
            // Si il n'a pas déjà cette ligne, on la créé, et on y met le prix de cette réservation.
            PreparedStatement stmtSommeDue = Main.connection.prepareStatement(
                "INSERT INTO SommeDueUser " +
                "VALUES (?, ?, ?)"
            );
            stmtSommeDue.setInt(1, Main.idUser);
            stmtSommeDue.setString(2, "coûtsREF");
            stmtSommeDue.setInt(3, prixTotal);
            
            stmtSommeDue.executeUpdate();
            Main.connection.commit();
        } else {
            // Si la ligne existe déjà, on y ajoute le prix de cette réservation.
            PreparedStatement stmtSommeDue = Main.connection.prepareStatement(
                "UPDATE SommeDueUser " +
                "SET prix=prix+? " +
                "WHERE idUser=? AND typeSO='coûtsREF'"
            );
            stmtSommeDue.setInt(1, prixTotal);
            stmtSommeDue.setInt(2, Main.idUser);
            
            stmtSommeDue.executeUpdate();
            Main.connection.commit();
        }

        System.out.println("La réservation du refuge a bien été prise en compte.");
    
        PrincipalMenu.displayMenu();
    }

    // Fonction pour avoir le nombre de place restant pour dormir dans un Refuge aux dates spécifiées par l'utilisateur.
    public static int getNbRestantDormir(String emailRef, Date dateDeb, 
            int nbJour) throws SQLException {
        PreparedStatement stmtDodo = Main.connection.prepareStatement(
            "SELECT *" +
            " FROM ReservationRefuge" +
            " WHERE emailRef=?" 
        );
        stmtDodo.setString(1, emailRef);

        ResultSet rsDodo = stmtDodo.executeQuery();

        int nbPlacePriseDodo = 0;
        while (rsDodo.next()) {
            Date dateDebReservFaite = rsDodo.getDate("dateRR");
            int nbNuitsR = rsDodo.getInt("nbNuits");
            Date dateEndReservFaite = new Date(dateDebReservFaite.getYear(), dateDebReservFaite.getMonth(), dateDebReservFaite.getDay() + nbNuitsR);
            Date dateEnd = new Date(dateDeb.getYear(), dateDeb.getMonth(), dateDeb.getDay() + nbJour);
            if ((dateDebReservFaite.compareTo(dateDeb) <= 0 && dateEndReservFaite.compareTo(dateEnd) >= 0) ||
                    dateDebReservFaite.compareTo(dateDeb) >= 0 && dateDebReservFaite.compareTo(dateEnd) <= 0) {
                if (nbNuitsR > 0) nbPlacePriseDodo++;
            }
        }

        ResultSet rs = Main.connection.createStatement().executeQuery(
            "SELECT nbPlaceDormir " +
            "FROM Refuge " +
            "WHERE emailRef='" + emailRef + "'"
        );
        rs.next();
        return rs.getInt("nbPlaceDormir") - nbPlacePriseDodo;
    }

    // Fonction pour avoir le nombre de place restant pour manger dans un Refuge aux dates spécifiées par l'utilisateur.
    public static int getNbRestantManger(String emailRef, Date dateDeb,
            int nbJour) throws SQLException {
        PreparedStatement stmtManger = Main.connection.prepareStatement(
            "SELECT *" +
            " FROM ReservationRefuge" +
            " WHERE emailRef=?"
        );
        stmtManger.setString(1, emailRef);
        
        ResultSet rsManger = stmtManger.executeQuery();

        int nbPlacePriseManger = 0;
        while (rsManger.next()) {
            Date dateDebReservFaite = rsManger.getDate("dateRR");
            int nbNuitsR = rsManger.getInt("nbNuits");
            Date dateEndReservFaite = new Date(dateDebReservFaite.getYear(), dateDebReservFaite.getMonth(), dateDebReservFaite.getDay() + nbNuitsR);
            Date dateEnd = new Date(dateDeb.getYear(), dateDeb.getMonth(), dateDeb.getDay() + nbJour);
            if ((dateDebReservFaite.compareTo(dateDeb) <= 0 && dateEndReservFaite.compareTo(dateEnd) >= 0) ||
                    (dateDebReservFaite.compareTo(dateDeb) >= 0 && dateDebReservFaite.compareTo(dateEnd) <= 0)) {
                nbPlacePriseManger += rsManger.getInt("nbRepas");
            }
        }

        ResultSet rs = Main.connection.createStatement().executeQuery(
            "SELECT nbPlaceRepas " +
            "FROM Refuge " +
            "WHERE emailRef='" + emailRef + "'"
        );
        rs.next();
        return rs.getInt("nbPlaceRepas") - nbPlacePriseManger;
    }

    // Fonction pour déterminer l'id de la réservation.
    public static int getIdReservation() throws SQLException {
        PreparedStatement stmt = Main.connection.prepareStatement(
            "SELECT MAX(idRR) AS maxIdRR " +
            "FROM ReservationRefuge");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt("maxIdRR") + 1;
    }

    // Fonction pour déterminer le prix d'une nuit dans une Refuge.
    public static int getPrixDodo(String emailRef) throws SQLException {
        ResultSet rs = Main.connection.createStatement().executeQuery(
            "SELECT prixNuit " +
            "FROM Refuge " +
            "WHERE emailRef='" + emailRef + "'"
        );
        rs.next();
        return rs.getInt("prixNuit");
    }

    // Fonction pour déterminer le prix d'un type de repas dans un Refuge.
    public static int getPrixRepas(String emailRef, String type) throws SQLException {
        ResultSet rs = Main.connection.createStatement().executeQuery(
            "SELECT prix" + type + " " +
            "FROM RefugeAPrix" + type + " " +
            "WHERE emailRef='" + emailRef + "'"
        );
        rs.next();
        return rs.getInt("prix" + type);
    }

    // Fonction pour avoir le numéro de téléphone d'un Refuge (s'il en a un).
    public static String getNumTel(String emailRef) throws SQLException {
        PreparedStatement stmt = Main.connection.prepareStatement(
            "SELECT numTel " +
            "FROM Telephone " +
            "WHERE emailRef=?"
        );
        stmt.setString(1, emailRef);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getString("numTel");
        }
        return "XX";
    }

    // Fonction pour déterminer les types de paiement possible d'un Refuge.
    public static ArrayList<String> getTypePaiement(String emailRef) throws SQLException {
        PreparedStatement stmt = Main.connection.prepareStatement(
            "SELECT typePaiement " +
            "FROM RefugeATypePaiement " +
            "WHERE emailRef=?"
        );
        stmt.setString(1, emailRef);
        ResultSet rs = stmt.executeQuery();

        ArrayList<String> types = new ArrayList<>();
        while (rs.next()) {
            types.add(rs.getString("typePaiement"));
        }
        return types;
    }

}