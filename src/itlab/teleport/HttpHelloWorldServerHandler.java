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
import java.util.ArrayList;
import java.util.List;



public class HttpHelloWorldServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };

    private final StringBuilder buf = new StringBuilder();
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
//        if (msg instanceof HttpObjectAggregator) {
            HttpRequest req = (HttpRequest) msg;

//            if (HttpHeaders.is100ContinueExpected(req)) {
//                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
//            }
//            boolean keepAlive = HttpHeaders.isKeepAlive(req);
//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(CONTENT));
//            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
//            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());

//            if (!keepAlive) {
//                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//            } else {
//                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//                ctx.write(response);
//            }

//            System.out.println(req.trailingHeaders().get(HttpHeaders.Names.CONTENT_TYPE));
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
//
//            HttpResponse resp = new DefaultHttpResponse(HTTP_1_1, OK);
//            ctx.writeAndFlush(resp);                                             �����
//            ctx.close();
//            final InterfaceHttpData data1 = decoder.getBodyHttpData("value");

//            try {
//            while (decoder.hasNext()) {
//                final InterfaceHttpData data = decoder.next();
//                if (data != null) {
//                    try {
//                        Attribute attribute = (Attribute) data;
//                        System.out.println(attribute.getName());
//                        System.out.println(attribute.getValue());
//                    } catch (IOException e) {
////                        e.printStackTrace();
//                    } finally {
//                        data.release();
//                    }
//                }
//            }
//            } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
////                e.toString()
//            }
        }

        if (msg instanceof HttpContent) {


            String json_input = "";
            String json_output = "";
            JSONArray array = new JSONArray();



            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                json_input = content.toString(CharsetUtil.UTF_8);
//                json_input = content.toString();
                buf.append("CONTENT: ");
                buf.append(content.toString(CharsetUtil.UTF_8));
                buf.append("\r\n");
//                appendDecoderResult(buf, request);
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


                    for (int i = 0; i < HttpHelloWorldServer.list.size(); i++) {
                        array.add(HttpHelloWorldServer.list.get(i).getJOSON());
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ARRAY", array);
                    json_output = jsonObject.toString();
                }
                if (request.get("REQUEST").toString().equals("ADDOBJECT"))
                {
                    JSONObject ons = (JSONObject)request.get("OBJECT");
                    HttpHelloWorldServer.list.add(0, new Element_list(ons.get("TAG").toString(),ons.get("URI").toString()));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ARRAY", "HEllO");
                    json_output = jsonObject.toString();
                }
                if (request.get("REQUEST").toString().equals("DELOBJECT"))
                {
                    JSONObject ons = (JSONObject)request.get("ID");
                    HttpHelloWorldServer.list.remove(Integer.parseInt(ons.get("ID").toString()));
                }

                ByteBuf response_content = Unpooled.wrappedBuffer(json_output.getBytes(CharsetUtil.UTF_8));
                HttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, OK, response_content);

                ctx.writeAndFlush(resp);
                ctx.close();

//                if (!writeResponse(trailer, ctx)) {
//                    // If keep-alive is off, close the connection once the content is fully written.
//                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
//                }
            }
        }
//        if (msg instanceof DefaultLastHttpContent) {
//            DefaultLastHttpContent content = (DefaultLastHttpContent) msg;
//            System.out.print(content.toString());
//        }
//        if (msg instanceof HttpObjectAggregator) {
//            HttpObjectAggregator content = (HttpObjectAggregator) msg;
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
