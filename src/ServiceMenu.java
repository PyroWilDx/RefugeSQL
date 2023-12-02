import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ServiceMenu {
    
    private ServiceMenu() {}

    // Affichage du menu de service.
    public static void displayMenu() throws SQLException {
        while (true) {
            System.out.println("0 - Retour au menu principal.");
            System.out.println("1 - Formations.");
            System.out.println("2 - Matériels disponibles à la location.");
            System.out.println("3 - Refuges.");

            String choiceStr = Utils.scanner.nextLine();
            try {
                int choice = Integer.parseInt(choiceStr);
                switch (choice) {
                    case 0:
                        PrincipalMenu.displayMenu();
                        break;
                    case 1:
                        ServiceMenu.parcoursFormation();
                        break;
                    case 2:
                        // Choix du tri de l'utilisateur (par catégorie ou activité).
                        boolean triCat = ServiceMenu.askParcoursMateriel();
                        if (triCat) {
                            ServiceMenu.parcoursMateriel("NULL");
                        } else {
                            String activite = ServiceMenu.selectActivite();
                            ServiceMenu.parcoursMaterielActivite(activite);
                        }
                        break;
                    case 3:
                        ServiceMenu.parcoursRefuges();
                        break;

                    default:
                        throw new NumberFormatException();
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro entre 0 et 3.");
            }
        }
    }

    // Menu du parcours des formations.
    public static void parcoursFormation() throws SQLException {
        System.out.println("0 - Retour en arrière.");

        Statement stmt = Main.connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        );
        // Requête où on affiche toutes les formations triées par dateStart, puis nom.
        ResultSet rs =  stmt.executeQuery("SELECT * " +
            "FROM Formation " +
            "ORDER BY dateStart, nom");
        int start = 1;
        int cpt = start;
        ArrayList<ArrayList<String>> activiteListList = new ArrayList<>();
        // Affichage des formations.
        while (rs.next()) {
            ArrayList<String> activites = ReserveFormationMenu.getActivitesFromFormation(rs.getInt("anneeForm"), rs.getInt("rangForm"));
            System.out.println(cpt + " - " + rs.getString("nom") + ", activités=" + activites + ", " +
                rs.getDate("dateStart") + ", duree=" + rs.getInt("duree") + ", nbPlace=" + rs.getInt("nbPlace"));
            cpt++;
            activiteListList.add(activites);
        }
        
        while (true) {
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);
                
                if (choice == 0) {
                    ServiceMenu.displayMenu();
                    break;
                }

                if (choice < start || choice >= cpt) {
                    throw new NumberFormatException();
                }

                rs.first();
                int i = start;
                while (i < choice) {
                    rs.next();
                    i++;
                };

                // On détermine le nombre de place restante et éventuellement le rang dans la liste d'attente.
                int placeRestante = ReserveFormationMenu.getPlaceRestantFormation(rs.getInt("anneeForm"), rs.getInt("rangForm"));
                int listeAttente = 0;
                if (placeRestante < 0) {
                    listeAttente = -placeRestante;
                    placeRestante = 0;
                }

                // Affichage de la fiche complète de la formation.
                System.out.println("Voici les détails de cette formation :\n" +
                    "année=" + rs.getInt("anneeForm") + ", rang=" + rs.getInt("rangForm") + ", " + rs.getString("nom") +
                    ", activités=" + activiteListList.get(i - start) + ", " + rs.getDate("dateStart") + ", duree=" + rs.getInt("duree") +
                    ",\nnbPlace=" + rs.getInt("nbPlace") + ", nbPlaceRestante=" + placeRestante + ", listeAttente=" + listeAttente +
                    "\ndescription=" + rs.getString("descFor"));

                if (Main.idAdherent != null) {
                    // On demande à l'Adhérent s'il veut bien réserver cette formation après avoir vu sa fiche complète.
                    boolean reserve = askYesNo("Voulez-vous réserver cette formation ?");

                    if (reserve) {
                        // Réservation de la formation.
                        int v = ReserveFormationMenu.reserverFormation(rs.getInt("anneeForm"),
                            rs.getInt("rangForm"));
                        if (v == 1) {
                            // Cas normal.
                            System.out.println("Formation bien réservée.");
                        } else if (v == 0) {
                            // Cas liste d'attente.
                            System.out.println("Il n'y a plus de place dans la formation, vous êtes placé en liste d'attente.");
                        } else {
                            // Cas liste d'attente remplie.
                            System.out.println("Il n'y a plus de place dans la formation, et la liste d'attente est remplie (déjà 20 personnes), vous ne pouvez pas vous inscrire, désolé.");
                        }
                    }
                } else {
                    // Si ce n'est pas un Adhérent, il n'a pas le droit de réserver.
                    System.out.println("Il faut être adhérent pour réserver.");
                }

                ServiceMenu.displayMenu();

                break;
            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide");
            }
        }
    }

    // Fonction pour demander comment l'utilisateur veut parcourir les matériels.
    public static boolean askParcoursMateriel() {
        return askYesNo("Voulez-vous afficher les matériels par catégorie ? (Oui pour trier par catégorie, et Non pour trier par activité)");
    }

    // Fonction pour parcourir l'arbre des catégories des matériels.
    public static void parcoursMateriel(String categoriePere) throws SQLException {
        System.out.println("0 - Retour en arrière.");
        System.out.println("1 - Afficher les matériels de cette catégorie pour réserver.");

        // On détermine et affiche le père.
        Statement stmt0 = Main.connection.createStatement();
        ResultSet rs0 = stmt0.executeQuery("SELECT * " +
            "FROM ArbreCategorie " +
            "WHERE categorieFils='" + categoriePere + "'");
        if (rs0.next()) {
            System.out.println("2 - " + rs0.getString("categoriePere") + " (Revenir au père).");
        }

        Statement stmt1 = Main.connection.createStatement(            
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        );
        // On détermine les fils.
        ResultSet rs1 =  stmt1.executeQuery("SELECT * " +
            "FROM ArbreCategorie " +
            "WHERE categoriePere='" + categoriePere + "'");
        int start = 3;
        int cpt = start;
        // Affichage des fils.
        while (rs1.next()) {
            System.out.println(cpt + " - " + rs1.getString("categorieFils") + ".");
            cpt++;
        }
        
        while (true) {
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);
                
                if (choice == 0) {
                    ServiceMenu.displayMenu();
                    break;
                }

                if (choice == 1) {
                    if (cpt == start) {
                        // Réservation d'un matériel.
                        ReserveMateriel.reserverMateriel(categoriePere, null);
                    } else {
                        // Il faut arriver à une feuille de l'arbre pour réserver un matériel.
                        System.out.println("Spécifiez encore la catégorie s.v.p.");
                        parcoursMateriel(categoriePere);
                    }
                    break;
                }

                if (choice == 2) {
                    // Retour au père.
                    ServiceMenu.parcoursMateriel(rs0.getString("categoriePere"));
                    break;
                }

                if (choice < start || choice >= cpt) {
                    throw new NumberFormatException();
                }

                rs1.first();
                int i = start;
                while (i < choice) {
                    rs1.next();
                    i++;
                };

                // Aller à un fils.
                ServiceMenu.parcoursMateriel(rs1.getString("categorieFils"));

                break;
            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide");
            }
        }
    }

    // Demande à l'utilisateur pour quel activité il veut afficher les matériels.
    public static String selectActivite() throws SQLException {
        System.out.println("Veuillez selectionner l'activité pour laquelle vous voulez avoir les matériels.");

        // Requête pour avoir toutes les activités associés à un matériel.
        ResultSet rs = Main.connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        ).executeQuery(
            "SELECT DISTINCT nomActivite " +
            "FROM LotMaterielPourActivites "
        );
        int start = 1;
        int cpt = start;
        // Affichage des activités.
        while (rs.next()) {
            System.out.println(cpt + " - " + rs.getString("nomActivite"));
            cpt++;
        }

        while (true) {
            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);

                if (choice < start || choice >= cpt) {
                    throw new NumberFormatException();
                }

                rs.first();
                int i = start;
                while (i < choice) {
                    rs.next();
                    i++;
                };

                // On retourne le choix de l'activé.
                return rs.getString("nomActivite");
            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide");
            }
        }
    }

    // Fonction pour parcourir les matériels quand on a choisi l'activité.
    public static void parcoursMaterielActivite(String activite) throws SQLException {
        ReserveMateriel.reserverMateriel(null, activite);
    }

    // Parcours des Refuges.
    public static void parcoursRefuges() throws SQLException {
        // Choix des tris, on peut choisir plusieurs tris, quand on a fini de choisir, il faut entrer 6.
        ArrayList<String> tris = new ArrayList<>();
        while (true) {
            System.out.println("0 - Retour en arrière.");
            System.out.println("1 - Tri par nom.");
            System.out.println("2 - Tri par date début.");
            System.out.println("3 - Tri par date fin.");
            System.out.println("4 - Tri par nombre de place repas.");
            System.out.println("5 - Tri par nombre de place pour dormir.");
            System.out.println("6 - Fin du choix des tris.");

            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);
                
                if (choice == 0) {
                    ServiceMenu.displayMenu();
                    break;
                }

                if (choice < 0 && choice > 6) {
                    throw new NumberFormatException();
                }

                String choixTri = "";
                if (choice == 1) {
                    choixTri = "nomRef";
                } else if (choice == 2) {
                    choixTri = "dateStart";
                } else if (choice == 3) {
                    choixTri = "dateEnd";
                } else if (choice == 4) {
                    choixTri = "nbPlaceRepas";
                } else if (choice == 5) {
                    choixTri = "nbPlaceDormir";
                } else if (choice == 6) {
                    break;
                }
                if (!tris.contains(choixTri)) {
                    tris.add(choixTri);
                } else {
                    System.out.println("Tri déjà choisi.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide");
            }
        }

        while (true) {
            System.out.println("0 - Retour en arrière.");
            Statement stmt = Main.connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            // Requête pour afficher les Refuge en triant par les tris choisis.
            String query = "SELECT * ";
            query += "FROM Refuge ";
            if (!tris.isEmpty()) {
                query += "ORDER BY ";
                query += tris.get(0);
                tris.remove(0);
            }
            for (String tri : tris) {
                query += ", " + tri;
            }
            ResultSet rs = stmt.executeQuery(query);
            int start = 1;
            int cpt = start;
            // Affichage des Refuges.
            while (rs.next()) {
                System.out.println(cpt + " - " + rs.getString("nomRef") + ", " + rs.getString("sectGeo") + ", placesRepas=" + 
                    rs.getString("nbPlaceRepas") + ", placesDormir=" + rs.getString("nbPlaceDormir") + ".");
                cpt++;
            }

            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);
                
                if (choice == 0) {
                    ServiceMenu.displayMenu();
                    break;
                }

                if (choice < 0 && choice >= cpt) {
                    throw new NumberFormatException();
                }

                rs.first();
                int i = start;
                while (i < choice) {
                    rs.next();
                    i++;
                };

                // Réservation du Refuge.
                ReserveRefuge.reserverRefuge(rs.getString("emailRef"));

                break;
            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide");
            }

        }
    }

    // Fonction qui affichage un message msg et demande Oui ou Non.
    public static boolean askYesNo(String msg) {
        while (true) {
            System.out.println(msg);
            System.out.println("0 - Non.");
            System.out.println("1 - Oui.");

            try {
                String choiceStr = Utils.scanner.nextLine();
                int choice = Integer.parseInt(choiceStr);
                if (choice == 0) {
                    return false;
                } else if (choice == 1) {
                    return true;
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.println("Numéro Invalide");
            }
        }
    }

}
