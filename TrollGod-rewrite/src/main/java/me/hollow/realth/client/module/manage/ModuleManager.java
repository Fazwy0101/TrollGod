package me.hollow.realth.client.module.manage;

import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.modules.combat.*;
import me.hollow.realth.client.module.modules.misc.*;
import me.hollow.realth.client.module.modules.movement.*;
import me.hollow.realth.client.module.modules.other.FontMod;
import me.hollow.realth.client.module.modules.other.ClickGuiMod;
import me.hollow.realth.client.module.modules.other.Colors;
import me.hollow.realth.client.module.modules.other.HUD;
import me.hollow.realth.client.module.modules.player.*;
import me.hollow.realth.client.module.modules.visual.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();
    private int size;

    public void init() {
        //OTHER
        register(new FontMod());
        register(new HUD());
        register(new ClickGuiMod());
        register(new Colors());

        //COMBAT
        register(new AutoBed());
        register(new Head());
        register(new Aura());
        register(new AntiRegear());
        register(new AutoArmor());
        register(new AutoTotem());
        register(new Criticals());
        register(new SelfFill());
        register(new Offhand());
        register(new AutoCrystal());
        register(new AutoFeetPlace());
        register(new HoleFiller());
        register(new AutoTrap());
        register(new AutoWeb());

        //MOVEMENT
        register(new Sprint());
        register(new LiquidTweaks());
        register(new FastDrop());
        register(new Speed());
        register(new NoWeb());
        register(new VanillaSpeed());
        register(new Step());

        //MISC
        register(new AntiPush());
        register(new AutoGG());
        register(new ExtraTab());
        register(new Announcer());
        register(new FPSLimit());
        register(new PearlViewer());
        register(new ChorusViewer());
        register(new Swing());
        register(new AutoFish());
        register(new ChatTimeStamps());
        register(new MiddleClick());
        register(new PopCounter());
        register(new EntityControl());

        //VISUAL
        register(new FOVModifier());
        register(new BetterChat());
        register(new Crosshair());
        register(new HoleESP());
        register(new CustomFog());
        register(new ESP());
        register(new Nametags());
        register(new Chams());
        register(new LavaESP());
        register(new CustomWeather());
        register(new EnchantColor());
        register(new RenderTweaks());
        register(new ViewmodelChanger());
        register(new BlockHighlight());
        register(new ShulkerViewer());
        register(new InventoryViewer());
        register(new Tracers());

        //PLAYER
        register(new AutoStackFill());
        register(new Velocity());
        register(new FastPlace());
        register(new NoFall());
        register(new FastBreak());
        register(new InstaMine());
        register(new Heil());
        register(new AutoBreed());
        register(new AutoSheer());

        size = modules.size();
        System.out.println(setCapacity(modules));
        System.out.println(modules.size() + " Modules found.");
        FontMod.init();
        HUD.INSTANCE.init();
    }

    //pasted straight from stackoverflow
    //gets the capacity of the list
    private int setCapacity(List<?> l)  {
        try {
            Field dataField = ArrayList.class.getDeclaredField("elementData");
            dataField.setAccessible(true);
            dataField.setInt(modules, modules.size());
            return ((Object[]) dataField.get(l)).length;
        } catch (Exception e) {
            return 0;
        }
    }

    public void register(Module mod) {
        try {
            for (final Field field : mod.getClass().getDeclaredFields()) {
                if (Value.class.isAssignableFrom(field.getType())) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    final Value val = (Value) field.get(mod);
                    mod.register(val);
                }
            }
            modules.add(mod);
        } catch (Exception e) {
            exit();
        }
    }

    public static void exit() {
        Realth.LOGGER.info("Loading failed... quitting.");
        Runtime.getRuntime().exit(0);
    }

    public int getSize() {
        return size;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> findByCategory(Module.Category category) {
        return modules
                .stream()
                .filter(mod -> mod.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Module findByLabel(String label) {
        return modules
                .stream()
                .filter(mod -> mod.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .orElse(null);
    }

}
