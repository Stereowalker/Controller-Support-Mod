package com.stereowalker.controllermod.client.controller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.client.settings.IKeyConflictContext;

public enum ControllerConflictContext implements IKeyConflictContext
{
	
    /**
     * Gui key bindings are only used when a {@link GuiScreen} is open.
     */
    GUI {
        @SuppressWarnings("resource")
		@Override
        public boolean isActive()
        {
            return Minecraft.getInstance().screen != null && !CONTAINER.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    },

    /**
     * Gui key bindings are only used when a {@link AbstractContainerScreen} is open.
     */
    CONTAINER {
        @SuppressWarnings("resource")
		@Override
        public boolean isActive()
        {
            return Minecraft.getInstance().screen instanceof AbstractContainerScreen<?>;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    },

    /**
     * In-game key bindings are only used when a {@link GuiScreen} is not open.
     */
    IN_GAME {
        @Override
        public boolean isActive()
        {
            return !CONTAINER.isActive() && !GUI.isActive();
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    }
}
