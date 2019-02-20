/**
 * @author Francesco Ferlin
 * @version 4.0
 */
package me.ferlin;

import java.util.Random;

/**
 * Classe runnable che trova in una stringa data la vocale richiesta
 */
public class CercaVocaleRunnable implements Runnable {
    
    // Constants
    
    /**
     * Delay massimo da usare nel {@link Thread#sleep(long)}
     */
    private static final int MAX_DELAY = 50;
    
    
    // Attributes
    
    /**
     * Vocale minuscola (in lower-case) tra quelle in {@link #VOCALI}
     * che questo runnable deve trovare nella frase {@link #daAnalizzare}.
     */
    private final char vocaleDaTrovare;
    
    /**
     * Stringa in cui questo runnable deve trovare 
     * la vocale {@link #vocaleDaTrovare}.
     * 
     * Quando viene chiamato il metodo {@link #run()},
     * non deve essere nulla.
     */
    private String daAnalizzare;
    /**
     * Indica se, dopo ogni lettera, il 
     * runnable deve aspettare un tempo random
     */
    private boolean usaDelay;
    /**
     * Indica se, dopo ogni lettera, il runnable 
     * deve usare {@link Thread#yield()} 
     */
    private boolean usaYield;
    /**
     * Puntatore all'oggetto contenente 
     * i dati condivisi tra i thread e il main
     */
    private DatiCondivisi ptrDati;
    
    /**
     * @brief Costruisce un nuovo runnable che cerca la vocale data. 
     * 
     * La vocale passata come parametro è case-insensitive e, per essere valida,
     * deve essere una tra quelle contenute in {@link #VOCALI}.
     * 
     * Se viene utilizzato questo costruttore è necessario utilizzare i setter
     * {@link #setDaAnalizzare(java.lang.String)} e 
     * {@link #setTrovatoRunnable(java.lang.Runnable)} prima della chiamata 
     * al metodo {@link #run()}.
     * 
     * @see #CercaVocaleRunnable(char, java.lang.String, java.lang.Runnable) 
     * 
     * @param vocaleDaTrovare vocale (case-insensitive) da trovare
     * @throws RuntimeException se vocaleDaTrovare non è una vocale valida.
     */
    public CercaVocaleRunnable(char vocaleDaTrovare) {
        this(vocaleDaTrovare, null);
    }
    
    /**
     * @brief Costruisce un nuovo runnable che cerca la vocale data nella stringa data. 
     * 
     * La vocale passata come parametro è case-insensitive e, per essere valida,
     * deve essere una tra quelle contenute in {@link #VOCALI}.
     * 
     * Se viene utilizzato questo costruttore è necessario utilizzare il setter
     * {@link #setTrovatoRunnable(java.lang.Runnable)} prima della chiamata 
     * al metodo {@link #run()}.
     * 
     * @see #CercaVocaleRunnable(char, java.lang.String, java.lang.Runnable) 
     * 
     * @param vocaleDaTrovare vocale (case-insensitive) da trovare
     * @param daAnalizzare stringa in cui va trovata la vocale
     * @throws RuntimeException se vocaleDaTrovare non è una vocale valida.
     */
    public CercaVocaleRunnable(char vocaleDaTrovare,
                               String daAnalizzare) {
        
        this.vocaleDaTrovare = Character.toLowerCase(vocaleDaTrovare);
        this.daAnalizzare = daAnalizzare;
    }
    
    
    /**
     * @brief Cerca la vocale richiesta nella stringa data
     * 
     * La vocale cercata è {@link #vocaleDaTrovare}, 
     * la stringa è {@link #daAnalizzare} e, quando viene trovata una vocale,
     * viene chiamato {@link #trovatoRunnable}; 
     * 
     * @throws RuntimeException se {@link #daAnalizzare} o {@link #trovatoRunnable}
     *                          sono nulli.
     */
    @Override
    public void run() {
        final Random rn = new Random();

        if(daAnalizzare == null)
            throw new RuntimeException("L'oggetto daAnalizzare non puo\' essere null.");
        if(ptrDati == null)
            throw new RuntimeException("L'oggetto ptrDati non puo\' essere null.");
              
        try {
            for(char currentChar : daAnalizzare.toLowerCase().toCharArray()) {
                if(usaDelay)
                    Thread.sleep(rn.nextInt(MAX_DELAY));
            
                if(usaYield)
                    Thread.yield();
            
                if(currentChar == vocaleDaTrovare) {
                    ptrDati.incrementa(vocaleDaTrovare);
                    ptrDati.getSchermo().push("Ho trovato la vocale " + currentChar);
                }
            }
        } catch(InterruptedException ex) {
            // E' stato richiesto l'interrompimento del thread
            System.err.println(String.format(
                    "Il thread %s è stato interrotto",
                    Thread.currentThread()
            ));
            ex.printStackTrace();
        }
        
        ptrDati.termina(vocaleDaTrovare);
    }
    
    /**
     * @brief Vocale minuscola (lower-case) che questo runnable 
     *        deve trovare nella frase data.
     * 
     * Restituisce il parametro {@link #vocaleDaTrovare}.
     * @return vocale che questo runnable deve trovare
     */
    public char getVocaleDaTrovare() {
        return vocaleDaTrovare;
    }
    
    /**
     * @brief Setta la stringa da analizzare
     * 
     * Setta l'attributo {@link #daAnalizzare}.
     * @param daAnalizzare stringa in cui va trovata la vocale
     */
    public void setDaAnalizzare(String daAnalizzare) {
        this.daAnalizzare = daAnalizzare;
    }

    /**
     * @brief Setta se, dopo ogni lettera, 
     *        il runnable deve aspettare un tempo random
     * 
     * Setta l'attributo {@link #usaDelay}.
     * @param usaDelay true se il runnable deve aspettare
     */
    public void setUsaDelay(boolean usaDelay) {
        this.usaDelay = usaDelay;
    }
    
    /**
     * @brief Setta se, dopo ogni lettera, il runnable 
     *        deve usare {@link Thread#yield()}
     * 
     * Setta l'attributo {@link #usaYield}.
     * @param usaYield true se il runnable deve aspettare
     */
    public void setUsaYield(boolean usaYield) {
        this.usaYield = usaYield;
    }

    /**
     * @brief Setta il puntatore all'oggetto contenente i dati condivisi
     * 
     * Setta {@link #ptrDati}
     * @param ptrDati puntatore all'oggetto
     */
    public void setPtrDati(DatiCondivisi ptrDati) {
        this.ptrDati = ptrDati;
    }
}
