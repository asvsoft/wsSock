/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CLIENTES;

import main.*;
import org.json.JSONObject;

/**
 *
 * @author adolfoyoyo
 */
public class main extends mainBk {

    public main(dbCon bd) throws Exception {
        super(bd);
    }

    public void test() throws Exception {
        String dStr = (String) bd.inData.get("dString");
        String dNum = (String) bd.inData.get("dNumber");
        int Num = Integer.parseInt(dNum);
        Num = Num * 2;
        dStr = dStr + " --> CLIENTES. macaco .test ";

        if (Num == 0) {
            throw new Exception("Numero no puede ser cero-CERO");
        }
        
        bd.outData.put("dStr", dStr);
        bd.outData.put("dNum", Num);
    }
    

}
