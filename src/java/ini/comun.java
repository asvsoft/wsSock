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
import main.mainClass;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author adolfoyoyo
 */
public class comun extends mainClass {

    public comun(dbCon bd) throws Exception {
        super(bd);
    }

    public void gRoles() throws Exception {
        String ss = (String) bd.inData.get("ss");
        eje = ""
                + " select "
                + "   logi_usu usu"
                + " from t_logini"
                + " where logi_sesion=?"
                + "";

        JSONArray dat = bd.pejeQry(0, eje, new String[]{ss, "N"});
        int reg = bd.gNumRows(dat);
        String usu = null;
        if (reg == 1) {
            usu = bd.gValue(dat, 0).toString();
        }

        eje = ""
                + " select "
                + "  ROLE_ROL ROL ,ROLE_LROL LROL ,CASR_CUSO CUSO ,CASR_CASREL CASREL ,CASR_DESCRIP LCASO ,CASR_EJBK EJBK "
                + " from T_ROLES ,T_USUROL,T_CASREL "
                + " where USUR_USUA=? "
                + "   and ROLE_ROL=usur_rol "
                + "   and casr_rol=role_rol "
                + "   and CASR_CUSO='ini.ldRoles' "
                + " ORDER BY ROLE_LROL, CASR_DESCRIP "
                + "";

        dat = bd.pejeQry(0, eje, new String[]{usu, "N"});

        if (bd.gNumRows(dat) == 0) {
            throw new Exception("No hay casos para usuario " + usu);
        }

        eje = dat.toString();

        bd.outData.put("roles", eje);

    }

    public void lista(String ini, String cusop, String cuso) throws Exception {
// Identificamos Consulta        
        eje = ""
                + " select "
                + "   casr_lddata ld "
                + "  ,ifnull(casr_fmtData,'') fmt "
                + " from t_casrel "
                + " where casr_cusop=? "
                + "   and casr_cuso=? "
                + "";

        JSONObject jsQry = bd.ejeQry(bd.conxSrv, eje, new String[]{
            cusop, "C",
            cuso, "C"
        });

        eje = (String) bd.JSTblgetvCampo(jsQry, 0, "ld");
        bd.outData.put("fmt", (String) bd.JSTblgetvCampo(jsQry, 0, "fmt"));

        //bd.conxDat
        jsQry = bd.ejeQry(100, eje, new String[]{ini,"N"});
        bd.outData.put("lista", jsQry.toString());
    }

    ;
    
    public void lista() throws Exception {
//        String cusop = (String) bd.inData.get("cusop");
//        String cusoh = (String) bd.inData.get("cusoh");
        lista("0", (String) bd.inData.get("cusop"), (String) bd.inData.get("cuso"));

    }

    public void fetch() throws Exception {
        String cusop = (String) bd.inData.get("cusop_org");
        String cuso = (String) bd.inData.get("cuso_org");
        String _last =(String) bd.inData.get("_last");
        lista(_last, cusop, cuso);
    }

}
