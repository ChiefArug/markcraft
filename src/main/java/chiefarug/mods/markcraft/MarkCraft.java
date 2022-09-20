package chiefarug.mods.markcraft;

import chiefarug.mods.markcraft.config.MarkCraftConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(MarkCraft.MODID)
public class MarkCraft {
	public static final String MODID = "markcraft";
	@SuppressWarnings("unused")
	public static final Logger LGGR = LogUtils.getLogger();

	public MarkCraft() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MarkCraftConfig.SPEC, MODID + "-server.toml");
		ForgeMod.enableServerChatPreview();
	}
}