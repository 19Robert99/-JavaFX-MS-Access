/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;
import javax.swing.JOptionPane;

/**
 *
 * @author robert.talabishka
 */
public class FXMLDocumentController implements Initializable {

    private static Statement st;
    private static ResultSet rs;

    @FXML
    private Label count, bd;
    @FXML
    private TextArea selectText;

    private String[] list;
    private String[] fwords;
    private final ArrayList<String> out = new ArrayList<>();

    private final HashSet<String> HashSet = new HashSet<>();
    private final HashSet<String> serchwr = new HashSet<>();
    private String text = null, str = "", line, path = null, txtpath, rep;
    private File txtfile, dbfile;
    private boolean flag = true;

    @FXML
    private void search(ActionEvent event) {
        /*Определяем какой метод получения данных был использован.
          Выделяем найденные слова.
        */
        if(path!=null){
        if (flag) {
            serchwr.clear();
            text = selectText.getText();
            for (int i = 0, k = 0; i < list.length; i++) {
                Pattern regex = Pattern.compile(list[i]);
                if (regex.matcher(text).find()) // совпадение найдено
                {
                    k++;
                    serchwr.add(list[i]);
                    count.setText(Integer.toString(k));
                }
            }
            fwords = serchwr.toArray(new String[serchwr.size()]);
            System.out.println(serchwr);
            for (int i = 0; i < list.length; i++) {
                Pattern regex = Pattern.compile(list[i]);
                if (regex.matcher(text).find()) // совпадение найдено
                {
                    text = text.replace(list[i], " /" + list[i] + "/ ");
                }
            }
            selectText.clear();
            selectText.appendText(text);
        }
        if (!flag) {
            serchwr.clear();
            text = selectText.getText();
            for (int i = 0, k = 0; i < list.length; i++) {
                Pattern regex = Pattern.compile(list[i]);
                if (regex.matcher(text).find()) // совпадение найдено
                {   
                    serchwr.add(list[i]);
                    k++;
                    count.setText(Integer.toString(k));
                    text = text.replace(list[i], " /" + list[i] + "/ ");
                }
            }
            fwords = serchwr.toArray(new String[serchwr.size()]);
            System.out.println(serchwr);
            selectText.clear();
            selectText.appendText(text);

        }
        }else JOptionPane.showMessageDialog(null, "Выберите базу данных", null, JOptionPane.OK_CANCEL_OPTION);

    }
    //Считываем данные из текстового файла  и задаем кодировку 
    @FXML
    private void view(ActionEvent event) {
        serchwr.clear();
        selectText.clear();
        FileChooser txt = new FileChooser();
        txt.setTitle("Open Text File");
        txtfile = txt.showOpenDialog(null);
        txtpath = txtfile.getAbsolutePath();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(txtpath), "Cp1251"))) {

            while ((line = reader.readLine()) != null) {
                out.add(line);
                selectText.appendText(line+"\n");
            }
        } catch (IOException ex) {
        }
        for (int i = 0; i < out.size(); i++) {
            str += out.get(i);
        }
        
        flag = false;
    }
    //Выбор базы данных
    @FXML
    private void dbconfig(ActionEvent event) {
        FileChooser dbfil = new FileChooser();
        dbfil.setTitle("Open DB File");
        dbfile = dbfil.showOpenDialog(null);
        path = dbfile.getAbsolutePath();
        bd.setText(path);
        selBsWord();
    }
    //Подключение к базе данных и получение слов
    public void selBsWord() {
        try {

            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            Properties connInfo = new Properties();
            connInfo.put("user", "");
            connInfo.put("password", "");
            connInfo.put("charSet", "Cp1251");
            String db = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + path;//E:/mydb.accdb
            try (Connection conn = DriverManager.getConnection(db, connInfo)) {
                st = conn.createStatement();

                rs = st.executeQuery("SELECT * FROM [Основа слова]");
                while (rs.next()) {
                    HashSet.add(rs.getString(2));

                }
                rs.close();
                rs = st.executeQuery("SELECT * FROM [Сленговые слова]");
                while (rs.next()) {
                    HashSet.add(rs.getString(2));
                }
                rs.close();
                conn.close();
                list = HashSet.toArray(new String[HashSet.size()]);

            }
        } catch (ClassNotFoundException | SQLException sqlex) {
            System.out.println(sqlex);
        }

    }
    
    //Переход на форму вывода с передачей параметров
    @FXML
    private void output(ActionEvent event) throws IOException {
        Stage primaryStage = new Stage();
        primaryStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("out.fxml"));
        Parent root = (Parent) loader.load();
        primaryStage.setScene(new Scene(root));
        OutController controllerEditBook = loader.getController(); //получаем контроллер для второй формы
        controllerEditBook.select(fwords, path); // передаем необходимые параметры
        primaryStage.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
