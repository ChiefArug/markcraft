package chiefarug.mods.markcraft;

public class Util {
	private Util(){}

	public static boolean isUsernameCharacter(char c) {
		// See: https://help.minecraft.net/hc/en-us/articles/4408950195341-Minecraft-Java-Edition-Username-VS-Gamertag-FAQ#:~:text=Accepted%C2%A0characters%3A%C2%A0
		return Character.isLetterOrDigit(c) || c == '_';
	}

	public static boolean isValidUsername(String name) {
		if (name.isEmpty()) return false;
		for (int i = 0; i < name.length(); i++) {
			if (!isUsernameCharacter(name.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
