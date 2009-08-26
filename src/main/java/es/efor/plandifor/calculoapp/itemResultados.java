/*
 * itemResultados.java
 *
 * Created on 4 de junio de 2007, 16:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package es.efor.plandifor.calculoapp;

/**
 *
 * @author rvelamazan
 */
public class itemResultados {
    
    private Float APP=new Float(0);
    private Float APP_plus=new Float(0);
    private Float IDP=new Float(0);
    private Float APP_con=new Float(0);
    private Float APP_com=new Float(0);
    private Float APP_tit=new Float(0);
    private Float APP_exp=new Float(0);
    
    private String errores="";
        /** Creates a new instance of itemResultados */
    public itemResultados() {
    }

    public Float getAPP() {
        return this.APP;
    }

    public void setAPP(Float APP) {
        this.APP = APP;
    }

    public Float getAPP_com() {
        return this.APP_com;
    }

    public void setAPP_com(Float APP_com) {
        this.APP_com = APP_com;
    }

    public Float getAPP_con() {
        return this.APP_con;
    }

    public void setAPP_con(Float APP_con) {
        this.APP_con = APP_con;
    }

    public Float getAPP_exp() {
        return this.APP_exp;
    }

    public void setAPP_exp(Float APP_exp) {
        this.APP_exp = APP_exp;
    }

    public Float getAPP_plus() {
        return this.APP_plus;
    }

    public void setAPP_plus(Float APP_plus) {
        this.APP_plus = APP_plus;
    }

    public Float getAPP_tit() {
        return this.APP_tit;
    }

    public void setAPP_tit(Float APP_tit) {
        this.APP_tit = APP_tit;
    }

    public Float getIDP() {
        return this.IDP;
    }

    public void setIDP(Float IDP) {
        this.IDP = IDP;
    }

    public String getError() {
        return this.errores;
    }

    public void setError(String error) {
        this.errores = this.errores + " \n " + error;
    }
    
}
