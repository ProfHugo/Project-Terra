package profhugo.terra;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import profhugo.terra.proxy.CommonProxy;

@Mod(modid = ProjectTerra.MODID, name = ProjectTerra.NAME, version = ProjectTerra.VERSION, acceptedMinecraftVersions = "[1.10.2]")
public class ProjectTerra {
	public static final String MODID = "project_terra";
	public static final String NAME = "Project Terra: The Overhaul";
	public static final String VERSION = "0.0.4";

	@Mod.Instance(MODID)
	public static ProjectTerra instance;

	@SidedProxy(serverSide = "profhugo.terra.proxy.CommonProxy", clientSide = "profhugo.terra.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		System.out.println(NAME + " is loading!");
		proxy.preInit();

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
