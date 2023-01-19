package com.stereowalker.controllermod.client;

import java.util.List;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.stereowalker.controllermod.client.controller.ControllerMapping;
import com.stereowalker.controllermod.client.controller.ControllerUtil;
import com.stereowalker.controllermod.client.controller.ControllerUtil.ListeningMode;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

@SuppressWarnings("deprecation")
//TODO: Use the newer package "org.apache.commons.text" instead of "org.apache.commons.lang3"
@Environment(EnvType.CLIENT)
public class OnScreenKeyboard {

	enum Layout{
		QWERTY(new String[][]{
			new String[]{"11","22","33","44","55","66","77","8","99","00"},
			new String[]{"Qq","Ww","Ee","Rr","Tt","Yy","Uu","Ii","Oo","Pp"},
			new String[]{"Aa","Ss","Dd","Ff","Gg","Hh","Jj","Kk","Ll","\"'"},
			new String[]{"Zz","Xx","Cc","Vv","Bb","Nn","Mm","<,",">.","?/"}
		});

		List<List<Pair<Integer,Integer>>> keys;
		int xSize = 0;
		int ySize = 0;
		private Layout(String[][] keys) {
			this.keys = Lists.newArrayList();
			for (String[] s : keys) {
				this.keys.add(assignKeys(s, Lists.newArrayList()));
			}
			this.ySize = this.keys.size();
		}

		List<Pair<Integer,Integer>> assignKeys(String[] keyArray, List<Pair<Integer,Integer>> keyList) {
			if (xSize != 0 && keyArray.length > xSize) {
				System.out.println("This is too large ");
			}
			else {
				for (String s : keyArray) {
					if (s.length() > 2) {
						System.out.println("Invalid key "+s);
					} else {
						int i = Integer.parseInt(CharUtils.unicodeEscaped(s.charAt(0)).substring(2), 16);
						int j = s.length() == 2 ? Integer.parseInt(CharUtils.unicodeEscaped(s.charAt(1)).substring(2), 16) : -1;
						keyList.add(Pair.of(i, j));
					}
				}
				if (xSize == 0) {
					xSize = keyList.size();
				}
			}
			return keyList;
		}

		int getKey(boolean shift, int x, int y) {
			return shift || keys.get(y).get(x).getSecond() == -1 ? keys.get(y).get(x).getFirst() : keys.get(y).get(x).getSecond();
		}

		String unicode(boolean shift, int x, int y) {
			String hex = Integer.toHexString(getKey(shift, x, y));
			if (hex.length() == 1) {
				return "\\u000"+hex;
			} else if (hex.length() == 2) {
				return "\\u00"+hex;
			} else if (hex.length() == 3) {
				return "\\u0"+hex;
			} else {
				return "\\u"+hex;
			}
		}
	}

	private final Minecraft minecraft;
	public int currentKey = GLFW.GLFW_KEY_A;
	public boolean isCapsLocked = false;
	public int switchCooldown;
	public int changeKeyCooldown;
	public Layout layout = Layout.QWERTY;

	public int xPos;
	public int yPos;

	public OnScreenKeyboard(Minecraft minecraft) {
		this.minecraft = minecraft;
	}

	public void switchKeyboard() {
		switchCooldown = 20;
		ControllerUtil.listeningMode = ControllerUtil.listeningMode == ListeningMode.KEYBOARD ? ListeningMode.LISTEN_TO_MAPPINGS : ListeningMode.KEYBOARD;
		currentKey = GLFW.GLFW_KEY_A;
		isCapsLocked = false;
		changeKeyCooldown = 0;

		xPos = 0;
		yPos = 0;
		ControllerMapping.releaseAll();
	}

