/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package POINTER;

/**
 *
 * @author adolfoyoyo
 */
import main.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class main extends mainClass {

    public main(dbCon bd) throws Exception {
        super(bd);
    }

    public void rastro() throws Exception {
        String usua = (String) bd.inData.get("usupoint");
        String rastro = (String) bd.inData.get("rastro");
        int regis;
        long lrastro;

        JSONObject jsQry = bd.ejeQry(100, "select usua_usua usua from tusuarios where usua_email=?", new String[]{usua, "C"});

        if (((JSONArray) jsQry.get("datos")).length() == 0) {
            throw new Exception("No existe usuario ");
        };

        int iusua = (int) bd.JSTblgetvCampo(jsQry, 0, "usua");
        String cusua = Integer.toString(iusua);

        if (rastro.equals("")) {
            jsQry = bd.ejeQry(100, "SELECT nextval('sqrastros') rastro", new String[]{});
            lrastro = (long) bd.JSTblgetvCampo(jsQry, 0, "rastro");
            rastro = Long.toString(lrastro);
            eje = " insert into trastros (rast_rastro ,rast_usua ,rast_ini) values (? ,? ,now()) ";
            regis = bd.ejePs(eje, new String[]{rastro, "N", cusua, "N"});
        } else {
            eje = " update trastros set rast_fin=now() where rast_rastro=?  "; //and rast_fin is null values (?)
            regis = bd.ejePs(eje, new String[]{rastro, "N"});
        }

        bd.outData.put("usua", cusua);
        bd.outData.put("rastro", rastro);
    }

}
