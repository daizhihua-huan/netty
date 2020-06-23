package com.huanyuenwei.exmaple.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

//消息编码器
public class MsgPckEncode extends MessageToByteEncoder<Model> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Model msg, ByteBuf buf)
            throws Exception {
        // TODO Auto-generated method stub
        MessagePack pack = new MessagePack();
        pack.register(Model.class);
        byte[] write = pack.write(msg);

        buf.writeBytes(write);

    }


}
