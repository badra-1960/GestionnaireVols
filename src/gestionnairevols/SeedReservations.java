package gestionnairevols;

import java.io.*;
import java.util.*;

public class SeedReservations {
    public static void main(String[] args) {
        List<Reservation> list = new ArrayList<>();
        list.add(new Reservation(1, "Test User", "Paris", "New York", "AF123"));
        list.add(new Reservation(2, "Alice", "Lyon", "Tokyo", "JL456"));

        File f = new File("reservations.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(new ArrayList<>(list));
            System.out.println("Wrote reservations.dat (" + f.getAbsolutePath() + ")");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // now read back
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                List<?> read = (List<?>) obj;
                System.out.println("Read " + read.size() + " reservations:");
                for (Object o : read) {
                    if (o instanceof Reservation) {
                        Reservation r = (Reservation) o;
                        System.out.println(r.getId() + ": " + r.getNomPassager() + " - " + r.getdepart() + " -> " + r.getDestination() + " (" + r.getNumeroVol() + ")");
                    } else {
                        System.out.println(" - unexpected item: " + o);
                    }
                }
            } else {
                System.out.println("File did not contain a List");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
