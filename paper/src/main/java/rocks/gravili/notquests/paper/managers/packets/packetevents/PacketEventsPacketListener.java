/*
 * NotQuests - A Questing plugin for Minecraft Servers
 * Copyright (C) 2022 Alessio Gravili
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rocks.gravili.notquests.paper.managers.packets.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import rocks.gravili.notquests.paper.NotQuests;

import java.util.ArrayList;

public class PacketEventsPacketListener implements PacketListener {
  private final NotQuests main;

  public PacketEventsPacketListener(final NotQuests main) {
    this.main = main;
  }

  public void handleMainChatHistorySavingLogic(
      final Component component, final Player player) {
    if (component == null) {
      return;
    }

    try {
      final ArrayList<Component> convHist =
          main.getConversationManager().getConversationChatHistory().get(player.getUniqueId());
      if (convHist != null && convHist.contains(component)) {
        return;
      }

      ArrayList<Component> hist =
          main.getConversationManager().getChatHistory().get(player.getUniqueId());
      if (hist != null) {
        hist.add(component);
      } else {
        hist = new ArrayList<>();
        hist.add(component);
      }

      int toRemove = hist.size() - main.getConversationManager().getMaxChatHistory();
      if (toRemove > 0) {
        hist.subList(0, toRemove).clear();
      }

      main.getConversationManager().getChatHistory().put(player.getUniqueId(), hist);
    } catch (Exception ignored) {
    }
  }

  @Override
  public void onPacketSend(PacketSendEvent event) {
    if (event.getPacketType() == PacketType.Play.Server.CHAT_MESSAGE) {

      WrapperPlayServerChatMessage wrapper = new WrapperPlayServerChatMessage(event);
      var message = wrapper.getMessage();

      // Skip actionbar messages
      if (message.getType() == ChatTypes.GAME_INFO) {
        return;
      }

      Component component = message.getChatContent();
      if (component == null) {
        return;
      }

      Player player = (Player) event.getPlayer();

      // Check for the conversation replay marker
      String plainText = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
          .plainText().serialize(component);

      if (!plainText.contains("fg9023zf729ofz")) {
        handleMainChatHistorySavingLogic(component, player);
      }
    }
  }
}
