/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.core.command.interaction;

import co.groovybot.bot.GroovyBot;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;

public abstract class InteractableMessage {

    @Getter
    private final Message infoMessage;
    @Getter
    private final TextChannel channel;
    @Getter
    private final Member author;
    @Getter
    private final long identifier;

    public InteractableMessage(Message infoMessage, TextChannel channel, Member author, long identifier) {
        this.infoMessage = infoMessage;
        this.channel = channel;
        this.author = author;
        this.identifier = identifier;
        GroovyBot.getInstance().getInteractionManager().register(this);
    }

    protected void update() {
        GroovyBot.getInstance().getInteractionManager().update(this);
    }

    protected void unregister() {
        onDelete();
        infoMessage.delete().queue();
        GroovyBot.getInstance().getInteractionManager().unregister(this);
    }

    protected void handleReaction(GuildMessageReactionAddEvent event) {

    }

    protected void handleMessage(GuildMessageReceivedEvent event) {

    }

    protected String translate(User user, String key) {
        return GroovyBot.getInstance().getTranslationManager().getLocaleByUser(user.getId()).translate(key);
    }

    public void onDelete() {
        //Empty method
    }
}
