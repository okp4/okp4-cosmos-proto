import com.google.gson.GsonBuilder
import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import io.github.classgraph.ClassGraph
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URLClassLoader

data class ReflectionMethod(
    val name: String,
    val parameterTypes: List<String>
)

data class ReflectionConfig(
    val name: String,
    val methods: List<ReflectionMethod>,
)

open class GenerateReflectionConfig : DefaultTask() {
    @Input
    lateinit var output: String

    @Input
    lateinit var sources: List<String>

    @TaskAction
    fun generateReflectionConfigJSON() =
        File(output).writeText(
            GsonBuilder().setPrettyPrinting().create()
                .toJson(
                    listOf(
                        ClassGraph()
                            .addClassLoader(
                                URLClassLoader(
                                    sources.map {
                                        File(it)
                                            .toURI()
                                            .toURL()
                                    }.toTypedArray(),
                                    null,
                                )
                            )
                            .enableAllInfo()
                            .scan()
                            .getSubclasses(GeneratedMessageV3::class.java)
                            .map {
                                ReflectionConfig(
                                    name = it.name,
                                    methods = listOf(
                                        ReflectionMethod(
                                            name = "getDescriptor",
                                            parameterTypes = emptyList()
                                        )
                                    )
                                )
                            },
                        listOf(
                            Descriptors.Descriptor::class.java.let { c ->
                                ReflectionConfig(
                                    name = c.name,
                                    methods = c.methods .map {
                                        ReflectionMethod(
                                            name = it.name,
                                            parameterTypes = it.parameterTypes.map { param ->
                                                param.name
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    ).flatten()
                )
        )
}
