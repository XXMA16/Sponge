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
package org.spongepowered.common.scoreboard;

import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.common.adventure.SpongeAdventure;
import org.spongepowered.common.bridge.world.scores.ObjectiveBridge;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public final class SpongeScore implements Score {

    private final String name;
    public ScoreHolder holder;
    private int score;
    private boolean locked;

    @Nullable
    private net.minecraft.network.chat.Component display;
    @Nullable
    private NumberFormat numberFormat;

    private Set<net.minecraft.world.scores.Objective> objectives = new HashSet<>();

    public SpongeScore(final String name) {
        this.name = name;
        this.holder = ScoreHolder.forNameOnly(name);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int score() {
        return this.score;
    }

    @Override
    public void setScore(final int score) {
        this.score = score;
        this.updateScore();
    }

    @Override
    public boolean isLocked() {
        return this.locked;
    }

    @Override
    public void setLocked(final boolean locked) {
        this.locked = locked;
        this.updateScore();
    }

    @Override
    public void setDisplay(final Component display) {
        this.display = SpongeAdventure.asVanilla(display);
        this.updateScore();
    }

    @Override
    public Optional<Component> display() {
        return Optional.ofNullable(SpongeAdventure.asAdventure(this.display));
    }

    private void updateScore() {
        for (final net.minecraft.world.scores.Objective objective : this.objectives) {
            var scoreboard = objective.getScoreboard();
            for (final ScoreHolder holder : scoreboard.getTrackedPlayers()) {
                final ScoreAccess access = scoreboard.getOrCreatePlayerScore(holder, objective);
                this.registerAndUpdate(objective, access);
            }
        }
    }

    @Override
    public Set<Objective> objectives() {
        final Set<Objective> objectives = new HashSet<>();
        for (final net.minecraft.world.scores.Objective objective: this.objectives) {
            objectives.add(((ObjectiveBridge) objective).bridge$getSpongeObjective());
        }
        return objectives;
    }

    public void registerAndUpdate(net.minecraft.world.scores.Objective objective, final ScoreAccess accessor) {
        this.objectives.add(objective);
        accessor.set(this.score);
        accessor.display(this.display);
        if (this.locked) {
            accessor.lock();
        } else {
            accessor.unlock();
        }
    }

    public void unregister(net.minecraft.world.scores.Objective objective) {
        this.objectives.remove(objective);
    }

    public record SpongeScoreAccess(ScoreHolder holder, net.minecraft.world.scores.Objective objective, ScoreAccess access) implements ScoreAccess {

        @Override
        public int get() {
            return this.access.get();
        }

        @Override
        public boolean locked() {
            return this.access.locked();
        }

        @Nullable @Override
        public net.minecraft.network.chat.Component display() {
            return this.access.display();
        }

        @Override
        public void set(final int var1) {
            final SpongeObjective spongeObjective = ((ObjectiveBridge) objective).bridge$getSpongeObjective();
            // TODO maybe stackoverflow issue
            spongeObjective.findScore(holder.getScoreboardName()).ifPresent(spongeScore -> spongeScore.setScore(var1));

            this.access.set(var1);
        }

        @Override
        public void unlock() {
            final SpongeObjective spongeObjective = ((ObjectiveBridge) objective).bridge$getSpongeObjective();
            // TODO maybe stackoverflow issue
            spongeObjective.findScore(holder.getScoreboardName()).ifPresent(spongeScore -> spongeScore.setLocked(false));

            this.access.unlock();
        }

        @Override
        public void lock() {
            final SpongeObjective spongeObjective = ((ObjectiveBridge) objective).bridge$getSpongeObjective();
            // TODO maybe stackoverflow issue
            spongeObjective.findScore(holder.getScoreboardName()).ifPresent(spongeScore -> spongeScore.setLocked(true));

            this.access.lock();
        }

        @Override
        public void display(@Nullable final net.minecraft.network.chat.Component var1) {
            final SpongeObjective spongeObjective = ((ObjectiveBridge) objective).bridge$getSpongeObjective();
            // TODO maybe stackoverflow issue
            spongeObjective.findScore(holder.getScoreboardName()).ifPresent(spongeScore -> spongeScore.setDisplay(SpongeAdventure.asAdventure(var1)));

            this.access.display(var1);
        }

        @Override
        public void numberFormatOverride(@Nullable final NumberFormat var1) {
            this.access.numberFormatOverride(var1);
        }
    }
}
