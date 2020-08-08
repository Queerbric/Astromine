/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.common.screenhandler.base;

import com.github.chainmailstudios.astromine.common.block.entity.base.DefaultedFluidBlockEntity;
import com.github.chainmailstudios.astromine.common.widget.FluidVerticalBarWidget;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import com.github.vini2003.blade.common.data.Position;
import com.github.vini2003.blade.common.data.Size;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;


public class DefaultedFluidScreenHandler extends DefaultedBlockEntityScreenHandler {
	public DefaultedFluidBlockEntity blockEntity;

	public FluidVerticalBarWidget fluidBar;

	public DefaultedFluidScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerEntity player, BlockPos position) {
		super(type, syncId, player, position);
		
		blockEntity = (DefaultedFluidBlockEntity) player.world.getBlockEntity(position);
	}

	@Override
	public void initialize(int width, int height) {
		super.initialize(width, height);

		fluidBar = new FluidVerticalBarWidget();
		fluidBar.setPosition(new Position(mainTab.getPosition().getX() + 7F, mainTab.getPosition().getY() + 20F));
		fluidBar.setSize(new Size(24F, 28F));
		fluidBar.setVolume(() -> blockEntity.getSidedComponent(null, AstromineComponentTypes.FLUID_INVENTORY_COMPONENT).getVolume(0));

		addWidget(fluidBar);
	}
}
