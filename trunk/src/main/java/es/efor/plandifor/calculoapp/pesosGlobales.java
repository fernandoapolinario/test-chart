/*
 * pesosGlobales.java
 *
 * Created on 4 de junio de 2007, 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package es.efor.plandifor.calculoapp;

/**
 *
 * @author rvelamazan
 */
public class pesosGlobales {
    
    private Float appexperiencia=new Float(0);
    private Float appestudios=new Float(0);
    private Float appconocimientos=new Float(0);
    private Float appcompetencias=new Float(0);
    
    private Float index_actitud=new Float(0);
    private Float index_estrategia=new Float(0);
    private Float index_prioritaria=new Float(0);
    private Float index_deseable=new Float(0);
    
    private Float idpexperiencia=new Float(0);
    private Float idpestudios=new Float(0);
    private Float idpconocimientos=new Float(0);
    private Float idpcompetencias=new Float(0);
        
    private boolean app=false;
    private boolean idp=false;
    private boolean index=false;
    
    /** Creates a new instance of pesosGlobales */
    public pesosGlobales() {
    }
    
    public void setAPP (String exp, String est, String con, String com){
        
        this.appexperiencia=convierte(exp);
        this.appestudios=convierte(est);
        this.appconocimientos=convierte(con);
        this.appcompetencias=convierte(com);
        app=true;
    }
    
    public void setIDP (String exp, String est, String con, String com){
        
        this.idpexperiencia=convierte(exp);
        this.idpestudios=convierte(est);
        this.idpconocimientos=convierte(con);
        this.idpcompetencias=convierte(com);
        idp=true;
    }
    
    public void setIndexCompetencias (String act, String est, String prio, String des){
        
        this.index_actitud=convierte(act);
        this.index_estrategia=convierte(est);
        this.index_prioritaria=convierte(prio);
        this.index_deseable=convierte(des);
        index=true;
    }

    public boolean isOk()
        {
        boolean x=false;
        if ((app) && (idp) && (index))
            {
            x=true;
            }
        return(x);
        }
    
    public Float getAppcompetencias() {
        return appcompetencias;
    }

    public Float getAppconocimientos() {
        return appconocimientos;
    }

    public Float getAppestudios() {
        return appestudios;
    }

    public Float getAppexperiencia() {
        return appexperiencia;
    }

    public Float getIdpcompetencias() {
        return idpcompetencias;
    }

    public Float getIdpconocimientos() {
        return idpconocimientos;
    }

    public Float getIdpestudios() {
        return idpestudios;
    }

    public Float getIdpexperiencia() {
        return idpexperiencia;
    }

    public Float getIndex_actitud() {
        return index_actitud;
    }

    public Float getIndex_deseable() {
        return index_deseable;
    }

    public Float getIndex_estrategia() {
        return index_estrategia;
    }

    public Float getIndex_prioritaria() {
        return index_prioritaria;
    }
    
   private Float convierte (String a) {
       Float F= new Float(a);
       float f=F.floatValue();
       f=f/100;
       Float Fr = new Float(f);
       return(Fr);
   }
}
