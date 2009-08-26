package es.efor.plandifor.calculoapp;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
@SuppressWarnings("unchecked")
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	System.out.println("coreAPP");       
        
        pesosGlobales pg = busca_indices_app();
        contenedorItems container = new contenedorItems();
        
        // Experiencia
        container.setExperiencia(getExperiencia());
        
        // Titulaciones
        container.setTitulaciones(getTitulaciones());
        
        // Competencias
        container.setCompetencias(getCompetencias(), pg);
        
        // Conocimientos
        container.setConocimientos(getConocimientos());        
        
        coreAPP instance = new coreAPP();
        
        itemResultados result = instance.coreAPP(container, pg);
        assertEquals(22, (long) Math.ceil(result.getIDP().floatValue()));        
    }
    
    private pesosGlobales busca_indices_app() {
        
        pesosGlobales pG=new pesosGlobales();
        
        String valor_item1="60";     // APP_COMP
        String valor_item2="15";     // APP_CONO
        String valor_item3="15";     // APP_TITU
        String valor_item4="10";     // APP_EXPE
        String valor_item5="65";     // IDP_COMP
        String valor_item6="15";     // IDP_CONO
        String valor_item7="0";      // IDP_TITU
        String valor_item8="20";     // IDP_EXPE
        String valor_item9="10";     // COMP_ACT
        String valor_item10="30";    // COMP_EST
        String valor_item11="40";    // COMP_PRIO
        String valor_item12="20";    // COMP_DESE
        
        pG.setAPP (valor_item4,valor_item3,valor_item2,valor_item1);
        pG.setIDP (valor_item8,valor_item7,valor_item6,valor_item5);
        pG.setIndexCompetencias (valor_item9,valor_item10,valor_item11,valor_item12);
        
        return pG;
    }
    
    private itemExp getExperiencia() {
        itemExp exp = new itemExp(new Integer(8), new Integer(9), true);
        return exp;
    }
    
    private ArrayList getTitulaciones() {
        ArrayList lt=new ArrayList();
        itemTit tit;
        
        tit=new itemTit("ES00001","Estudio requerido 1", true, true);
        lt.add(tit);
        
        tit=new itemTit("ES00002","Estudio requerido 2", false, true);
        lt.add(tit);
        
        return lt;
    }
    
   
	private ArrayList getCompetencias() {           
        
        ArrayList lcomp=new ArrayList();
       
        // actitud // motivacion
        itemComp motivacion=new itemComp("CO00001", "Motivación", true, 4, 5);
        lcomp.add(motivacion);
        
        // confianza en el puesto 
        itemComp confianza=new itemComp("CO00002", "Confianza", false, 4, 6);
        lcomp.add(confianza);
        
        itemComp estrategica1=new itemComp("CO00003", "CE1", true, 4, 7);
        lcomp.add(estrategica1);
        
        itemComp estrategica2=new itemComp("CO00004", "CE2", false, 4, 8);
        lcomp.add(estrategica2);
            
        itemComp prioritaria1=new itemComp("CO00005", "CP1", false, 4, 9);
        lcomp.add(prioritaria1);
        itemComp prioritaria2=new itemComp("CO00006", "CP2", false, 4, 10);
        lcomp.add(prioritaria2);
        itemComp prioritaria3=new itemComp("CO00007", "CP3", false, 4, 4);
        lcomp.add(prioritaria3);
        itemComp prioritaria4=new itemComp("CO00008", "CP4", false, 4, 5);
        lcomp.add(prioritaria4);

        itemComp deseable1=new itemComp("CO00009", "CD1", false, 4, 6);
        lcomp.add(deseable1);
        itemComp deseable2=new itemComp("CO00010", "CD2", false, 4, 7);
        lcomp.add(deseable2);
        itemComp deseable3=new itemComp("CO00011", "CD3", false, 4, 8);
        lcomp.add(deseable3);
        itemComp deseable4=new itemComp("CO00012", "CD4", false, 4, 9);
        lcomp.add(deseable4);
        
        return lcomp;
    }
      
    private ArrayList getConocimientos() {
        
        itemCon con;
        ArrayList lcon=new ArrayList();
        
        con = new itemCon("CN00001", "Conocimiento requerido 1", true, 4, 5);
        lcon.add(con);  
        
        con = new itemCon("CN00002", "Conocimiento requerido 2", false, 4, 6);
        lcon.add(con);
        
        con = new itemCon("CN00003", "Conocimiento requerido 3", false, 4, 7);
        lcon.add(con);
        
        con = new itemCon("CN00004", "Conocimiento requerido 4", false, 4, 8);
        lcon.add(con);
        
        con = new itemCon("CN00005", "Conocimiento requerido 5", false, 4, 9);
        lcon.add(con);
        
        con = new itemCon("CN00006", "Conocimiento requerido 6", false, 4, 10);
        lcon.add(con);
        
        con = new itemCon("CN00007", "Conocimiento requerido 7", false, 4, 5);
        lcon.add(con);
        
        con = new itemCon("CN00008", "Conocimiento requerido 8", false, 4, 6);
        lcon.add(con);
        
        con = new itemCon("CN00009", "Conocimiento requerido 9", false, 4, 7);
        lcon.add(con);
                
        return lcon;        
    }
}
