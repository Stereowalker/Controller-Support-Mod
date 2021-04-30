package com.stereowalker.controllermod.client.events;

import com.stereowalker.controllermod.client.controller.ControllerBindings;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.config.Config;

import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
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
				if (event.getGui() instanceof ContainerScreen<?> && !(event.getGui() instanceof CreativeScreen)) {
					event.setCanceled(true);
					ContainerScreen<?> container = (ContainerScreen<?>)event.getGui();
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
				if (event.getGui() instanceof ContainerScreen<?> && !(event.getGui() instanceof CreativeScreen)) {
					event.setCanceled(true);
					ContainerScreen<?> container = (ContainerScreen<?>)event.getGui();
					mouseReleased(container, event.getGui(), event.getMouseX(), event.getMouseY(), event.getButton());
				}
			}
		}
	}

	public static boolean mouseClicked(ContainerScreen<?> container, INestedGuiEventHandler event, double mouseX, double mouseY, int p_231044_5_) {
//		if (event.mouseClicked(mouseX, mouseY, p_231044_5_)) {
//			return true;
//		} else {
			InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(p_231044_5_);
			boolean flag = container.getMinecraft().gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey);
			Slot slot = container.getSelectedSlot(mouseX, mouseY);
			long i = Util.milliTime();
			container.doubleClick = container.lastClickSlot == slot && i - container.lastClickTime < 250L && container.lastClickButton == p_231044_5_;
			container.ignoreMouseUp = false;
			if (p_231044_5_ != 0 && p_231044_5_ != 1 && !flag) {
				container.hotkeySwapItems(p_231044_5_);
			} else {
				int j = container.getGuiLeft();
				int k = container.getGuiTop();
				boolean flag1 = hasClickedOutside(container, mouseX, mouseY, j, k, p_231044_5_);
				if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
				int l = -1;
				if (slot != null) {
					l = slot.slotNumber;
				}

				if (flag1) {
					l = -999;
				}

				if (container.getMinecraft().gameSettings.touchscreen && flag1 && container.getMinecraft().player.inventory.getItemStack().isEmpty()) {
					container.getMinecraft().displayGuiScreen((Screen)null);
					return true;
				}

				if (l != -1) {
					if (container.getMinecraft().gameSettings.touchscreen) {
						if (slot != null && slot.getHasStack()) {
							container.clickedSlot = slot;
							container.draggedStack = ItemStack.EMPTY;
							container.isRightMouseClick = p_231044_5_ == 1;
						} else {
							container.clickedSlot = null;
						}
					} else if (!container.dragSplitting) {
						if (container.getMinecraft().player.inventory.getItemStack().isEmpty()) {
							if (container.getMinecraft().gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
								handleMouseClick(container, slot, l, p_231044_5_, ClickType.CLONE);
							} else {
								boolean flag2 = l != -999 && (hasShiftDown());
								ClickType clicktype = ClickType.PICKUP;
								if (flag2) {
									container.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
									clicktype = ClickType.QUICK_MOVE;
								} else if (l == -999) {
									clicktype = ClickType.THROW;
								}

								System.out.println("HANDLE CLICK");
								handleMouseClick(container, slot, l, p_231044_5_, clicktype);
							}

							container.ignoreMouseUp = true;
						} else {
							container.dragSplitting = true;
							container.dragSplittingButton = p_231044_5_;
							container.dragSplittingSlots.clear();
							if (p_231044_5_ == 0) {
								container.dragSplittingLimit = 0;
							} else if (p_231044_5_ == 1) {
								container.dragSplittingLimit = 1;
							} else if (container.getMinecraft().gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
								container.dragSplittingLimit = 2;
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

	public static boolean mouseReleased(ContainerScreen<?> container, INestedGuiEventHandler event, double mouseX, double mouseY, int button) {
		System.out.println("HANDLE RELEASE");
		event.mouseReleased(mouseX, mouseY, button); //Forge, Call parent to release buttons
		Slot slot = container.getSelectedSlot(mouseX, mouseY);
		int i = container.getGuiLeft();
		int j = container.getGuiTop();
		boolean flag = hasClickedOutside(container, mouseX, mouseY, i, j, button);
		if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
		InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(button);
		int k = -1;
		if (slot != null) {
			k = slot.slotNumber;
		}

		if (flag) {
			k = -999;
		}

		if (container.doubleClick && slot != null && button == 0 && container.getContainer().canMergeSlot(ItemStack.EMPTY, slot)) {
			if (hasShiftDown()) {
				if (!container.shiftClickedSlot.isEmpty()) {
					for(Slot slot2 : container.getContainer().inventorySlots) {
						if (slot2 != null && slot2.canTakeStack(container.getMinecraft().player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, container.shiftClickedSlot, true)) {
							handleMouseClick(container, slot2, slot2.slotNumber, button, ClickType.QUICK_MOVE);
						}
					}
				}
			} else {
				handleMouseClick(container, slot, k, button, ClickType.PICKUP_ALL);
			}

			container.doubleClick = false;
			container.lastClickTime = 0L;
		} else {
			if (container.dragSplitting && container.dragSplittingButton != button) {
				container.dragSplitting = false;
				container.dragSplittingSlots.clear();
				container.ignoreMouseUp = true;
				return true;
			}

			if (container.ignoreMouseUp) {
				container.ignoreMouseUp = false;
				return true;
			}

			if (container.clickedSlot != null && container.getMinecraft().gameSettings.touchscreen) {
				if (button == 0 || button == 1) {
					if (container.draggedStack.isEmpty() && slot != container.clickedSlot) {
						container.draggedStack = container.clickedSlot.getStack();
					}

					boolean flag2 = Container.canAddItemToSlot(slot, container.draggedStack, false);
					if (k != -1 && !container.draggedStack.isEmpty() && flag2) {
						handleMouseClick(container, container.clickedSlot, container.clickedSlot.slotNumber, button, ClickType.PICKUP);
						handleMouseClick(container, slot, k, 0, ClickType.PICKUP);
						if (container.getMinecraft().player.inventory.getItemStack().isEmpty()) {
							container.returningStack = ItemStack.EMPTY;
						} else {
							handleMouseClick(container, container.clickedSlot, container.clickedSlot.slotNumber, button, ClickType.PICKUP);
							container.touchUpX = MathHelper.floor(mouseX - (double)i);
							container.touchUpY = MathHelper.floor(mouseY - (double)j);
//							container.returningStackDestSlot = container.clickedSlot;
							container.returningStack = container.draggedStack;
//							container.returningStackTime = Util.milliTime();
						}
					} else if (!container.draggedStack.isEmpty()) {
						container.touchUpX = MathHelper.floor(mouseX - (double)i);
						container.touchUpY = MathHelper.floor(mouseY - (double)j);
//						container.returningStackDestSlot = container.clickedSlot;
						container.returningStack = container.draggedStack;
//						container.returningStackTime = Util.milliTime();
					}

					container.draggedStack = ItemStack.EMPTY;
					container.clickedSlot = null;
				}
			} else if (container.dragSplitting && !container.dragSplittingSlots.isEmpty()) {
				handleMouseClick(container, (Slot)null, -999, Container.getQuickcraftMask(0, container.dragSplittingLimit), ClickType.QUICK_CRAFT);

				for(Slot slot1 : container.dragSplittingSlots) {
					handleMouseClick(container, slot1, slot1.slotNumber, Container.getQuickcraftMask(1, container.dragSplittingLimit), ClickType.QUICK_CRAFT);
				}

				handleMouseClick(container, (Slot)null, -999, Container.getQuickcraftMask(2, container.dragSplittingLimit), ClickType.QUICK_CRAFT);
			} else if (!container.getMinecraft().player.inventory.getItemStack().isEmpty()) {
				if (container.getMinecraft().gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
					handleMouseClick(container, slot, k, button, ClickType.CLONE);
				} else {
					boolean flag1 = k != -999 && (hasShiftDown());
					if (flag1) {
						container.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
					}

					handleMouseClick(container, slot, k, button, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
				}
			}
		}

		if (container.getMinecraft().player.inventory.getItemStack().isEmpty()) {
			container.lastClickTime = 0L;
		}

		container.dragSplitting = false;
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
	protected static boolean hasClickedOutside(ContainerScreen<?> container, double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
		return mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + container.getXSize()) || mouseY >= (double)(guiTopIn + container.getYSize());
	}



	/**
	 * Convenience method
	 * Called when the mouse is clicked over a slot or outside the gui.
	 */
	protected static void handleMouseClick(ContainerScreen<?> container, Slot slotIn, int slotId, int mouseButton, ClickType type) {
		if (slotIn != null) {
			slotId = slotIn.slotNumber;
		}

		container.getMinecraft().playerController.windowClick(container.getContainer().windowId, slotId, mouseButton, type, container.getMinecraft().player);
	}
}
