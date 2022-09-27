package chiefarug.mods.markcraft.config;

import chiefarug.mods.markcraft.Util;
import chiefarug.mods.markcraft.parser.Emojis;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkCraftConfig {

	public static final ForgeConfigSpec SPEC;

	public static ForgeConfigSpec.BooleanValue mentionAnyone;
	public static ForgeConfigSpec.BooleanValue translatableMentions;
	public static ForgeConfigSpec.ConfigValue<String> everyoneWord;
	public static ForgeConfigSpec.ConfigValue<String> escapeCharacters;
	public static ForgeConfigSpec.ConfigValue<String> emojiCharacters;
	public static ForgeConfigSpec.ConfigValue<String> boldCharacters;
	public static ForgeConfigSpec.ConfigValue<String> underlinedCharacters;
	public static ForgeConfigSpec.ConfigValue<String> italicCharacters;
	public static ForgeConfigSpec.ConfigValue<String> strikethroughCharacters;
	public static ForgeConfigSpec.ConfigValue<String> obfuscatedCharacters;
	public static ForgeConfigSpec.ConfigValue<String> coloredCharacters;
	public static ForgeConfigSpec.ConfigValue<String> mentionCharacters;
	private static ForgeConfigSpec.ConfigValue<String> _mentionColor;
	public static TextColor mentionColor;
	private static ForgeConfigSpec.ConfigValue<String> _defaultColor;
	public static TextColor defaultColor;

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> _coloringCharacters;
	public static final Map<Character, TextColor> coloringCharacters = new HashMap<>();

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> _emojis;
	public static final Map<String, String> emojis = new HashMap<>();

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		setupConfig(builder);

		SPEC = builder.build();

		// Because they use different buses we cannot use the annotations nicely
		MinecraftForge.EVENT_BUS.addListener(MarkCraftConfig::serverStart);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(MarkCraftConfig::configUpdate);
	}

	private static void setupConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("""
			Allows @mentions to mention any valid minecraft username.
			NOT RECOMMENDED
			It currently freezes the server while doing the lookup the first time each username is typed in""");
		mentionAnyone = builder.define("mention_anyone", false);
		builder.comment("""
			Use a translatable component for mentions hover text. This allows changing what it says with a lang file (mainly to support different languages),
			however requires that the mod or a compatible resource pack is also on client side""");
		translatableMentions = builder.define("translatable_mentions", false);
		builder.comment("The word for mentioning all players. everyone by default. Must be a valid minecraft username");
		everyoneWord = builder.define("everyone_word", "everyone", o -> o instanceof String s && Util.isValidUsername(s));

		builder.push("Character sets").comment("""
				Characters used for the different types of formatting.
				They are evaluated in the same order as below (this is important for things like bold and italic that both use *)""");
			escapeCharacters = builder.define("escape_characters",  "\\");
			emojiCharacters = builder.define("emoji_characters", ":");
			boldCharacters = builder.define("bold_characters",  "**");
			underlinedCharacters = builder.define("underlined_characters",  "__");
			italicCharacters = builder.define("italic_characters",  "*");
			strikethroughCharacters = builder.define("strikethrough_characters",  "~~");
			obfuscatedCharacters = builder.define("obfuscated_characters",  "||");
			coloredCharacters = builder.define("colored_characters",  "`");
			mentionCharacters = builder.define("mention_characters",  "@");
		builder.pop();

		builder.comment("The color used for @mentions");
		_mentionColor = builder.define("mention_color", "6E6EFF", Util::isHexColor);
		builder.comment("The default color when no color is set using colored_characters and colors.");
		_defaultColor = builder.define("default_color", "FFFFFF", Util::isHexColor);

		builder.comment("""
			The characters and colors used along with the colored_characters to color text.
			Should be in the format c:3f3f3f where c is any character and 3f3f3f is any number in hex format""");
		_coloringCharacters = builder.defineList("colors", defaultColors(), o ->
				o instanceof String s &&
						s.length() > 2 &&
						s.length() < 9 &&
						s.charAt(1) == ':' &&
						StringUtils.isAlphanumeric(s.substring(2))
		);

		builder.comment("""
		Emoji's in the format of 🙂:name where 🙂 is the emoji character and name is the emoji name.
		Note that Minecraft's default font only supports a few emojis and so some emojis that you add may show up as boxes in chat.
		This can be fixed with a font in a resource pack that includes these characters.
		Emoji names can contain almost anything except whatever is set as emoji_characters. Case is ignored""");
		_emojis = builder.defineList("emojis", Emojis.defaultEmojis(), o ->
				o instanceof String s &&
						s.length() > 2 &&
						s.indexOf(':') > 0 &&
						!s.substring(s.indexOf(':') + 1).contains(emojiCharacters.get())
		);
	}

	@SuppressWarnings("ConstantConditions")
	private static List<? extends String> defaultColors() {
		List<String> colors = new ArrayList<>();
		for (ChatFormatting format : ChatFormatting.values()) {
			if (format.isColor()) {
				colors.add("" + format.getChar() + ':' + Integer.toHexString(format.getColor()).toUpperCase());
			}
		}
		return colors;
	}

	private static void configUpdate(ModConfigEvent.Reloading event) {
		if (event.getConfig().getSpec() == SPEC) {
			updateColors();
			updateEmojis();
		}
	}

	private static void serverStart(ServerStartedEvent event) {
		updateColors();
		updateEmojis();
	}

	private static void updateEmojis() {
		emojis.clear();
		for (String emojiPair : _emojis.get()) {
			int split = emojiPair.indexOf(':');

			String emoji = emojiPair.substring(0, split);
			String name = emojiPair.substring(split + 1).toLowerCase();

			emojis.put(name, emoji);
		}
	}

	private static void updateColors() {
		coloringCharacters.clear();
		for (String colorPair : _coloringCharacters.get()) {
			// We know these are valid because they get validated by the lambda we pass to the builder
			char character = colorPair.charAt(0);
			int number = Integer.parseInt(colorPair.substring(2), 16);

			coloringCharacters.put(character, TextColor.fromRgb(number));
		}

		mentionColor = TextColor.fromRgb(Integer.parseInt(_mentionColor.get(), 16));
		defaultColor = TextColor.fromRgb(Integer.parseInt(_defaultColor.get(), 16));
	}
}
