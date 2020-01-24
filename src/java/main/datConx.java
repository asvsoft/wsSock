/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author adolfoyoyo
 */
public class datConx {

    public String strCon;
    public String user;
    public String paswd;

    public datConx() {
        String URL = "";
        String bdatos = "";
        String jdbc="";

////////////////////////  Sqlite ////////////////////////////////

        URL= datConx.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        bdatos= "dbsql-01.db" ;
        jdbc="sqlite";
        strCon = "jdbc:"+jdbc+":"+ URL + "/" + bdatos;
        
        
////////////////  Postgre ////////////////////////////////
//      
//        URL = "//68.66.193.222:5432";        
//        bdatos = "gasApp";
//        jdbc="postgresql";
//        user = "postgres";
//        paswd = "atlas@Campeon.51";        
//        strCon = "jdbc:"+jdbc+ ":" + URL + "/" + bdatos;
//      
////////////////////////////////////////////////////////////////////////////////
      
      
//
////////////////////////////////////////////////////////////////////////////////

    }

}
