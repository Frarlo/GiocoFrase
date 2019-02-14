/**
 * @author Francesco Ferlin
 * @version 4.0
 */
package me.ferlin;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Classe dell'entry point che contiene le istruzioni principali del programma.
 * 
 * Il programma è il seguente:
 *      Programma con 5 thread che cercano nella stessa frase memorizzata 
 *      come costante di classe una delle 5 vocali visualizzando la vocale 
 *      ogni volta che viene trovata. Ogni thread cerca una e una sola vocale.
 * 
 *      Ogni thread accetta due possibili opzioni:
 *      - Random Delay: se, dopo il controllo di ogni lettera, utilizzare
 *                      un delay randomico;
 *      - Yield: se, dopo il controllo di ogni lettera, 
 *               richiamare {@link Thread#yield()};
 * 
 *     L'utente, dopo aver digitato la frase, ha 10 secondi per indovinare
 *     la vocale presente più volte.
 */
public final class Main {
    private static final char[] VOCALI = new char[] {'a', 'e', 'i', 'o', 'u'};
    
    /**
     * @brief Costruttore privato per prevenire l'istanziamento della classe
     */
    private Main() {}
    
    
    /**
     * @brief Entry point, esegue la logica principale del programma
     * 
     * Programma con 5 thread che cercano nella stessa frase memorizzata 
     * come costante di classe una delle 5 vocali visualizzando la vocale 
     * ogni volta che viene trovata. Ogni thread cerca una e una sola vocale.
     * 
     * Ogni thread accetta due possibili opzioni:
     * - Random Delay: se, dopo il controllo di ogni lettera, utilizzare
     *                 un delay randomico;
     * - Yield: se, dopo il controllo di ogni lettera, 
     *          richiamare {@link Thread#yield()};
     * 
     * L'utente, dopo aver digitato la frase, ha 10 secondi per indovinare
     * la vocale presente più volte.
     * 
     * @param args parametri passati da command line
     */
    public static void main(String[] args) {
        final Scanner sc = new Scanner(System.in);
        final AsyncScanner asyncSc = new AsyncScanner(System.in);
        
        boolean running = true;
        
        DatiCondivisi datiCondivisi;
        Schermo schermo;
        
        // Crea i runnables
        
        final CercaVocaleRunnable[] runnablesVocali = 
                new CercaVocaleRunnable[VOCALI.length];
        
        for(int i = 0; i < runnablesVocali.length; i++) {
            final char vocale = VOCALI[i];
            
            runnablesVocali[i] = new CercaVocaleRunnable(vocale);
        }
        
        while(running) {

            System.out.print("Vuoi utilizzare il delay randomico? [Y/N] ");
            boolean delay = (sc.nextLine().toLowerCase().equals("y"));
            System.out.print("Vuoi utilizzare lo yield? [Y/N] ");
            boolean yield = (sc.nextLine().toLowerCase().equals("y"));
            
            System.out.print("Inserisci la frase in cui cercare: ");
            final String daAnalizzare = sc.nextLine();
            
            try {
                System.out.print("Hai 10 secondi per trovare la vocale più usata: ");
                        
                final char letteraScelta = asyncSc
                        .nextLine()
                        .get(10, TimeUnit.SECONDS)
                        .charAt(0);

                // Fai partire i Thread
                System.out.println(String.format(
                        "Cercando vocali nella frase \'%s\' (delay: %s, yield: %s)...",
                        daAnalizzare, delay, yield
                ));

                schermo = new Schermo();
                datiCondivisi = new DatiCondivisi(VOCALI, schermo);

                final Thread[] runningThreads = new Thread[runnablesVocali.length];
                for (int i = 0; i < runnablesVocali.length; i++) {
                    final CercaVocaleRunnable runn = runnablesVocali[i];

                    runn.setDaAnalizzare(daAnalizzare);
                    runn.setPtrDati(datiCondivisi);
                    runn.setUsaDelay(delay);
                    runn.setUsaYield(yield);

                    runningThreads[i] = new Thread(runn, "Thread_" + runn.getVocaleDaTrovare());
                    runningThreads[i].start();
                }

                while(!datiCondivisi.isAllOver()) {
                    
                    schermo.pulisciSchermo();
                    
                    schermo.getSemaphore().acquireUninterruptibly();
                    for(String s : schermo.getMessages())
                        System.out.println(s);
                    schermo.getSemaphore().release();
                }
                
                final char mostUsed = datiCondivisi.getPiuUsata();
                if(mostUsed == letteraScelta)
                    System.out.println("Hai indovinato!");
                else
                    System.out.println("Hai perso :C");
                System.out.println("La vocale era " + mostUsed);
            
            } catch(ExecutionException | InterruptedException ex) {
                System.out.println();
                System.out.println("Errore durante la lettura dell'input");
                ex.printStackTrace();
                
            } catch(TimeoutException tookTooLong) {
                System.out.println();
                System.out.println("Ci hai impiegato troppo!");
                
            }

            System.out.print("Vuoi riprovare? [Y/N] ");
            running = (sc.nextLine().toLowerCase().equals("y"));
        }
        
        System.out.println("Ci vediamo alla prossima");
    }
}
