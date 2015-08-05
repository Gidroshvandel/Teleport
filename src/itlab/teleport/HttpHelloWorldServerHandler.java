package itlab.teleport;/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



import java.io.IOException;
import java.sql.*;
import java.util.List;



public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<Object> {

    private final StringBuilder buf = new StringBuilder();
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws SQLException {
        if (msg instanceof FullHttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            final HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), req);

            List<InterfaceHttpData> data = decoder.getBodyHttpDatas();
            for (InterfaceHttpData entry : data) {
                Attribute attr = (Attribute) entry;
                try {
                    System.out.println(String.format("name: %s value: %s", attr.getName(), attr.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (msg instanceof HttpContent) {
            //      Подключение к БД
            Connection c = null;
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
            try{
                c = DriverManager.getConnection(url, user, password);//Установка соединения с БД
                System.out.println("Connect to BD: Success");
            } catch(Exception e){
                System.out.println("Connect to BD: Faild");
                e.printStackTrace();
            }
//      Подключение к БД


            String json_input = "";
            String json_output = "";
            JSONArray array = new JSONArray();



            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                json_input = content.toString(CharsetUtil.UTF_8);
                buf.append("CONTENT: ");
                buf.append(content.toString(CharsetUtil.UTF_8));
                buf.append("\r\n");
            }

            if (msg instanceof LastHttpContent) {
                buf.append("END OF CONTENT\r\n");

                LastHttpContent trailer = (LastHttpContent) msg;
                if (!trailer.trailingHeaders().isEmpty()) {
                    buf.append("\r\n");
                    for (CharSequence name: trailer.trailingHeaders().names()) {
                        for (CharSequence value: trailer.trailingHeaders().getAll(name)) {
                            buf.append("TRAILING HEADER: ");
                            buf.append(name).append(" = ").append(value).append("\r\n");
                        }
                    }
                    buf.append("\r\n");
                }
                System.out.println(buf);

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
                        Statement st = c.createStatement();//Готовим запрос
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


                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("ARRAY", array);
                        json_output = jsonObject.toString();

                }
                if (request.get("REQUEST").toString().equals("ADDOBJECT"))
                {
                    try {
                        JSONObject ons = (JSONObject) request.get("OBJECT");
                        Statement st = c.createStatement();//Готовим запрос
                        System.out.println("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','" + ons.get("URI").toString()+ "','" + ons.get("LOGIN").toString() + "')");
                        st.executeUpdate("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','" + ons.get("URI").toString()+ "','" + ons.get("LOGIN").toString() + "')");//Выполняем запрос к БД
                    }
                    catch (Exception e)
                    {
                        System.out.println("Disconnected_BD");
                    }
//                    Statement st = c.createStatement();//Готовим запрос
//                    ResultSet rs = st.executeQuery("select * from Request_list");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Status", "OK");
                    json_output = jsonObject.toString();
                }
                if (request.get("REQUEST").toString().equals("DELOBJECT"))
                {
                    int ons = Integer.parseInt(request.get("ID").toString());
                    try {
                        Statement st = c.createStatement();//Готовим запрос
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
                        Statement st = c.createStatement();//Готовим запрос
                        System.out.println("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','http://192.168.0.210:81/hls/" + ons.get("LOGIN").toString() + ".m3u8','" + ons.get("LOGIN").toString() + "')");
                        st.executeUpdate("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','rtmp://192.168.0.210:1936/videochat/" + ons.get("LOGIN").toString() + "','" + ons.get("LOGIN").toString() + "')");//Выполняем запрос к БД
                    }
                    catch (Exception e)
                    {
                        System.out.println("Disconnected_BD");
                    }
                }
                if (request.get("REQUEST").toString().equals("AUTHORIZATION")) //НЕДОДЕЛАНО!!! Вообще не сделано!!!
                {
                    try {
                        JSONObject ons = (JSONObject) request.get("OBJECT");
                        Statement st = c.createStatement();//Готовим запрос
                       ResultSet rs = st.executeQuery("Select 1");
                        try {
                            rs = st.executeQuery("select * from User_info WHERE Login = '" +ons.get("LOGIN")+ "'");//Выполняем запрос к БД
                        }
                        catch (Exception e)
                        {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("Status", "INVALID_LOGIN");
                            json_output = jsonObject.toString();
                            System.out.println("INVALID_LOGIN");
                        }
                        rs.next();
                        System.out.println(rs.getString("User_password"));
                        if (ons.get("PASSWORD").toString().equals(rs.getString("User_password")))
                        {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("Status", "OK");
                            json_output = jsonObject.toString();
                        }
                        else
                        {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("Status", "PASSWORD_WRONG");
                            json_output = jsonObject.toString();
                            System.out.println("PASSWORD_WRONG");
                        }
                    } catch (Exception e)
                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Status", "ERROR");
                        json_output = jsonObject.toString();
                        System.out.println("Disconnected_BD");
                    }
                }
                if (request.get("REQUEST").toString().equals("REGISTRATION")) //НЕДОДЕЛАНО!!! Вообще не сделано!!!
                {
                    try {
                        JSONObject ons = (JSONObject) request.get("OBJECT");
                        Statement st = c.createStatement();//Готовим запрос
                        System.out.println("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','http://192.168.0.210:81/hls/" + ons.get("LOGIN").toString() + ".m3u8','" + ons.get("LOGIN").toString() + "')");
                        st.executeUpdate("INSERT INTO Request_list (TAG, URI, LOGIN) values('" + ons.get("TAG").toString() + "','rtmp://192.168.0.210:1936/videochat/" + ons.get("LOGIN").toString() + "','" + ons.get("LOGIN").toString() + "')");//Выполняем запрос к БД
                    }
                    catch (Exception e)
                    {
                        System.out.println("Disconnected_BD");
                    }
                }

                ByteBuf response_content = Unpooled.wrappedBuffer(json_output.getBytes(CharsetUtil.UTF_8));
                HttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, OK, response_content);

                ctx.writeAndFlush(resp);
                ctx.close();
                    //закрытие соединения с БД
                    try {
                        if(c != null)
                            System.out.println("Connection_close");
                            c.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
