package chiefarug.mods.markcraft;

import chiefarug.mods.markcraft.parser.Parser;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static chiefarug.mods.markcraft.MarkCraft.LGGR;
import static chiefarug.mods.markcraft.MarkCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class DecorateChatEvent {

	public static Parser PARSER;

	// We don't use the preview specific event because clients with preview off would not be able to use markdown.
	@SubscribeEvent
	static void onChatReceived(ServerChatEvent event) {
		if (event.canChangeMessage()) {
			String message = event.getRawText();

			if (PARSER == null) {
				LGGR.info("First message received. MarkCraft is go!");
				PARSER = new Parser(event.getPlayer().getServer());
			}
			event.setMessage(decorateMessage(message, PARSER));
		}
	}

	public static Component decorateMessage(String message, Parser parser) {
		parser.setText(message);
		return parser.parse();
	}

	private DecorateChatEvent(){}
}
