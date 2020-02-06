/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
//import main.datConx;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author adolfoyoyo
 */
public class dbCon {

    private Object[] conex;
    public String excep = "";
    public int wait = 60;
    public String params;

//--- Nuevos
    public JSONObject inData;
    public JSONObject outData = new JSONObject();
    public String cuso;
    public String cusorg;
    public String sid;
    public String bd = "0"; // 0=Postgres   1=Oracle    2=Sqlite
//    public int mxRegs= 10 ; // Registros por fetch (0 es sin limite)
    public Connection conxDat;
    public Connection conxSrv;

    public dbCon(String datos) throws Exception {

// Conectamos a base de datos de         
        inData = new JSONObject(datos);
        String URL = datConx.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String bdatos = "dbsql-01.db";
        String strCon = "jdbc:sqlite:" + URL + bdatos;
        Class.forName("org.sqlite.JDBC");
        conxSrv = DriverManager.getConnection(strCon);
        conxSrv.setAutoCommit(false);

        String eje = ""
                + " select "
                + "   bdat_bdatos bdatos ,bdat_jdbc jdbc ,bdat_usr usr ,bdat_pswd paswd ,bdat_url url ,bdat_driver driver"
                + " from t_bdatos "
                + " where bdat_num=? "
                + "";

        JSONObject dat = ejeQry(conxSrv, eje, new String[]{bd, "N"});
// Identificamos a base de datos de producción
        String user = (String) JSTblgetvCampo(dat, 0, "usr");
        String paswd = (String) JSTblgetvCampo(dat, 0, "paswd");
        String drive = (String) JSTblgetvCampo(dat, 0, "driver");
        strCon = "jdbc:" + (String) JSTblgetvCampo(dat, 0, "jdbc")
                + ":" + (String) JSTblgetvCampo(dat, 0, "url")
                + (String) JSTblgetvCampo(dat, 0, "bdatos");
        Class.forName(drive);

        if (drive.contains("oracle")) {
//            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        }

        conxDat = DriverManager.getConnection(strCon, user, paswd);

//        strCon = "jdbc:" + gStringValue(dat, "jdbc") + ":" + gStringValue(dat, "url") + "/" + gStringValue(dat, "bdatos");
//        conxDat = DriverManager.getConnection(strCon, user, paswd);
//        conxDat.setAutoCommit(false);
    }

    public void Commit(int numConx) throws Exception {
        if (numConx == -1) {
            Connection.class.cast(conex[0]).commit();
            Connection.class.cast(conex[1]).commit();
        } else {
            Connection.class.cast(conex[numConx]).commit();
        }
    }

    public void Commit() throws Exception {
        Connection.class.cast(conex[1]).commit();
    }

    public void rollBack(int numConx) throws Exception {
        if (numConx == -1) {
            if (Connection.class.cast(conex[0]) != null) {
                Connection.class.cast(conex[0]).rollback();
            }

            if (Connection.class.cast(conex[1]) != null) {
                Connection.class.cast(conex[1]).rollback();
            }
        } else if (Connection.class.cast(numConx) != null) {
            Connection.class.cast(conex[numConx]).rollback();
        }
    }

    public void rollBack() throws Exception {
        if (Connection.class.cast(1) != null) {
            Connection.class.cast(conex[1]).rollback();
        }
    }

    public void conxClose(int numConx) throws Exception {

        if (numConx == -1) {
            Connection x = (Connection) conex[0];
            Connection y = (Connection) conex[1];

            if (x != null) {
                x.close();
                //Connection.class.cast(conex[0]).close();
            }
//            if (Connection.class.cast(1) != null) {
            if (y != null) {
                y.close();
//                Connection.class.cast(conex[1]).close();                
            }
        } else if (Connection.class.cast(numConx) != null) {
            Connection.class.cast(conex[numConx]).close();
        }
    }

    public void conxClose() throws Exception {
        Connection.class.cast(conex[1]).rollback();
    }

    ///// A que conexión por numero
    public JSONArray pejeQry(int numConx, String eje, String[] flds) throws Exception {
        return pejeQry(eje, flds, 0, 10, Connection.class.cast(conex[numConx]));
    }

    public JSONArray pejeQry(String eje, Connection conx, String[] flds) throws Exception {
        return pejeQry(eje, flds, 0, 10, conx);
    }

    //// Con numero de Registros
//    public JSONArray pejeQry(String eje, String[] flds, int mxRegs, int numConx) throws Exception {        
//        return pejeQry(eje, flds, mxRegs, 10, numConx );
//    }
    public JSONArray pejeQry(String eje, String[] flds, int mxRegs, int wait, Connection tconx) throws Exception {

        //Connection tconx = Connection.class.cast(conex[numConx]);
        JSONArray tabla = new JSONArray();
        JSONArray data = new JSONArray();
        JSONArray meta = new JSONArray();

        JSONArray mHead = new JSONArray();
        JSONArray mColName = new JSONArray();
        JSONArray mColTipo = new JSONArray();
        JSONArray regis;

//        ArrayList regis;
        PreparedStatement ps = tconx.prepareStatement(eje);
        ponParam(ps, flds);
        ps.setQueryTimeout(wait);
        ps.setMaxRows(mxRegs);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        //mHead.put(cols);
/// Nombres
        //metaRegis = new JSONArray();
        for (int i = 0; i < cols; i++) {
            mColName.put(rsmd.getColumnName(i + 1));
            mColTipo.put(rsmd.getColumnTypeName(i + 1));
        }

        meta.put(mColName);
        meta.put(mColTipo);

//rsmd.getColumnType(cols);
        while (rs.next()) {
            Object val;
            regis = new JSONArray();

            for (int i = 0; i < cols; i++) {
                val = rs.getObject(i + 1);

                //regis.put(val);
                regis.put((val == null ? "" : val));
            }
            data.put(regis);
        }
        ps.close();
        tabla.put(meta);
        tabla.put(data);
        return tabla;
    }

    public JSONObject ejeQry(Connection conx, String eje, String[] flds) throws Exception {
        return ejeQry(conx, wait, 0, eje, flds);
    }

    //// A BDatos 1
    public JSONObject ejeQry(int mxRegs, String eje, String[] flds) throws Exception {
        return ejeQry(conxDat, wait, mxRegs, eje, flds);
    }

    //// Con numero de Registros
//    public JSONArray pejeQry(String eje, String[] flds, int mxRegs, int numConx) throws Exception {        
//        return pejeQry(eje, flds, mxRegs, 10, numConx );
//    }
    public JSONObject ejeQry(Connection tconx, int espera, int mxRegs, String eje, String[] flds) throws Exception {
        PreparedStatement ps = tconx.prepareStatement(eje);
        ponParam(ps, flds);
        ps.setQueryTimeout(wait);
//        ps.setMaxRows(mxRegs);

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        JSONObject JSTable = new JSONObject();

        String[] aCols = new String[cols];
        String[] aTCols = new String[cols];

//        String[] aRengs;
//        String[] aDatos;
        for (int i = 0; i < cols; i++) {
            aCols[i] = rsmd.getColumnName(i + 1);
            aTCols[i] = rsmd.getColumnTypeName(i + 1);
//            cName = cName + coma + "\"" + rsmd.getColumnName(i + 1) + "\"";
//            cTipo = cTipo + coma + "\"" + rsmd.getColumnTypeName(i + 1) + "\"";
        }

        JSTable.put("cols", aCols);
        JSTable.put("tCols", aTCols);

        int regis = 0;
        ArrayList aDatos = new ArrayList();

        while (rs.next()) {
            regis++;
            //Object val;
            Object[] aRegis = new Object[cols];
            for (int i = 0; i < cols; i++) {
                aRegis[i] = rs.getObject(i + 1);
                //aRegis.add(val);
            }
            aDatos.add(aRegis);
        }
        JSTable.put("datos", aDatos);
        //JSTable.put("nRegis", regis);
        return JSTable;
    }

    public int ejePs(String eje, String[] flds) throws Exception {
        return ejePs(eje, flds, conxDat);
    }

    public int ejePs(String eje, String[] flds, Connection tconx) throws Exception {
        PreparedStatement ps = tconx.prepareStatement(eje);
        ponParam(ps, flds);

        int regre = ps.executeUpdate();
        ps.close();
        return regre;
    }

//        public int ejePs(String eje, String[] flds) throws Exception {
//        return ejePs(eje, flds, 1);
//    }
//    public JSONObject ejeQry(Connection tconx, int mxRegs, int espera, String eje, String[] flds) throws Exception {
//
//        String strJSON;
//
//        PreparedStatement ps = tconx.prepareStatement(eje);
//        ponParam(ps, flds);
//        ps.setQueryTimeout(wait);
//        //ps.setMaxRows(mxRegs);
//
//        ResultSet rs = ps.executeQuery();
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int cols = rsmd.getColumnCount();
//        strJSON = "{";
//
///// Nombres
//        String cName = "", cTipo = "", coma = "", cDato = "";
//        for (int i = 0; i < cols; i++) {
//            coma = ",";
//            if (i == 0) {
//                coma = "";
//            }
//            cName = cName + coma + "\"" + rsmd.getColumnName(i + 1) + "\"";
//            cTipo = cTipo + coma + "\"" + rsmd.getColumnTypeName(i + 1) + "\"";
//        }
//        strJSON = strJSON + "\"cols\":[" + cName + "]\n ,\"tipos\":[" + cTipo + "]\n,\"datos\": [\n";
////
//
//////rsmd.getColumnType(cols);
//        String comaR;
//        int regis = 0;
//        while (rs.next()) {
//            comaR = ",";
//            if (regis == 0) {
//                comaR = "";
//            }
//
//            regis++;
//
//            Object val;
//
////<--            strJSON = strJSON + comaR + "\t{";
//            strJSON = strJSON + comaR + "\t["; // -->
//
//            for (int i = 0; i < cols; i++) {
//                coma = ",";
//                if (i == 0) {
//                    coma = "";
//                }
//
//                val = rs.getObject(i + 1);
//                if (val != null && val.toString().contains(("\""))) {
//                    val = val.toString().replace("\"", "\\\"");
//                }
//                strJSON += coma + "\"" + val + "\"";  //-->
//            }
//            strJSON += "]\n ";
//        }
//        strJSON = strJSON + "] \n }";
//        return new JSONObject(strJSON);
//    }
//    public int ejePs(String eje, String[] flds) throws Exception {
//        return ejePs(eje, flds, 1);
//    }
//
//    public int ejePs(String eje, String[] flds, int numConx) throws Exception {
//        Connection tconx = Connection.class.cast(conex[numConx]);
//        PreparedStatement ps = tconx.prepareStatement(eje);
//        ponParam(ps, flds);
//
//        int regre = ps.executeUpdate();
//        ps.close();
//        return regre;
//    }
    public void ponParam(PreparedStatement ps, String[] flds) throws Exception {
        int ix;
        String Fld, Tipo;
        for (int i = 0; i < flds.length; i++) {
            Fld = flds[i];
            i++;
            Tipo = flds[i];
            ix = (i + 1) / 2;
            if (Tipo.equals("I")) {
                if (Fld.equals("")) {
                    ps.setNull(ix, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(ix, Integer.parseInt(Fld));
                }
            }
            if (Tipo.equals("C")) {
                if (Fld.equals("")) {
                    ps.setNull(ix, java.sql.Types.VARCHAR);
                } else {
                    Fld = Fld.replace("CONEXION", "CONEXIÓN").replace("conexion", "conexión").replace("Conexion", "Conexión");
                    ps.setString(ix, Fld);
//                    ps.setString(ix, Fld);
                }
            }
            if (Tipo.equals("N")) {
                if (Fld.equals("")) {
                    ps.setNull(ix, java.sql.Types.DOUBLE);
                } else {
                    ps.setDouble(ix, Double.parseDouble(Fld.replace("$", "").replace(",", "").replace("%", "")));
                }
            }
            if (Tipo.equals("F")) {
                if (Fld.equals("")) {
                    ps.setNull(ix, java.sql.Types.DATE);
                } else {
                    ps.setDate(ix, java.sql.Date.valueOf(Fld.substring(6, 10) + "-" + Fld.substring(3, 5) + "-" + Fld.substring(0, 2)));
                }
            }
        }

    }

    public int gNumRows(JSONArray tabla) throws Exception {
        return ((JSONArray) (JSONArray) tabla.get(1)).length();
    }

    public String gStringValue(JSONArray tabla, int numFld) throws Exception {
        return ((JSONArray) ((JSONArray) tabla.get(1)).get(0)).getString(numFld);
    }

    public Object gValue(JSONArray tabla, int numFld) throws Exception {
        return ((JSONArray) ((JSONArray) tabla.get(1)).get(0)).get(numFld);
    }

    public String gStringValue(JSONArray tabla, String nomFld) throws Exception {
        int numFld = -1;
        int i;
        String cc;
        for (i = 0; i < ((JSONArray) ((JSONArray) tabla.get(0)).get(0)).length(); i++) {
            cc = ((JSONArray) ((JSONArray) tabla.get(0)).get(0)).getString(i);
            if (cc.equals(nomFld)) {
                numFld = i;
                break;
            }
        }
        if (numFld == -1) {
            throw new Exception("No existe Campo " + nomFld);
        }

        return gStringValue(tabla, numFld);
    }

    public int JSTblgetNCampo(JSONObject tabla, String nCampo) throws Exception {
        int reg = -1;
        String[] nCampos = (String[]) tabla.get("cols");
        //Arrays.asList(nCampos).forEach();        

        for (reg = 0; reg < nCampos.length; reg++) {
            if (nCampos[reg].equals(nCampo)) {
                break;
            }
        }
        return (reg == nCampos.length) ? -1 : reg;
    }

    public Object JSTblgetvCampo(JSONObject tabla, int regNum, String nCampo) throws Exception {
        return JSTblgetvCampo(tabla, regNum, JSTblgetNCampo(tabla, nCampo));
    }

    public Object JSTblgetvCampo(JSONObject tabla, int regNum, int nCampo) throws Exception {
        return (((JSONArray) tabla.get("datos")).getJSONArray(regNum)).get(nCampo);
    }

//    private Connection ConexionBD(String URL, String User, String Pass, String DB, String Puerto) {
//        try {
//            String BaseDeDatos = "";
//
//            if (TipoBD == TipoBaseDatos.Oracle) {
//                Class.forName("oracle.jdbc.OracleDriver");
//                BaseDeDatos = "jdbc:oracle:thin:@" + URL + ":" + Puerto + ":" + DB + "";
//            }
//
//            if (TipoBD == TipoBaseDatos.Mysql) {
//                Class.forName("com.mysql.jdbc.Driver");
//                BaseDeDatos = "jdbc:mysql://" + URL + ":" + Puerto + "/" + DB;
//            }
//
//            if (TipoBD == TipoBaseDatos.Sql) {
//                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                BaseDeDatos = "jdbc:sqlserver://" + URL + ":" + Puerto + ";databaseName=" + DB;
//            }
//
//            if (TipoBD == TipoBaseDatos.Postgresql) {
//                Class.forName("org.postgresql.Driver");
//                BaseDeDatos = "jdbc:postgresql://" + URL + ":" + Puerto + "/" + DB;
//            }
//
//            Connection conexion = DriverManager.getConnection(BaseDeDatos, User, Pass);
//
//            conexion.setAutoCommit(AutoCommit);
//
//            return conexion;
//
//        } catch (Exception ex) {
//            return null;
//        }
//    }
}
