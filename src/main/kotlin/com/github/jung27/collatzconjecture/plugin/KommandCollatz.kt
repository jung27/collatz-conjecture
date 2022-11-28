package com.github.jung27.collatzconjecture.plugin

import io.github.monun.kommand.PluginKommand
import io.github.monun.kommand.getValue
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.ArmorStand
import org.bukkit.scheduler.BukkitTask
import kotlin.Exception
import kotlin.math.roundToInt

object KommandCollatz {
    private val entities: ArrayList<ArmorStand> = ArrayList()
    private lateinit var plugin: CollatzConjecturePlugin
    private lateinit var graph: BukkitTask

    private const val GroundY = 0

    internal fun register(plugin: CollatzConjecturePlugin, kommand: PluginKommand) {
        KommandCollatz.plugin = plugin

        kommand.register("collatz") {
            permission("collatz.commands")

            executes {
                if(::graph.isInitialized) graph.cancel()

                for (entity in entities) {
                    entity.remove()
                }
                entities.clear()
            }

            then("num" to int()){
                executes {
                    if(::graph.isInitialized) graph.cancel()

                    for (entity in entities) {
                        entity.remove()
                    }
                    entities.clear()

                    val num: Int by it
                    val startLoc: Location = Location(world, -19.1, num.toDouble()*0.01 + GroundY, -12.5)

                    var startNum = num

                    while (true){
                        world.spawn(startLoc, ArmorStand::class.java) { stand ->
                            stand.isVisible = false
                            stand.isMarker = true
                            stand.setGravity(false)
                            stand.customName(text("$startNum"))
                            stand.isCustomNameVisible = true
                            entities.add(stand)
                        }

                        if(startNum == 1) break

                        startNum = collatz(startNum)
                        startLoc.y = startNum * 0.01 + GroundY
                        startLoc.z += 1
                    }

                    val scheduler = Bukkit.getScheduler()
                    graph = scheduler.runTaskTimer(plugin, Runnable {
                        for(i in 0 until entities.size -1)
                            if(entities[i].location.distance(player.location) <= 50) line(entities[i].location, entities[i+1].location)

                        entities.forEach {stand ->
                            world.spawnParticle(Particle.REDSTONE, stand.location, 1,
                                0.0, 0.0, 0.0, Particle.DustOptions(Color.LIME, 1.5f))
                        }

                    }, 0L, 3L)
                }
            }
        }
    }

    private fun collatz(num: Int): Int{
        if(num <= 0) throw Exception("num is not natural")

        return if(num % 2 == 0) num/2 else 3*num+1
    }

    private fun line(a: Location, b: Location){
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z

        val repeatCount = (a.distance(b)*7).roundToInt()

        repeat(repeatCount) {
            b.world.spawnParticle(Particle.REDSTONE, b.add(dx/repeatCount, dy/repeatCount, dz/repeatCount), 1,
                0.0, 0.0, 0.0, Particle.DustOptions(Color.RED, 0.7f))
        }
    }
}
















