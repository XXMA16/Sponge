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
package org.spongepowered.common.mixin.core.world.entity.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.bridge.world.level.LevelBridge;
import org.spongepowered.common.event.SpongeCommonEventFactory;
import org.spongepowered.common.util.DamageEventUtil;

@Mixin(ShulkerBullet.class)
public abstract class ShulkerBulletMixin extends ProjectileMixin {

    @Inject(method = "onHit", at = @At("HEAD"), cancellable = true)
    private void onBulletHitBlock(final HitResult result, final CallbackInfo ci) {
        if (!((LevelBridge) this.shadow$level()).bridge$isFake() && result.getType() != HitResult.Type.MISS
                && SpongeCommonEventFactory.handleCollideImpactEvent(
                (ShulkerBullet) (Object) this, this.impl$getProjectileSource(), result)) {
            this.shadow$discard();
            ci.cancel();
        }
    }

    @Inject(method = "hurt", cancellable = true, at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"))
    private void attackImpl$onAttackEntityFrom(final DamageSource source, final float amount, final CallbackInfoReturnable<Boolean> cir) {
        if (DamageEventUtil.callOtherAttackEvent((Entity) (Object) this, source, amount).isCancelled()) {
            cir.setReturnValue(true);
        }
    }
}
