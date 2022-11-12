package aed.mysql;
import java.sql.*;
public class pruebaSQL {
    public static void main(String[] args) {
          
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/central_reservas", "root","");
            Statement stat = conn.createStatement();

            ResultSet rs = stat.executeQuery(
                "SELECT * FROM estancias"
            );

            while (rs.next()) {
                String cliente = rs.getString("nombre");
                String id = rs.getString("id");
                String fechaInit = rs.getString("fechaInicio");
                String fechaFin = rs.getString("fechaFin");
                String codHotel = rs.getString("codHotel");
                String numHabitacion = rs.getString("numHabitacion");

                System.out.printf("Cliente %s:\t%s ocupó la habitación %s del hotel %s de %s a %s\n",id,cliente,numHabitacion,codHotel,fechaInit,fechaFin);
            }

            rs.close();
            stat.close();
            conn.close();


        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
