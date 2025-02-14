/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.client.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.EngineConnection;
import org.spongepowered.api.network.EngineConnectionState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.bridge.network.ConnectionBridge;
import org.spongepowered.common.network.PacketUtil;
import org.spongepowered.common.network.channel.PacketSender;
import org.spongepowered.common.network.channel.SpongeChannelManager;
import org.spongepowered.common.network.channel.SpongeChannelPayload;

@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class ClientHandshakePacketListenerImplMixin {

    // @formatter:off
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private Connection connection;
    // @formatter:on

    @Inject(method = "handleCustomQuery", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/network/Connection;send(Lnet/minecraft/network/protocol/Packet;)V"), cancellable = true)
    private void impl$onHandleCustomQuery(final ClientboundCustomQueryPacket packet, final CallbackInfo ci) {
        if (!(packet.payload() instanceof final SpongeChannelPayload payload)) {
            return;
        }

        ci.cancel();

        this.minecraft.execute(() -> {
            final SpongeChannelManager channelRegistry = (SpongeChannelManager) Sponge.channelManager();
            final EngineConnection connection = ((ConnectionBridge) this.connection).bridge$getEngineConnection();
            if (!channelRegistry.handleLoginRequestPayload(connection, (EngineConnectionState) this, payload.id(), packet.transactionId(), payload.consumer())) {
                PacketSender.sendTo(connection, PacketUtil.createLoginPayloadResponse(null, packet.transactionId()));
            }
        });
    }
}
