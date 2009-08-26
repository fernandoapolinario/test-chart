/*
 * itemTit.java
 *
 * Created on 4 de junio de 2007, 13:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package es.efor.plandifor.calculoapp;

/**
 *
 * @author rvelamazan
 */
public class itemTit {
    
    private String codigo="";
    private String descripcion="";
    private boolean requerido=false;
    private boolean posee=false;
    
       
    /** Creates a new instance of itemTit */
    public itemTit() {
    }
    
    public itemTit(String codigo, String descripcion, boolean requerido, boolean poseido) {
    this.codigo=codigo;
    this.descripcion=descripcion;
    this.requerido=requerido;
    this.posee=poseido;
    }
    
   /* public itemTit(String codigo, String descripcion) {
    this.codigo=codigo;
    this.descripcion=descripcion;
    this.requerido=false;
    this.posee=true;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isPosee() {
        return posee;
    }

    public boolean isRequerido() {
        return requerido;
    }

    public void setPosee(boolean posee) {
        this.posee = posee;
    }

    public void setRequerido(boolean requerido) {
        this.requerido = requerido;
    }
    
    /* 
     * Funcion que nos dirá si anula el APP por tener no poseer una competencia requerida.
     */
    public boolean esAnulador(){
        boolean x=false;
        if (this.requerido)
            {
            if (this.posee)
                {
                x=false;
                }
            else
                {
                x=true;
                }
            }
        else
            {
            x=false;
            }
    return(x);
    }
    
   
    public boolean esSobrevalor () {
       return(false);
    }
    
    public Float getCalculo(){
        
    float i=0;
    float cien=100;
    float cero=0;
    
    if (this.posee)
        {
         i=cien;
        }
    else
        {
         i=cero;
        } 
    //System.out.println("Tit getvalor posee:" + this.posee +"  salida: "+ i);
    return(new Float(i));
    }
    
    public Float getSobreValor(){
    return(new Float(0));
    }
}
