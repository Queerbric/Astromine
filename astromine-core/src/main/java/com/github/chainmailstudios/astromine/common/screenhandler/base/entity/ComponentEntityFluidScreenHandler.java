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

package com.github.chainmailstudios.astromine.common.screenhandler.base.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerType;

import com.github.chainmailstudios.astromine.common.entity.base.ComponentFluidEntity;
import com.github.chainmailstudios.astromine.common.widget.blade.FluidVerticalBarWidget;
import com.github.vini2003.blade.common.miscellaneous.Position;
import com.github.vini2003.blade.common.miscellaneous.Size;

public abstract class ComponentEntityFluidScreenHandler extends ComponentEntityScreenHandler {
	public ComponentFluidEntity blockEntity;

	public FluidVerticalBarWidget fluidBar;

	public ComponentEntityFluidScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerEntity player, int entityId) {
		super(type, syncId, player, entityId);

		blockEntity = (ComponentFluidEntity) player.world.getEntityById(entityId);
	}

	@Override
	public void initialize(int width, int height) {
		super.initialize(width, height);

		fluidBar = new FluidVerticalBarWidget();
		fluidBar.setPosition(Position.of(mainTab, 7, 11));
		fluidBar.setSize(Size.of(24F, 48F));
		fluidBar.setVolume(() -> blockEntity.getFluidComponent().getVolume(0));

		mainTab.addWidget(fluidBar);
	}
}
