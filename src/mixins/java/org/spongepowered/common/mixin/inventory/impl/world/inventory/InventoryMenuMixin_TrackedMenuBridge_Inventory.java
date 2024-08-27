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
package org.spongepowered.common.mixin.inventory.impl.world.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.common.bridge.world.inventory.container.TrackedMenuBridge;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin_TrackedMenuBridge_Inventory extends AbstractCraftingMenuMixin_TrackedMenuBridge_Inventory {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inventory$attachContainerMenu(final Inventory $$0, final boolean $$1, final Player $$2, final CallbackInfo ci) {
        if ($$0 instanceof final TrackedMenuBridge trackedMenu) {
            trackedMenu.bridge$trackContainerMenu((AbstractContainerMenu) (Object) this);
        }

        if (this.craftSlots instanceof final TrackedMenuBridge trackedMenu) {
            trackedMenu.bridge$trackContainerMenu((AbstractContainerMenu) (Object) this);
        }

        if (this.resultSlots instanceof final TrackedMenuBridge trackedMenu) {
            trackedMenu.bridge$trackContainerMenu((AbstractContainerMenu) (Object) this);
        }
    }
}
