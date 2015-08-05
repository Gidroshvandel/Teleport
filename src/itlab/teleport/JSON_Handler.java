package itlab.teleport;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Gidro on 05.08.2015.
 */
public class JSON_Handler {
   public static String json_input = "";
    public static  String json_output = "";
    private void JSON_Return(Object a, Object b)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(a, b);
        json_output = jsonObject.toString();
    }
    public void JSON_Handler()
    {
        JSONArray array = new JSONArray();

        JSONParser parser = new JSONParser();
        JSONObject request=new JSONObject();

        try {
            request  = (JSONObject)parser.parse(json_input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (request.get("REQUEST").toString().equals("GETLIST"))
        {
            try {
                Statement st = BD_Connect.c.createStatement();//Готовим запрос
                ResultSet rs = st.executeQuery("select * from Request_list");//Выполняем запрос к БД, результат в переменной rs
                while (rs.next()) {
                    JSONObject buf = new JSONObject();
                    buf.put("ID", rs.getString("ID"));
                    buf.put("URI", rs.getString("URI"));
                    buf.put("TAG", rs.getString("TAG"));
                    buf.put("LOGIN", rs.getString("LOGIN"));
                    array.add(buf);
                }
            }
            catch (Exception e) {
                System.out.println("Disconnected_BD");
            }

            JSON_Return("ARRAY", array);

        }
        if (request.get("REQUEST").toString().equals("ADDOBJECT"))
        {
            try {
                JSONObject ons = (JSONObject) request.get("OBJECT");
                Statement st = BD_Connect.c.createStatement();//Готовим запрос
                System.out.println("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','" + ons.get("URI").toString()+ "','" + ons.get("LOGIN").toString() + "')");
                st.executeUpdate("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','" + ons.get("URI").toString()+ "','" + ons.get("LOGIN").toString() + "')");//Выполняем запрос к БД
            }
            catch (Exception e)
            {
                System.out.println("Disconnected_BD");
            }
            JSON_Return("Status", "OK");
        }
        if (request.get("REQUEST").toString().equals("DELOBJECT"))
        {
            int ons = Integer.parseInt(request.get("ID").toString());
            try {
                Statement st = BD_Connect.c.createStatement();//Готовим запрос
                System.out.println("DELETE Request_list WHERE ID ="+ ons);
                st.execute("DELETE FROM Request_list WHERE ID = " + ons);//Выполняем запрос к БД
            }
            catch (Exception e)
            {
                System.out.println("Disconnected_BD");
            }
        }
        if (request.get("REQUEST").toString().equals("PUSHSTREAM")) //НЕДОДЕЛАНО!!! Вообще не сделано!!!
        {
            try {
                JSONObject ons = (JSONObject) request.get("OBJECT");
                Statement st = BD_Connect.c.createStatement();//Готовим запрос
                System.out.println("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','http://192.168.0.210:81/hls/" + ons.get("LOGIN").toString() + ".m3u8','" + ons.get("LOGIN").toString() + "')");
                st.executeUpdate("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','rtmp://192.168.0.210:1936/videochat/" + ons.get("LOGIN").toString() + "','" + ons.get("LOGIN").toString() + "')");//Выполняем запрос к БД
            }
            catch (Exception e)
            {
                System.out.println("Disconnected_BD");
            }
        }
        if (request.get("REQUEST").toString().equals("AUTHORIZATION"))
        {
            try {
                int error = 0;
                JSONObject ons = (JSONObject) request.get("OBJECT");
                Statement st = BD_Connect.c.createStatement();//Готовим запрос
                ResultSet rs = st.executeQuery("Select 1");
                try {
                    rs = st.executeQuery("select * from User_info WHERE Login = '" +ons.get("LOGIN")+ "'");//Выполняем запрос к БД
                    rs.next();
                    rs.getString("User_password");
                }
                catch (Exception e)
                {
                    JSON_Return("Status", "INVALID_LOGIN");
                    System.out.println("INVALID_LOGIN");
                    error = 1;
                }
                if (error != 1) {
                    System.out.println(rs.getString("User_password"));
                    if (ons.get("PASSWORD").toString().equals(rs.getString("User_password"))) {
                        JSON_Return("Status", "OK");
                    } else {
                        JSON_Return("Status", "PASSWORD_WRONG");
                        System.out.println("PASSWORD_WRONG");
                    }
                }
            } catch (Exception e)
            {
                JSON_Return("Status", "ERROR");
                System.out.println("Disconnected_BD");
            }
        }
        if (request.get("REQUEST").toString().equals("REGISTRATION")) //НЕДОДЕЛАНО!!! Вообще не сделано!!!
        {
            try {
                int error = 0;
                JSONObject ons = (JSONObject) request.get("OBJECT");
                Statement st = BD_Connect.c.createStatement();//Готовим запрос
                ResultSet rs;
                try {
                    rs = st.executeQuery("select * from User_info WHERE Login = '" + ons.get("LOGIN") + "'");//Выполняем запрос к БД
                    rs.next();
                    rs.getString("Login");
                } catch (Exception e) {
                    error = 1;
                    st.executeUpdate("INSERT INTO User_info (Login, User_password, User_name) values('" + ons.get("LOGIN").toString() + "','" + ons.get("PASSWORD").toString() + "','" + ons.get("USERNAME").toString() + "')");//Выполняем запрос к БД
                    JSON_Return("Status", "OK");
                    System.out.println("INSERT INTO User_info (Login, User_password, User_name) values('" + ons.get("LOGIN").toString() + "','" + ons.get("PASSWORD").toString() + "','" + ons.get("USERNAME").toString() + "')");
                }
                if (error == 0) {
                    JSON_Return("Status", "INVALID_LOGIN");
                    System.out.println("INVALID_LOGIN");
                }
            }
            catch (Exception e)
            {
                System.out.println("Disconnected_BD");
            }
        }
    }
}
