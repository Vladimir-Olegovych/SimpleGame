package di.components

import dagger.Component
import di.modules.AppModule
import org.example.ServerApplication
import org.example.di.modules.GeneratorModule
import org.example.di.modules.ProcessorModule
import org.example.di.modules.SystemModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    SystemModule::class,
    GeneratorModule::class,
    ProcessorModule::class
])
interface AppComponent {
    fun inject(serverApplication: ServerApplication)
}