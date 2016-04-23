/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.seznam.wenaaa.is243vrl;

import static cz.seznam.wenaaa.is243vrl.TypyDne.NEDELE;
import static cz.seznam.wenaaa.is243vrl.TypyDne.PATEK;
import static cz.seznam.wenaaa.is243vrl.TypyDne.SOBOTA;
import static cz.seznam.wenaaa.is243vrl.TypyDne.VSEDNI;
import static cz.seznam.wenaaa.is243vrl.TypyDne.VSEDNI_DVOJSVATEK;
import static cz.seznam.wenaaa.is243vrl.TypyDne.VSEDNI_SVATEK;
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
public class SluzboDenPuvodni {

    GregorianCalendar datum;
    TypyDne typdne;
    String typsluzby;
    Slouzici slouzici;
    private float maxsluzebpresmiru;
    private int maxpocetsvatku;
    private int maxpocetsobot;
    private int maxpocetnedel;
    private int maxpocetpatku;
    SluzboDenPuvodni nahoru;
    List<Sluzby> minulyMesic;//6 poslednich dni
    int hloubka;
    //SluzboDen dolu;

    public String getTypsluzby() {
        return typsluzby;
    }

    public void setMinulyMesic(List<Sluzby> minulyMesic) {
        if (minulyMesic.size() != 6) {
            throw new IllegalArgumentException("požadováno 6 služeb z minulého měsíce obdrženo " + minulyMesic.size());
        }
        this.minulyMesic = minulyMesic;
    }

    public void setTypsluzby(String typsluzby) {
        this.typsluzby = typsluzby;
    }

    public SluzboDenPuvodni getNahoru() {
        return nahoru;
    }

    public Slouzici getSlouzici() {
        return slouzici;
    }

    public void setSlouzici(Slouzici slouzici) {
        this.slouzici = slouzici;
    }

    public int getDen() {
        return datum.get(Calendar.DAY_OF_MONTH);
    }