	public int[] getNextKey(boolean up, boolean down, boolean left, boolean right) {
		if (layout == Layout.QWERTY) {
			int i = layout.xSize - 1;
			int j = layout.ySize - 1;
			
			int k = xPos;
			int l = yPos;
			//Up - Down Controls
			if (down && yPos < j) {
				l = yPos+1;
			} else if (down && yPos == j) {
				l = 0;
			} else if (up && yPos > 0) {
				l = yPos-1;
			} else if (up && yPos == 0) {
				l = j;
			}
			
			if (right && xPos < i) {
				k = xPos+1;
			} else if (right && xPos == i) {
				k = 0;
			} else if (left && xPos > 0) {
				k = xPos-1;
			} else if (left && xPos == 0) {
				k = i;
			}
			

			return new int[]{layout.getKey(isCapsLocked, k, l), k, l};
		}
		return new int[]{GLFW.GLFW_KEY_WORLD_1, xPos, yPos};


	}

	
	public void drawKeyboard(PoseStack poseStack, Font font, int x, int y) {
		List<List<MutableComponent>> layers = Lists.newArrayList();
		for (int j = 0; j < layout.ySize; j++) {
			layers.add(Lists.newArrayList());
			for (int i = 0; i < layout.xSize; i++) {
				String ke = StringEscapeUtils.unescapeJava(layout.unicode(isCapsLocked, i, j));
				if (xPos == i && yPos == j) {
					layers.get(j).add(Component.literal(ke).withStyle(Style.EMPTY.withColor(getKeyboardColors("Highlighted-Text")[0])));
				} else {
					layers.get(j).add(Component.literal(ke).withStyle(Style.EMPTY.withColor(getKeyboardColors("Text")[0])));
				}
			}
		}
		int width = 0;
		int textWidth = 0;
		int height = 0;
		for(List<MutableComponent> clienttooltipcomponent : layers) {
            int k = 0;
            for(MutableComponent mut : clienttooltipcomponent) {
            	int l = 0;
            	l = font.width(mut);
            	
                if (l > textWidth) {
                	textWidth = l;
                }
            }
        	k += textWidth*1.5*clienttooltipcomponent.size()+3;
            if (k > width) {
            	width = k;
            }

            height += 10;
         }

		//		System.out.println(layer1);

		int j2 = x + 12;
		int k2 = y - 12;
		if (j2 + width > minecraft.screen.width) {
			j2 -= 28 + width;
		}

		if (k2 + height + 6 > minecraft.screen.height) {
			k2 = minecraft.screen.height - height - 6;
		}

		poseStack.pushPose();
		float f = minecraft.screen.itemRenderer.blitOffset;
		minecraft.screen.itemRenderer.blitOffset = 400.0f;
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		Matrix4f matrix4f = poseStack.last().pose();
		Screen.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 4, j2 + width + 3, k2 - 3, 400, getKeyboardColors("Background")[0], getKeyboardColors("Background")[0]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + height + 3, j2 + width + 3, k2 + height + 4, 400, getKeyboardColors("Background")[1], getKeyboardColors("Background")[1]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + width + 3, k2 + height + 3, 400, getKeyboardColors("Background")[0], getKeyboardColors("Background")[1]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 - 4, k2 - 3, j2 - 3, k2 + height + 3, 400, getKeyboardColors("Background")[0], getKeyboardColors("Background")[1]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 + width + 3, k2 - 3, j2 + width + 4, k2 + height + 3, 400, getKeyboardColors("Background")[0], getKeyboardColors("Background")[1]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + height + 3 - 1, 400, getKeyboardColors("Border")[0], getKeyboardColors("Border")[1]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 + width + 2, k2 - 3 + 1, j2 + width + 3, k2 + height + 3 - 1, 400, getKeyboardColors("Border")[0], getKeyboardColors("Border")[1]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 - 3, j2 + width + 3, k2 - 3 + 1, 400, getKeyboardColors("Border")[0], getKeyboardColors("Border")[0]);
		Screen.fillGradient(matrix4f, bufferbuilder, j2 - 3, k2 + height + 2, j2 + width + 3, k2 + height + 3, 400, getKeyboardColors("Border")[1], getKeyboardColors("Border")[1]);
		RenderSystem.enableDepthTest();
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
        BufferUploader.drawWithShader(bufferbuilder.end());
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
		MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		poseStack.translate(0.0D, 0.0D, 400.0D);
		int l1 = k2 + 1;
		for(int i2 = 0; i2 < layers.size(); ++i2) {
			int m1 = j2;
			List<MutableComponent> keyRow = layers.get(i2);
			for(int i3 = 0; i3 < keyRow.size(); ++i3) {
				MutableComponent clienttooltipcomponent1 = keyRow.get(i3);
				font.drawInBatch(clienttooltipcomponent1, (float)((m1 + (textWidth))- font.width(clienttooltipcomponent1) / 2), l1, -1, true, matrix4f, multibuffersource$buffersource, false, 0, 15728880);
				m1 += textWidth*1.5;// + (i3 == 0 ? 2 : 0);
			}
			l1 += 10;
		}
		//        for(int i2 = 0; i2 < pClientTooltipComponents.size(); ++i2) {
		//           ClientTooltipComponent clienttooltipcomponent1 = pClientTooltipComponents.get(i2);
		//           clienttooltipcomponent1.renderText(preEvent.getFont(), j2, l1, matrix4f, multibuffersource$buffersource);
		//           l1 += clienttooltipcomponent1.getHeight() + (i2 == 0 ? 2 : 0);
		//        }

		multibuffersource$buffersource.endBatch();
		poseStack.popPose();
		minecraft.screen.itemRenderer.blitOffset = f;
	}

	public int[] getKeyboardColors(String id) {
		if (id.equals("Background"))
			return new int[] {0xff050429, 0xff1a1761};
		if (id.equals("Text"))
			return new int[] {0x6dd1f2};
		if (id.equals("Highlighted-Text"))
			return new int[] {0xffffff};
		else
			return new int[] {0xa4d2f6ff, 0xa4d2f6ff};

	}

	public int getUnicodeKey() {
		return layout.getKey(isCapsLocked, xPos, yPos);
	}

	public void changeKey(boolean up, boolean down, boolean left, boolean right) {
		int oldKey = currentKey;
		if (changeKeyCooldown == 0 && (up || down || left || right)) {
			int[] results = getNextKey(up, down, left, right);
			currentKey = results[0];
			xPos = results[1];
			yPos = results[2];
		}

		if (oldKey != currentKey)
			changeKeyCooldown = 2;
		else if (changeKeyCooldown > 0)
			changeKeyCooldown--;
	}
}
