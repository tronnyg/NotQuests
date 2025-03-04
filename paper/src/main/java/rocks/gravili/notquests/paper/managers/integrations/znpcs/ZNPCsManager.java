package rocks.gravili.notquests.paper.managers.integrations.znpcs;

import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.npc.NPC;
import rocks.gravili.notquests.paper.NotQuests;

import java.util.ArrayList;

public class ZNPCsManager {
  private final NotQuests main;
  private final ServersNPC serversNPC;

  public ZNPCsManager(final NotQuests main) {
    this.main = main;
    serversNPC = (ServersNPC) main.getMain().getServer().getPluginManager().getPlugin("ServersNPC");
  }


  public final ArrayList<Integer> getAllNPCIDs(){
    final ArrayList<Integer> npcIDs = new ArrayList<>();

    for (final NPC npc : NPC.all()) {
      npcIDs.add(npc.getEntityID());
    }
    return npcIDs;
  }
}
