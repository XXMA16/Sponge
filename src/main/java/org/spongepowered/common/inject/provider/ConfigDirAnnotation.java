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
package org.spongepowered.common.inject.provider;

import org.spongepowered.api.config.ConfigDir;

import java.lang.annotation.Annotation;
import java.util.StringJoiner;

// This is strange, but required for Guice and annotations with values.
public class ConfigDirAnnotation implements ConfigDir {

    public static final ConfigDir NON_SHARED = new ConfigDirAnnotation(false);
    public static final ConfigDir SHARED = new ConfigDirAnnotation(true);

    private final boolean shared;

    private ConfigDirAnnotation(boolean shared) {
        this.shared = shared;
    }

    @Override
    public boolean sharedRoot() {
        return this.shared;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return ConfigDir.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigDir)) {
            return false;
        }

        ConfigDir that = (ConfigDir) o;
        return this.sharedRoot() == that.sharedRoot();
    }

    @Override
    public int hashCode() {
        return (127 * "sharedRoot".hashCode()) ^ Boolean.valueOf(this.sharedRoot()).hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", '@' + this.getClass().getSimpleName() + "[", "]")
                .add("shared=" + this.shared)
                .toString();
    }

}
