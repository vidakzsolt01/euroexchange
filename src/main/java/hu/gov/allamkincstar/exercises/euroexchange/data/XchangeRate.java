package hu.gov.allamkincstar.exercises.euroexchange.data;

/**
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
