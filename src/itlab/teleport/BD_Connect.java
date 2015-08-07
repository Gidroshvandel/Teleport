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
        //�������� ���������� � ��
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
        //      ����������� � ��
        String user = list.get(0);//"root";//����� ������������
        String password = list.get(1);//������ ������������
        String url = "jdbc:mysql://"+ list.get(2);//URL �����
        String driver = "com.mysql.jdbc.Driver";//��� ��������
        try {
            Class.forName(driver);//������������ �������
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //���������� � ��
        try {
            c = DriverManager.getConnection(url, user, password);//��������� ���������� � ��
            System.out.println("Connect to BD: Success");
        } catch (Exception e) {
            System.out.println("Connect to BD: Faild");
            e.printStackTrace();
        }
//      ����������� � ��
    }
}
