/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.wenaaa.is243vrl.planovani;

import cz.wenaaa.is243vrl.JPAcontrollers.PomtabJpaController;
import cz.wenaaa.is243vrl.JPAcontrollers.exceptions.RollbackFailureException;
import cz.wenaaa.is243vrl.TypySluzby;
import cz.wenaaa.is243vrl.entityClasses.Pomtab;
import cz.wenaaa.is243vrl.entityClasses.Sluzby;
import cz.wenaaa.is243vrl.entityClasses.jsf.LetajiciSluzbyController;
import cz.wenaaa.is243vrl.entityClasses.jsf.SluzbyController;
import cz.wenaaa.utils.AI.NodeItemFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TemporalType;

/**
 *
 * @author vena
 */
public class PlanovaniSluzeb implements NodeItemFactory<Slouzici> {

    private static PlanovaniSluzeb pilotiThis = null;
    private static PlanovaniSluzeb palubariThis = null;

    private PlanovaniSluzeb(boolean proPalubare) {
        planuji = false;
        naplanovano = false;
        this.proPalubare = proPalubare;
        pomtabJPA = new PomtabJpaController();
    }

    public static PlanovaniSluzeb getInstance(boolean proPalubare) {
        if (proPalubare) {
            if (palubariThis == null) {
                palubariThis = new PlanovaniSluzeb(proPalubare);
            }
            return palubariThis;
        }

        if (pilotiThis == null) {
            pilotiThis = new PlanovaniSluzeb(proPalubare);
        }
        return pilotiThis;
    }

    @Inject
    SluzbyController sc;
    @Inject
    LetajiciSluzbyController lsc;

    private boolean planuji;
    private boolean naplanovano;
    private boolean proPalubare;
    private PomtabJpaController pomtabJPA;
    private List<Sluzby> minulyMesic;
    private List<TypySluzby> sluzbyKNaplanovani;
    private List<SluzboDen> poradiSluzeb;
    private List<Slouzici> listSlouzici;

    private String[] textInfo;
    private static final int TI_DB_CLOSE = 1;

    public void naplanuj(GregorianCalendar gc) {
        //zamezeni dvojiteho volani
        if (planuji) {
            return;
        }
        planuji = true;

        uzavriDB(gc);

        minulyMesic = sc.nactiMinulyMesic(gc);

        sluzbyKNaplanovani = nactiSluzbyKNaplanovani();

        listSlouzici = nactiSlouzici();

        poradiSluzeb = nactiPoradiSluzeb(gc);
        
    }

    public boolean isPlanuji() {
        return planuji;
    }

    public boolean isNaplanovano() {
        return naplanovano;
    }

    private void uzavriDB(GregorianCalendar gc) {
        List<Pomtab> pomtabL = pomtabJPA.findPomtabEntities();
        Pomtab pomtab = pomtabL.get(0);
        textInfo[TI_DB_CLOSE] = "\nUzavírám DB ...";
        if (proPalubare) {
            pomtab.setPozadavkyodpalubaci(gc.getTime());
        } else {
            pomtab.setPozadavkyodpiloti(gc.getTime());
        }
        try {
            pomtabJPA.edit(pomtab);
            textInfo[TI_DB_CLOSE] += "done";
        } catch (RollbackFailureException ex) {
            Logger.getLogger(PlanovaniSluzeb.class.getName()).log(Level.SEVERE, null, ex);
            textInfo[TI_DB_CLOSE] += "WARNING: " + ex.getMessage();
        } catch (Exception ex) {
            Logger.getLogger(PlanovaniSluzeb.class.getName()).log(Level.SEVERE, null, ex);
            textInfo[TI_DB_CLOSE] += "WARNING: " + ex.getMessage();
        }
    }

    private List<TypySluzby> nactiSluzbyKNaplanovani() {
        return proPalubare ? TypySluzby.getFlightEngineersTypySluzby() : TypySluzby.getPilotsTypySluzby();
    }

    private List<Slouzici> nactiSlouzici() {
        return proPalubare ? lsc.getSlouziciPalubari() : lsc.getSlouziciPiloti();
    }

    private List<SluzboDen> nactiPoradiSluzeb(GregorianCalendar gc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Slouzici> getInitialNodes() {
        return getNexts(null);
    }

    @Override
    public List<Slouzici> getNexts(List<Slouzici> actualPath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAim(List<Slouzici> actualPath) {
        return actualPath.size() == poradiSluzeb.size();
    }

    @Override
    public double getPathValue(List<Slouzici> actualPath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
