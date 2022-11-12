package aed.mysql;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
    private TextField clienteField, habitacionField;

    @FXML
    private DatePicker fechaINField, fechaOUTField;

    @FXML
    private ChoiceBox<String> selectHotel;

    private Statement stat;
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
        update();
        tabla.setItems(lista);
    }
    
    public void add() throws SQLException{
        int idCount = getIdCount();
        nombre = clienteField.getText();
        codHotel = selectHotel.getValue().toString();
        numHabitacion = habitacionField.getText();
        fechaInicio = fechaINField.getValue().toString();
        fechaFin = fechaOUTField.getValue().toString();  

        rs = stat.executeQuery(
                String.format("INSERT INTO estancias values (%d,%s,%s,%s,%s,%s)", idCount, nombre, fechaInicio, fechaFin, numHabitacion, codHotel)
            );

        rs.close();
        update();
    }

    public void delete() throws SQLException{
        int idCount = getIdCount();
        nombre = clienteField.getText();
        codHotel = selectHotel.getValue().toString();
        numHabitacion = habitacionField.getText();
        fechaInicio = fechaINField.getValue().toString();
        fechaFin = fechaOUTField.getValue().toString();  

        rs = stat.executeQuery(
                String.format("DELETE FROM estancias WHERE id=%d", idCount)
            );

        rs.close();
        update();
    }

    private void update(){
        lista.clear();
        try {
            //Me creo la conexión con la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/central_reservas","root","");
            stat = conn.createStatement();
            rs = stat.executeQuery(
                "SELECT * FROM estancias"
            );

            while (rs.next()) {
                if(rs.getString("codHotel")!=codHotel)
                    selectHotel.getItems().add(rs.getString("codHotel"));

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

    private int getIdCount() {
        return Integer.parseInt(lista.get(lista.size()-1).getId()) + 1;
    }

    public GridPane getView(){
        return view;
    }


}
