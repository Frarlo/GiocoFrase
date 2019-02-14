/**
 * @author Francesco Ferlin
 * @version 1.0
 */
package me.ferlin;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Classe che gestisce un vettore di stringhe in cui
 * tutti i thread scrivono senza utilizzare direttamente
 * l'output stream di sistema.
 * L'interazione con l'utente viene poi gestita dal main.
 */
public class Schermo {
    
    /**
     * Coda di stringhe gestita dalla classe
     */
    private final Queue<String> messageQueue;
    
    private final Collection<String> umnodifiableMessages;
    
    private final Semaphore semaphore;
    
    /**
     * @brief Costruisce uno schermo
     */
    public Schermo() {
        messageQueue = new ArrayDeque();
        umnodifiableMessages = Collections.unmodifiableCollection(messageQueue);
        semaphore = new Semaphore(1);
    }
    
    /**
     * @brief Aggiunge un elemento da visualizzare su schermo in coda
     * 
     * L'elemento viene aggiunto a {@link #vect} e viene aggiornato
     * {@link #numEl} di conseguenza.
     * @param msg messaggio da visualizzare su schermo
     * @throws RuntimeException se viene raggiunta la capienza massima {@link #MAXEL}
     */
    public void push(String msg) {
        messageQueue.offer(msg);        
    }
    
    public Collection<String> getMessages() {
        return umnodifiableMessages;
    }
    
    public Semaphore getSemaphore() {
        return semaphore;
    }
    
    /**
     * @brief Pulisce lo schermo
     */
    public void pulisciSchermo() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
            
        } catch (final Exception e) {
            System.err.println("Eccezione durante il tentativo di pulire lo schermo");
            e.printStackTrace();
        }
    }
}
