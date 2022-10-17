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
package org.spongepowered.vanilla.mixin.core.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.bridge.world.entity.EntityBridge;
import org.spongepowered.common.world.portal.PortalLogic;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class EntityMixin_Vanilla implements EntityBridge {

    // @formatter:off
    @Shadow public Level level;
    // @formatter:on

    /**
     * @author dualspiral - 19th December 2020 - 1.16.4
     * @reason Overwrite to redirect call to
     *         {@link #bridge$changeDimension(net.minecraft.server.level.ServerLevel, PortalLogic)}, this
     *         is to support Forge mods and their ITeleporter
     */
    @Overwrite
    @Nullable
    public Entity changeDimension(final net.minecraft.server.level.ServerLevel originalDestinationWorld) {
        return this.bridge$changeDimension(originalDestinationWorld, (PortalLogic) originalDestinationWorld.getPortalForcer());
    }

}
