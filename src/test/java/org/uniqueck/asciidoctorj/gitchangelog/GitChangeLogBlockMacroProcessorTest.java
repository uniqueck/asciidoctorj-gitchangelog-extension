package org.uniqueck.asciidoctorj.gitchangelog;

import org.junit.jupiter.api.Test;
import org.uniqueck.asciidoctorj.AbstractAsciidoctorTestHelper;

import static org.junit.jupiter.api.Assertions.*;

class GitChangeLogBlockMacroProcessorTest extends AbstractAsciidoctorTestHelper {


    @Test
    void testProcess() {
        String actualContent = convert("changelog::../../..[]");
        assertNotNull(actualContent);
        assertFalse(actualContent.trim().isEmpty());
    }

}