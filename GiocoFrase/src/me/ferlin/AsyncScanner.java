/**
 * @author Francesco Ferlin
 * @version 3.0
 */
package me.ferlin;

import java.io.*;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Scanner in grado di leggere input da tastiera in modo
 * non bloccante e asincrono
 */
public class AsyncScanner implements Closeable {

    /**
     * InputStream da cui leggere
     */
    private final InputStream source;
    /**
     * Esecutore a thread singolo che gestisce le richieste di input
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        final Thread th = new Thread(r);
        th.setName(AsyncScanner.class.getName() + "#" + System.identityHashCode(AsyncScanner.this) + "_thread");
        th.setDaemon(true);
        return th;
    });
    /**
     * Promesse in attesa di input
     */
    private final Set<CompletableFuture<String>> futures = ConcurrentHashMap.newKeySet();
    /**
     * Booleano che indica se l'{@link #executor} sta eseguendo un thread
     */
    private final AtomicBoolean isExecuting = new AtomicBoolean(false);
    /**
     * Booleano che indica se questo scanner e' stato aperto
     */
    private boolean closed = false;

    /**
     * @brief Costruisce un oggetto in grado di leggere input in maniera 
     *        non bloccante dallo stream dato
     * 
     * @param source InputStream da cui leggere
     */
    public AsyncScanner(InputStream source) {
        this.source = source;
    }

    @Override
    public void close() throws IOException {
        if (closed)
            return;

        source.close();
        executor.shutdown();
        closed = true;
    }

    /**
     * Runnable in grado di leggere input da {@link #source}
     * e adempiere alle promesse contenute in {@link #futures}
     */
    private final class ScannerHandler implements Runnable {

        @Override
        public void run()  {
            final BufferedReader br = new BufferedReader(new InputStreamReader(source));

            try {
                while(!Thread.interrupted()) {
                    
                    while (!br.ready()) {
                        // System.out.println("not ready");
                        Thread.sleep(200);
                    }

                    // System.out.println("ready");
                    final String in = br.readLine();
                    // System.out.println("in " + in);
                    
                    if(in.equals(""))
                        continue;

                    // System.out.println("remove future");
                    futures.removeIf(f -> {
                        f.complete(in);
                        return true;
                    });

                    isExecuting.set(false);
                    break;
                }
            } catch (InterruptedException e) {
                // ignored
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @brief Legge una linea da input in maniera asyncrona
     * 
     * @see BufferedReader#readLine()
     * 
     * @return promessa di completamento dell'operazione
     */
    public Future<String> nextLine() {

        if(closed)
            throw new RuntimeException("AsyncScanner has been closed");

        final CompletableFuture<String> future = new CompletableFuture<>();
        futures.add(future);

        if(!isExecuting.getAndSet(true))
            executor.submit(new ScannerHandler());
        
        return future;
    }
}
