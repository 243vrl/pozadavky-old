/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl;

import cz.seznam.wenaaa.is243vrl.beans.PlanovaniBean;
import cz.seznam.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.seznam.wenaaa.utils.Kalendar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author vena
 */
public class SluzboDen {

    GregorianCalendar datum;
    char typdne;
    String typsluzby;
    String slouzici;
    private float maxsluzebpresmiru;
    private int maxpocetsvatku;
    private int maxpocetsobot;
    private int maxpocetnedel;
    private int maxpocetpatku;
    SluzboDen nahoru;
    List<Sluzby> minulyMesic;//6 poslednich dni
    int hloubka;
    //SluzboDen dolu;

    public String getTypsluzby() {
        return typsluzby;
    }

    public void setMinulyMesic(List<Sluzby> minulyMesic) {
        this.minulyMesic = minulyMesic;
    }

    public void setTypsluzby(String typsluzby) {
        this.typsluzby = typsluzby;
    }

    public SluzboDen getNahoru() {
        return nahoru;
    }

    public String getSlouzici() {
        return slouzici;
    }

    public void setSlouzici(String slouzici) {
        this.slouzici = slouzici;
    }

    public int getDen() {
        return datum.get(Calendar.DAY_OF_MONTH);
    }

    public SluzboDen(int den, String typSluzby, SluzboDen nahoru, Slouzici slouzici) {
        this.slouzici = slouzici.getJmeno();
        this.minulyMesic = null;
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.MONTH, 1);
        gc.set(Calendar.DAY_OF_MONTH, den);
        this.datum = new GregorianCalendar();
        this.datum.add(Calendar.MONTH, 1);
        this.datum.set(Calendar.DAY_OF_MONTH, den);
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SATURDAY:
                this.typdne = 'C';
                break;
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
                this.typdne = 'F';
                if (Kalendar.jeSvatek(gc)) {
                    this.typdne = 'B';
                }
                gc.add(Calendar.DAY_OF_MONTH, 1);
                if (Kalendar.jeSvatek(gc)) {
                    if (this.typdne == 'B') {
                        this.typdne = 'A';
                    } else {
                        this.typdne = 'B';
                    }
                }
                gc.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case Calendar.FRIDAY:
                this.typdne = 'E';
                if (Kalendar.jeSvatek(gc)) {
                    this.typdne = 'B';
                }
                break;
            case Calendar.SUNDAY:
                this.typdne = 'D';
                gc.add(Calendar.DAY_OF_MONTH, 1);
                if (Kalendar.jeSvatek(gc)) {
                    this.typdne = 'B';
                }
                gc.add(Calendar.DAY_OF_MONTH, -1);
                break;
        }
        this.typsluzby = typSluzby;
        this.nahoru = nahoru;
        int pocetSvatku = 0;
        int pocetSobot = 0;
        int pocetNedel = 0;
        int pocetPatku = 0;
        int pocetSluzeb = 1;
        switch (this.typdne) {
            case 'A':
                pocetSvatku = 2;
                break;
            case 'B':
                pocetSvatku = 1;
                break;
            case 'C':
                pocetSobot = 1;
                break;
            case 'D':
                pocetNedel = 1;
                break;
            case 'E':
                pocetPatku = 1;
                break;
        }
        if (nahoru != null) {
            hloubka = nahoru.hloubka + 1;
            maxsluzebpresmiru = nahoru.maxsluzebpresmiru;
            maxpocetsvatku = nahoru.maxpocetsvatku;
            maxpocetsobot = nahoru.maxpocetsobot;
            maxpocetnedel = nahoru.maxpocetnedel;
            maxpocetpatku = nahoru.maxpocetpatku;

            SluzboDen pom = nahoru;
            while (pom != null) {
                if (pom.slouzici.equals(this.slouzici)) {
                    pocetSluzeb++;
                    switch (pom.typdne) {
                        case 'A':
                            pocetSvatku += 2;
                            break;
                        case 'B':
                            pocetSvatku++;
                            break;
                        case 'C':
                            pocetSobot++;
                            break;
                        case 'D':
                            pocetNedel++;
                            break;
                        case 'E':
                            pocetPatku++;
                            break;
                    }
                }
                pom = pom.nahoru;
            }
            if (pocetSvatku > this.maxpocetsvatku) {
                this.maxpocetsvatku = pocetSvatku;
            }
            if (pocetSobot > this.maxpocetsobot) {
                this.maxpocetsobot = pocetSobot;
            }
            if (pocetNedel > this.maxpocetnedel) {
                this.maxpocetnedel = pocetNedel;
            }
            if (pocetPatku > this.maxpocetpatku) {
                this.maxpocetpatku = pocetPatku;
            }
            float pocetSluzebPresMiru = (pocetSluzeb > slouzici.getPlanujSluzeb()) ? pocetSluzeb - slouzici.getPlanujSluzeb() : 0;
            pocetSluzebPresMiru = pocetSluzeb > PlanovaniBean.getMAX_PLANOVAT() ? pocetSluzeb : pocetSluzebPresMiru;
            if (pocetSluzebPresMiru > this.maxsluzebpresmiru) {
                this.maxsluzebpresmiru = pocetSluzebPresMiru;
            }
        } else {
            maxsluzebpresmiru = 0;
            maxpocetsvatku = pocetSvatku;
            maxpocetsobot = pocetSobot;
            maxpocetnedel = pocetNedel;
            maxpocetpatku = pocetPatku;
            hloubka = 0;
        }

    }

    public boolean isValid(Slouzici slouzici) {
        //System.out.println("kontrola isValid...");
        //System.out.print(this);
        long nemuze = slouzici.getPlneVolneDny();
        long novaSluzba = (long) Math.pow(2, datum.get(Calendar.DAY_OF_MONTH));
        if ((novaSluzba & nemuze) != 0) {
            //System.out.print("nemuze na pozadavek");
            return false;
        }// ze ma volny pozadavek
        SluzboDen pom = this.nahoru;
        long slouzi = 0;
        while (pom != null) {
            if (this.slouzici.equals(pom.slouzici)) {
                slouzi += (long) Math.pow(2, pom.datum.get(Calendar.DAY_OF_MONTH));
            }
            pom = pom.nahoru;
        }
        long denPo = novaSluzba << 1;
        long denPred = novaSluzba >> 1;
        if ((novaSluzba & slouzi) != 0) {
            //System.out.print("nemuze na ten samy den");
            return false;
        }//ze neslouzi ten samy den
        if ((denPo & slouzi) != 0) {
            //System.out.print("nemuze na den po");
            return false;
        }//den po
        if ((denPred & slouzi) != 0) {
            //System.out.print("nemuze na den pred");
            return false;
        }//den pred
        slouzi = slouzi | novaSluzba;
        //pro konecne sluzby z minuleho mesice, jsou v sestupnem poradi
        //nastavim nulty bit na 1 pokud slouzi
        if (minulyMesic != null) {
            for (Sluzby sl : minulyMesic) {
                boolean jeVeSluzbe = false;
                if(sl.getLd().getLetajici().equals(this.slouzici))
                    jeVeSluzbe = true;
                if(sl.getSd().getLetajici().equals(this.slouzici))
                    jeVeSluzbe = true;
                if(sl.getLk().getLetajici().equals(this.slouzici))
                    jeVeSluzbe = true;
                if(sl.getSk().getLetajici().equals(this.slouzici))
                    jeVeSluzbe = true;
                if(sl.getLp().getLetajici().equals(this.slouzici))
                    jeVeSluzbe = true;
                if(sl.getSp().getLetajici().equals(this.slouzici))
                    jeVeSluzbe = true;
                if(jeVeSluzbe){
                    //nastaveni nulteho bitu na jedna
                    slouzi |= 1;
                }
                slouzi <<= 1;
            }
        }
        do {
            if ((slouzi & 85) == 85) {
                //System.out.print("nemuze na obdenky");
                return false;
            }//85d = 1010101b
            slouzi = slouzi >> 1;
        } while (slouzi != 0);
        //System.out.print("ok");
        return true;
    }

    public boolean jeMensiNezParam(SluzboDen sd) {
        return this.hloubka > sd.hloubka;
    }

    public int getHloubka() {
        return hloubka;
    }

    public String toStringII() {
        return String.format("%s:%d", this.typsluzby, this.datum.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public String toString() {
        return "SluzboDen{" + new SimpleDateFormat("dd").format(this.datum.getTime()) + ", " + typsluzby + ", " + slouzici + "," + hloubka + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.datum);
        hash = 37 * hash + this.typdne;
        hash = 37 * hash + Objects.hashCode(this.typsluzby);
        hash = 37 * hash + Objects.hashCode(this.slouzici);
        hash = 37 * hash + Float.floatToIntBits(this.getMaxsluzebpresmiru());
        hash = 37 * hash + this.getMaxpocetsvatku();
        hash = 37 * hash + this.getMaxpocetsobot();
        hash = 37 * hash + this.getMaxpocetnedel();
        hash = 37 * hash + this.getMaxpocetpatku();
        hash = 37 * hash + Objects.hashCode(this.nahoru);
        hash = 37 * hash + this.hloubka;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SluzboDen other = (SluzboDen) obj;
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (this.typdne != other.typdne) {
            return false;
        }
        if (!Objects.equals(this.typsluzby, other.typsluzby)) {
            return false;
        }
        if (!Objects.equals(this.slouzici, other.slouzici)) {
            return false;
        }
        if (Float.floatToIntBits(this.getMaxsluzebpresmiru()) != Float.floatToIntBits(other.getMaxsluzebpresmiru())) {
            return false;
        }
        if (this.getMaxpocetsvatku() != other.getMaxpocetsvatku()) {
            return false;
        }
        if (this.getMaxpocetsobot() != other.getMaxpocetsobot()) {
            return false;
        }
        if (this.getMaxpocetnedel() != other.getMaxpocetnedel()) {
            return false;
        }
        if (this.getMaxpocetpatku() != other.getMaxpocetpatku()) {
            return false;
        }
        if (!Objects.equals(this.nahoru, other.nahoru)) {
            return false;
        }
        if (this.hloubka != other.hloubka) {
            return false;
        }
        return true;
    }

    /**
     * @return the maxsluzebpresmiru
     */
    public float getMaxsluzebpresmiru() {
        return maxsluzebpresmiru;
    }

    /**
     * @return the maxpocetsvatku
     */
    public int getMaxpocetsvatku() {
        return maxpocetsvatku;
    }

    /**
     * @return the maxpocetsobot
     */
    public int getMaxpocetsobot() {
        return maxpocetsobot;
    }

    /**
     * @return the maxpocetnedel
     */
    public int getMaxpocetnedel() {
        return maxpocetnedel;
    }

    /**
     * @return the maxpocetpatku
     */
    public int getMaxpocetpatku() {
        return maxpocetpatku;
    }

}
