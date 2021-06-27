package me.hollow.realth.client.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleManifest {
    String label();

    Module.Category category();

    boolean enabled() default false;
    boolean listenable() default true;
    boolean persistent() default false;
    int key() default -999;
}
