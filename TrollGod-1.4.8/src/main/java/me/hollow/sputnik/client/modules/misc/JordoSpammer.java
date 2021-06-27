package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import tcb.bces.listener.Subscribe;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@ModuleManifest(label = "JordoFacts", category = Module.Category.MISC, color = 0xff33ff33)
public class JordoSpammer extends Module {

    public final Setting<Integer> delay = register(new Setting<>("Delay", 4999, 0, 5000));

    public final Setting<Boolean> sound = register(new Setting("Sound", true));
    public final Setting<Boolean> randomize = register(new Setting("Randomize", false));

    private final Timer delayTimer = new Timer();
    private final List<String> facts = new LinkedList<>();

    public JordoSpammer() {
        facts.add("Jordo fact #1 : Jordo sleep with eye open");
        facts.add("Jordo fact #12 : Jordo never feel");
        facts.add("Jordo fact #32 : Horny");
        facts.add("Jordo fact #53 : Jordo mauled baby to Death");
        facts.add("Jordo fact #51 : 2054 Jordo death incident");
        facts.add("Jordo fact #15 : Jordo got Caught lackin by Greg In Brazil Forets");
        facts.add("Jordo fact #72 : The Pain. Will. Never. End.");
        facts.add("Jordo fact #931 : Help");
        facts.add("Jordo fact #3 : Jordo arrested For Terrorism");
        facts.add("Jordo fact #76 : Jordo live near Kfc");
        facts.add("Jordo fact #82 : Knife Vs Newborn Babei");
        facts.add("Jordo fact #721 : Jordo is Coming for You");
        facts.add("Jordo fact #21 : The 2014 whats 9 + 10 incident");
        facts.add("Jordo fact #77 : Jorde Floyd");
        facts.add("Jordo fact #61 : Jordos Life is empty.");
        facts.add("Jordo fact #52 : null");
        facts.add("Jordo fact #0 : existence");
        facts.add("Jordo fact #-1 : Wake up");
        facts.add("Jordo fact #83 : Jordo Doesn ot need Strength when is Strong chimp");
        facts.add("Jordo ran 8 meters like A chimanzee thanks to Jordohaxm and lil b based god.");
        facts.add("Jordo Fact #4626 : Im god Lil b");
        facts.add("Jordo Fact #46 : Why did god abandon me.");
        facts.add("Jordo Fact #97 : [SX-HACK] SXBURROW TURN ON - MY COORDS 0 121 0 AND MY IP - 17.233.122");
        facts.add("Jordo fact #233 : Kunghugge echest dump incident.");
        facts.add("Jordo fact #2643 : Joby and the chimp munks.");
        facts.add("Jordo fact #2356 : A study looking at 500 couples from 5 different countries found the average time taken to ejaculate during intercourse was around 5-and-a-half minutes. However, it's up to each couple to decide if they're happy with the time taken – there's no definition of how long sex should last.");
        facts.add("Jordo fact #365 : Pop up out my car, Swag, then i drop my roof, Roof, eat that wonton soup, Swag, Swag, thats just how it do, Do");
        facts.add(">Different monkey species eat a variety of foods, such as fruit, insects, flowers, leaves and reptiles. Most monkeys have tails. Groups of monkeys are known as a 'tribe', 'troop' or 'mission'. The Pygmy Marmoset is the smallest type of monkey, with adults weighing between 120 and 140 grams");
        facts.add("Monkey facts for kids");
        facts.add("Jordo needs to take his scizophernia miedciaton.");
    }

    private int stage = 0;

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null) {
            return;
        }
        if (stage >= facts.size()) {
            stage = 0;
        }
        Random random = new Random();
        if (delayTimer.hasReached(delay.getValue())) {
            if (!randomize.getValue()) {
                mc.player.sendChatMessage(facts.get(stage));
            } else {
                mc.player.sendChatMessage(facts.get(random.ints(0, facts.size()).iterator().nextInt()));
            }
            if (sound.getValue()) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.AMBIENT_CAVE, 1.0F));
            }
            stage++;
            delayTimer.reset();
        }
    }

}
