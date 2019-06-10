package mx.contraloria.dap.Adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import mx.contraloria.dap.models.ScreenItem
import mx.contraloria.dap.models.mList
import mx.contraloria.dap.R

class IntroViewPagerAdapter(var mCtx: Context, var mListScreen :List<ScreenItem>): PagerAdapter(){

    override fun getCount(): Int {
        return mListScreen.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater: LayoutInflater = mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE ) as LayoutInflater
        val layoutScreen: View = layoutInflater.inflate(R.layout.layout_screen,null)

        val image : ImageView = layoutScreen.findViewById(R.id.iIntroImage)
        val titulo : TextView = layoutScreen.findViewById(R.id.txtIntroTitle)
        val descripcion : TextView = layoutScreen.findViewById(R.id.txtIntroDesc)

        titulo.setText(mListScreen.get(position).Title)
        descripcion.setText(mListScreen.get(position).Description)
        image.setImageResource(mListScreen.get(position).ScreenImage)

        container.addView(layoutScreen )
        return layoutScreen
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
       return p0 == p1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}