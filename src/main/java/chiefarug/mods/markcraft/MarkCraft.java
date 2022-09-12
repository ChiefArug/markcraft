package chiefarug.mods.markcraft;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(MarkCraft.MODID)
public class MarkCraft {
	public static final String MODID = "markcraft";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MarkCraft() {
		ForgeMod.enableServerChatPreview();
	}
}