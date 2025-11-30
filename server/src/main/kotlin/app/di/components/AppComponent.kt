package app.di.components

import dagger.Component
import di.modules.AppModule
import app.ServerApplication
import org.example.app.di.modules.ItemModule
import org.example.app.di.modules.PhysicsModule
import org.example.app.di.modules.SystemModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    PhysicsModule::class,
    ItemModule::class,
    SystemModule::class
])
interface AppComponent {
    fun inject(serverApplication: ServerApplication)
}