    private void setTypDne() {
        GregorianCalendar gc = new GregorianCalendar(datum.get(Calendar.YEAR), datum.get(Calendar.MONTH), datum.get(Calendar.DAY_OF_MONTH));
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SATURDAY:
                this.typdne = SOBOTA;
                break;
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
                this.typdne = VSEDNI;
                if (Kalendar.jeSvatek(gc)) {
                    this.typdne = VSEDNI_SVATEK;
                }
                gc.add(Calendar.DAY_OF_MONTH, 1);
                if (Kalendar.jeSvatek(gc)) {
                    if (this.typdne == VSEDNI_SVATEK) {
                        this.typdne = VSEDNI_DVOJSVATEK;
                    } else {
                        this.typdne = VSEDNI_SVATEK;
                    }
                }
                gc.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case Calendar.FRIDAY:
                this.typdne = PATEK;
                if (Kalendar.jeSvatek(gc)) {
                    this.typdne = VSEDNI_SVATEK;
                }
                break;
            case Calendar.SUNDAY:
                this.typdne = NEDELE;
                gc.add(Calendar.DAY_OF_MONTH, 1);
                if (Kalendar.jeSvatek(gc)) {
                    this.typdne = VSEDNI_SVATEK;
                }
                gc.add(Calendar.DAY_OF_MONTH, -1);
                break;
        }
    }

    private void setMaxHodnoty() {
        int pocetSvatku = 0;
        int pocetSobot = 0;
        int pocetNedel = 0;
        int pocetPatku = 0;
        int pocetSluzeb = 1;
        switch (this.typdne) {
            case VSEDNI_DVOJSVATEK:
                pocetSvatku = 2;
                break;
            case VSEDNI_SVATEK:
                pocetSvatku = 1;
                break;
            case SOBOTA:
                pocetSobot = 1;
                break;
            case NEDELE:
                pocetNedel = 1;
                break;
            case PATEK:
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

            SluzboDenPuvodni pom = nahoru;
            while (pom != null) {
                if (pom.slouzici.equals(this.slouzici)) {
                    pocetSluzeb++;
                    switch (pom.typdne) {
                        case VSEDNI_DVOJSVATEK:
                            pocetSvatku += 2;
                            break;
                        case VSEDNI_SVATEK:
                            pocetSvatku++;
                            break;
                        case SOBOTA:
                            pocetSobot++;
                            break;
                        case NEDELE:
                            pocetNedel++;
                            break;
                        case PATEK:
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

    public SluzboDenPuvodni(int den, String typSluzby, SluzboDenPuvodni nahoru, Slouzici slouzici) {
        this.slouzici = slouzici;
        this.minulyMesic = null;
        this.datum = new GregorianCalendar();
        this.datum.set(Calendar.DAY_OF_MONTH, 1);
        this.datum.add(Calendar.MONTH, 1);
        this.datum.set(Calendar.DAY_OF_MONTH, den);
        this.typsluzby = typSluzby;
        this.nahoru = nahoru;

        setTypDne();
        setMaxHodnoty();
    }

    public boolean isValid() {
        //System.out.println("kontrola isValid...");
        //System.out.print(this);
        long nemuze = slouzici.getPlneVolneDny();
        long novaSluzba = (long) Math.pow(2, datum.get(Calendar.DAY_OF_MONTH));
        if ((novaSluzba & nemuze) != 0) {
            //System.out.print("nemuze na pozadavek");
            return false;
        }// ze ma volny pozadavek
        long slouzi = kdySlouzi();
        long denPo = novaSluzba << 1;
        long denPred = novaSluzba >> 1;
        if ((novaSluzba & slouzi) != 0) {
            //System.out.print("nemuze na ten samy den");
            return false;
        }//ze neslouzi ten samy den
        /*
        if ((denPo & slouzi) != 0) {
            //System.out.print("nemuze na den po");
            return false;
        }//den po
        if ((denPred & slouzi) != 0) {
            //System.out.print("nemuze na den pred");
            return false;
        }//den pred
        */
        slouzi = slouzi | novaSluzba;
        slouzi = posunSlouziOMinulyMesic(slouzi);
        do {
            if ((slouzi & 85) == 85) {
                //System.out.print("nemuze na obdenky");
                return false;
            }//85d = 1010101b
            if ((slouzi & 0b11) == 0b11) {
                return false;// na den po/pred
            }
            slouzi = slouzi >> 1;
        } while (slouzi != 0);
        //System.out.print("ok");
        return true;
    }

    private long posunSlouziOMinulyMesic(long slouzi) {
        //pro konecne sluzby z minuleho mesice, jsou v sestupnem poradi
        //nastavim nulty bit na 1 pokud slouzi
        SluzboDenPuvodni pom = this;
        while (pom.getNahoru() != null) {
            pom = pom.getNahoru();
        }
        if (pom.minulyMesic != null) {
            for (Sluzby sl : pom.minulyMesic) {
                boolean jeVeSluzbe = false;
                try {
                    if (sl.getLd().getLetajici().equals(this.slouzici.getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getSd().getLetajici().equals(this.slouzici.getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getLk().getLetajici().equals(this.slouzici.getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getSk().getLetajici().equals(this.slouzici.getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getLp().getLetajici().equals(this.slouzici.getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                try {
                    if (sl.getSp().getLetajici().equals(this.slouzici.getJmeno())) {
                        jeVeSluzbe = true;
                    }
                } catch (NullPointerException e) {

                }
                if (jeVeSluzbe) {
                    //nastaveni nulteho bitu na jedna
                    slouzi |= 1;
                }
                slouzi <<= 1;
            }
        }
        return slouzi;
    }

    private long kdySlouzi() {
        SluzboDenPuvodni pom = this.nahoru;
        long slouzi = 0;
        while (pom != null) {
            if (this.slouzici.equals(pom.slouzici)) {
                slouzi += (long) Math.pow(2, pom.datum.get(Calendar.DAY_OF_MONTH));
            }
            pom = pom.nahoru;
        }
        return slouzi;
    }

    public boolean jeMensiNezParam(SluzboDenPuvodni sd) {
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
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.datum);
        hash = 89 * hash + Objects.hashCode(this.typdne);
        hash = 89 * hash + Objects.hashCode(this.typsluzby);
        hash = 89 * hash + Objects.hashCode(this.slouzici);
        hash = 89 * hash + Objects.hashCode(this.nahoru);
        hash = 89 * hash + this.hloubka;
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
        final SluzboDenPuvodni other = (SluzboDenPuvodni) obj;
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
