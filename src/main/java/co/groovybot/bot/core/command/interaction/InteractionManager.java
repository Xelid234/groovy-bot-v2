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

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class InteractionManager {

    @Getter
    private final Map<Long, InteractableMessage> interactionStorage;

    public InteractionManager() {
        this.interactionStorage = new HashMap<>();
    }

    protected void register(InteractableMessage message) {
        interactionStorage.put(message.getIdentifier(), message);
    }

    public void unregister(InteractableMessage message) {
        interactionStorage.remove(message.getIdentifier());
    }

    protected void update(InteractableMessage message) {
        interactionStorage.replace(message.getIdentifier(), message);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onReaction(GuildMessageReactionAddEvent event) {
        log.debug("REACTION");
        if (event.getUser().isBot())
            return;
        log.debug("REACTION 2");
        if (!isInteractable(event.getUser().getIdLong())) return;
        log.debug("REACTION 3");
        InteractableMessage interactableMessage = interactionStorage.get(event.getUser().getIdLong());
        log.debug("REACTION 4");
        event.getReaction().removeReaction(event.getUser()).queue();
        log.debug("REACTION 5");
        if (!checkAuthor(interactableMessage, event.getUser())) return;
        log.debug("REACTION 6");
        interactableMessage.handleReaction(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onMessage(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        final long authorId = event.getAuthor().getIdLong();
        if (!isInteractable(authorId)) return;
        InteractableMessage interactableMessage = interactionStorage.get(authorId);
        event.getMessage().delete().queue();
        if (!checkAuthor(interactableMessage, event.getAuthor())) return;
        interactableMessage.handleMessage(event);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    private void onMessageDeletion(GuildMessageDeleteEvent event) {
        interactionStorage.remove(event.getMessageIdLong());
    }

    private boolean isInteractable(long message) {
        return interactionStorage.containsKey(message);
    }

    private boolean checkAuthor(InteractableMessage message, User author) {
        return message.getAuthor().getUser().equals(author);
    }
}
