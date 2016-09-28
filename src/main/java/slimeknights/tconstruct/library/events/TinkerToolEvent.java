package slimeknights.tconstruct.library.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ranged.BowCore;

public abstract class TinkerToolEvent extends TinkerEvent {

  public final ItemStack itemStack;
  public final ToolCore tool;

  public TinkerToolEvent(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.tool = (ToolCore) itemStack.getItem();
  }

  @Cancelable
  public static class ExtraBlockBreak extends TinkerToolEvent {

    public final EntityPlayer player;
    public final IBlockState state;

    public int width;
    public int height;
    public int depth;
    public int distance;

    public ExtraBlockBreak(ItemStack itemStack, EntityPlayer player, IBlockState state) {
      super(itemStack);
      this.player = player;
      this.state = state;
    }

    public static ExtraBlockBreak fireEvent(ItemStack itemStack, EntityPlayer player, IBlockState state, int width, int height, int depth, int distance) {
      ExtraBlockBreak event = new ExtraBlockBreak(itemStack, player, state);
      event.width = width;
      event.height = height;
      event.depth = depth;
      event.distance = distance;

      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }
  }

  public static class OnRepair extends TinkerToolEvent {

    public final int amount;

    public OnRepair(ItemStack itemStack, int amount) {
      super(itemStack);
      this.amount = amount;
    }

    public static boolean fireEvent(ItemStack itemStack, int amount) {
      OnRepair event = new OnRepair(itemStack, amount);
      return !MinecraftForge.EVENT_BUS.post(event);
    }
  }

  public static class OnMattockHoe extends TinkerToolEvent {

    public final BlockPos pos;
    public final World world;
    public final EntityPlayer player;

    public OnMattockHoe(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      super(itemStack);
      this.player = player;
      this.pos = pos;
      this.world = world;
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      MinecraftForge.EVENT_BUS.post(new OnMattockHoe(itemStack, player, world, pos));
    }
  }

  public static class OnShovelMakePath extends TinkerToolEvent {

    public final BlockPos pos;
    public final EntityPlayer player;
    private final World world;

    public OnShovelMakePath(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      super(itemStack);
      this.pos = pos;
      this.player = player;
      this.world = world;
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos) {
      MinecraftForge.EVENT_BUS.post(new OnShovelMakePath(itemStack, player, world, pos));
    }
  }

  @HasResult
  public static class ScytheCanHarvest extends TinkerToolEvent {
    public final BlockPos pos;
    public final EntityPlayer player;
    public final IBlockState blockState;
    private final World world;

    public ScytheCanHarvest(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, IBlockState blockState) {
      super(itemStack);
      this.pos = pos;
      this.player = player;
      this.world = world;
      this.blockState = blockState;
    }

    public static boolean fireEvent(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, IBlockState blockState, boolean canHarvest) {
      ScytheCanHarvest event = new ScytheCanHarvest(itemStack, player, world, pos, blockState);
      event.setResult(canHarvest ? Result.ALLOW : Result.DENY);
      MinecraftForge.EVENT_BUS.post(event);

      return event.getResult() != Result.DENY;
    }
  }

  @Cancelable
  public static class OnScytheHarvest extends TinkerToolEvent {
    public final BlockPos pos;
    public final EntityPlayer player;
    public final IBlockState blockState;
    private final World world;

    public OnScytheHarvest(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, IBlockState blockState) {
      super(itemStack);
      this.pos = pos;
      this.player = player;
      this.world = world;
      this.blockState = blockState;
    }

    public static boolean fireEvent(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, IBlockState blockState) {
      return !MinecraftForge.EVENT_BUS.post(new OnScytheHarvest(itemStack, player, world, pos, blockState));
    }
  }

  public static class OnBowShoot extends TinkerToolEvent {

    public final EntityPlayer entityPlayer;
    public final BowCore bowCore;
    public final ItemStack ammo;
    public final int useTime;

    public int projectileCount = 1;
    public boolean consumeAmmoPerProjectile = true;
    public boolean consumeDurabilityPerProjectile = true;

    public OnBowShoot(ItemStack bow, ItemStack ammo, EntityPlayer entityPlayer, int useTime) {
      super(bow);
      this.bowCore = (BowCore) bow.getItem();
      this.ammo = ammo;
      this.entityPlayer = entityPlayer;
      this.useTime = useTime;
    }

    public static OnBowShoot fireEvent(ItemStack bow, ItemStack ammo, EntityPlayer entityPlayer , int useTime) {
      OnBowShoot event = new OnBowShoot(bow, ammo, entityPlayer, useTime);
      MinecraftForge.EVENT_BUS.post(event);
      return event;
    }

    public void setProjectileCount(int projectileCount) {
      this.projectileCount = projectileCount;
    }

    public void setConsumeAmmoPerProjectile(boolean consumeAmmoPerProjectile) {
      this.consumeAmmoPerProjectile = consumeAmmoPerProjectile;
    }

    public void setConsumeDurabilityPerProjectile(boolean consumeDurabilityPerProjectile) {
      this.consumeDurabilityPerProjectile = consumeDurabilityPerProjectile;
    }
  }
}
