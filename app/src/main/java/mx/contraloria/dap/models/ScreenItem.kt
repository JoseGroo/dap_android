package mx.contraloria.dap.models

class ScreenItem(val Titles : String,val Descriptions: String,val ScreenImages: Int) {

    var Title=""
    var Description=""
    var ScreenImage=1
    init {
        this.Title = Titles
        this.Description = Descriptions
        this.ScreenImage = ScreenImages
    }


}