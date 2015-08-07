package itlab.teleport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Gidro on 05.08.2015.
 */
public class File_config {
    public static List<String> Read_ini() throws IOException {
        List<String> list = new ArrayList<>();
        File file = new File("config.ini");
        if(!file.exists()) {
            //Создаем его.
            file.createNewFile();
            try (FileWriter writer = new FileWriter("config.ini", false)){
                writer.write("DB_user= root\r\n" + "DB_password= \r\n" + "DB_url= 127.0.0.1:3306/Teleport");
            }
            catch (Exception e)
            {
                System.out.println("Error");
            }
        }
        try{
            Properties p = new Properties();
            p.load(new FileInputStream("config.ini"));
//            System.out.println("user = " + p.getProperty("DB_user"));
//            System.out.println("password = " + p.getProperty("DB_password"));
//            System.out.println("url = " + p.getProperty("DB_url"));
//            p.list(System.out);
            list.add(p.getProperty("DB_user"));
            list.add(p.getProperty("DB_password"));
            list.add(p.getProperty("DB_url"));
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }
}
