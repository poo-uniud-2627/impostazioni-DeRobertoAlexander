// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {
        this.puntiPerVincere=checker(puntiPerVincere, 1, 99, "puntiPerVincere");
        this.scartoMinimo=checker(scartoMinimo, 1, 10, "scartoMinimo");
        this.velocitaPalla=checker(velocitaPalla, 1, 20, "velocitaPalla");
    }

    /**
     * funzione il cui scopo e far si che non si deve scrivere tutta una papardela quando si controlla che un'impostazione abbia un valore che sia nel range consentito
     * @param arg la variabile che si vuole controllare
     * @param min il valore minimo che la variabile può avere
     * @param max il valore massimo che la variabile può avere
     * @param argument la chiave del valore che si vuole controllare
     * @return
     */
    private static int checker(int arg, int min, int max, String argument){
        if (arg>max || arg<min){
            throw new IllegalArgumentException(
                "L'impostazione "+argument+" è fuori dal range consentito. Il range consentito è "+min+"-"+max+" estremi inclusi mentre il valore fornito è:"+arg
            );
        } else {return arg;}
    }

    /** estensione della funzione checker che viene utilizzata nella funzione leggi. 
     * lo scopo di questa funzione e di ridurre di un sei righe il codice
     * @param row la riga in cui si trova l'errore
     */
    private static int leggiChecker(int arg, int min, int max, int row, String argument){
        int out;
        try {
            out= checker(arg, min, max, argument);
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("riga "+row+": "+ e);
        }
        return out;
    }
    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        //i valori di default. Se nel file impostazioni non vengono scritti tutte e tre le impostazioni queste non vengono modificate.
        int punti= 11;
        int scarto= 2;
        int vel= 5;
        
        /*Array che tiene conto se una delle tre variabili precedenti è stata modificata.
        DupliCheck[0] tiene conto di punti
        DupliCheck[1] tiene conto di scarto
        DupliCheck[2] tiene conto di vel
        */
        Boolean[] DuplicCheck= {false,false,false};
        
        //ciclo su tutto il file
        for (int i = 0; i < righe.size(); i++){
            //faccio un trim per rimuovere spazi indesiderati
            String test = righe.get(i).trim();


            /*controllo che la riga corrente non sia vuota(!test.isEmpty()) e non sia un commento(!test.startsWith("#")).
            Se è vuota o un commento la ignoro
            */ 
            if (!test.isEmpty() && !test.startsWith("#")){

                /*
                controllo che ci sia l'uguale che mi delimita una coppia chiave=valore.
                Se non c'e lancio un'illegalArgumentException
                 */
                if (test.indexOf('=')==-1){
                    throw new IllegalArgumentException("riga "+(i+1)+": manca il segno = ");
                } else {

                    //divido la coppia chiave=valore nella chiave e nel valore sotto forma di string
                    String chiave = test.substring(0, test.indexOf('=')).trim();
                    String valore = test.substring(test.indexOf('=')+1).trim();
                    //inizializzo la variabile che terra il valore sotto forma di integer.
                    int IntValue;
                    //controllo se il valore sia scritto in modo errato e lancio un'illegalArgumentException se lo è.
                    try {
                        IntValue= Integer.parseInt(valore);
                    } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("riga "+(i+1)+": il valore è scritto in modo errato");
                    }
                    //Se tutto va bene arrivo infine a controllare quale impostazione sto cambiando dal default
                    switch (chiave) {
                        /*
                        Caso puntiPerVincere
                        conrollo che sia la prima volta che modifico questa impostazione.
                        Se lo è, allora controllo che sia nel range.
                        Se non lo è oppure lo è però l'impostazione non è nel range allora lancio un'illegalArgumentException
                         */
                        case "puntiPerVincere":
                            if (DuplicCheck[0]==false){
                                punti=leggiChecker(IntValue, 1, 99,(i+1), "puntiPerVincere");
                                DuplicCheck[0]=true;
                            } else {
                                throw new IllegalArgumentException("riga "+(i+1)+": si sta tentando di modificare due volte la stessa impostazione");
                            }   break;
                        /*
                        Caso scartoMinimo
                        conrollo che sia la prima volta che modifico questa impostazione.
                        Se lo è, allora controllo che sia nel range.
                        Se non lo è oppure lo è però l'impostazione non è nel range allora lancio un'illegalArgumentException
                         */
                        case "scartoMinimo":
                            if (DuplicCheck[1]==false){
                                scarto=leggiChecker(IntValue, 1, 10,(i+1), "scartoMinimo");
                                DuplicCheck[1]=true;
                            } else {
                                throw new IllegalArgumentException("riga "+(i+1)+": si sta tentando di modificare due volte la stessa impostazione");
                            }   break;
                        /*
                        Caso velocitaPalla
                        conrollo che sia la prima volta che modifico questa impostazione.
                        Se lo è, allora controllo che sia nel range.
                        Se non lo è oppure lo è però l'impostazione non è nel range allora lancio un'illegalArgumentException
                         */
                        case "velocitaPalla":
                            if (DuplicCheck[2]==false){
                                vel= leggiChecker(IntValue, 1 ,20 ,(i+1), "velocitaPalla");
                                DuplicCheck[2]=true;
                            } else {
                                throw new IllegalArgumentException("riga "+(i+1)+": si sta tentando di modificare due volte la stessa impostazione");
                            }   break;
                        /*
                        nessuno dei casi precedenti:
                        la chiave è stata scritta male è allora bisogna lanciare un'illegalArgumentException
                         */
                        default:
                            throw new IllegalArgumentException("riga "+(i+1)+": la chiave: "+ chiave+" è scritta in modo errato");
                    }

                }
            }
        }

        //fuori dal ciclo si può finalmente creare un nuovo oggetto impostazioni.
        return new Impostazioni(punti,scarto,vel);
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }
}
