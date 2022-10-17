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
package org.spongepowered.common.resourcepack;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.URI;
import java.net.URISyntaxException;


public final class SpongeURIResourcePack extends SpongeResourcePack {

    private final URI uri;
    private final String name;

    public SpongeURIResourcePack(URI uri, @Nullable String hash, Component component) {
        super(hash, component);
        this.uri = uri;
        this.name = this.getName0();
    }

    public SpongeURIResourcePack(String uri, @Nullable String hash, Component component) throws URISyntaxException {
        this(new URI(uri), hash, component);
    }

    private String getName0() {
        String name = this.uri.getPath();
        name = name.substring(name.lastIndexOf("/") + 1);
        return name.replaceAll("\\W", "");
    }

    @Override
    public String getUrlString() {
        return this.uri.toString();
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public URI uri() {
        return this.uri;
    }

}
