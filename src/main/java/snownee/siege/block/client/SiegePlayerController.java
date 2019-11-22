package snownee.siege.block.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.network.play.ClientPlayNetHandler;

public class SiegePlayerController extends PlayerController {

    public SiegePlayerController(Minecraft mcIn, ClientPlayNetHandler netHandler) {
        super(mcIn, netHandler);
    }

}
