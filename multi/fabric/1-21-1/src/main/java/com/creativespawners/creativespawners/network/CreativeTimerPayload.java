package com.creativespawners.creativespawners.network;

import com.creativespawners.creativespawners.CreativeSpawners;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CreativeTimerPayload(int ticks) implements CustomPacketPayload {
    public static final Type<CreativeTimerPayload> TYPE = new Type<>(CreativeSpawners.id("creative_timer"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreativeTimerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CreativeTimerPayload::ticks,
            CreativeTimerPayload::new
    );

    @Override
    public Type<CreativeTimerPayload> type() {
        return TYPE;
    }
}
