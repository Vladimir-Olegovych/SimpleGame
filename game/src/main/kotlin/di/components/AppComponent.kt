package di.components

import app.GameApplication
import app.screens.MainFragment
import app.screens.GameFragment
import dagger.Component
import di.modules.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(gameApplication: GameApplication)
    fun inject(fragment: MainFragment)
    fun inject(fragment: GameFragment)
}