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
        //      ����������� � ��
        String user = "root";//����� ������������
        String password = "";//������ ������������
        String url = "jdbc:mysql://localhost:3306/Teleport";//URL �����
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
