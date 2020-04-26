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
package org.spongepowered.common.mixin.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.bridge.data.VanishableBridge;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public abstract class EntityPredicatesMixin {

    @Shadow @Final @Mutable public static Predicate<Entity> NOT_SPECTATING = entity -> {
        if (entity instanceof VanishableBridge && ((VanishableBridge) entity).bridge$isVanished()) {
            // Sponge: Count vanished entities as spectating
            return false;
        }
        if (entity.isSpectator()) {
            return false;
        }
        return true;
    };

    @Shadow @Final @Mutable public static Predicate<Entity> CAN_AI_TARGET = entity ->{
        if (entity instanceof VanishableBridge
            && ((VanishableBridge) entity).bridge$isVanished()
            && ((VanishableBridge) entity).bridge$isUntargetable()) {
            // Sponge: Take into account untargetability from vanishing
            return false;
        }
        if (entity instanceof PlayerEntity && (entity.isSpectator() || ((PlayerEntity) entity).isCreative())) {
            return false;
        }
        return true;
    };


}
