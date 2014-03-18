package jackpot;

import java.util.Calendar;

public class HistorialJackpot {

    private Calendar calendar;
    private float saldo;
    private float deposito;
    private float premio;
    private int[] combinacion;
    
    public HistorialJackpot(Calendar calendar, float saldo, float deposito, float premio, int[] combinacion) {
        this.calendar = calendar;
        this.saldo = saldo;
        this.deposito = deposito;
        this.premio = premio;
        this.combinacion = combinacion;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public float getSaldo() {
        return saldo;
    }

    public float getDeposito() {
        return deposito;
    }

    public float getPremio() {
        return premio;
    }

    public int[] getCombinacion() {
        return combinacion;
    }
}
