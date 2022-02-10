package hu.gov.allamkincstar.exercises.euroexchange.data;

/**
 * Egyszerű "adat" osztály, az egyes árfolyam-bejegyzések modellje, mely tartalmazza 
 * az deviza megnevezését és az - Euró-hoz képesti - árfolyamát<br>
 * A konstruktorban megkap mindent, az tagváltozók nem módiíthatóak.
 * Egy specialitás van benn, hogy - miután a form-on egy combobox listájában akarom 
 * megjeleníteni az ebből képzett listát, a toString() metódust felüldefiniáltam, hogy 
 * csak a devizanemet adja vissza<br>
 * 
 * @author Zsolt
 */
public class XchangeRate implements Comparable<Object>{
    private final String currency;
    private final float rate;

    public XchangeRate(String currency, String rate) {
        this.currency = currency;
        this.rate = Float.valueOf(rate);
    }

    public String getCurrency() {
        return currency;
    }

    public float getRate() {
        return rate;
    }
    
    @Override
    public String toString(){
        return this.currency;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) return 1;
        if (!(o instanceof XchangeRate)) return 1;
        return this.getCurrency().compareTo(((XchangeRate)o).currency);
    }
    
}
