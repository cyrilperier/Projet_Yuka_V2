package fr.epf.min.projet_yuka


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.epf.min.projet_yuka.model.Favori
import kotlinx.android.synthetic.main.list_favori_view.view.*



class ListFavoriAdapter ( val favoris : List<Favori>,val context : Context) : RecyclerView.Adapter<ListFavoriAdapter.FavoriViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class FavoriViewHolder(val favoriView: View) : RecyclerView.ViewHolder(favoriView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.list_favori_view, parent, false)

        return FavoriViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FavoriViewHolder, position: Int)  {
        val favori: Favori = favoris[position]
        holder.favoriView.favorie_name_textview.text=
                "${favori.name} "

        Glide.with(context)
                .load(favori.url_image_small)
            .into(holder.favoriView.favorie_imageview)


      

        holder.favoriView.setOnClickListener{

            with(it.context){
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("favori_name",favori.name )
                intent.putExtra("already_favori",true)
                intent.putExtra("nutriscore_grade",favori.nutri_score)
                intent.putExtra("ingredients_text",favori.ingredients)
                intent.putExtra("url_image",favori.url_image)
                intent.putExtra("url_image_small",favori.url_image_small)
                intent.putExtra("code",favori.code)
                startActivity(intent)

            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = favoris.size

}
