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
				// Then we subtract two more so that it actually works (π€·)
 				lastEmojiEnd = lastEmojiEnd - (emojiName.length() - emoji.length() + 2);
			}
		}

		return buffer.toString();
	}

	public static List<? extends String> defaultEmojis() {
		List<String> defaultEmojis = new ArrayList<>();
		// Fractions
		defaultEmojis.add("β:0/3");
		defaultEmojis.add("Β½:1/2");
		defaultEmojis.add("β:1/3");
		defaultEmojis.add("ΒΌ:1/4");
		defaultEmojis.add("β:1/5");
		defaultEmojis.add("β:1/6");
		defaultEmojis.add("β:1/7");
		defaultEmojis.add("β:1/8");
		defaultEmojis.add("β:1/9");
		defaultEmojis.add("β:1/10");
		defaultEmojis.add("β:2/3");
		defaultEmojis.add("β:2/5");
		defaultEmojis.add("ΒΎ:3/4");
		defaultEmojis.add("β:3/5");
		defaultEmojis.add("β:3/8");
		defaultEmojis.add("β:4/5");
		defaultEmojis.add("β:5/6");
		defaultEmojis.add("β:7/8");
		// Special MC stuff
		defaultEmojis.add("β§:target");
		defaultEmojis.add("β :skull");
		defaultEmojis.add("β:sword");
		defaultEmojis.add("β:pickaxe");
		defaultEmojis.add("πΉ:bow");
		defaultEmojis.add("πͺ:axe");
		defaultEmojis.add("π±:trident");
		defaultEmojis.add("π£:fishing_rod");
		defaultEmojis.add("π§ͺ:potion");
		defaultEmojis.add("β:splash_potion");
		defaultEmojis.add("π‘:shield");
		defaultEmojis.add("β¨:shield2");
		defaultEmojis.add("β:shears");
		defaultEmojis.add("π:hunger");
		defaultEmojis.add("πͺ£:bucket");
		defaultEmojis.add("π:bell");
		// Symbols
		defaultEmojis.add("β:tick");
		defaultEmojis.add("β:check");
		defaultEmojis.add("β:snowflake");
		defaultEmojis.add("β:cross");
		defaultEmojis.add("β€:heart");
		defaultEmojis.add("β­:star");
		defaultEmojis.add("β¦:diamond");
		defaultEmojis.add("β©:crotchet");
		defaultEmojis.add("β:1_die");
		defaultEmojis.add("β:2_die");
		defaultEmojis.add("β:3_die");
		defaultEmojis.add("β:4_die");
		defaultEmojis.add("β:5_die");
		defaultEmojis.add("β:6_die");
		defaultEmojis.add("β‘:lightning");
		defaultEmojis.add("β:anchor");
		defaultEmojis.add("οΏ½:unknown");
		defaultEmojis.add("β:flag");
		defaultEmojis.add("β³:hourglass");
		defaultEmojis.add("π₯:fire");
		defaultEmojis.add("π§:rain_cloud");
		defaultEmojis.add("π:wave");
		defaultEmojis.add("βΉ:information");
		defaultEmojis.add("β:point_left");
		defaultEmojis.add("β?:peace");
		defaultEmojis.add("β :warning");
		defaultEmojis.add("β―:yin_yang");
		defaultEmojis.add("β:box");
		defaultEmojis.add("β:checked_box");
		defaultEmojis.add("β:crossed_box");
		// Faces
		defaultEmojis.add("βΊ:smiley");
		defaultEmojis.add("β»:smiley2");
		defaultEmojis.add("βΉ:sad");

		// Controls
		defaultEmojis.add("β―:play_pause");
		defaultEmojis.add("β:eject");
		defaultEmojis.add("β©:fast_forward");
		defaultEmojis.add("βͺ:rewind");
		defaultEmojis.add("β­:skip");
		defaultEmojis.add("β?:back");
		defaultEmojis.add("β΅:play");
		defaultEmojis.add("β΅:right");
		defaultEmojis.add("β΄:left");
		defaultEmojis.add("βΆ:up");
		defaultEmojis.add("β·:down");
		defaultEmojis.add("βΈ:pause");
		defaultEmojis.add("βΉ:stop");
		defaultEmojis.add("βΊ:record");
		defaultEmojis.add("β»:power");
		defaultEmojis.add("βΌ:power2");
		defaultEmojis.add("β½:power_on");
		defaultEmojis.add("β­:power_off");
		// Cards
		defaultEmojis.add("β¦:diamond_symbol");
		defaultEmojis.add("β :spade_symbol");
		defaultEmojis.add("β₯:heart_symbol");
		defaultEmojis.add("β£:club_symbol");
		// Musical
		defaultEmojis.add("β©:quarter_note");
		defaultEmojis.add("βͺ:eighth_note");
		defaultEmojis.add("β«:beamed_eighth_note");
		defaultEmojis.add("β¬:beamed_sixteenth_note");
		// Coloring helpers
		for (ChatFormatting format : ChatFormatting.values()) {
			if (format.isColor()) {
				defaultEmojis.add("" + format.getChar() + ':' + format.name().toLowerCase());
			}
		}

		return defaultEmojis;
	}
}
