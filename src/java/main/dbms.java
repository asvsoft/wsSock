/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.sql.*;
import java.util.HashMap;

/**
 *
 * @author adolfoyoyo
 */
public class dbms {

    public dbCon BD;

    public dbms(dbCon bd) throws Exception {
        BD = bd;
    }

    //public shkTable pejeQry(String eje, String[] flds, int mxRegs ) throws Exception {            
    //}


/*    
    public shkTable pejeQry(String eje, String[] flds) throws Exception {
        return pejeQry(eje, flds, -1, BD.wait);
    }

    public shkTable pejeQry(String eje, String[] flds, int mxRegs) throws Exception {
        return pejeQry(eje, flds, mxRegs ,-1);
    }
    
    public shkTable pejeQry(String eje, String[] flds, int mxRegs, int espera) throws Exception {
        shkTable regre = null;

        int regis = -1;
        boolean inserta = false;
        PreparedStatement ps = BD.conx.prepareStatement(eje);
        ponParam(ps, flds);
        
        //ps.setQueryTimeout(espera);
        
        if (mxRegs != -1) {
            ps.setMaxRows(mxRegs);
        }

        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        //regre = new shkTable(rsmd.getColumnCount());

// Apuntamos los datos de las Columnas
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            regre.sNameCol(i, rsmd.getColumnName(i + 1));
            regre.tipCols[i] = rsmd.getColumnTypeName(i + 1);
            regre.tamCols[i] = String.valueOf(rsmd.getColumnDisplaySize(i + 1));
        }

//// Escribimos los datos
        int nRegis = regis;
        while (rs.next() && nRegis != 0) {
            if (inserta) {
                regre.ins();
            } else {
                regre.add();
            }
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
//                    regre.regAct.datos[i]= rs.getObject(i+1) ;
                Object n = rs.getObject(i + 1);
                //n = new serializa(n).gSerializa();
                regre.regAct.datos[i] = n;
            }

            nRegis--;
        }

        ps.close();
        return regre;

    }
*/

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

}
