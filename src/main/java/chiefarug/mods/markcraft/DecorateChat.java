package chiefarug.mods.markcraft;

import chiefarug.mods.markcraft.parser.Parser;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static chiefarug.mods.markcraft.MarkCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DecorateChat {

	public static Parser parser;

	@SubscribeEvent
	public static void onServerStart(ServerStartedEvent event) {
		// We need the server to be able to get a player list. We could get this from the player passed in from the chat event, but I feel its nicer doing it here.
		parser = new Parser(event.getServer());
	}


	@SubscribeEvent
	public static void onChatReceived(ServerChatEvent event) {
		if (event.canChangeMessage()) {
			String message = event.getRawText();
			if (parser == null) {
				throw new IllegalStateException("Chat message received before the server started. How did this happen?");
			}
			parser.setText(message);

			event.setMessage(parser.parse());
		}
	}

	private DecorateChat(){}
}
