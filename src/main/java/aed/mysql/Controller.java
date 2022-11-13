package aed.mysql;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
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

public class Controller implements Initializable {

    // Declaración de variables para el programa

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

    public Controller() {
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

        // Preparamos las columnas de la tabla
        hotel.setCellValueFactory(new PropertyValueFactory<Estancia, String>("hotel"));
        habitacion.setCellValueFactory(new PropertyValueFactory<Estancia, String>("habitacion"));
        id.setCellValueFactory(new PropertyValueFactory<Estancia, String>("id"));
        cliente.setCellValueFactory(new PropertyValueFactory<Estancia, String>("nombre"));
        fechaEntrada.setCellValueFactory(new PropertyValueFactory<Estancia, String>("fechaInicio"));
        fechaSalida.setCellValueFactory(new PropertyValueFactory<Estancia, String>("fechaFin"));

        // Añadimos listener al selectHotel para que siempre salgan las habitaciones del
        // hotel seleccionado
        selectHotel.setOnAction(e -> getHabitaciones());
        tabla.getSelectionModel().selectedItemProperty().addListener(e -> prepareModify());
        // obtenemos conexion
        try {
            getConnection();
        } catch (ClassNotFoundException | IOException | SQLException e) {
            e.printStackTrace();
        }

        // Bindeamos la tabla al arraylist para que siempre mantengan el mismo valor
        tabla.setItems(lista);
    }

    

    public void add() throws SQLException {
        int idCount;
        if (idField.getText().isEmpty())
            idCount = getIdCount();
        else
            idCount = Integer.parseInt(idField.getText());
        nombre = clienteField.getText();
        codHotel = selectHotel.getValue().toString();
        numHabitacion = selecHab.getValue().toString();
        fechaInicio = fechaINField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        fechaFin = fechaOUTField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        pstat = conn.prepareStatement(
                "INSERT INTO estancias VALUES ((?),(?),(?),(?),(?),(?))");
        pstat.setInt(1, idCount);
        pstat.setString(2, nombre);
        pstat.setString(3, fechaInicio);
        pstat.setString(4, fechaFin);
        pstat.setString(5, numHabitacion);
        pstat.setString(6, codHotel);

        pstat.executeUpdate();
        update();
    }

    public void delete() throws SQLException {
        int idCount = Integer.parseInt(tabla.getSelectionModel().getSelectedItem().getId());
        pstat = conn.prepareStatement(
                "DELETE FROM estancias WHERE id=?");
        pstat.setInt(1, idCount);
        pstat.executeUpdate();
        update();
    }

    private void prepareModify() {
        try {
            
        
        Estancia e = tabla.getSelectionModel().getSelectedItem();
        selectHotel.setValue(e.getHotel());
        selecHab.setValue(e.getHabitacion());
        idField.setText(e.getId());
        clienteField.setText(e.getNombre());
        fechaINField.setValue(LocalDate.parse(e.getFechaInicio()));
        fechaOUTField.setValue(LocalDate.parse(e.getFechaFin()));
    } catch (NullPointerException e) {
    }
    }

    public void modify() throws SQLException{
        idCliente = idField.getText();
        nombre = clienteField.getText();
        codHotel = selectHotel.getValue().toString();
        numHabitacion = selecHab.getValue().toString();
        fechaInicio = fechaINField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        fechaFin = fechaOUTField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        pstat = conn.prepareStatement(
                "UPDATE estancias SET codHotel=(?), numHabitacion=(?), nombre=(?), fechaInicio=(?), fechaFin=(?) where id=(?)"
        );
        pstat.setString(3, nombre);
        pstat.setString(4, fechaInicio);
        pstat.setString(5, fechaFin);
        pstat.setString(2, numHabitacion);
        pstat.setString(1, codHotel);
        pstat.setString(6, idCliente);

        pstat.executeUpdate();
        update();
    }

    private void getConnection() throws ClassNotFoundException, SQLException, IOException {
        Properties p = new Properties();
        p.load(getClass().getResourceAsStream("/sql/conexion.props"));
        Class.forName(p.getProperty("driver"));
        conn = DriverManager.getConnection(p.getProperty("url"), p.getProperty("user"), p.getProperty("pass"));
        update();
    }

    private void update() {
        selectHotel.setValue(null);
        clienteField.setText("");
        idField.setText("");
        selecHab.setValue(null);
        fechaINField.setValue(null);
        fechaOUTField.setValue(null);
        lista.clear();
        try {
            // Me creo la conexión con la base de datos

            stat = conn.createStatement();
            rs = stat.executeQuery(
                    "SELECT * FROM estancias order by codHotel, id");

            while (rs.next()) {
                ObservableList<String> items = selectHotel.getItems();
                if (!items.contains(rs.getString("codHotel")))
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

    private void getHabitaciones() {
        try{
        String hotelSeleccionado = selectHotel.getValue().toString();
        selecHab.setValue(null);
        selecHab.getItems().clear();
            rs = stat.executeQuery(
                    String.format("SELECT numHabitacion FROM habitaciones WHERE codHotel='%s'", hotelSeleccionado));
            while (rs.next()) {
                selecHab.getItems().add(rs.getString("numHabitacion"));
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
        }
    }

    private int getIdCount() {
        int cont = 0;
        for (Estancia estancia : lista) {
            if (Integer.parseInt(estancia.getId()) > cont)
                cont = Integer.parseInt(estancia.getId());
        }

        return cont + 1;
    }

    public GridPane getView() {
        return view;
    }

}
