package itlab.teleport;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gidro on 05.08.2015.
 */
public class BD_Connect {
   public static Connection c = null;
    public  void BD_Connection_close()
    {
        //закрытие соединения с БД
        try {
            if(BD_Connect.c != null) {
                BD_Connect.c.close();
                System.out.println("Connection_close");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Connection can't be close");
        }
    }
    public void BD_Connection_open() throws IOException {
        List<String> list = new ArrayList<>();
        list = File_config.Read_ini();
        //      Подключение к БД
        String user = list.get(0);//"root";//Логин пользователя
        String password = list.get(1);//Пароль пользователя
        String url = "jdbc:mysql://"+ list.get(2);//URL адрес
        String driver = "com.mysql.jdbc.Driver";//Имя драйвера
        try {
            Class.forName(driver);//Регистрируем драйвер
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Соединение с БД
        try {
            c = DriverManager.getConnection(url, user, password);//Установка соединения с БД
            System.out.println("Connect to BD: Success");
        } catch (Exception e) {
            System.out.println("Connect to BD: Faild");
            e.printStackTrace();
        }
//      Подключение к БД
    }
}
