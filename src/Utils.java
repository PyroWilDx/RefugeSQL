import java.util.Scanner;

public class Utils {
    
    public static final Scanner scanner = new Scanner(System.in);

    private Utils() {}

    // Fonction pour avoir le nombre de milli-secondes dans 2 semaines.
    // Utilisée pour la réservation d'un matériel car on ne peut pas réserver plus de 2 semaines.
    public static long getTwoWeekMs() {
        return 14 * 24 * 60 * 60 * 1000;
    }

}
