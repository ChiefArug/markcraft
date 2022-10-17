package chiefarug.mods.markcraft.parser;

import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

import static chiefarug.mods.markcraft.config.MarkCraftConfig.emojiCharacters;
import static chiefarug.mods.markcraft.config.MarkCraftConfig.emojis;

public class Emojis {

	public static String decorate(String string) {
		if (!string.contains(emojiCharacters.get())) return string;

		final StringBuilder buffer = new StringBuilder(string);
		boolean hasEmojiLeft = true;
		int lastEmojiEnd = -1;

		while (hasEmojiLeft) {
			// TODO: Move this into another method for readability
			int startIndex = buffer.indexOf(emojiCharacters.get(), lastEmojiEnd + 1) + 1;
			if (startIndex == 0) return buffer.toString();
			// add 1 here again because minimum emoji length is 1
			int endIndex = buffer.indexOf(emojiCharacters.get(), startIndex);
			if (endIndex == -1) {
				hasEmojiLeft = false;
				endIndex = buffer.length();
			}
			lastEmojiEnd = endIndex;

			String emojiName = buffer.substring(startIndex, endIndex).toLowerCase();
			String emoji = emojis.get(emojiName);
			if (emoji != null) {
				buffer.replace(startIndex - 1, endIndex == buffer.length() ? endIndex : endIndex + 1, emoji);
				// Could be a better way to do this, but for now this works.
				// subtract the difference in length from the lastEmojiEnd so that it's looking in the correct place
				// Then we subtract two more so that it actually works (🤷)
 				lastEmojiEnd = lastEmojiEnd - (emojiName.length() - emoji.length() + 2);
			}
		}

		return buffer.toString();
	}

	public static List<? extends String> defaultEmojis() {
		List<String> defaultEmojis = new ArrayList<>();
		// Fractions
		defaultEmojis.add("↉:0/3");
		defaultEmojis.add("½:1/2");
		defaultEmojis.add("⅓:1/3");
		defaultEmojis.add("¼:1/4");
		defaultEmojis.add("⅕:1/5");
		defaultEmojis.add("⅙:1/6");
		defaultEmojis.add("⅐:1/7");
		defaultEmojis.add("⅛:1/8");
		defaultEmojis.add("⅑:1/9");
		defaultEmojis.add("⅒:1/10");
		defaultEmojis.add("⅔:2/3");
		defaultEmojis.add("⅖:2/5");
		defaultEmojis.add("¾:3/4");
		defaultEmojis.add("⅗:3/5");
		defaultEmojis.add("⅜:3/8");
		defaultEmojis.add("⅘:4/5");
		defaultEmojis.add("⅚:5/6");
		defaultEmojis.add("⅞:7/8");
		// Special MC stuff
		defaultEmojis.add("⧈:target");
		defaultEmojis.add("☠:skull");
		defaultEmojis.add("⚔:sword");
		defaultEmojis.add("⛏:pickaxe");
		defaultEmojis.add("🏹:bow");
		defaultEmojis.add("🪓:axe");
		defaultEmojis.add("🔱:trident");
		defaultEmojis.add("🎣:fishing_rod");
		defaultEmojis.add("🧪:potion");
		defaultEmojis.add("⚗:splash_potion");
		defaultEmojis.add("🛡:shield");
		defaultEmojis.add("⛨:shield2");
		defaultEmojis.add("✂:shears");
		defaultEmojis.add("🍖:hunger");
		defaultEmojis.add("🪣:bucket");
		defaultEmojis.add("🔔:bell");
		// Symbols
		defaultEmojis.add("✔:tick");
		defaultEmojis.add("✔:check");
		defaultEmojis.add("❄:snowflake");
		defaultEmojis.add("❌:cross");
		defaultEmojis.add("❤:heart");
		defaultEmojis.add("⭐:star");
		defaultEmojis.add("♦:diamond");
		defaultEmojis.add("♩:crotchet");
		defaultEmojis.add("⚀:1_die");
		defaultEmojis.add("⚁:2_die");
		defaultEmojis.add("⚂:3_die");
		defaultEmojis.add("⚃:4_die");
		defaultEmojis.add("⚄:5_die");
		defaultEmojis.add("⚅:6_die");
		defaultEmojis.add("⚡:lightning");
		defaultEmojis.add("⚓:anchor");
		defaultEmojis.add("�:unknown");
		defaultEmojis.add("⚑:flag");
		defaultEmojis.add("⏳:hourglass");
		defaultEmojis.add("🔥:fire");
		defaultEmojis.add("🌧:rain_cloud");
		defaultEmojis.add("🌊:wave");
		defaultEmojis.add("ℹ:information");
		defaultEmojis.add("☜:point_left");
		defaultEmojis.add("☮:peace");
		defaultEmojis.add("⚠:warning");
		defaultEmojis.add("☯:yin_yang");
		defaultEmojis.add("☐:box");
		defaultEmojis.add("☑:checked_box");
		defaultEmojis.add("☒:crossed_box");
		// Faces
		defaultEmojis.add("☺:smiley");
		defaultEmojis.add("☻:smiley2");
		defaultEmojis.add("☹:sad");

		// Controls
		defaultEmojis.add("⏯:play_pause");
		defaultEmojis.add("⏏:eject");
		defaultEmojis.add("⏩:fast_forward");
		defaultEmojis.add("⏪:rewind");
		defaultEmojis.add("⏭:skip");
		defaultEmojis.add("⏮:back");
		defaultEmojis.add("⏵:play");
		defaultEmojis.add("⏵:right");
		defaultEmojis.add("⏴:left");
		defaultEmojis.add("⏶:up");
		defaultEmojis.add("⏷:down");
		defaultEmojis.add("⏸:pause");
		defaultEmojis.add("⏹:stop");
		defaultEmojis.add("⏺:record");
		defaultEmojis.add("⏻:power");
		defaultEmojis.add("⏼:power2");
		defaultEmojis.add("⏽:power_on");
		defaultEmojis.add("⭘:power_off");
		// Cards
		defaultEmojis.add("♦:diamond_symbol");
		defaultEmojis.add("♠:spade_symbol");
		defaultEmojis.add("♥:heart_symbol");
		defaultEmojis.add("♣:club_symbol");
		// Musical
		defaultEmojis.add("♩:quarter_note");
		defaultEmojis.add("♪:eighth_note");
		defaultEmojis.add("♫:beamed_eighth_note");
		defaultEmojis.add("♬:beamed_sixteenth_note");
		// Coloring helpers
		for (ChatFormatting format : ChatFormatting.values()) {
			if (format.isColor()) {
				defaultEmojis.add("" + format.getChar() + ':' + format.name().toLowerCase());
			}
		}

		return defaultEmojis;
	}
}
