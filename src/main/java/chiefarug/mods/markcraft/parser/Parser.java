package chiefarug.mods.markcraft.parser;

import chiefarug.mods.markcraft.Util;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static chiefarug.mods.markcraft.config.MarkCraftConfig.*;

public class Parser {

	public String message;
	public final List<Component> parsed = new ArrayList<>();
	public final MinecraftServer server;
	public String textBuffer = "";
	public Style style = Style.EMPTY.withColor(defaultColor);
	public boolean mention = false;
	@Nullable
	// null when no color set
	public TextColor color = null;

	public Parser(MinecraftServer s) {server = s;}

	public void setText(String text) {
		message = text;

		parsed.clear();
		textBuffer = "";
		style = Style.EMPTY.withColor(defaultColor);
		mention = false;
		color = null;
	}

	public Component parse() {
		Reader reader = new Reader(Emojis.decorate(message));
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

	@SuppressWarnings("SpellCheckingInspection")
	public void parseCharacter(Reader reader) {
		char c = reader.next();
		boolean charConsumed = false;

		// end of mentions (basically any punctuation)
		if (isMention() && (!Util.isUsernameCharacter(c) || reader.isLast())) {
			if (reader.isLast() || Util.isUsernameCharacter(c)) {
				charConsumed = true;
				addChar(c);
			}
			MutableComponent mentionComponent = getMentionComponent(textBuffer);
			if (mentionComponent == null) {
				update();
			} else {
				update(mentionComponent);
			}

			mention = false;
			style = style.withHoverEvent(null).withColor(color != null ? color : defaultColor);
			// don't return or add the character so that other processing can still happen
		}

		// \escape character
		// Has priority over everything except the end of a mention (note it will still get processed if used to end a mention as the mention does not return)
		if (isFormatted(escapeCharacters.get(), reader)) {
			if (reader.hasNext()) {
				addChar(reader.next());
			}
			return;
		}

		// **bold**
		if (isFormatted(boldCharacters.get(), reader)) {
			update();
			style = style.withBold(!isBold());
			return;
		}

		// __underline__
		if (isFormatted(underlinedCharacters.get(), reader)) {
			update();
			style = style.withUnderlined(!isUnderlined());
			return;
		}

		// *italics*
		if (isFormatted(italicCharacters.get(), reader)) {
			update();
			style = style.withItalic(!isItalic());
			return;
		}

		// ~~strikethrough~~
		if (isFormatted(strikethroughCharacters.get(), reader)) {
			update();
			style = style.withStrikethrough(!isStrikethrough());
			return;
		}

		// ||obfuscated||
		if (isFormatted(obfuscatedCharacters.get(), reader)) {
			update();
			style = style.withObfuscated(!isObfuscated());
			return;
		}

		// `ccolor`
		if (isFormatted(coloredCharacters.get(), reader)) {
			update();
			if (isColored() && reader.hasNext()) {
				TextColor _color = coloringCharacters.get(reader.next());
				color = _color == null ? defaultColor : _color;
			} else {
				color = null;
			}
			style = style.withColor(color != null ? color : defaultColor);
			return;
		}

		// @players
		if (isFormatted(mentionCharacters.get(), reader)) {
			update();
			mention = true;
			// Set the colour here so that it shows up even if the mention is not complete. We can't set the hover component here yet because we do not know the name.
			// We could resolve the name here though, because all the characters after this till a character that ends the mention will be part of the name
			// That would also minisculy improve performance because we don't do a bunch of unnescary format character checks.
			// TODO: Make this use a 'lookahead' system and do all logic here, instead of checking seperately for the end. (L#88)
			style = style.withColor(mentionColor);
			addChar(c);
			return;
		}

		// normal, boring text
		if (!charConsumed) {
			addChar(c);
		}
	}

	private boolean isFormatted(String formatCharacters, Reader reader) {
		if (reader.isLast()) {
			return reader.current() == formatCharacters.charAt(0);
		}
		int i = 0;
		for (;i < formatCharacters.length();i++) {
			if (reader.has(i) && reader.peek(i) != formatCharacters.charAt(i)) {
				return false;
			}
		}
		reader.move(i - 1);
		return true;
	}

	// TODO: Split mentions into their own class to tidy some of this up.
	private MutableComponent getMentionComponent(String name) {
		if (getEveryoneMention().equals(name)) {
			return getEveryoneMentionComponent();
		} else if (name.length() > 1) {
			return getPlayerNameHoverComponent(name.substring(mentionCharacters.get().length()));
		}
		return null;
	}

	@NotNull
	private String getEveryoneMention() {
		return mentionCharacters.get() + everyoneWord.get();
	}

	private MutableComponent getPlayerNameHoverComponent(String name) {
		ServerPlayer serverPlayer = getServerPlayer(name);
		if (serverPlayer != null) {
			return nameHoverComponent(serverPlayer.getGameProfile().getName(), serverPlayer.getName().copy(), serverPlayer.getUUID());
		}

		if (mentionAnyone.get()) {
			GameProfile profile = getPlayerProfile(name);
			if (profile != null) {
				String username = profile.getName();
				return nameHoverComponent(username, Component.literal(username) , profile.getId());
			}
		}
		return Component.literal(mentionCharacters.get() + name);
	}

	private MutableComponent getEveryoneMentionComponent() {
		style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translatableMentions.get() ? Component.translatable("markcraft.mentions.everyone") : Component.literal(everyoneWord.get())));
		return Component.literal(getEveryoneMention()).withStyle(style);
	}

	private MutableComponent nameHoverComponent(String username, MutableComponent name, UUID id) {
		MutableComponent hoverComponent;
		if (translatableMentions.get()) {
			hoverComponent = Component.translatable("markcraft.mentions.player", username, Component.literal(id.toString()).withStyle(ChatFormatting.GRAY));
		} else {
			hoverComponent = Component.literal(username).append(Component.literal('#' + id.toString()).withStyle(ChatFormatting.GRAY));
		}
		style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent));


		return Component.literal(mentionCharacters.get()).append(name);

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

	@Nullable
	private GameProfile getPlayerProfile(String name) {
		// TODO: use GameProfileCache#getAsync so the server doesn't freeze
		return server.getProfileCache().get(name).orElse(null);
	}

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
