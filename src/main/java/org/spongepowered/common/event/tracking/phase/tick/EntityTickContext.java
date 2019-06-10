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
package org.spongepowered.common.event.tracking.phase.tick;

import net.minecraft.entity.Entity;
import org.spongepowered.common.event.tracking.IPhaseState;
import org.spongepowered.common.bridge.entity.IMixinEntity;

public class EntityTickContext extends TickContext<EntityTickContext> {

    double posX;
    double posY;
    double posZ;
    public double prevX;
    public double prevY;
    public double prevZ;


    EntityTickContext(IPhaseState<EntityTickContext> phaseState) {
        super(phaseState);
    }

    @Override
    public EntityTickContext source(Object owner) {
        super.source(owner);
        if (owner instanceof IMixinEntity) {
            final IMixinEntity mixinEntity = (IMixinEntity) owner;
            setBulkBlockCaptures(mixinEntity.allowsBlockBulkCapture());
            setBlockEvents(mixinEntity.allowsBlockEventCreation());
            setBulkEntityCaptures(mixinEntity.allowsEntityBulkCapture());
            setEntitySpawnEvents(mixinEntity.allowsEntityEventCreation());
        }
        this.populateEntityPosition((Entity) owner);
        return super.source(owner);
    }

    public void populateEntityPosition(Entity entity) {
        this.posX = entity.posX;
        this.posY = entity.posY;
        this.posZ = entity.posZ;
        this.prevX = entity.lastTickPosX;
        this.prevY = entity.lastTickPosY;
        this.prevZ = entity.lastTickPosZ;

    }
}
