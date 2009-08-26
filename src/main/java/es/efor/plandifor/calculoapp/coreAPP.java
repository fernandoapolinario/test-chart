/*
 * coreAPP.java
 *
 * Created on 4 de junio de 2007, 17:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/* 
 * Version 1.0.0 - Beta
 * 
 * Calculo de indices APP. 
 *
 */

package es.efor.plandifor.calculoapp;

import java.util.Iterator;

/**
 *
 * @author rvelamazan
 */
public class coreAPP {
    
    /** Creates a new instance of coreAPP */    
	public itemResultados coreAPP(contenedorItems container,pesosGlobales pg) {
        
    	itemResultados calculos=new itemResultados();
        boolean esanulado=false;
        float temporal=0;    
        float indice=0;   
        float peso=0;
        float idp_temporal=0;
        float idp_temporal_categorias=0;
        float cero=0;
        float uno=1;
        float cien=100;
        float tolerancia=new Float("0.01").floatValue ();
        
        if (!(container.isOk())){
            //System.out.println(" Container no isOk. Parámetros de cálculo no establecidos. !");
            //System.err.println(" Container no isOk. Parámetros de cálculo no establecidos. !");
            calculos.setError("Container no isOk. Parámetros de cálculo no establecidos. !");
            calculos.setError(container.getMotivoFallo());
            return(calculos);
        }
        
        if (!(pg.isOk())){
            //System.out.println(" Container no isOk. Pesos globales no establecidos. !");
            //System.err.println(" Container no isOk. Pesos globales no establecidos. !");
            calculos.setError(" Container no isOk. Pesos globales no establecidos. !"); 
            return(calculos);
        }
        
        
        /** Calculo de competencias. */
        
        Iterator itr=container.getLista_competencias().listIterator();    
        Iterator itr2=container.getLista_pesos_competencias().listIterator();    
        temporal=cero;
        idp_temporal=cero;
        
        if (itr.hasNext())         
        {
            while (itr.hasNext())
            {
                
            	itemComp comp = (itemComp) itr.next();
                indice = comp.getCalculo().floatValue();                
                peso =((Float) itr2.next()).floatValue();
                temporal = temporal+(indice*peso);                
                
                if (comp.esSobrevalor())
                {
                    idp_temporal = idp_temporal+((Float)comp.getSobreValor()).floatValue()*peso;
                }
                
                System.out.println("COMP Indice: " + indice + " Peso: " + peso + " Temporal: " + temporal + " IDP Temporal: " + idp_temporal);
                
            }
        }
        else
        {
            temporal=cien;
        }

        calculos.setAPP_com(new Float(temporal));
        idp_temporal_categorias=idp_temporal_categorias+(idp_temporal*((Float) pg.getIdpcompetencias()).floatValue());
        
        System.out.println(" IDP : "+idp_temporal + " IDP Categorías : "+idp_temporal_categorias);        
        
        /** Calculo de conocimientos. */
        
        itr=container.getLista_conocimientos().listIterator();    
        itr2=container.getLista_pesos_conocimientos().listIterator();    
        temporal=cero;
        idp_temporal=cero;
        
        if (itr.hasNext())            
        {
            while (itr.hasNext())
            {
                
            	itemCon con=(itemCon) itr.next();
                indice=((Float) con.getCalculo()).floatValue();
                peso=((Float) itr2.next()).floatValue();
                temporal=temporal+(indice*peso);
                
                if (con.esSobrevalor())
                {
                    idp_temporal=idp_temporal+((Float) con.getSobreValor()).floatValue()*peso;
                }
                 
                System.out.println("CON Indice: " + indice + " Peso: " + peso + " Temporal: " + temporal + " IDP Temporal: " + idp_temporal);
                 
            }
        }
        else
        {
        	temporal=cien;
        }
        
        calculos.setAPP_con(new Float(temporal));
        idp_temporal_categorias=idp_temporal_categorias+(idp_temporal/container.getLista_conocimientos().size())*(((Float) pg.getIdpconocimientos()).floatValue());
        
        System.out.println(" IDP : "+idp_temporal + " IDP Categorías : "+idp_temporal_categorias);          
        
        /** Calculo de Titulaciones/estudios.. */
        
        itr=container.getLista_titulaciones().listIterator();    
        itr2=container.getLista_pesos_titulaciones().listIterator();    
        temporal=cero;
        idp_temporal=cero;
        
        if (itr.hasNext())            
        {
            while (itr.hasNext() && itr2.hasNext())
            {
                itemTit tit=(itemTit) itr.next();
                indice=((Float)tit.getCalculo()).floatValue();
                peso=((Float) itr2.next()).floatValue();
                temporal=temporal+(indice*peso);
                
                if (tit.esAnulador())
                {
                    esanulado=true;
                }
                
                if (tit.esSobrevalor())
                {
                    idp_temporal=idp_temporal+((Float)tit.getSobreValor()).floatValue()*peso;
                }
        
                System.out.println("TIT Indice: " + indice + " Peso: " + peso + " Temporal: " + temporal + " IDP Temporal: " + idp_temporal);                
            }
        }
        else
        {
            temporal=cien;
        }
        
        calculos.setAPP_tit(new Float(temporal));        
        idp_temporal_categorias=idp_temporal_categorias+(idp_temporal*((Float) pg.getIdpestudios()).floatValue());
        
        System.out.println(" IDP Categorías : "+idp_temporal_categorias);
        
        /** Calculo de experiencia. */
        
        temporal=cero;
        idp_temporal=cero;
        temporal=((Float) container.getExperiencia().getCalculo()).floatValue();
        //System.out.println("EXP Temporal: "+temporal);
        calculos.setAPP_exp(new Float (temporal));
        
        if (container.getExperiencia().esSobrevalor())
        {
            idp_temporal=((Float) container.getExperiencia().getSobreValor()).floatValue();
            peso=((Float) pg.getIdpexperiencia()).floatValue();            
            idp_temporal_categorias=idp_temporal_categorias+(idp_temporal*((Float) pg.getIdpexperiencia()).floatValue());
            
            System.out.println("peso: " + peso + " mult: " + idp_temporal*((Float) pg.getIdpexperiencia()).floatValue());
        }
                
        System.out.println(" IDP : "+idp_temporal + " IDP Categorías : "+idp_temporal_categorias);       
        
        // APP total. 
        temporal=cero;
        temporal=calculos.getAPP_com().floatValue()*pg.getAppcompetencias().floatValue();
        temporal=temporal+calculos.getAPP_con().floatValue()*pg.getAppconocimientos().floatValue();
        temporal=temporal+calculos.getAPP_tit().floatValue()*pg.getAppestudios().floatValue();
        temporal=temporal+calculos.getAPP_exp().floatValue()*pg.getAppexperiencia().floatValue();
        
        calculos.setAPP_plus(new Float(temporal));
        if (esanulado)
        {
            calculos.setAPP(new Float(cero));
        }
        else
        {
            calculos.setAPP(new Float(temporal));
        }
    
        //System.out.println("IDP: "+idp_temporal_categorias);
        
        
        
        System.out.println(new Float(Math.abs(temporal-cien)));
        System.out.println((new Float(Math.abs(temporal-cien))).compareTo(new Float(tolerancia)));
        System.out.println(tolerancia);        
        System.out.println(((float) Math.abs(temporal-cien))<tolerancia);
        
        //if (((float) Math.abs(temporal-cien))<tolerancia)
        if ((new Float(Math.abs(temporal-cien))).compareTo(new Float(tolerancia)) > 0)
        {
            calculos.setIDP(new Float(idp_temporal_categorias/3));           
        }
        else
        {
        	calculos.setIDP(new Float(cero));           
        }
         
        System.out.println("IDP FINAL: The ceiling of " + calculos.getIDP() + " is " + Math.ceil(calculos.getIDP().floatValue()));       
        
        return(calculos);
    }
    
}
