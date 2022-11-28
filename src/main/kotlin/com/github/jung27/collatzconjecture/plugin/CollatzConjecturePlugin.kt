package com.github.jung27.collatzconjecture.plugin

import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class CollatzConjecturePlugin : JavaPlugin() {

    override fun onEnable() {
        setupCommands()

        logger.info("collatz-conjecture plugin is enabled")
    }

    override fun onDisable() {
        logger.info("collatz-conjecture plugin is disabled")
    }

    private fun setupCommands() = kommand {
        KommandCollatz.register(this@CollatzConjecturePlugin, this)
    }

}