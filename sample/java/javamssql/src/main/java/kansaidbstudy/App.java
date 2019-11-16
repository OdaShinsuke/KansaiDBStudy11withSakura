package kansaidbstudy;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.data.Row;
import org.sql2o.data.Table;
import org.sql2o.quirks.NoQuirks;

/**
 * Hello world!
 *
 */
public class App {
    static final String ConnectionString = "jdbc:sqlserver://<servername>\\SQLEXPRESS;databaseName=SampleDB;user=<user>;password=<password>;";
    public static void main( String[] args ) {
        標準実装();
        useSql2o();
        useSql2oWithResultClass();
    }

    static void 標準実装() {
        System.out.println("標準実装");
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(ConnectionString);

        try (Connection con = ds.getConnection();
            PreparedStatement cstmt = con.prepareStatement("select [受注日], [金額] from [受注] where [受注日] < ?");) {
            cstmt.setDate(1, Date.valueOf(LocalDate.of(2019, 9, 1)));

            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                System.out.println("受注日:" + rs.getDate("受注日") + "金額:" + rs.getInt("金額"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void useSql2o() {
        System.out.println("useSql2o");
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(ConnectionString);
        Sql2o sql2o = new Sql2o(ds);
        
        try (org.sql2o.Connection con = sql2o.open()) {
            Table t = con.createQuery("select [受注日], [金額] from [受注] where [受注日] < :p1")
                .addParameter("p1", Date.valueOf(LocalDate.of(2019, 9, 1)))
                .executeAndFetchTable();
            for (Row r : t.rows()) {
                System.out.println("受注日:" + r.getDate("受注日") + "金額:" + r.getInteger("金額"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void useSql2oWithResultClass() {
        System.out.println("useSql2oWithResultClass");
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setURL(ConnectionString);
        Map<Class, Converter> converterMap = new HashMap<>();
        converterMap.put(LocalDate.class, new LocalDateConverter());
        Sql2o sql2o = new Sql2o(ds, new NoQuirks(converterMap));
        
        try (org.sql2o.Connection con = sql2o.open()) {
            List<受注> results = con.createQuery("select [受注日], [金額] from [受注] where [受注日] < :p1")
                .addParameter("p1", LocalDate.of(2019, 9, 1))
                .executeAndFetch(受注.class);
            for (受注 r : results) {
                System.out.println("受注日:" + r.受注日 + "金額:" + r.金額);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class 受注 {
        public LocalDate 受注日;
        public int 金額;
    }
}
