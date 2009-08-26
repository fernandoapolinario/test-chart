/*
 * contenedorItems.java
 *
 * Created on 4 de junio de 2007, 16:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package es.efor.plandifor.calculoapp;

import java.util.ArrayList;

/**
 *
 * @author rvelamazan
 */
public class contenedorItems {
    
    private ArrayList lista_conocimientos=new ArrayList();
    private ArrayList lista_pesos_conocimientos=new ArrayList();
    private boolean lcintro=false;
    
    private ArrayList lista_titulaciones=new ArrayList();
    private ArrayList lista_pesos_titulaciones=new ArrayList();
    private boolean ltintro=false;
    
    private itemExp experiencia=new itemExp();
    private boolean leintro=false;
    
    private ArrayList lista_competencias=new ArrayList();
    private ArrayList lista_pesos_competencias=new ArrayList();
    private boolean lcmintro=false;
    
    private boolean preguntado=false;
    
    /** Creates a new instance of contenedorItems 
     *
     * Clase que nos servirá de soporte para almacenar los items. 
     *
     */
    public contenedorItems() {
    }
    
    public void setConocimientos(ArrayList lista){
        int contador=0;
        this.lista_conocimientos=lista;
        this.lista_pesos_conocimientos.clear();
        float elementos=(float) this.lista_conocimientos.size();
        float peso=0;
        float uno=1;
        if (elementos>0)
            {
            //peso=uno/elementos;
        	peso = 1;
            }
        for (contador=0;contador<elementos;contador++){
            this.lista_pesos_conocimientos.add(new Float(peso));
            }
        this.lcintro=true;
    }
    
    public void setTitulaciones(ArrayList lista){
        int contador=0;
        this.lista_titulaciones=lista;
        this.lista_pesos_titulaciones.clear();
        float elementos=(float) this.lista_titulaciones.size();
        float peso=0;
        float uno=1;
        if (elementos>0)
            {
            peso=1/elementos;
            }
        for (contador=0;contador<elementos;contador++){
            this.lista_pesos_titulaciones.add(new Float(peso));
            }
        this.ltintro=true;
    }
    
    public void setExperiencia (itemExp experiencia){
        this.experiencia=experiencia;
        this.leintro=true;
    }
    
    public void setCompetencias (ArrayList lista, pesosGlobales pg){
        int a;
        //System.out.println("Antes: "+this.lista_competencias.size());
        this.lista_competencias=lista;
        //System.out.println("Después: "+this.lista_competencias.size());
        this.lista_pesos_competencias.clear();
        this.lista_pesos_competencias.add(new Float(pg.getIndex_actitud().floatValue()/2));
        this.lista_pesos_competencias.add(new Float(pg.getIndex_actitud().floatValue()/2));
        this.lista_pesos_competencias.add(new Float(pg.getIndex_estrategia().floatValue()/2));
        this.lista_pesos_competencias.add(new Float(pg.getIndex_estrategia().floatValue()/2));
        for (a=0;a<4;a++)
            {
            this.lista_pesos_competencias.add(new Float(pg.getIndex_prioritaria().floatValue()/4));
            }
        for (a=0;a<4;a++)
            {
            this.lista_pesos_competencias.add(new Float(pg.getIndex_deseable().floatValue()/4));
            }
        this.lcmintro=true;
    }
    
    public boolean isOk ()
        {
        if ((this.lcintro) && (this.lcmintro) && (this.leintro) && (this.ltintro))
        {
           this.preguntado=true; 
           return(true);
        }
        return(false);
        }

    public itemExp getExperiencia() {
        if (this.preguntado){
            return experiencia;
            }
        return(null);
    }

    public ArrayList getLista_competencias() {
        if (this.preguntado){
            return lista_competencias;
         }
        return(null);   
    }

    public ArrayList getLista_conocimientos() {
        if (this.preguntado){
        return lista_conocimientos;
        }
        return(null);
    }

    public ArrayList getLista_pesos_competencias() {
        if (this.preguntado){
        return lista_pesos_competencias;
        }
        return(null);        
    }

    public ArrayList getLista_pesos_conocimientos() {
        if (this.preguntado){
        return lista_pesos_conocimientos;
        }
        return(null);    
        }

    public ArrayList getLista_pesos_titulaciones() {
        if (this.preguntado){
        return lista_pesos_titulaciones;
        }
        return(null);
        }

    public ArrayList getLista_titulaciones() {
        if (this.preguntado){
        return lista_titulaciones;
        }
        return(null);        
    }
    
    public String getMotivoFallo(){
        String cadena="";
        if (!(this.lcintro)){
           cadena=cadena+" * No se ha introducido conocimientos. "; 
        } 
        if (!(this.lcmintro)){
            cadena=cadena+" * No se ha introducido competencias. "; 
        }
        if (!(this.leintro)){
            cadena=cadena+" * No se ha introducido experiencia. "; 
        }
        if (!(this.ltintro)){
            cadena=cadena+" * No se ha introducido titulaciones. "; 
        }
     return (cadena);
    }
}
