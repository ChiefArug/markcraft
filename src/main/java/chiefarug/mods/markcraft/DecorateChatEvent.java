package chiefarug.mods.markcraft;

import chiefarug.mods.markcraft.parser.Parser;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static chiefarug.mods.markcraft.MarkCraft.LGGR;
import static chiefarug.mods.markcraft.MarkCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class DecorateChatEvent {

	public static Parser parser;

	// We don't use the preview specific event because clients with preview off would not be able to use markdown.
	@SubscribeEvent
	public static void onChatReceived(ServerChatEvent event) {
		if (event.canChangeMessage()) {
			String message = event.getRawText();

			if (parser == null) {
				LGGR.info("First message received. MarkCraft is go!");
				parser = new Parser(event.getPlayer().getServer());
			}
			parser.setText(message);

			event.setMessage(parser.parse());
		}
	}

	private DecorateChatEvent(){}
}
