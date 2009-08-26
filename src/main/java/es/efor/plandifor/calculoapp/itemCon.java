/*
 * item.java
 *
 * Created on 4 de junio de 2007, 13:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package es.efor.plandifor.calculoapp;

/**
 *
 * @author rvelamazan
 */
public class itemCon {
    
    private String codigo="";
    private String descripcion="";        
    private boolean requerido=false;
    private Float valor_requerido=new Float(0);
    private Float valor_valorado=new Float(0);
        
    /** Creates a new instance of item */
    public itemCon() {
    }
    
    /**
     * Constructor de clase itemCon que admite todo texto. (salvo el valor booleano)
     */
    public itemCon(String codigo, String descripcion, boolean requerido, String req, String val) {
    this.codigo=codigo;
    this.descripcion=descripcion;
    this.requerido=requerido;
    this.valor_requerido=new Float(req);
    this.valor_valorado=new Float(val);
    }

    public itemCon(String codigo, String descripcion, boolean requerido, int req, int val) {
    this.codigo=codigo;
    this.descripcion=descripcion;
    this.requerido=requerido;
    this.valor_requerido=new Float(req);
    this.valor_valorado=new Float(val);
    }
    
    
    /*
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

    public boolean isRequerido() {
        return requerido;
    }

    public void setRequerido(boolean requerido) {
        this.requerido = requerido;
    }

    public Integer getValor_requerido() {
        return valor_requerido;
    }

    public void setValor_requerido(Integer valor_requerido) {
        this.valor_requerido = valor_requerido;
    }

    public void setValor_requerido(String valor_requerido) {
        this.valor_requerido.parseInt(valor_requerido);
    }

    public Integer getValor_valorado() {
        return valor_valorado;
    }

    public void setValor_valorado(Integer valor_valorado) {
        this.valor_valorado = valor_valorado;
    }
    
    public void setValor_valorado(String valor_valorado) {
        this.valor_valorado.parseInt(valor_valorado);
    }
*/
      public boolean esSobrevalor () {
        boolean x = false;
        if (this.valor_valorado.floatValue()>this.valor_requerido.floatValue())
            {
            x=true;
            }
        return(x);
    }
    
    public Float getCalculo(){
        
        float cien=100;
        float uno=1;
        float req=this.valor_requerido.floatValue();
        float val=this.valor_valorado.floatValue();
        float cero=0;
        
        float i=cero;
        float ii=cero;
        
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
        //System.out.println("CON getvalor req:" + req +"  val: "+val+"   salida: "+ i);
        return(new Float(i));
    } 

    public Float getSobreValor(){
        
        float cien=100;
        float uno=1;
        float req=this.valor_requerido.floatValue();
        float val=this.valor_valorado.floatValue();
        float cero=0;
        
        float i=cero;
        
        if (val!=cero)
           {
           i=((val-req)/req)*100;
           }
        
        return(new Float(i));
        }
}