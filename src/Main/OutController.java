/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author robert.talabishka
 */
public class OutController implements Initializable {

    private static Statement st;
    private static ResultSet rs;
    @FXML
    private TextArea TextField, meth;
    private final ArrayList<String> methods = new ArrayList<>();
    private final HashSet<String> selectmth = new HashSet<>();
    private final ArrayList<String> test = new ArrayList<>();
    private final ArrayList<String> mtest = new ArrayList<>();
    private String[] hashMeth;
    private int j = 1;
    private String gettxt = null;

    public void select(String[] list, String path) {
        try {

            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            Properties connInfo = new Properties();
            connInfo.put("user", "");
            connInfo.put("password", "");
            connInfo.put("charSet", "Cp1251");
            String db = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ=" + path;//E:/mydb.accdb
            try (Connection conn = DriverManager.getConnection(db, connInfo)) {
                st = conn.createStatement();

                //Получаем способы образования
                for (int i = 0; i < list.length; i++) {
                    rs = st.executeQuery("SELECT * FROM [Способы образования]");
                    while (rs.next()) {
                        methods.add(rs.getString(2));
                    }
                }
                //Получаем описание слов из таблицы Сленговые слова
                for (int i = 0; i < list.length; i++) {
                    rs = st.executeQuery("SELECT * FROM (((((Словарь\n"
                            + "INNER JOIN [Основа слова] ON Словарь.[Основа слова] = [Основа слова].Код)\n"
                            + "INNER JOIN [Деффиниция слова] ON Словарь.Деффиниция = [Деффиниция слова].Код)\n"
                            + "INNER JOIN [Способы образования] ON Словарь.[Способ образования] = [Способы образования].Код)\n"
                            + "INNER JOIN [Вид сленга] ON Словарь.[Вид сленга] = [Вид сленга].Код)\n"
                            + "INNER JOIN [Оригинальное слово] ON Словарь.[Оригинальное слово] = [Оригинальное слово].Код)\n"
                            + "INNER JOIN [Сленговые слова] ON Словарь.Слово = [Сленговые слова].Код\n"
                            + "WHERE [Сленговые слова].Слово = '" + list[i] + "'");
                    while (rs.next()) {
                        test.add(list[i]);
                        TextField.appendText(j + ") Слово: " + list[i] + ", Деффиниция: " + rs.getString(11) + "\n"
                                + "Способ образования: " + rs.getString(13) + "\n"
                                + "Вид сленга :" + rs.getString(15) + "\n"
                                + "Оригинальное слово: " + rs.getString(17) + "\n");
                        j++;
                        break;
                    }
                }
                //Убераем слова к которым уже полученно описание
                for (int i = 0; i < list.length; i++) {
                    mtest.add(list[i]);
                }
                for (int i = 0; i < test.size(); i++) {
                    if (mtest.contains(test.get(i))) {
                        mtest.remove(test.get(i));
                    }
                }
                //Получаем описание слов из таблицы Основа слова
                for (int i = 0; i < mtest.size(); i++) {

                    rs = st.executeQuery("SELECT * FROM (((((Словарь\n"
                            + "INNER JOIN [Основа слова] ON Словарь.[Основа слова] = [Основа слова].Код)\n"
                            + "INNER JOIN [Деффиниция слова] ON Словарь.Деффиниция = [Деффиниция слова].Код)\n"
                            + "INNER JOIN [Способы образования] ON Словарь.[Способ образования] = [Способы образования].Код)\n"
                            + "INNER JOIN [Вид сленга] ON Словарь.[Вид сленга] = [Вид сленга].Код)\n"
                            + "INNER JOIN [Оригинальное слово] ON Словарь.[Оригинальное слово] = [Оригинальное слово].Код)\n"
                            + "INNER JOIN [Сленговые слова] ON Словарь.Слово = [Сленговые слова].Код\n"
                            + "WHERE [Основа слова].[Основа слова] = '" + mtest.get(i) + "'");
                    while (rs.next()) {
                        TextField.appendText(j + ") Слово: " + list[i] + ", Деффиниция: " + rs.getString(11) + "\n"
                                + "Способ образования: " + rs.getString(13) + "\n"
                                + "Вид сленга :" + rs.getString(15) + "\n"
                                + "Оригинальное слово: " + rs.getString(17) + "\n");

                        j++;
                        break;
                    }
                }
                //Проверяем какие методы образования использовались
                gettxt = TextField.getText();
                for (int i = 0; i < methods.size(); i++) {
                    boolean isContain1 = gettxt.contains(methods.get(i));
                    if (isContain1) // совпадение найдено
                    {
                        selectmth.add(methods.get(i));
                    }

                }
                hashMeth = selectmth.toArray(new String[selectmth.size()]);
                
                //Считаем количество каждого метода образования который использовался
                int count = 0, t = 0;
                while (t < hashMeth.length) {

                    Pattern p = Pattern.compile("\\b" + hashMeth[t] + "\\b", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(gettxt);
                    while (m.find()) {
                        count++;
                    }
                    meth.appendText(hashMeth[t] + ": " + count + "\n");
                    count = 0;
                    t++;
                }

                rs.close();
                conn.close();
            }
        } catch (ClassNotFoundException | SQLException sqlex) {
            System.out.println(sqlex);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
