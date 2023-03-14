package com.github.peco2282.durabilityviewer;

import com.github.peco2282.durabilityviewer.config.ConfigurationProvider;
import com.github.peco2282.durabilityviewer.event.KeyInputEvent;
import com.github.peco2282.durabilityviewer.event.TooltipEvent;
import com.github.peco2282.durabilityviewer.gui.GuiItemDurability;
import com.github.peco2282.durabilityviewer.gui.GuiModOptions;
import com.github.peco2282.durabilityviewer.handler.ConfigurationHandler;
import com.github.peco2282.durabilityviewer.handler.KeyHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Durabilityviewer.MODID)
public class Durabilityviewer {

  // Define mod id in a common place for everything to reference
  public static final String MODID = "newdurabilityviewer";
  // Directly reference a slf4j logger
  public static final Logger LOGGER = LogUtils.getLogger();
  static String windowTitle;
  private static ConfigurationHandler confHandler;

  /**
  // Create a Deferred Register to hold Blocks which will all be registered under the "durabilityviewer" namespace
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
  // Create a Deferred Register to hold Items which will all be registered under the "durabilityviewer" namespace
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

  // Creates a new Block with the id "durabilityviewer:example_block", combining the namespace and path
  public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
  // Creates a new BlockItem with the id "durabilityviewer:example_block", combining the namespace and path
  public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));
*/
  public Durabilityviewer() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // Register the Deferred Register to the mod event bus so blocks get registered
//    BLOCKS.register(modEventBus);
    // Register the Deferred Register to the mod event bus so items get registered
//    ITEMS.register(modEventBus);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);

    // Register the item to a creative tab
//    modEventBus.addListener(this::addCreative);
  }

  @OnlyIn(Dist.CLIENT)
  public static void openConfigScreen() {
    Minecraft.getInstance().setScreen(new GuiModOptions(null, MODID, confHandler));
  }

  public static String changedWindowTitle() {
    String result = windowTitle;
    windowTitle = null;
    return result;
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    if (FMLEnvironment.dist.isClient()) {
      windowTitle = null;
      confHandler = ConfigurationHandler.getInstance();
      confHandler.load(ConfigurationProvider.getSuggestedFile(MODID));
      KeyHandler.init();
      System.out.println("on Init, confHandler is " + confHandler);
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(confHandler);
      MinecraftForge.EVENT_BUS.register(new KeyInputEvent());
      MinecraftForge.EVENT_BUS.register(new TooltipEvent());
      MinecraftForge.EVENT_BUS.register(new GuiItemDurability());
    } else {
      System.err.println(MODID + " detected a dedicated server. Not doing anything.");
    }
    // Some common setup code
    LOGGER.info("HELLO FROM COMMON SETUP");
    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
  }

  /**
   * private void addCreative(CreativeModeTabEvent.BuildContents event) {
   * if (event.getTab() == CreativeModeTabs.BUILDING_BLOCKS)
   * event.accept(EXAMPLE_BLOCK_ITEM);
   * }
   */
  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {
    // Do something when the server starts
    LOGGER.info("HELLO from server starting");
  }

  // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
  @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  public static class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
      // Some client setup code
      LOGGER.info("HELLO FROM CLIENT SETUP");
      LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
  }
}
