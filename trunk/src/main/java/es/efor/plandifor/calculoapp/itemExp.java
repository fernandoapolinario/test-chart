/*
 * itemExp.java
 *
 * Created on 4 de junio de 2007, 13:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package es.efor.plandifor.calculoapp;

/**
 *
 * @author rvelamazan
 */
public class itemExp {
    
    private Float experiencia_valorada=new Float(0);
    private Float experiencia_requerida=new Float(0);
    private boolean requerido=false;
    
       /** Creates a new instance of itemExp */
    public itemExp() {
    }
 /*
    public itemExp(Integer req, Integer val) {
       this.experiencia_requerida=req; 
       this.experiencia_valorada=val; 
       this.requerido=false;
    }
    
    public itemExp(String req, Integer val) {
       this.experiencia_requerida=new Integer(req); 
       this.experiencia_valorada=val; 
       this.requerido=false;
    }
    
    public itemExp(String req, String val) {
       this.experiencia_requerida=new Integer(req); 
       this.experiencia_valorada=new Integer(val); 
       this.requerido=false;
    }
    
    public itemExp(Integer req, String val) {
       this.experiencia_requerida=req; 
       this.experiencia_valorada=new Integer(val); 
       this.requerido=false;
    }
*/
     public itemExp(Integer req, Integer val, boolean obligatorio) {
       this.experiencia_requerida=new Float(req.floatValue()); 
       this.experiencia_valorada=new Float(val.floatValue()); 
       this.requerido=obligatorio;
    }
    /*
    public itemExp(String req, Integer val, boolean obligatorio) {
       this.experiencia_requerida=new Integer(req); 
       this.experiencia_valorada=val; 
       this.requerido=obligatorio;      
    }
    
    public itemExp(String req, String val, boolean obligatorio) {
       this.experiencia_requerida=new Integer(req); 
       this.experiencia_valorada=new Integer(val); 
       this.requerido=obligatorio;
    }
    
    public itemExp(Integer req, String val, boolean obligatorio) {
       this.experiencia_requerida=req; 
       this.experiencia_valorada=new Integer(val); 
       this.requerido=obligatorio;
    } */
    /*
    public Integer getExperiencia_requerida() {
        return experiencia_requerida;
    }

    public void setExperiencia_requerida(Integer experiencia_requerida) {
        this.experiencia_requerida = experiencia_requerida;
    }

    public void setExperiencia_requerida(String experiencia_requerida) {
        this.experiencia_requerida=new Integer(experiencia_requerida);
    }
    
    public Integer getExperiencia_valorada() {
        return experiencia_valorada;
    }

    public void setExperiencia_valorada(Integer experiencia_valorada) {
        this.experiencia_valorada = experiencia_valorada;
    }

    public void setExperiencia_valorada(String experiencia_valorada) {
        this.experiencia_valorada.parseInt(experiencia_valorada);
    }
*/
    
      public boolean esSobrevalor () {
        if (this.experiencia_valorada.floatValue()>this.experiencia_requerida.floatValue())
            {
            return(true);
            }
        return(false);
    }
    
    public Float getCalculo(){
        
        float cero=0;
        float cien=100;
                
        float uno=1;
        float req=this.experiencia_requerida.floatValue();
        float val=this.experiencia_valorada.floatValue();
        
        float i=0;
        float ii=0;
        
        if (val>=req)
            {
            i=cien;
            }
        else
            {
            if (req!=cero)
                {
                if (this.requerido)
                    {
                    i=cero;
                    }
                else
                    {
                    ii=uno-(req-val)/req;
                    i=ii*cien;
                    }
                }
            }
        //System.out.println("Exp getvalor req:" + req +"  val: "+val+"   salida: "+ i);
        return(new Float(i));
    } 
    
    public Float getSobreValor(){
        
        float req=this.experiencia_requerida.floatValue();
        float val=this.experiencia_valorada.floatValue();
        float cien=100;
        float i=0;
        float cero=0;
        float uno=1;
         
        // en teoría no puede ser val=0 porque req será >=0 y no debería entrar.
        if (val!=cero)
            {
            i=((val-req)/req)*cien;
            }
        
        return(new Float(i));
        }
    
}
