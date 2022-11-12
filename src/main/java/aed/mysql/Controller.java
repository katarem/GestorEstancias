package aed.mysql;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class Controller implements Initializable{

    //Declaración de variables para el programa

    @FXML
    private GridPane view;

    @FXML
    private TableView<Estancia> tabla;

    @FXML
    private TableColumn<Estancia, String> hotel, habitacion, id, cliente, fechaEntrada, fechaSalida;

    @FXML
    private TextField clienteField, idField;

    @FXML
    private DatePicker fechaINField, fechaOUTField;

    @FXML
    private ChoiceBox<String> selectHotel, selecHab;

    private Statement stat;
    private PreparedStatement pstat;
    private Connection conn;
    private ResultSet rs;

    private String nombre, idCliente, codHotel, numHabitacion, fechaInicio, fechaFin;


    ObservableList<Estancia> lista = FXCollections.observableArrayList();

    public Controller(){
        try {
        FXMLLoader f = new FXMLLoader(getClass().getResource("/interfaz.fxml"));
        f.setController(this);
        f.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        
        //Preparamos las columnas de la tabla
        hotel.setCellValueFactory(new PropertyValueFactory<Estancia, String>("hotel"));
        habitacion.setCellValueFactory(new PropertyValueFactory<Estancia, String>("habitacion"));
        id.setCellValueFactory(new PropertyValueFactory<Estancia, String>("id"));
        cliente.setCellValueFactory(new PropertyValueFactory<Estancia, String>("nombre"));
        fechaEntrada.setCellValueFactory(new PropertyValueFactory<Estancia, String>("fechaInicio"));
        fechaSalida.setCellValueFactory(new PropertyValueFactory<Estancia, String>("fechaFin"));
        
        //Añadimos listener al selectHotel para que siempre salgan las habitaciones del hotel seleccionado
        selectHotel.setOnAction(e -> getHabitaciones());

        //Actualizamos la tabla en el arraylist
        update();

        //Bindeamos la tabla al arraylist para que siempre mantengan el mismo valor
        tabla.setItems(lista);
    }
    
    public void add() throws SQLException{
        int idCount;
        if(idField.getText().isEmpty())
            idCount = getIdCount();
        else
            idCount = Integer.parseInt(idField.getText());
        nombre = clienteField.getText();
        codHotel = selectHotel.getValue().toString();
        numHabitacion = selecHab.getValue();
        fechaInicio = fechaINField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        fechaFin = fechaOUTField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));  

        pstat = conn.prepareStatement(
                "INSERT INTO estancias VALUES ((?),(?),(?),(?),(?),(?))"
                );
        pstat.setInt(1, idCount);
        pstat.setString(2, nombre);
        pstat.setString(3, fechaInicio);
        pstat.setString(4, fechaFin);
        pstat.setString(5, numHabitacion);
        pstat.setString(6, codHotel);
        
        pstat.executeUpdate();
        update();
    }

    public void delete() throws SQLException{
        int idCount = Integer.parseInt(idField.getText());
        pstat = conn.prepareStatement(
                "DELETE FROM estancias WHERE id=?"
            );
        pstat.setInt(1, idCount);
        pstat.executeUpdate();
        update();
    }

    private void update(){
        selectHotel.setValue(null);
        clienteField.setText("");
        idField.setText("");
        selecHab.setValue(null);
        fechaINField.setValue(null);
        fechaOUTField.setValue(null);
        lista.clear();
        try {
            //Me creo la conexión con la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/central_reservas","root","");
            stat = conn.createStatement();
            rs = stat.executeQuery(
                "SELECT * FROM estancias order by codHotel, id"
            );

            while (rs.next()) {
                ObservableList<String> items = selectHotel.getItems();
                if(!items.contains(rs.getString("codHotel")))
                    selectHotel.getItems().addAll(rs.getString("codHotel"));
                nombre = rs.getString("nombre");
                idCliente = rs.getString("id");
                codHotel = rs.getString("codHotel");
                numHabitacion = rs.getString("numHabitacion");
                fechaInicio = rs.getString("fechaInicio");
                fechaFin = rs.getString("fechaFin");
                lista.add(new Estancia(codHotel, nombre, numHabitacion, fechaInicio, fechaFin, idCliente));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getHabitaciones(){
        String hotelSeleccionado = selectHotel.getValue().toLowerCase();
        try {
            rs = stat.executeQuery(
                String.format("SELECT numHabitacion FROM habitaciones WHERE codHotel='%s'",hotelSeleccionado));
                while (rs.next()) {
                    selectHotel.getItems().addAll(rs.getString("numHabitacion"));
                    System.out.println(rs.getString("numHabitacion"));
                }
                rs.close();
        
            } catch (SQLException e) {
            e.printStackTrace();
        }

        
}

    private int getIdCount() {
        int cont = 0;
        for (Estancia estancia : lista) {
            if(Integer.parseInt(estancia.getId())>cont)
                cont=Integer.parseInt(estancia.getId());
        }

        return cont + 1;
    }

    public GridPane getView(){
        return view;
    }


}
