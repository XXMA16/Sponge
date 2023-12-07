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
package org.spongepowered.test.scoreboard;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.criteria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("scoreboardtest")
public final class ScoreboardTest {

    private final PluginContainer pluginContainer;

    @Inject
    public ScoreboardTest(final PluginContainer pluginContainer) {
        this.pluginContainer = pluginContainer;
    }

    @Listener
    private void registerCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        event.register(this.pluginContainer, Command.builder()
                .executor(this::doScoreboardStuff)
                .build(), "scoreboardtest");
    }

    public CommandResult doScoreboardStuff(CommandContext ctx) {
        try {
            ctx.cause().first(ServerPlayer.class).ifPresent(player -> {
                final Scoreboard scoreboard = player.scoreboard();
                scoreboard.objective("testObjective").ifPresentOrElse(o -> {
                    scoreboard.removeObjective(o);
                    ctx.sendMessage(Component.text("Objective removed"));
                }, () -> {
                    final Objective test = Objective.builder().criterion(Criteria.DUMMY).name("testObjective").displayName(Component.text("testObjectiveDisplay")).build();
                    final Score score = test.findOrCreateScore("testScore");
                    score.setScore(200);
                    score.setDisplay(Component.text("TestScoreDisplay"));
                    scoreboard.addObjective(test);
                    scoreboard.updateDisplaySlot(test, DisplaySlots.SIDEBAR);
                    ctx.sendMessage(Component.text("Objective set"));
                });
                player.setScoreboard(scoreboard);
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return CommandResult.success();
    }


}
