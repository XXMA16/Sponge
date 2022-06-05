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
package org.spongepowered.common.datapack;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.datapack.DataPackSerializable;
import org.spongepowered.api.datapack.DataPackType;
import org.spongepowered.api.datapack.DataPackTypes;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.common.SpongeCommon;
import org.spongepowered.common.event.lifecycle.RegisterDataPackValueEventImpl;
import org.spongepowered.common.item.recipe.ingredient.IngredientResultUtil;
import org.spongepowered.common.item.recipe.ingredient.SpongeIngredient;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class SpongeDataPackManager {

    public static SpongeDataPackManager INSTANCE = new SpongeDataPackManager(Sponge.game());

    private final Game game;

    private Map<DataPackType, Consumer<RegistryAccess>> delayed = new HashMap<>();

    private SpongeDataPackManager(final Game game) {
        this.game = game;
    }

    public void callRegisterDataPackValueEvents(final Path dataPacksDirectory) {
        this.callRegisterDataPackValueEvents(dataPacksDirectory, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    public void callRegisterDataPackValueEvents(final Path dataPacksDirectory, final Collection<String> dataPacksToLoad) {
        SpongeIngredient.clearCache();
        IngredientResultUtil.clearCache();

        this.serialize(DataPackTypes.ADVANCEMENT, dataPacksDirectory, dataPacksToLoad, this.callRegisterDataPackValueEvent(DataPackTypes.ADVANCEMENT));
        this.serialize(DataPackTypes.RECIPE, dataPacksDirectory, dataPacksToLoad, this.callRegisterDataPackValueEvent(DataPackTypes.RECIPE));
        this.serialize(DataPackTypes.TAG, dataPacksDirectory, dataPacksToLoad, this.callRegisterDataPackValueEvent(DataPackTypes.TAG));
        this.serializeDelayed(DataPackTypes.BIOME, dataPacksDirectory, this.callRegisterDataPackValueEvent(DataPackTypes.BIOME));
        this.serializeDelayed(DataPackTypes.WORLD_TYPE, dataPacksDirectory, this.callRegisterDataPackValueEvent(DataPackTypes.WORLD_TYPE));
        this.serializeDelayed(DataPackTypes.WORLD, dataPacksDirectory, this.callRegisterDataPackValueEvent(DataPackTypes.WORLD));
    }

    private <T extends DataPackSerializable> List<T> callRegisterDataPackValueEvent(final DataPackType<T> type) {
        final RegisterDataPackValueEventImpl<T> event = new RegisterDataPackValueEventImpl<>(Cause.of(EventContext.empty(), this.game), this.game, type);
        this.game.eventManager().post(event);
        return event.serializables();
    }

    public <T extends DataPackSerializable> void serializeDelayedDataPack(final DataPackType<T> type, final RegistryAccess registryAccess) {
        final Consumer<RegistryAccess> runnable = this.delayed.get(type);
        if (runnable != null) {
            runnable.accept(registryAccess);
        }
    }

    private <T extends DataPackSerializable> void serialize(final DataPackType<T> type, final Path dataPacksDirectory, final Collection<String> dataPacksToLoad, final List<T> serializables) {
       this.serialize(type, dataPacksDirectory, dataPacksToLoad, serializables, null);
    }

    private <T extends DataPackSerializable> void serializeDelayed(final DataPackType<T> type, final Path dataPacksDirectory, final List<T> serializables) {
        this.delayed.put(type, (registryAccess) -> this.serialize(type, dataPacksDirectory, new ArrayList<>(), serializables, registryAccess));
    }

    @SuppressWarnings("unchecked")
    private <T extends DataPackSerializable> void serialize(final DataPackType<T> type, final Path dataPacksDirectory,
            final Collection<String> dataPacksToLoad, final List<T> serializables, @Nullable final RegistryAccess registryAccess) {
        if (serializables.isEmpty()) {
            return;
        }

        final SpongeDataPackType implType = (SpongeDataPackType) type;
        final List<DataPackSerializedObject> serialized = new ArrayList<>();
        for (final DataPackSerializable serializable : serializables) {
            final JsonObject o = (JsonObject) implType.getObjectSerializer().serialize(serializable, registryAccess);
            serialized.add((DataPackSerializedObject) implType.getObjectFunction().apply(serializable, o));
        }

        // Serialize the pack itself now - objects later
        this.serializePack(dataPacksDirectory, dataPacksToLoad, implType, serialized, serializables.size());
    }

    private void serializePack(final Path dataPacksDirectory, final Collection<String> dataPacksToLoad, final SpongeDataPackType implType,
                               final List<DataPackSerializedObject> serialized, final int count) {
        // When reloading we must update the dataPacksToLoad
        try {
            if (implType.getPackSerializer().serialize(implType, dataPacksDirectory, serialized, count)) {
                dataPacksToLoad.add("file/" + implType.getPackSerializer().getPackName());
            } else {
                dataPacksToLoad.remove("file/" + implType.getPackSerializer().getPackName());
            }
        } catch (final IOException e) {
            dataPacksToLoad.remove("file/" + implType.getPackSerializer().getPackName());
            SpongeCommon.logger().error(e);
        }
    }
}
