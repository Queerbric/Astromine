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

package com.github.chainmailstudios.astromine.technologies.common.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentEnergyItemBlockEntity;
import com.github.chainmailstudios.astromine.common.component.inventory.EnergyComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.ItemComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleEnergyComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleItemComponent;
import com.github.chainmailstudios.astromine.common.utilities.StackUtilities;
import com.github.chainmailstudios.astromine.common.utilities.tier.MachineTier;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.TierProvider;
import com.github.chainmailstudios.astromine.technologies.common.recipe.AlloySmeltingRecipe;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static java.lang.Math.min;

public abstract class AlloySmelterBlockEntity extends ComponentEnergyItemBlockEntity implements EnergySizeProvider, TierProvider, SpeedProvider {
	public double progress = 0;
	public int limit = 100;
	public boolean shouldTry = false;

	private Optional<AlloySmeltingRecipe> optionalRecipe = Optional.empty();

	public AlloySmelterBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Override
	public ItemComponent createItemComponent() {
		return SimpleItemComponent.of(3).withInsertPredicate((direction, stack, slot) -> {
			if (slot != 0 && slot != 1) {
				return false;
			}

			if (!StackUtilities.test(stack, getItemComponent().getFirst()) && !StackUtilities.test(stack, getItemComponent().getSecond())) {
				return false;
			}

			return AlloySmeltingRecipe.allows(world, ItemComponent.of(stack, getItemComponent().getSecond())) || AlloySmeltingRecipe.allows(world, ItemComponent.of(getItemComponent().getFirst(), stack));
		}).withExtractPredicate(((direction, stack, slot) -> {
			return slot == 2;
		})).withListener((inventory) -> {
			shouldTry = true;
			optionalRecipe = Optional.empty();
		});
	}

	@Override
	public EnergyComponent createEnergyComponent() {
		return SimpleEnergyComponent.of(getEnergySize());
	}

	@Override
	public IntSet getItemInputSlots() {
		return new IntArraySet(new int[] { 0, 1 });
	}

	@Override
	public IntSet getItemOutputSlots() {
		return IntSets.singleton(2);
	}

	@Override
	public void tick() {
		super.tick();

		if (world == null || world.isClient || !tickRedstone())
			return;

		ItemComponent itemComponent = getItemComponent();

		EnergyComponent energyComponent = getEnergyComponent();

		if (itemComponent != null && energyComponent != null) {
			EnergyVolume energyVolume = energyComponent.getVolume();

			if (!optionalRecipe.isPresent() && shouldTry) {
				optionalRecipe = AlloySmeltingRecipe.matching(world, itemComponent);
				shouldTry = false;

				if (!optionalRecipe.isPresent()) {
					progress = 0;
					limit = 100;
				}
			}

			if (optionalRecipe.isPresent()) {
				AlloySmeltingRecipe recipe = optionalRecipe.get();

				limit = recipe.getTime();

				double speed = min(getMachineSpeed(), limit - progress);
				double consumed = recipe.getEnergyInput() * speed / limit;

				if (energyVolume.hasStored(consumed)) {
					energyVolume.take(consumed);

					if (progress + speed >= limit) {
						optionalRecipe = Optional.empty();

						ItemStack first = itemComponent.getFirst();
						ItemStack second = itemComponent.getSecond();

						if (recipe.getFirstInput().test(first) && recipe.getSecondInput().test(second)) {
							first.decrement(recipe.getFirstInput().testMatching(first).getCount());
							second.decrement(recipe.getSecondInput().testMatching(second).getCount());
						} else if (recipe.getFirstInput().test(second) && recipe.getSecondInput().test(first)) {
							second.decrement(recipe.getFirstInput().testMatching(second).getCount());
							first.decrement(recipe.getSecondInput().testMatching(first).getCount());
						}

						itemComponent.setThird(StackUtilities.into(itemComponent.getThird(), recipe.getFirstOutput()));

						progress = 0;
					} else {
						progress += speed;
					}

					tickActive();
				} else {
					tickInactive();
				}
			} else {
				tickInactive();
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putDouble("progress", progress);
		tag.putInt("limit", limit);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, @NotNull CompoundTag tag) {
		progress = tag.getDouble("progress");
		limit = tag.getInt("limit");
		super.fromTag(state, tag);
	}

	public static class Primitive extends AlloySmelterBlockEntity {
		public Primitive() {
			super(AstromineTechnologiesBlockEntityTypes.PRIMITIVE_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().primitiveAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().primitiveAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.PRIMITIVE;
		}
	}

	public static class Basic extends AlloySmelterBlockEntity {
		public Basic() {
			super(AstromineTechnologiesBlockEntityTypes.BASIC_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().basicAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().basicAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.BASIC;
		}
	}

	public static class Advanced extends AlloySmelterBlockEntity {
		public Advanced() {
			super(AstromineTechnologiesBlockEntityTypes.ADVANCED_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().advancedAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().advancedAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ADVANCED;
		}
	}

	public static class Elite extends AlloySmelterBlockEntity {
		public Elite() {
			super(AstromineTechnologiesBlockEntityTypes.ELITE_ALLOY_SMELTER);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().eliteAlloySmelterSpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().eliteAlloySmelterEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ELITE;
		}
	}
}
