package app.di.components

import app.GameApplication
import app.di.modules.AppModule
import app.screens.game.screen.GameFragment
import app.screens.MainFragment
import app.screens.StructureEditorFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(gameApplication: GameApplication)
    fun inject(fragment: MainFragment)
    fun inject(fragment: GameFragment)
    fun inject(fragment: StructureEditorFragment)
}