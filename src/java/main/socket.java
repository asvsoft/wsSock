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
import java.sql.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONArray;
import org.json.JSONObject;

@ServerEndpoint("/sock")
public class socket {

    //private dbCon  ;
    @OnOpen
    public void abre() {
        //LOGGER.info("Iniciando la conexion");

    }

    @OnClose
    public void cierra() {
    }

    @OnMessage
    public String llama(String datos) throws Exception {
        String regre = "", clase, metod = "";
        dbCon bd = null;
//        boolean corre=false;

        try {

            bd = new dbCon(datos);

            String cusop = (String) bd.inData.get("cusop");
            String cuso = (String) bd.inData.get("cuso");
            String ss = (String) bd.inData.get("ss");
            String rol = (String) bd.inData.get("rol");
            String platf = (String) bd.inData.get("platf");

            String eje = ""
                    + " select "
                    + "   ifnull(casr_ejBk,casr_modulo||'.'||casr_cuso) ejbk "
                    + "  ,ifnull(casr_ldFr,casr_modulo||'.'||casr_cuso) ldFrm  "
                    + "  ,ifnull(casr_ldJS,casr_modulo||'.'||casr_cuso) ldJS  "
                    + "  ,ifnull(casr_nWIN ,0) nWIN "
                    + "  ,casr_modulo  modulo "
                    + "  ,casr_descrip  descrip "
                    + " from t_casrel, (select ? bd)  "
                    + " where casr_cusop= ?"
                    + "   and casr_cuso=? "
                    + "   and casr_platform=? "
                    + "   and ifnull(casr_bdatos,bd) =bd "
                    + "";

            JSONObject jsQry = bd.ejeQry(bd.conxSrv, eje, new String[]{
                bd.bd, "N",
                cusop, "C",
                cuso, "C",
                platf, "N"
            });

            if (((JSONArray) jsQry.get("datos")).length() == 0) {
                throw new Exception("No existe Caso " + cuso);
            };

            String modulo = (String) bd.JSTblgetvCampo(jsQry, 0, "modulo");
            String ejClase = modulo + "." + cuso;

            bd.outData.put("lbCuso", bd.JSTblgetvCampo(jsQry, 0, "descrip"));
            bd.outData.put("modulo", modulo);
            bd.outData.put("ldFrm", (String) bd.JSTblgetvCampo(jsQry, 0, "ldFrm"));
            bd.outData.put("ldJS", (String) bd.JSTblgetvCampo(jsQry, 0, "ldJS"));
            bd.outData.put("nWIN", (int) bd.JSTblgetvCampo(jsQry, 0, "nWIN"));
            bd.outData.put("cusop", cusop);
            bd.outData.put("cuso", cuso);

            clase = (String) bd.JSTblgetvCampo(jsQry, 0, "ejbk");

            eje = ""
                    + " select "
                    + "   casr_modulo modulo "
//                    + "  ,casr_cuso  "
                    + "  ,CASE  "
                    + "     WHEN substr(CASR_CUSO,1,1)='@' THEN cusox "
                    + "	    ELSE casr_cuso "
                    + "	    END as cuso "
                    + "  ,casr_cusop cusop "
                    + "  ,CASE  "
                    + "     WHEN substr(CASR_CUSO,1,1)='@' THEN lcusox "
                    + "	    ELSE CASR_DESCRIP "
                    + "	    END as lcuso "
                    + " from t_casrel c ,(select ? bd)  "
                    + "    left join (select casr_cusop cusopx ,casr_cuso cusox ,casr_descrip lcusox from t_casrel where casr_cusop like '@%') on cusopx=casr_cuso "
                    + " where casr_cusop= ? " //'Docus.lisDocus'--'ini.vaLogin'--'Docus.lisDocus'
                    + "   and casr_platform=? "
                    + "   and ifnull(casr_bdatos,bd) =bd"
                    + "";

            JSONObject jsRel = bd.ejeQry(bd.conxSrv, eje, new String[]{
                bd.bd, "N",
                ejClase, "C",
                platf, "N"
            });

            bd.outData.put("jsRel", jsRel.toString());

            if (!clase.equals("x")) {
                if (!(clase == null || clase.equals(""))) {
                    ejClase = clase;
                }

                String[] cusos = ejClase.split("\\.");

                if (cusos.length == 3) {
                    ejClase = cusos[0] + "." + cusos[1];
                    metod = cusos[2];
                }

                Object vars;
                Constructor ct;
                Method ej;
                Class cVars = Class.forName(ejClase);
                ct = cVars.getConstructor(dbCon.class);
                vars = ct.newInstance(bd);
                if (ejClase != null && ejClase != "" && metod != "") {
                    ej = vars.getClass().getDeclaredMethod(metod);
                    ej.invoke(vars);
                }
            }
//// Identficamos los Cusos Rel de acuerdo al rol
//            eje = ""
//                    + " select "
//                    + "   casr_cuso cuso ,casr_casrel casrel ,casr_ejBk ejBk ,casr_ldFr ejFr  "
//                    + "  ,casr_descrip LCasRel ,casr_rol rol ,casr_scrpt0 scrpt0 ,casr_scrpt1 scrpt1 "
//                    + " from t_casrel "
//                    + " where casr_cuso= ? "
//                    + "   and casr_rol=? "
//                    + "";
//            JSONArray cusRel = bd.pejeQry(0, eje, new String[]{
//                cuso, "C",
//                rol, "N"
//            });
//            bd.outData.put("cusRel", cusRel);

//// Inserta el log Bak            
//            if (ss != "-1") {
//            }
            regre = (String) bd.outData.toString();

            bd.conxSrv.commit();
            // bd.Commit(-1);
        } catch (Exception ex) {
            regre = ex.toString();
            if (regre.contains("reflect.InvocationTargetException")) {
                regre = ex.getCause().toString();
//                regre= regre.replaceAll(" ", "").replaceAll("\r", " ");
            }
            regre = "{\"excep\":\"" + regre.replaceAll("\"", "'").replaceAll(" ", "").replaceAll("\r", " ") + "\"}";

            if (bd != null) {
                bd.conxDat.rollback();
                //bd.rollBack(-1);
            }
        } finally {
            if (bd != null) {
                bd.conxSrv.close();
                bd.conxDat.close();
                // bd.conxClose(-1);
            }

            return regre;
        }

    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

}
