import com.google.gson.GsonBuilder
import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.reflections.Reflections
import java.io.File

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
    lateinit var configPath: String
    
    @Input
    lateinit var packageNames: List<String>

    @TaskAction
    fun generateReflectionConfigJSON() =
        File(configPath).writeText(
            GsonBuilder().setPrettyPrinting().create()
                .toJson(
                    packageNames.asSequence().map { Reflections(it) }
                        .map { reflections ->
                            reflections.getSubTypesOf(GeneratedMessageV3::class.java)
                                .map { it.name }
                        }
                        .flatten()
                        .map { className ->
                            ReflectionConfig(
                                name = className,
                                methods = listOf(
                                    ReflectionMethod(
                                        name = "getDescriptor",
                                        parameterTypes = emptyList()
                                    )
                                )
                            )
                        }
                        .toMutableList()
                        .apply {
                            this.add(
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
                        }
                )
        )
}
