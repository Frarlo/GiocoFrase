/**
 * @author Francesco Ferlin
 * @version 4.0
 */
package me.ferlin;

/**
 * Classe che gestisce le variabili condivise tra i thread e il main
 */
public class DatiCondivisi {

    /**
     * Vocali utilizzate dai thread
     */
    private final char[] vocali;
    /**
     * Conteggi delle lettere trovate da ogni thread ordinate
     * rispettivamente secondo l'ordine di {@link #vocali}
     */
    private final int[] conteggi;
    /**
     * Vettore di booleani che indicano quali thread sono stati completati 
     * ordinato secondo l'ordine di {@link #vocali}
     */
    private final boolean[] terminati;
    /**
     * Oggetto attraverso cui i thread scrivono su schermo
     */
    private final Schermo schermo;

    /**
     * @brief Costruisce una classe di dati condivisi
     * 
     * @param vocali vocali usate dai thread
     * @param schermo oggetto attraverso cui i thread devono scrivere
     */
    public DatiCondivisi(char[] vocali, Schermo schermo) {
        this.schermo = schermo;
        this.vocali = vocali;
        this.conteggi = new int[vocali.length];
        this.terminati = new boolean[vocali.length];
    }

    /**
     * @brief Restituisce la vocale più usata
     * 
     * Cerca la vocale tra {@link #vocali} con il conteggio 
     * in {@link #conteggi} più alto
     * @return  la vocale più usata
     */
    public char getPiuUsata() {
        
        int index = -1;
        int maxValue = -1;
        
        for(int i = 0; i < vocali.length; i++) {
            final int currMax = getConteggio(i);
            if(currMax > maxValue) {
                maxValue = currMax;
                index = i;
            }
        }
        
        if(index >= 0 && index < vocali.length)
            return vocali[index];
        return vocali[0];
    }

    /**
     * @brief Restituisce l'indice del thread che cerca la vocale data
     * 
     * Cerca la vocale in {@link #vocali} o restituisce -1 se non la trova.
     * @param carattere vocale da cercare
     * @return indice del thread o -1 se non esiste
     */
    private int getIndex(char carattere) {
        final char toFind = Character.toLowerCase(carattere);
        for(int i = 0; i < vocali.length; i++)
            if(toFind == vocali[i])
                return i;
        return -1;
    }
    
    /**
     * @brief Restituisce il numero di vocali trovate nella frase
     * 
     * Il numero viene preso da {@link #conteggi}
     * @param index indice del thread
     * @return numero di vocali trovate
     */
    public int getConteggio(int index) {
        return conteggi[index];
    }
    
    /**
     * @brief Restituisce il numero di vocali trovate nella frase
     * 
     * @param carattere vocale che il thread deve cercare
     * @return numero di vocali trovate
     */
    public int getConteggio(char carattere) {
        return getConteggio(getIndex(carattere));
    }

    /**
     * @brief Incrementa il numero di vocali trovate nella frase
     * 
     * Viene incrementato il numero in {@link #conteggi}
     * @param index indice del thread
     */
    public void incrementa(int index) {
        conteggi[index]++;
    }
    
    /**
     * @brief Incrementa il numero di vocali trovate nella frase
     * 
     * @param carattere vocale che il thread deve cercare
     */
    public void incrementa(char carattere) {
        incrementa(getIndex(carattere));
    }
    
    /**
     * @brief Setta il Thread con indice dato come terminato
     * 
     * Setta il booleano nel vettore {@link #terminati}
     * @param index indice del thread
     */
    public void termina(int index) {
        terminati[index] = true;
    }
    
    /**
     * @brief Setta il Thread con indice dato come terminato
     * 
     * Setta il booleano nel vettore {@link #terminati}
     * @param carattere vocale che il thread deve cercare
     */
    public void termina(char carattere) {
        termina(getIndex(carattere));
    }
    
    /**
     * @brief Restituisce se tutti i thread sono stati completati
     * 
     * @return true se tutti i thread sono stati completati
     */
    public boolean isAllOver() {
        for(boolean b : terminati)
            if(!b)
                return false;
        return true;
    }

    /**
     * @brief Restituisce l'oggetto attraverso cui i thread scrivono su schermo
     * 
     * Restituisce {@link #schermo}
     * @return oggetto attraverso cui i thread scrivono su schermo
     */
    public Schermo getSchermo() {
        return schermo;
    }
}
