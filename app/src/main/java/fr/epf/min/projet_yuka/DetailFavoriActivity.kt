package fr.epf.min.projet_yuka


import android.os.Bundle

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.bumptech.glide.Glide
import fr.epf.min.projet_yuka.data.AccesDataProduct
import fr.epf.min.projet_yuka.data.FavoriDao
import fr.epf.min.projet_yuka.data.FavoriDataBase
import fr.epf.min.projet_yuka.data.GetProductsResult
import fr.epf.min.projet_yuka.model.Favori
import kotlinx.android.synthetic.main.activity_detail_produit.*
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class DetailFavoriActivity : AppCompatActivity(){
    lateinit var database: FavoriDataBase
    lateinit var favoriDao: FavoriDao
    lateinit var favori_name: String
    lateinit var favori_ingredient:String
    lateinit var favori_nutri_score:String
    lateinit var favori_url_image:String
    var favori_code: Long=0
    lateinit var Newfavorie : Favori
    lateinit var result : GetProductsResult
    var already_favori =true
    var scan=false
    var code: Long =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent

        if (intent.hasExtra("code")) {
            code = intent.getLongExtra("code",0)
            already_favori=intent.getBooleanExtra("already_favori",true)
            scan=intent.getBooleanExtra("scan",false)
        }
        if (intent.hasExtra("favori_name")) {
            favori_name = intent.getStringExtra("favori_name").toString()
            favori_ingredient=intent.getStringExtra("ingredients_text").toString()
            favori_nutri_score=intent.getStringExtra("nutriscore_grade").toString()
            favori_url_image=intent.getStringExtra("url_image").toString()
            favori_code=intent.getLongExtra("code",0)
            already_favori=intent.getBooleanExtra("already_favori",false)
            scan=intent.getBooleanExtra("scan",false)

        }

        Dao()
        when(already_favori){
            true -> {
                when(scan){

                    true->{
                    recoverProduct()
                    Name_product_textview?.text= "${result.product.product_name}"
                    Nutri_Score_textView?.text="${result.product.nutriscore_grade}"
                    Ingredient_product_textView?.text="${result.product.ingredients_text}"
                    AffichePicture(result.product.image_url)}

                    false->{
                    Name_product_textview?.text= "${favori_name}"
                    Nutri_Score_textView?.text="${favori_nutri_score}"
                    Ingredient_product_textView?.text="${favori_ingredient}"
                    AffichePicture(favori_url_image)}}
            }

            false -> {
                recoverProduct()
                Name_product_textview?.text= "${result.product.product_name}"
                Nutri_Score_textView?.text="${result.product.nutriscore_grade}"
                Ingredient_product_textView?.text="${result.product.ingredients_text}"
                AffichePicture(result.product.image_url)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when(already_favori){
        true -> menuInflater.inflate(R.menu.detail_favori,menu)
        false -> menuInflater.inflate(R.menu.detail_product,menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_favori_action -> {
                AlertDialog.Builder(this)
                        .setTitle("Confirmation")
                        .setMessage("Voulez-vous vraiment supprimer ce produit des favoris ?")
                        .setPositiveButton("Oui"){
                            _,_ ->

                            Dao()
                            runBlocking {
                                val favoris = favoriDao.findByCode(favori_code)
                                favoriDao.deleteFavori(favoris) }
                            finish()
                            Toast.makeText(this,"Favori supprimé", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Non"){
                            _,_ ->
                            Log.d("epf","close dialog")
                        }
                        .show()
            }
            R.id.add_favori_action -> {
                runBlocking { favoriDao.addFavori(Newfavorie) }
                finish()
                Toast.makeText(this,"Favori ajouté", Toast.LENGTH_SHORT).show()

            }

        }
        return super.onOptionsItemSelected(item)
    }
    private fun Dao(){
        //accés a la base
        database = Room.databaseBuilder(
                this, FavoriDataBase::class.java, "favoris-db"

        ).build()
        favoriDao = database.getFavoriDao()
    }

    private fun recoverProduct() {

        runBlocking {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://world.openfoodfacts.org/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            val service = retrofit.create(AccesDataProduct::class.java)
            result = service.getProducts(code)
            Log.d("EPF","$result")


            Newfavorie = Favori(
                    null,result.product.product_name,
                    result.product.nutriscore_grade,
                    result.product.ingredients_text,
                    result.product.image_front_small_url,
                    result.product.image_url,
            result.product.code)





        }

    }


    private fun AffichePicture(url_picture: String){

        Glide.with(baseContext)
            .load(url_picture)
                .centerInside()
            .into(product_imageView)
    }

}