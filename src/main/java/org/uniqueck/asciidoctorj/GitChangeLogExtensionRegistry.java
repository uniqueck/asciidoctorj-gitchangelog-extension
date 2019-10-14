package org.uniqueck.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.extension.JavaExtensionRegistry;
import org.asciidoctor.jruby.extension.spi.ExtensionRegistry;
import org.uniqueck.asciidoctorj.gitchangelog.GitChangeLogBlockMacroProcessor;

public class GitChangeLogExtensionRegistry implements ExtensionRegistry {

    @Override
    public void register(Asciidoctor asciidoctor) {
        JavaExtensionRegistry javaExtensionRegistry = asciidoctor.javaExtensionRegistry();
        javaExtensionRegistry.blockMacro(GitChangeLogBlockMacroProcessor.class);
    }
}
