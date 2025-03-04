package dev.realsgii2.temperatures.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ModRegistry {
    public static void register(IEventBus eventBus) {
        ModDeterminants.register(eventBus);

        ModEffects.register(eventBus);
        ModEnchantments.register(eventBus);
        ModPotions.register(eventBus);
    }

    public static class JoinableArrayList<T> extends ArrayList<T> {
        public JoinableArrayList() {
        }

        public JoinableArrayList(Collection<? extends T> oldList) {
            super(oldList);
        }

        public JoinableArrayList<T> with(List<T> list) {
            JoinableArrayList<T> result = new JoinableArrayList<>(this);
            result.addAll(list);

            return result;
        }
    }

    public static class RegistryObjectMapper<T> extends ArrayList<RegistryObject<T>> {
        public RegistryObjectMapper(Collection<? extends RegistryObject<T>> oldList) {
            super(oldList);
        }

        public static <T1> RegistryObjectMapper<T1> ofRegistry(DeferredRegister<T1> registry) {
            return new RegistryObjectMapper<>(registry.getEntries());
        }

        public JoinableArrayList<ItemStack> map(Item item, BiFunction<ItemStack, T, ItemStack> map) {
            return stream().map(x -> map.apply(new ItemStack(item), x.get())).collect(Collectors.toCollection(JoinableArrayList::new));
        }
    }
}
