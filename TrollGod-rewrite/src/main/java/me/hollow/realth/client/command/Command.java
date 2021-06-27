package me.hollow.realth.client.command;

public class Command {

    private String label;
    private String[] aliases;

    public Command() {
        if (getClass().isAnnotationPresent(CommandManifest.class)) {
            CommandManifest manifest = getClass().getAnnotation(CommandManifest.class);
            this.label = manifest.label();
            this.aliases = manifest.aliases();
        }
    }

    public void execute(String[] arguments) {

    }

    public String getLabel() {
        return label;
    }

    public String[] getAliases() {
        return aliases;
    }

}
