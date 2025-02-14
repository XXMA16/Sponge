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
package org.spongepowered.common.advancement.criterion;


import net.minecraft.util.Mth;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementProgress;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.CriterionProgress;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.advancement.CriterionEvent;
import org.spongepowered.common.SpongeCommon;
import org.spongepowered.common.bridge.advancements.AdvancementProgressBridge;
import org.spongepowered.common.bridge.server.PlayerAdvancementsBridge;
import org.spongepowered.common.event.tracking.PhaseTracker;
import org.spongepowered.common.util.Preconditions;

import java.time.Instant;
import java.util.Optional;

public class SpongeScoreCriterionProgress implements ScoreCriterionProgress, ImplementationBackedCriterionProgress {

    private final SpongeScoreCriterion criterion;
    private final AdvancementProgress progress;

    private int score = -1;

    public SpongeScoreCriterionProgress(final AdvancementProgress progress, final SpongeScoreCriterion criterion) {
        this.criterion = criterion;
        this.progress = progress;
    }

    @Override
    public ScoreAdvancementCriterion criterion() {
        return this.criterion;
    }

    @Override
    public int score() {
        if (this.score == -1) {
            this.score = 0;
            for (final AdvancementCriterion criterion : this.criterion.internalCriteria) {
                final Optional<Instant> time1 = this.progress.get(criterion).get().get();
                if (time1.isPresent()) {
                    this.score++;
                }
            }
        }
        return this.score;
    }

    @Override
    public Optional<Instant> set(final int score) {
        Preconditions.checkState(score >= 0 && score <= this.goal(), "Score cannot be negative or greater than the goal.");
        int lastScore = this.score();
        if (lastScore == score) {
            return this.get();
        }
        final CriterionEvent.Score.Change event;
        final Cause cause = PhaseTracker.getCauseStackManager().currentCause();
        final Advancement advancement = this.progress.advancement();
        final ResourceKey advancementKey = (ResourceKey) (Object) ((AdvancementProgressBridge) this.progress).bridge$getAdvancementKey();
        final ServerPlayer player = ((PlayerAdvancementsBridge) ((AdvancementProgressBridge) this.progress).bridge$getPlayerAdvancements()).bridge$getPlayer();
        if (lastScore == this.goal()) {
            event = SpongeEventFactory.createCriterionEventScoreRevoke(
                    cause, advancement, advancementKey, this.criterion(), player, lastScore, score);
        } else if (score == this.goal()) {
            event = SpongeEventFactory.createCriterionEventScoreGrant(
                    cause, advancement, advancementKey, this.criterion(), player, Instant.now(), lastScore, score);
        } else {
            event = SpongeEventFactory.createCriterionEventScoreChange(
                    cause, advancement, advancementKey, this.criterion(), player, lastScore, score);
        }
        if (SpongeCommon.post(event)) {
            return this.get();
        }
        SpongeScoreCriterion.BYPASS_EVENT = true;
        // This is the only case a instant will be returned
        if (score == this.goal()) {
            Instant instant = null;
            for (final AdvancementCriterion criterion : this.criterion.internalCriteria) {
                final CriterionProgress progress = this.progress.get(criterion).get();
                if (!progress.achieved()) {
                    instant = progress.grant();
                }
            }
            this.score = score;
            return Optional.of(instant == null ? Instant.now() : instant);
        }
        for (final AdvancementCriterion criterion : this.criterion.internalCriteria) {
            final CriterionProgress progress = this.progress.get(criterion).get();
            // We don't have enough score, grant more criteria
            if (lastScore < score && !progress.achieved()) {
                progress.grant();
                lastScore++;

                // We have too much score, revoke more criteria
            } else if (lastScore > score && progress.achieved()) {
                progress.revoke();
                lastScore--;
            }
            // We reached the target score
            if (lastScore == score) {
                break;
            }
        }
        this.score = score;
        SpongeScoreCriterion.BYPASS_EVENT = false;
        return Optional.empty();
    }

    @Override
    public Optional<Instant> add(final int score) {
        return this.set(Mth.clamp(this.score() + score, 0, this.goal()));
    }

    @Override
    public Optional<Instant> remove(final int score) {
        return this.set(Mth.clamp(this.score() - score, 0, this.goal()));
    }

    @Override
    public Optional<Instant> get() {
        Optional<Instant> time = Optional.empty();
        for (final AdvancementCriterion criterion : this.criterion.internalCriteria) {
            final Optional<Instant> time1 = this.progress.get(criterion).get().get();
            if (!time1.isPresent()) {
                return Optional.empty();
            } else if (!time.isPresent() || time1.get().isAfter(time.get())) {
                time = time1;
            }
        }
        return time;
    }

    @Override
    public Instant grant() {
        return this.set(this.goal()).get();
    }

    @Override
    public Optional<Instant> revoke() {
        final Optional<Instant> previousState = this.get();
        this.set(0);
        return previousState;
    }

    @Override
    public void invalidateAchievedState() {
        this.score = -1;
    }
}
