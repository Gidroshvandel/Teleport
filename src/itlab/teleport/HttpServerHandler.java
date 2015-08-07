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



import java.io.IOException;
import java.sql.*;
import java.util.List;



public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {

    private final StringBuilder buf = new StringBuilder();
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws SQLException, IOException {
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
            String json_input = "";

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

                try{
                    Statement st = BD_Connect.c.createStatement();//Готовим запрос
                    ResultSet rs = st.executeQuery("Select 1");
                }
                catch (Exception e) {
                    new BD_Connect().BD_Connection_open(); //Подключение к базе данных
                }

                String json_output = new JSON_Handler().Parse_JSON(json_input); // Вызов обработчика JSON запросов

                ByteBuf response_content = Unpooled.wrappedBuffer(json_output.getBytes(CharsetUtil.UTF_8));
                HttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, OK, response_content);

                ctx.writeAndFlush(resp);
                ctx.close();
                new BD_Connect().BD_Connection_close(); // Закрытие соединения с БД

            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
