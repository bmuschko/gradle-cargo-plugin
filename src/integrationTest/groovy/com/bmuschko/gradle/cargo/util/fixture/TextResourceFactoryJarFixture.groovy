package com.bmuschko.gradle.cargo.util.fixture

import groovy.transform.InheritConstructors

@InheritConstructors
class TextResourceFactoryJarFixture extends ProjectFixture {

    private static final String CLASS_NAME = "TextResourceFactory"

    protected void configure() {
        super.configure()
        writeBuild()
        writeSources()
    }

    private void writeBuild() {
        new File(projectDir, "build.gradle") << """
            apply plugin: 'java'
        """
    }

    private void writeSources() {
        def sourcesDirectory = new File(projectDir, 'src/main/java')
        sourcesDirectory.mkdirs()

        new File(sourcesDirectory, "${CLASS_NAME}.java") << """
            import javax.naming.Context;
            import javax.naming.Name;
            import javax.naming.Reference;
            import javax.naming.spi.ObjectFactory;
            import java.util.Hashtable;
            
            public class $CLASS_NAME implements ObjectFactory {
                @Override
                public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) {
                    Reference reference = (Reference) obj;
                    return reference.get("value").getContent().toString();
                }
            }
        """
    }

}
