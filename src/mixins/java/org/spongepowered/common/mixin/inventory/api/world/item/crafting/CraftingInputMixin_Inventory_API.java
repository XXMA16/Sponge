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
package org.spongepowered.common.mixin.inventory.api.world.item.crafting;


import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.RecipeInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.bridge.world.inventory.LensGeneratorBridge;
import org.spongepowered.common.inventory.adapter.InventoryAdapter;
import org.spongepowered.common.inventory.fabric.Fabric;
import org.spongepowered.common.inventory.lens.Lens;
import org.spongepowered.common.inventory.lens.impl.comp.GridInventoryLens;
import org.spongepowered.common.inventory.lens.impl.slot.SlotLensProvider;

@Mixin(CraftingInput.class)
public abstract class CraftingInputMixin_Inventory_API implements RecipeInput.Crafting, LensGeneratorBridge {

    @Shadow @Final private int width;
    @Shadow @Final private int height;
    private GridInventory api$gridAdapter;

    @Override
    public GridInventory asGrid() {
        // override with caching
        final GridInventoryLens lens = (GridInventoryLens) ((InventoryAdapter) this).inventoryAdapter$getRootLens();
        if (this.api$gridAdapter == null) {
            this.api$gridAdapter = (GridInventory) lens.getAdapter((Fabric) this, this);
        }
        return this.api$gridAdapter;
    }

    @Override
    public Lens lensGeneratorBridge$generateLens(final SlotLensProvider slotLensProvider) {
        return new GridInventoryLens(0, this.width, this.height, slotLensProvider);
    }
}
