/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ini;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import main.dbCon;
import main.mainBk;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author adolfoyoyo
 */
public class vaLogin extends mainBk {

    public vaLogin(dbCon bd) throws Exception {
        super(bd);
        String usua = (String) bd.inData.get("usua");
        String paswd = (String) bd.inData.get("paswd");
        String nombre = "";

        if (! bd.bd.equals("5")) {
            bd.outData.put("lbUsr", "Usuario Local");
            return;
        }
// Usuario        
        String eje = ""
                + " select "
                + "  usua_nombre lusua "
                + " from \"ADMIN\".t_usuarios "
                + " where usua_usua=? "
                + "";

        if (bd.bd.equals("1")) {
            eje = ""
                    + " select "
                    + "   usua_nombre lusua "
                    //+ "  usua_usua ,usua_nombre ,ususeg.decrypt(usua_epaswd) pasw "
                    + " from tusuarios "
                    + " where usua_usua= ?"
                    + "";
        };

        JSONObject jsQry = bd.ejeQry(bd.conxDat, eje, new String[]{usua, "C"});

        if (((JSONArray) jsQry.get("datos")).length() == 0) {
            throw new Exception("No existe usuario ");
        };

// Usuario con Paswd        
        eje = ""
                + " select "
                + "  usua_nombre lusua "
                + " from \"ADMIN\".t_usuarios "
                + " where usua_usua=? "
                + "   and usua_paswd=? "
                + "";

        if (bd.bd.equals("1")) {
            eje = ""
                    + " select "
                    + "    usua_nombre \"lusua\"  "
                    + " from tusuarios "
                    + " where usua_usua=?"
                    + "   and ususeg.decrypt(usua_epaswd)=?"
                    + "";
        }

        jsQry = bd.ejeQry(bd.conxDat, eje, new String[]{
            usua, "C",
            paswd, "C"
        });

        if (((JSONArray) jsQry.get("datos")).length() == 0) {
            throw new Exception("Contraseña no coincide ");
        };

        bd.outData.put("lbUsr", bd.JSTblgetvCampo(jsQry, 0, "lusua"));

/////// Enviamos los caso de uso correspondientes para el menu        
//////// Registramos Sesion /////////////
    }
}
