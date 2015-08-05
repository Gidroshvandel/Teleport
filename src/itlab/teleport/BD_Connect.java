package itlab.teleport;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Gidro on 05.08.2015.
 */
public class BD_Connect {
   public static Connection c = null;
    public void BD_Connect()
    {
        //      Подключение к БД
        String user = "root";//Логин пользователя
        String password = "";//Пароль пользователя
        String url = "jdbc:mysql://localhost:3306/Teleport";//URL адрес
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
