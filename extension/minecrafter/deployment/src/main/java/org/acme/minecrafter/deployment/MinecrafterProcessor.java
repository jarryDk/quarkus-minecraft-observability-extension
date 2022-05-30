package org.acme.minecrafter.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;
import org.acme.minecrafter.runtime.HelloRecorder;
import org.acme.minecrafter.runtime.MinecraftLog;
import org.acme.minecrafter.runtime.MinecraftLogHandlerMaker;
import org.acme.minecrafter.runtime.MinecraftLogInterceptor;
import org.jboss.jandex.DotName;

import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

class MinecrafterProcessor {

    private static final String FEATURE = "minecrafter";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @Record(STATIC_INIT)
    @BuildStep
    public void helloBuildStep(HelloRecorder recorder) {
        recorder.sayHello("World");
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    LogHandlerBuildItem addLogHandler(final MinecraftLogHandlerMaker maker) {
        return new LogHandlerBuildItem(maker.create());
    }

    /**
     * Makes the interceptor as a bean so we can access it.
     */
    @BuildStep
    AdditionalBeanBuildItem loggingInterceptor() {
        return AdditionalBeanBuildItem.unremovableOf(MinecraftLogInterceptor.class);
    }

    @BuildStep
    AnnotationsTransformerBuildItem transform() {
        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            public boolean appliesTo(org.jboss.jandex.AnnotationTarget.Kind kind) {
                return kind == org.jboss.jandex.AnnotationTarget.Kind.METHOD;
            }

            public void transform(TransformationContext context) {
                if (context.getTarget().asMethod().hasAnnotation(DotName.createSimple("javax.ws.rs.GET"))) {
                    context.transform().add(MinecraftLog.class).done();
                }
            }
        });
    }

}
