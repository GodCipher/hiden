package dev.luzifer.hiden.items;

import dev.luzifer.hiden.HidenPlugin;
import dev.luzifer.hiden.game.PlayerTracker;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class BaseGrenade implements Grenade {

  private final Item item;
  private final int ticksUntilActivation;
  private final int ticksOfActivation;

  protected int ticks;
  protected final double radius;
  protected final Player thrower;
  protected final PlayerTracker playerTracker;

  private boolean done;

  public BaseGrenade(
      Item item,
      Player thrower,
      PlayerTracker playerTracker,
      int ticksUntilActivation,
      int ticksOfActivation,
      double radius) {
    this.item = item;
    this.thrower = thrower;
    this.playerTracker = playerTracker;
    this.ticksUntilActivation = ticksUntilActivation;
    this.ticksOfActivation = ticksOfActivation;
    this.radius = radius;
  }

  public void start() {
    new BukkitRunnable() {
      @Override
      public void run() {
        tick();
        if (done) cancel();
      }
    }.runTaskTimer(HidenPlugin.getInstance(), 0, 1);
  }

  @Override
  public void tick() {
    if (ticks < ticksUntilActivation) {
      if (ticks % 10 == 0) {
        showRadiusInACircleOutline();
      }
      untilActivation();
    } else if (ticks < ticksOfActivation) {
      afterActivation();
    } else {
      done = true;
      item.remove();
      onDone();
    }
    ticks++;
  }

  public Location getLocation() {
    return item.getLocation();
  }

  protected void showRadiusInACircleOutline() {
    int numParticles = 50;

    for (int i = 0; i < numParticles; i++) {
      double angle = 2 * Math.PI * i / numParticles;
      double x = radius * Math.cos(angle);
      double z = radius * Math.sin(angle);
      spawnParticleAtLocation(x, 0, z);
    }
  }

  private void spawnParticleAtLocation(double x, double y, double z) {
    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);
    getLocation()
        .getWorld()
        .spawnParticle(
            Particle.REDSTONE, getLocation().clone().add(x, y, z), 1, 0, 0, 0, 0, dustOptions);
  }

  public abstract void untilActivation();

  public abstract void afterActivation();

  public abstract void onDone();
}
