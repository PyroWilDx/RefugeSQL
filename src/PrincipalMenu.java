import java.sql.SQLException;

public class PrincipalMenu {
    
    private PrincipalMenu() {}

    // Affichage du menu principal
    public static void displayMenu() throws SQLException {
        while (true) {
            System.out.println("1 - Parcours des services.");
            System.out.println("2 - Retourner un matériel.");
            System.out.println("3 - Payer tout ce que vous devez.");
            System.out.println("4 - Droit à l'oubli.");
            System.out.println("5 - Quitter l'application.");

            String choiceStr = Utils.scanner.nextLine();
            try {
                int choice = Integer.parseInt(choiceStr);
                switch (choice) {
                    case 1:
                        ServiceMenu.displayMenu();
                        break;
                    case 2:
                        RetourMaterielMenu.showARetourner();
                        break;
                    case 3:
                        PaiementMenu.showAPayer();
                        break;
                    case 4:
                        DroitOubliMenu.displayMenu();
                        break;
                    case 5:
                        break;

                    default:
                        throw new NumberFormatException();
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un numéro entre 1 et 5.");
            }
        }
    }

}
