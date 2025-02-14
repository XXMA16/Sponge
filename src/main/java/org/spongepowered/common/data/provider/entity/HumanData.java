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
package org.spongepowered.common.data.provider.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.spongepowered.api.data.Keys;
import org.spongepowered.common.adventure.SpongeAdventure;
import org.spongepowered.common.data.provider.DataProviderRegistrator;
import org.spongepowered.common.entity.living.human.HumanEntity;

public final class HumanData {

    private HumanData() {
    }

    // @formatter:off
    public static void register(final DataProviderRegistrator registrator) {
        registrator
                .asMutable(HumanEntity.class)
                    .create(Keys.SKIN_PROFILE_PROPERTY)
                        .get(HumanEntity::getSkinProperty)
                        .set(HumanEntity::setSkinProperty)
                        .delete(h -> h.setSkinProperty(null))
                    .create(Keys.CUSTOM_NAME)
                        .get(h -> h.hasCustomName() ? SpongeAdventure.asAdventure(h.getCustomName()) : null)
                        .setAnd((h, v) -> {
                            final Component component = SpongeAdventure.asVanilla(v);
                            if (!StringUtil.isValidPlayerName(component.getString())) {
                                return false;
                            }
                            h.setCustomName(component);
                            return true;
                        })
                        .delete(h -> {
                            h.setCustomName(null);
                            h.setCustomNameVisible(false);
                        })

        ;
    }
    // @formatter:on
}
