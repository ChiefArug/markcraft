package chiefarug.mods.markcraft.parser;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static chiefarug.mods.markcraft.MarkCraft.LOGGER;

public class Parser {

	public static final TextColor MENTION_COLOR = TextColor.fromRgb(7237375);

	public String message;
	public final List<Component> parsed = new ArrayList<>();
	public final MinecraftServer server;
	public String textBuffer = "";
	public Style style = Style.EMPTY;
	public boolean mention = false;
	@Nullable
	public ChatFormatting color = null;

	public Parser(MinecraftServer s){
		server = s;
	}

	public void setText(String text) {
		message = text;

		parsed.clear();
		textBuffer = "";
		style = Style.EMPTY;
		mention = false;
		color = null;
	}

	public Component parse() {
		Reader reader = new Reader(message);
		while (reader.hasNext()) {
			parseCharacter(reader);
		}
		update();

		return combine(parsed);
	}

	private Component combine(List<Component> components) {
		MutableComponent component = Component.empty().copy();
		for (Component c : components) {
			component.append(c);
		}

		return component;
	}

	public void update() {
		if (!textBuffer.isEmpty()) {
			update(Component.literal(textBuffer));
		}
	}

	public void update(MutableComponent base) {
		textBuffer = "";
		MutableComponent t = base.withStyle(style);
		parsed.add(t);
	}

	public void addChar(char c) {
		textBuffer += c;
	}

	public void parseCharacter(Reader reader) {
		char c = reader.next();
		boolean charConsumed = false;

		// \escape character
		// Has priority over everything else.
		if (c == '\\') {
			if (reader.hasNext()) {
				addChar(reader.next());
			}
			return;
		}

		// end of mentions (basically any punctuation)
		if (isMention() && (isNonWordCharacter(c) || !reader.hasNext())) {
			if (!(reader.hasNext() || isNonWordCharacter(c))) {
				charConsumed = true;
				addChar(c);
			}

			if ("@everyone".equals(textBuffer)) {
				style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("everyone")));
			} else 	if (textBuffer.length() > 1) {
				MutableComponent playerName = getPlayerNameComponent(textBuffer.substring(1));

				if (playerName != null) {
					update(playerName);
				}
			}
			update();
			mention = false;
			style = style.withHoverEvent(null).withColor((ChatFormatting) null);
			// don't return or add the character so that other processing can still happen
		}

		if (c == '*') {
			// **bold**
			if (reader.incrementIfNext('*')) {
				update();
				style = style.withBold(!isBold());
				return;
			}
			// *italics*
			update();
			style = style.withItalic(!isItalic());
			return;
		}

		// ~~strikethrough~~
		if (c == '~' && reader.incrementIfNext('~')) {
			update();
			style = style.withStrikethrough(!isStrikethrough());
			return;
		}

		// ||obfuscated||
		if (c == '|' && reader.incrementIfNext('|')) {
			update();
			style = style.withObfuscated(!isObfuscated());
			return;
		}

		// @players
		if (c == '@') {
			update();
			mention = true;
			style = style.withColor(MENTION_COLOR);
			addChar(c);
			return;
		}

		// `ccolor`
		if (c == '`') {
			update();
			if (isColored() && reader.hasNext()) {
				switch (reader.next()) {
					case '0' -> color = ChatFormatting.BLACK;
					case '1' -> color = ChatFormatting.DARK_BLUE;
					case '2' -> color = ChatFormatting.DARK_GREEN;
					case '3' -> color = ChatFormatting.DARK_AQUA;
					case '4' -> color = ChatFormatting.DARK_RED;
					case '5' -> color = ChatFormatting.DARK_PURPLE;
					case '6' -> color = ChatFormatting.GOLD;
					case '7' -> color = ChatFormatting.GRAY;
					case '8' -> color = ChatFormatting.DARK_GRAY;
					case '9' -> color = ChatFormatting.BLUE;
					case 'a' -> color = ChatFormatting.GREEN;
					case 'b' -> color = ChatFormatting.AQUA;
					case 'c' -> color = ChatFormatting.RED;
					case 'd' -> color = ChatFormatting.LIGHT_PURPLE;
					case 'e' -> color = ChatFormatting.YELLOW;
					default -> color = ChatFormatting.WHITE;
				}
			} else {
				color = null;
			}
			style = style.withColor(color);
			return;
		}

		// __underline__
		if (c == '_' && reader.incrementIfNext('_')) {
			update();
			style = style.withUnderlined(!isUnderlined());
			return;
		}

		// normal, boring text
		if (!charConsumed) {
			addChar(c);
		}
	}

	private boolean isNonWordCharacter(char c) {
		// special handling of underscores
		return !(Character.isLetter(c) || Character.isDigit(c) || c == '_');
	}

	private MutableComponent getPlayerNameComponent(String name) {
		ServerPlayer serverPlayer = getServerPlayer(name);
		if (serverPlayer != null) {
			return nameComponent(serverPlayer.getGameProfile().getName(), serverPlayer.getName().copy(), serverPlayer.getUUID());
		}

//		GameProfile profile = getPlayerProfile(name);
//		if (profile != null) {
//			return nameComponent(profile.getName(), profile.getId());
//		}
		return Component.literal('@' + name);
	}

//	private MutableComponent nameComponent(String username, UUID id) {
//		return nameComponent(username, Component.literal(username) ,id);
//	}

	private MutableComponent nameComponent(String username, MutableComponent name, UUID id) {
		style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(username).append(Component.literal('#' + id.toString()).withStyle(ChatFormatting.GRAY))));
		return Component.literal("@").append(name);

	}

	@Nullable
	private ServerPlayer getServerPlayer(String name) {
		name = name.toLowerCase(Locale.ROOT);
		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			if (p.getGameProfile().getName().toLowerCase(Locale.ROOT).contains(name)) {
				return p;
			}
		}
		return null;
	}

//	@Nullable
//	private  GameProfile getPlayerProfile(String name) {
//		return server.getProfileCache().get(name).orElse(null);
//	}

	private boolean isBold() {
		return style.isBold();
	}
	private boolean isItalic() {
		return style.isItalic();
	}
	private boolean isMention() {
		return mention;
	}
	private boolean isObfuscated() {
		return style.isObfuscated();
	}
	private boolean isUnderlined() {
		return style.isUnderlined();
	}
	private boolean isStrikethrough() {
		return style.isStrikethrough();
	}
	private boolean isColored() {
		return color == null;
	}


}
