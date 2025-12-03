package models.textures

abstract class TextureTypeComponent {

    abstract fun getItems(): Array<String>
    abstract fun getSkinID(): SkinID

}