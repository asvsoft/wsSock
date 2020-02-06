/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENTES;

import main.dbCon;
import main.mainClass;
import org.json.JSONObject;

/**
 *
 * @author adolfoyoyo
 */
public class directo extends mainClass {


    public directo(dbCon bd) throws Exception {
        super(bd);
        String dStr = (String) bd.inData.get("dString");
        String dNum = (String) bd.inData.get("dNumber");

        int Num = Integer.parseInt(dNum);
        Num = Num * 4 ;
        dStr = dStr + " --> CLIENTES.directo  ";                

        bd.outData.put("dStr", dStr);
        bd.outData.put("dNum", Num);        

    }

}
