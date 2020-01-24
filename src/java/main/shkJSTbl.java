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

import java.io.Serializable;
import org.json.JSONObject;

public class shkJSTbl implements Serializable {
    private JSONObject jsTable ;
    
     public shkJSTbl(JSONObject tbl) {                  
     }
    
    public int gNumCols() {
        return 1;
    }
//---- Numero de Registros ----//    

    public int gNumRows() {
        return 2;
    }
    
}
