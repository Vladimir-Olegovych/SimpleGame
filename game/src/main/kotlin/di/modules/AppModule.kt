package di.modules

import model.Event
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.Viewport
import dagger.Module
import dagger.Provides
import tools.kyro.client.GameClient
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideSpriteBatch(): SpriteBatch = SpriteBatch()

    @Provides
    @Singleton
    fun provideShapeRender(): ShapeRenderer = ShapeRenderer()

    @Provides
    @Singleton
    fun provideGameClient(): GameClient<Event> = GameClient()

    @Provides
    @Singleton
    fun provideOrthographicCamera(): OrthographicCamera = OrthographicCamera(100F, 100F)

    @Provides
    @Singleton
    fun provideViewport(camera: OrthographicCamera): Viewport = FillViewport(100F, 100F, camera)

    @Provides
    @Singleton
    fun provideStage(viewport: Viewport): Stage = Stage(viewport)

    @Provides
    @Singleton
    fun provideAssetManager(): AssetManager = AssetManager()
}