package chiefarug.mods.markcraft.config;

import chiefarug.mods.markcraft.Util;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static chiefarug.mods.markcraft.MarkCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MarkCraftConfig {

	public static final ForgeConfigSpec SPEC;

	public static ForgeConfigSpec.BooleanValue mentionAnyone;
	public static ForgeConfigSpec.BooleanValue translatableMentions;
	public static ForgeConfigSpec.ConfigValue<String> everyoneWord;
	public static ForgeConfigSpec.ConfigValue<String> escapeCharacters;
	public static ForgeConfigSpec.ConfigValue<String> boldCharacters;
	public static ForgeConfigSpec.ConfigValue<String> underlinedCharacters;
	public static ForgeConfigSpec.ConfigValue<String> italicCharacters;
	public static ForgeConfigSpec.ConfigValue<String> strikethroughCharacters;
	public static ForgeConfigSpec.ConfigValue<String> obfuscatedCharacters;
	public static ForgeConfigSpec.ConfigValue<String> coloredCharacters;
	public static ForgeConfigSpec.ConfigValue<String> mentionCharacters;

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> _coloringCharacters;
	public static final Map<Character, Integer> coloringCharacters = new HashMap<>();

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		setupConfig(builder);

		SPEC = builder.build();
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

		builder.push("Character sets").comment("Characters used for the different types of formatting");
			escapeCharacters = builder.define("escape_characters",  "\\");
			boldCharacters = builder.define("bold_characters",  "**");
			underlinedCharacters = builder.define("underlined_characters",  "__");
			italicCharacters = builder.define("italic_characters",  "*");
			strikethroughCharacters = builder.define("strikethrough_characters",  "~~");
			obfuscatedCharacters = builder.define("obfuscated_characters",  "||");
			coloredCharacters = builder.define("colored_characters",  "`");
			mentionCharacters = builder.define("mention_characters",  "@");
		builder.pop();

		builder.comment("""
			The characters and colors used along with the colored_characters to color text.
			Should be in the format c:3f3f3f where c is any character and 3f3f3f is any number in hex format""");
		_coloringCharacters = builder.defineList("colors", defaultColors(),
				o -> o instanceof String s &&
						s.length() > 2 &&
						s.length() < 9 &&
						s.charAt(1) == ':' &&
						StringUtils.isAlphanumeric(s.substring(2)));
	}

	@SuppressWarnings("ConstantConditions")
	private static List<? extends String> defaultColors() {
		List<String> colors = new ArrayList<>();
		for (ChatFormatting format : ChatFormatting.values()) {
			if (format.isColor()) {
				colors.add(String.valueOf(format.getChar()) + ':' + Integer.toHexString(format.getColor()));
			}
		}
		return colors;
	}

	@SubscribeEvent
	public static void configUpdate(ModConfigEvent.Reloading event) {
		if (event.getConfig().getSpec() == SPEC) {
			coloringCharacters.clear();
			for (String colorPair : _coloringCharacters.get()) {
				// We know these are valid because they get validated by the lambda we pass to the builder
				char character = colorPair.charAt(0);
				int number = Integer.parseInt(colorPair.substring(2), 16);
				coloringCharacters.put(character, number);
			}
		}
	}
}
