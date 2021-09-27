package com.stereowalker.controllermod.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.stereowalker.controllermod.client.controller.ControllerBindings;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.Util;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class MouseClickEvents {

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void mouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
		if(ControllerUtil.isControllerAvailable(ControllerUtil.controller) && ControllerUtil.enableController) {
			if (/*ControllerMod.instance.getActiveController().isButtonDown("button3")*/ControllerBindings.SHIFT_MOVE_INPUT.isDown(Config.controllerModel.get())) {
				if (event.getGui() instanceof AbstractContainerScreen<?> && !(event.getGui() instanceof CreativeModeInventoryScreen)) {
					event.setCanceled(true);
					AbstractContainerScreen<?> container = (AbstractContainerScreen<?>)event.getGui();
					mouseClicked(container, event.getGui(), event.getMouseX(), event.getMouseY(), event.getButton());
				}
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void mouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
		if(ControllerUtil.isControllerAvailable(ControllerUtil.controller) && ControllerUtil.enableController) {
			if (/*ControllerMod.instance.getActiveController().isButtonDown("button3")*/ControllerBindings.SHIFT_MOVE_INPUT.isDown(Config.controllerModel.get())) {
				if (event.getGui() instanceof AbstractContainerScreen<?> && !(event.getGui() instanceof CreativeModeInventoryScreen)) {
					event.setCanceled(true);
					AbstractContainerScreen<?> container = (AbstractContainerScreen<?>)event.getGui();
					mouseReleased(container, event.getGui(), event.getMouseX(), event.getMouseY(), event.getButton());
				}
			}
		}
	}

	public static boolean mouseClicked(AbstractContainerScreen<?> container, ContainerEventHandler event, double mouseX, double mouseY, int p_231044_5_) {
//		if (event.mouseClicked(mouseX, mouseY, p_231044_5_)) {
//			return true;
//		} else {
			InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(p_231044_5_);
			boolean flag = container.getMinecraft().options.keyPickItem.isActiveAndMatches(mouseKey);
			Slot slot = container.findSlot(mouseX, mouseY);
			long i = Util.getMillis();
			container.doubleclick = container.lastClickSlot == slot && i - container.lastClickTime < 250L && container.lastClickButton == p_231044_5_;
			container.skipNextRelease = false;
			if (p_231044_5_ != 0 && p_231044_5_ != 1 && !flag) {
				container.checkHotbarMouseClicked(p_231044_5_);
			} else {
				int j = container.getGuiLeft();
				int k = container.getGuiTop();
				boolean flag1 = hasClickedOutside(container, mouseX, mouseY, j, k, p_231044_5_);
				if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
				int l = -1;
				if (slot != null) {
					l = slot.index;
				}

				if (flag1) {
					l = -999;
				}

				if (container.getMinecraft().options.touchscreen && flag1 && container.getMenu().getCarried().isEmpty()) {
					container.getMinecraft().setScreen((Screen)null);
					return true;
				}

				if (l != -1) {
					if (container.getMinecraft().options.touchscreen) {
						if (slot != null && slot.hasItem()) {
							container.clickedSlot = slot;
							container.draggingItem = ItemStack.EMPTY;
							container.isSplittingStack = p_231044_5_ == 1;
						} else {
							container.clickedSlot = null;
						}
					} else if (!container.isQuickCrafting) {
						if (container.getMenu().getCarried().isEmpty()) {
							if (container.getMinecraft().options.keyPickItem.isActiveAndMatches(mouseKey)) {
								handleMouseClick(container, slot, l, p_231044_5_, ClickType.CLONE);
							} else {
								boolean flag2 = l != -999 && (hasShiftDown());
								ClickType clicktype = ClickType.PICKUP;
								if (flag2) {
									container.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
									clicktype = ClickType.QUICK_MOVE;
								} else if (l == -999) {
									clicktype = ClickType.THROW;
								}

								System.out.println("HANDLE CLICK");
								handleMouseClick(container, slot, l, p_231044_5_, clicktype);
							}

							container.skipNextRelease = true;
						} else {
							container.isQuickCrafting = true;
							container.quickCraftingButton = p_231044_5_;
							container.quickCraftSlots.clear();
							if (p_231044_5_ == 0) {
								container.quickCraftingType = 0;
							} else if (p_231044_5_ == 1) {
								container.quickCraftingType = 1;
							} else if (container.getMinecraft().options.keyPickItem.isActiveAndMatches(mouseKey)) {
								container.quickCraftingType = 2;
							}
						}
					}
				}
			}

			container.lastClickSlot = slot;
			container.lastClickTime = i;
			container.lastClickButton = p_231044_5_;
			return true;
//		}
	}

	public static boolean mouseReleased(AbstractContainerScreen<?> container, ContainerEventHandler event, double mouseX, double mouseY, int button) {
		System.out.println("HANDLE RELEASE");
		event.mouseReleased(mouseX, mouseY, button); //Forge, Call parent to release buttons
		Slot slot = container.findSlot(mouseX, mouseY);
		int i = container.getGuiLeft();
		int j = container.getGuiTop();
		boolean flag = hasClickedOutside(container, mouseX, mouseY, i, j, button);
		if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
		InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(button);
		int k = -1;
		if (slot != null) {
			k = slot.index;
		}

		if (flag) {
			k = -999;
		}

		if (container.doubleclick && slot != null && button == 0 && container.getMenu().canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
			if (hasShiftDown()) {
				if (!container.lastQuickMoved.isEmpty()) {
					for(Slot slot2 : container.getMenu().slots) {
						if (slot2 != null && slot2.mayPickup(container.getMinecraft().player) && slot2.hasItem() && slot2.isSameInventory(slot) && AbstractContainerMenu.canItemQuickReplace(slot2, container.lastQuickMoved, true)) {
							handleMouseClick(container, slot2, slot2.index, button, ClickType.QUICK_MOVE);
						}
					}
				}
			} else {
				handleMouseClick(container, slot, k, button, ClickType.PICKUP_ALL);
			}

			container.doubleclick = false;
			container.lastClickTime = 0L;
		} else {
			if (container.isQuickCrafting && container.quickCraftingButton != button) {
				container.isQuickCrafting = false;
				container.quickCraftSlots.clear();
				container.skipNextRelease = true;
				return true;
			}

			if (container.skipNextRelease) {
				container.skipNextRelease = false;
				return true;
			}

			if (container.clickedSlot != null && container.getMinecraft().options.touchscreen) {
				if (button == 0 || button == 1) {
					if (container.draggingItem.isEmpty() && slot != container.clickedSlot) {
						container.draggingItem = container.clickedSlot.getItem();
					}

					boolean flag2 = AbstractContainerMenu.canItemQuickReplace(slot, container.draggingItem, false);
					if (k != -1 && !container.draggingItem.isEmpty() && flag2) {
						handleMouseClick(container, container.clickedSlot, container.clickedSlot.index, button, ClickType.PICKUP);
						handleMouseClick(container, slot, k, 0, ClickType.PICKUP);
						if (container.getMenu().getCarried().isEmpty()) {
							container.snapbackItem = ItemStack.EMPTY;
						} else {
							handleMouseClick(container, container.clickedSlot, container.clickedSlot.index, button, ClickType.PICKUP);
							container.snapbackStartX = Mth.floor(mouseX - (double)i);
							container.snapbackStartY = Mth.floor(mouseY - (double)j);
//							container.returningStackDestSlot = container.clickedSlot;
							container.snapbackItem = container.draggingItem;
//							container.returningStackTime = Util.getMillis();
						}
					} else if (!container.draggingItem.isEmpty()) {
						container.snapbackStartX = Mth.floor(mouseX - (double)i);
						container.snapbackStartY = Mth.floor(mouseY - (double)j);
//						container.returningStackDestSlot = container.clickedSlot;
						container.snapbackItem = container.draggingItem;
//						container.returningStackTime = Util.getMillis();
					}

					container.draggingItem = ItemStack.EMPTY;
					container.clickedSlot = null;
				}
			} else if (container.isQuickCrafting && !container.quickCraftSlots.isEmpty()) {
				handleMouseClick(container, (Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, container.quickCraftingType), ClickType.QUICK_CRAFT);

				for(Slot slot1 : container.quickCraftSlots) {
					handleMouseClick(container, slot1, slot1.index, AbstractContainerMenu.getQuickcraftMask(1, container.quickCraftingType), ClickType.QUICK_CRAFT);
				}

				handleMouseClick(container, (Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, container.quickCraftingType), ClickType.QUICK_CRAFT);
			} else if (!container.getMenu().getCarried().isEmpty()) {
				if (container.getMinecraft().options.keyPickItem.isActiveAndMatches(mouseKey)) {
					handleMouseClick(container, slot, k, button, ClickType.CLONE);
				} else {
					boolean flag1 = k != -999 && (hasShiftDown());
					if (flag1) {
						container.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
					}

					handleMouseClick(container, slot, k, button, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
				}
			}
		}

		if (container.getMenu().getCarried().isEmpty()) {
			container.lastClickTime = 0L;
		}

		container.isQuickCrafting = false;
		return true;
	}
	
	public static boolean hasShiftDown() {
//		System.out.println("SHIFTING: "+ControllerMod.instance.getActiveController().isButtonDown("button3"));
		return /*ControllerMod.instance.getActiveController().isButtonDown("button3")*/ControllerBindings.SHIFT_MOVE_INPUT.isDown(Config.controllerModel.get());
	}


	/**
	 * Convenience method
	 * @param mouseX
	 * @param mouseY
	 * @param guiLeftIn
	 * @param guiTopIn
	 * @param mouseButton
	 * @return
	 */
	protected static boolean hasClickedOutside(AbstractContainerScreen<?> container, double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
		return mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + container.getXSize()) || mouseY >= (double)(guiTopIn + container.getYSize());
	}



	/**
	 * Convenience method
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	protected static void handleMouseClick(AbstractContainerScreen<?> container, Slot slotIn, int slotId, int mouseButton, ClickType type) {
		if (slotIn != null) {
			slotId = slotIn.index;
		}

		container.getMinecraft().gameMode.handleInventoryMouseClick(container.getMenu().containerId, slotId, mouseButton, type, container.getMinecraft().player);
	}
}
